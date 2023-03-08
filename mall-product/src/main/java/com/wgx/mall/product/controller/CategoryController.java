package com.wgx.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.mall.product.entity.CategoryBrandRelationEntity;
import com.wgx.mall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.wgx.mall.product.entity.CategoryEntity;
import com.wgx.mall.product.service.CategoryService;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.R;

import javax.annotation.Resource;


/**
 * 商品三级分类
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:06:12
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    //返回所有的分类，并以树形结构返回
    @GetMapping("/list/tree")
    public R tree() {
        List<CategoryEntity> entities = categoryService.tree();
        return R.ok().put("data", entities);
    }

    //批量更新节点
    @PostMapping("/updateBatchById")
    public R updateByIds(@RequestBody List<CategoryEntity> categories) {
        categoryService.updateBatchById(categories);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        //修改联级表
        if (StringUtils.hasLength(category.getName())) {
            categoryBrandRelationService.update(
                    Wrappers.lambdaUpdate(CategoryBrandRelationEntity.class)
                            .eq(CategoryBrandRelationEntity::getCatelogId, category.getCatId())
                            .set(CategoryBrandRelationEntity::getCatelogName, category.getName())
            );
        }

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
