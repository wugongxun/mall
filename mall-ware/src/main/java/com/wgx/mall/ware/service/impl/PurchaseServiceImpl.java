package com.wgx.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.common.constants.WareConstant;
import com.wgx.common.exception.RRException;
import com.wgx.mall.ware.entity.PurchaseDetailEntity;
import com.wgx.mall.ware.service.PurchaseDetailService;
import com.wgx.mall.ware.service.WareSkuService;
import com.wgx.mall.ware.vo.DoneVo;
import com.wgx.mall.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.ware.dao.PurchaseDao;
import com.wgx.mall.ware.entity.PurchaseEntity;
import com.wgx.mall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils unreceiveList(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                Wrappers.lambdaQuery(PurchaseEntity.class)
                        .in(PurchaseEntity::getStatus, 0, 1)
        );
        return new PageUtils(page);

    }

    @Override
    @Transactional
    public void merge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();

        //如果没有purchaseId，新建一个
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatus.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        } else {
            //如果有采购单，判断是否为新建或者未领取状态
            Integer status = this.getObj(
                    Wrappers.lambdaQuery(PurchaseEntity.class)
                            .eq(PurchaseEntity::getId, purchaseId),
                    i -> (Integer) i
            );
            if (status != WareConstant.PurchaseStatus.CREATED.getCode() || status != WareConstant.PurchaseStatus.ASSIGNED.getCode()) {
                throw new RRException(purchaseId + "号采购单已经被领取");
            }
        }

        Long[] items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> purchaseDetailEntities = Arrays.stream(items).map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatus.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(purchaseDetailEntities);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setId(purchaseId);
        this.updateById(purchaseEntity);
    }


    @Override
    @Transactional
    public void received(List<Long> purchaseIds) {
        //过滤掉不是新建或者已分配的采购单，并设置新的状态
        List<PurchaseEntity> purchaseEntities = this.listByIds(purchaseIds).stream()
                .filter(purchase -> purchase.getStatus() == WareConstant.PurchaseStatus.CREATED.getCode()
                        || purchase.getStatus() == WareConstant.PurchaseStatus.ASSIGNED.getCode()
                ).map(purchase -> {
                    purchase.setStatus(WareConstant.PurchaseStatus.RECEIVED.getCode());
                    purchase.setUpdateTime(new Date());
                    return purchase;
                }).collect(Collectors.toList());
        this.updateBatchById(purchaseEntities);

        purchaseDetailService.update(
                Wrappers.lambdaUpdate(PurchaseDetailEntity.class)
                        .set(PurchaseDetailEntity::getStatus, WareConstant.PurchaseDetailStatus.BUYING.getCode())
                        .in(PurchaseDetailEntity::getPurchaseId, purchaseIds)
        );

    }

    @Override
    @Transactional
    public void done(DoneVo doneVo) {
        //更新采购单，如果所有item都为已完成采购单为完成，否则未完成
        boolean allMatch = doneVo.getItems().stream().allMatch(item -> item.getStatus() == WareConstant.PurchaseDetailStatus.FINISH.getCode());
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(doneVo.getId());
        purchaseEntity.setStatus(allMatch ? WareConstant.PurchaseStatus.FINISH.getCode() : WareConstant.PurchaseStatus.ERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

        //更新所有的采购项
        List<PurchaseDetailEntity> purchaseDetailEntities = doneVo.getItems().stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setStatus(item.getStatus());
            purchaseDetailEntity.setId(item.getItemId());

            //如果改采购项为成功，更新库存
            if (item.getStatus() == WareConstant.PurchaseDetailStatus.FINISH.getCode()) {
                PurchaseDetailEntity byId = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(byId.getSkuId(), byId.getWareId(), byId.getSkuNum());
            }


            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(purchaseDetailEntities);

    }

}