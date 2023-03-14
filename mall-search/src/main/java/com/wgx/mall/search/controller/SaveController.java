package com.wgx.mall.search.controller;

import com.wgx.common.exception.ExceptionCode;
import com.wgx.common.to.SkuEsTo;
import com.wgx.common.utils.R;
import com.wgx.mall.search.service.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author wgx
 * @since 2023/3/14 17:22
 */
@RestController
@RequestMapping("/search/save")
public class SaveController {
    @Autowired
    private SaveService saveService;

    @PostMapping("/up")
    public R up(@RequestBody List<SkuEsTo> skuEsTos) {
        try {
            saveService.up(skuEsTos);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error(ExceptionCode.PRODUCT_UP_EXCEPTION);
        }
        return R.ok();
    }


}
