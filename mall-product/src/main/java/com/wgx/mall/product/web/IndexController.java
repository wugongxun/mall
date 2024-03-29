package com.wgx.mall.product.web;

import com.wgx.mall.product.entity.CategoryEntity;
import com.wgx.mall.product.service.CategoryService;
import com.wgx.mall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author wgx
 * @since 2023/3/15 15:58
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/", "index.html"})
    public String index(Model model) {
        List<CategoryEntity> categroies = categoryService.getLevel1Categroies();
        model.addAttribute("categroies", categroies);
        return "index";
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }
}
