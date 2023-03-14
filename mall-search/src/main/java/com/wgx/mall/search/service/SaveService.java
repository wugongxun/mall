package com.wgx.mall.search.service;

import com.wgx.common.to.SkuEsTo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author wgx
 * @since 2023/3/14 17:24
 */
public interface SaveService {
    void up(List<SkuEsTo> skuEsTos) throws IOException;
}
