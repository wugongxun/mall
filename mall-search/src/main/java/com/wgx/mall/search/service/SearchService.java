package com.wgx.mall.search.service;

import com.wgx.mall.search.vo.SearchParam;
import com.wgx.mall.search.vo.SearchResult;

/**
 * @author wgx
 * @since 2023/3/21 14:42
 */
public interface SearchService {

    SearchResult search(SearchParam searchParam);
}
