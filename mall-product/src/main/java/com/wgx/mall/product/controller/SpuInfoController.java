package com.wgx.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.wgx.mall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wgx.mall.product.entity.SpuInfoEntity;
import com.wgx.mall.product.service.SpuInfoService;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.R;



/**
 * spu信息
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:06:11
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;


    /**
     * 商品上架
     */
    @PostMapping("/{spuId}/up")
    public R up(@PathVariable("spuId") Long spuId) {
        this.spuInfoService.up(spuId);
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo spuSaveVo){
        spuInfoService.saveSpuInfo(spuSaveVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
