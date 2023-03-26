package com.wgx.mall.search.service.impl;

import com.alibaba.fastjson2.JSON;
import com.wgx.common.to.SkuEsTo;
import com.wgx.mall.search.constants.EsConstant;
import com.wgx.mall.search.service.SearchService;
import com.wgx.mall.search.vo.SearchParam;
import com.wgx.mall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wgx
 * @since 2023/3/21 14:43
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient client;


    @Override
    public SearchResult search(SearchParam searchParam) {
        SearchRequest searchRequest = buildSearchRequest(searchParam);


        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchResult searchResult = buildSearchResult(response);
            searchResult.setPageNum(Optional.ofNullable(searchParam.getPageNum()).orElse(1));
            return searchResult;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    //封装查询的dsl语句
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        SearchSourceBuilder builder = new SearchSourceBuilder();

        //查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        //must
        if (StringUtils.hasLength(searchParam.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
        }

        //filter
        if (searchParam.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }

        if (!CollectionUtils.isEmpty(searchParam.getBrandId())) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }

        boolQuery.filter(QueryBuilders.termQuery("hasStock", Optional.ofNullable(searchParam.getHasStock()).orElse(1) == 1));

        if (StringUtils.hasLength(searchParam.getSkuPrice())) {
            String skuPrice = searchParam.getSkuPrice();

            String[] s = searchParam.getSkuPrice().split("_");
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");

            if (skuPrice.startsWith("_")) {
                boolQuery.filter(rangeQuery.lte(s[1]));
            } else if (skuPrice.endsWith("_")) {
                boolQuery.filter(rangeQuery.gte(s[0]));
            } else {
                boolQuery.filter(rangeQuery.gte(s[0]).lte(s[1]));
            }
        }

        if (!CollectionUtils.isEmpty(searchParam.getAttrs())) {
            for (String attrStr : searchParam.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                String attrId = s[0];
                String[] attrValue = s[1].split(":");

                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", Arrays.stream(attrValue).collect(Collectors.toList())));

                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(
                        "attrs",
                        nestedBoolQuery,
                        ScoreMode.None
                );

                boolQuery.filter(nestedQuery);
            }
        }
        builder.query(boolQuery);

        //排序
        if (StringUtils.hasLength(searchParam.getSort())) {
            String[] s = searchParam.getSort().split("_");
            builder.sort(s[0], s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC);
        }

        //分页
        builder.from((Optional.ofNullable(searchParam.getPageNum()).orElse(1) - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        builder.size(EsConstant.PRODUCT_PAGE_SIZE);

        //高亮
        if (StringUtils.hasLength(searchParam.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            builder.highlighter(highlightBuilder);
        }


        //聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders
                .terms("brand_agg")
                .field("brandId")
                .size(50)
                .subAggregation(
                        AggregationBuilders
                                .terms("brand_name_agg")
                                .field("brandName")
                                .size(1)
                )
                .subAggregation(
                        AggregationBuilders
                                .terms("brand_img_agg")
                                .field("brandImg")
                                .size(1)
                );
        builder.aggregation(brandAgg);

        TermsAggregationBuilder catalogAgg = AggregationBuilders
                .terms("catalog_agg")
                .field("catalogId")
                .size(50)
                .subAggregation(
                        AggregationBuilders
                                .terms("catalog_name_agg")
                                .field("catalogName")
                                .size(1)
                );
        builder.aggregation(catalogAgg);

        NestedAggregationBuilder attrAgg = AggregationBuilders
                .nested("attr_agg", "attrs")
                .subAggregation(
                        AggregationBuilders
                                .terms("attr_id_agg")
                                .field("attrs.attrId")
                                .size(50)
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("attr_name_agg")
                                                .field("attrs.attrName")
                                                .size(1)
                                )
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("attr_value_agg")
                                                .field("attrs.attrValue")
                                                .size(50)
                                )
                );
        builder.aggregation(attrAgg);


        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, builder);
    }

    //封装返回结果
    private SearchResult buildSearchResult(SearchResponse response) {
        SearchResult searchResult = new SearchResult();

        SearchHits hits = response.getHits();


        //总记录数
        long total = hits.getTotalHits().value;
        searchResult.setTotal(total);
        searchResult.setTotalPage((int) (total + EsConstant.PRODUCT_PAGE_SIZE - 1) / EsConstant.PRODUCT_PAGE_SIZE);


        //封装查询到的记录
        SearchHit[] searchHits = hits.getHits();
        if (searchHits != null && searchHits.length != 0) {
            List<SkuEsTo> skuEsTos = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                String source = hit.getSourceAsString();
                SkuEsTo skuEsTo = JSON.parseObject(source, SkuEsTo.class);

                if (!CollectionUtils.isEmpty(hit.getHighlightFields())) {
                    skuEsTo.setSkuTitle(hit.getHighlightFields().get("skuTitle").fragments()[0].string());
                }

                skuEsTos.add(skuEsTo);
            }
            searchResult.setProducts(skuEsTos);
        }

        //封装聚合结果
        Aggregations aggregations = response.getAggregations();

        //分类聚合
        List<SearchResult.Catalog> catalogs = new ArrayList<>();
        ParsedLongTerms catalogAgg = aggregations.get("catalog_agg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            SearchResult.Catalog catalog = new SearchResult.Catalog();
            catalog.setCatalogId(bucket.getKeyAsNumber().longValue());

            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            catalog.setCatalogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());

            catalogs.add(catalog);
        }
        searchResult.setCatalogs(catalogs);

        //品牌聚合
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brandAgg = aggregations.get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(bucket.getKeyAsNumber().longValue());

            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            brandVo.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());

            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            brandVo.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());

            brandVos.add(brandVo);
        }
        searchResult.setBrands(brandVos);

        //属性聚合
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attrAgg = aggregations.get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());

            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            attrVo.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());

            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }
        searchResult.setAttrs(attrVos);

        return searchResult;
    }


}
