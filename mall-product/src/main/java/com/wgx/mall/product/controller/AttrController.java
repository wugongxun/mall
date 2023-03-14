package com.wgx.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.wgx.mall.product.entity.ProductAttrValueEntity;
import com.wgx.mall.product.service.ProductAttrValueService;
import com.wgx.mall.product.vo.AttrRespVo;
import com.wgx.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wgx.mall.product.entity.AttrEntity;
import com.wgx.mall.product.service.AttrService;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.R;



/**
 * 商品属性
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:06:12
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    //修改spu规格参数
    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId, @RequestBody List<ProductAttrValueEntity> entities) {
        productAttrValueService.updateSpuAttr(spuId, entities);
        return R.ok();
    }

    //获取spu规格
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrList(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrList(spuId);
        return R.ok().put("data", productAttrValueEntities);
    }


    //根据categoryId分页查询attr
    @GetMapping("/{type}/list/{categoryId}")
    public R queryBaseAttrPage(@RequestParam Map<String, Object> params, @PathVariable("categoryId") Long categoryId, @PathVariable("type") String type) {
        PageUtils page = attrService.queryBaseAttrPage(params, categoryId, type);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrRespVo attr = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attrVo){
		attrService.saveAttr(attrVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attrVo){
		attrService.updateAttr(attrVo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
