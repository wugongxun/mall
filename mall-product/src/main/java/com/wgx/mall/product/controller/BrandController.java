package com.wgx.mall.product.controller;

import java.util.*;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.common.validator.group.AddGroup;
import com.wgx.common.validator.group.UpdateGroup;
import com.wgx.mall.product.entity.CategoryBrandRelationEntity;
import com.wgx.mall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wgx.mall.product.entity.BrandEntity;
import com.wgx.mall.product.service.BrandService;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.R;


/**
 * 品牌
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:06:12
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand){
		brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand){
		brandService.updateById(brand);

        //修改联级表
        if (StringUtils.hasLength(brand.getName())) {
            categoryBrandRelationService.update(
                    Wrappers.lambdaUpdate(CategoryBrandRelationEntity.class)
                            .eq(CategoryBrandRelationEntity::getBrandId, brand.getBrandId())
                            .set(CategoryBrandRelationEntity::getBrandName, brand.getName())
            );
        }

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
