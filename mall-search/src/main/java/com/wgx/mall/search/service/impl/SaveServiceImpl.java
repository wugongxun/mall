package com.wgx.mall.search.service.impl;

import com.alibaba.fastjson2.JSON;
import com.wgx.common.to.SkuEsTo;
import com.wgx.mall.search.constants.EsConstant;
import com.wgx.mall.search.service.SaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wgx
 * @since 2023/3/14 17:24
 */
@Service
@Slf4j
public class SaveServiceImpl implements SaveService {
    @Autowired
    private RestHighLevelClient client;

    @Override
    public void up(List<SkuEsTo> skuEsTos) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        skuEsTos.forEach(skuEsTo -> {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsTo.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(skuEsTo), XContentType.JSON);

            bulkRequest.add(indexRequest);
        });
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (bulk.hasFailures()) {
            List<String> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
            log.error("商品上架失败{}", collect);
        }
    }
}
