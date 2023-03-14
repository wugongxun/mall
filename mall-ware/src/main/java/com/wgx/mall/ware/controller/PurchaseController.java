package com.wgx.mall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wgx.mall.ware.vo.DoneVo;
import com.wgx.mall.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.wgx.mall.ware.entity.PurchaseEntity;
import com.wgx.mall.ware.service.PurchaseService;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.R;



/**
 * 采购信息
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:41:43
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;


    /**
     * 完成采购单
     */
    @PostMapping("/done")
    public R done(@Validated @RequestBody DoneVo doneVo) {
        purchaseService.done(doneVo);
        return R.ok();
    }


    /**
     * 领取采购单
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> purchaseIds) {
        purchaseService.received(purchaseIds);
        return R.ok();
    }


    /**
     * 合并采购单
     */
    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo) {
        purchaseService.merge(mergeVo);
        return R.ok();
    }

    /**
     * 查询未分配的采购单
     */
    @GetMapping("/unreceive/list")
    private R unreceiveList(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.unreceiveList(params);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
