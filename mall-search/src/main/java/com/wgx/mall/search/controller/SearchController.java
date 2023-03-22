package com.wgx.mall.search.controller;

import com.wgx.mall.search.service.SearchService;
import com.wgx.mall.search.vo.SearchParam;
import com.wgx.mall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author wgx
 * @since 2023/3/21 14:12
 */
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;


    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model) {
        SearchResult searchResult = searchService.search(searchParam);
        model.addAttribute("result", searchResult);
        return "list";
    }

}
