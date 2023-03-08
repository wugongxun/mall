package com.wgx.mall.product.controller;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.mall.product.entity.BrandEntity;
import com.wgx.mall.product.entity.CategoryEntity;
import com.wgx.mall.product.service.BrandService;
import com.wgx.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.wgx.mall.product.entity.CategoryBrandRelationEntity;
import com.wgx.mall.product.service.CategoryBrandRelationService;
import com.wgx.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:06:12
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取当前品牌关联的所有分类列表
     */
    @GetMapping("/catelog/list")
    public R list(@RequestParam Long brandId){
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
                Wrappers.lambdaQuery(CategoryBrandRelationEntity.class)
                        .eq(CategoryBrandRelationEntity::getBrandId, brandId)
        );

        return R.ok().put("data", data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        String brandName = brandService.getObj(
                Wrappers.lambdaQuery(BrandEntity.class)
                        .select(BrandEntity::getName)
                        .eq(BrandEntity::getBrandId, categoryBrandRelation.getBrandId()),
                String::valueOf
        );
        String categoryName = categoryService.getObj(
                Wrappers.lambdaQuery(CategoryEntity.class)
                        .select(CategoryEntity::getName)
                        .eq(CategoryEntity::getCatId, categoryBrandRelation.getCatelogId()),
                String::valueOf
        );
		categoryBrandRelationService.save(
                categoryBrandRelation.setBrandName(brandName)
                        .setCatelogName(categoryName)
        );

        System.out.println(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
