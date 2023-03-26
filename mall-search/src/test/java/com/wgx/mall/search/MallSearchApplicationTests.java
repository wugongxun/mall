package com.wgx.mall.search;

import com.wgx.mall.search.constants.EsConstant;
import org.apache.lucene.index.Term;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author wgx
 * @since 2023/3/13 16:14
 */
@SpringBootTest
public class MallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("new_bank");

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("address", "mill"));
        builder.aggregation(AggregationBuilders.terms("ageAgg").field("age"));


        searchRequest.source(builder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Arrays.stream(response.getHits().getHits()).forEach(hit -> System.out.println(hit.getSourceAsString()));
        Terms ageAgg = response.getAggregations().get("ageAgg");
        ageAgg.getBuckets().forEach(bucket -> System.out.println(bucket.getKeyAsString() + ":" + bucket.getDocCount()));

    }

}
