package com.wgx.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.ware.dao.PurchaseDetailDao;
import com.wgx.mall.ware.entity.PurchaseDetailEntity;
import com.wgx.mall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String wareId = (String) params.get("wareId");

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                Wrappers.lambdaQuery(PurchaseDetailEntity.class)
                        .eq(StringUtils.hasLength(wareId), PurchaseDetailEntity::getWareId, wareId)
                        .eq(StringUtils.hasLength(status), PurchaseDetailEntity::getStatus, status)
                        .and(StringUtils.hasLength(key), w -> {
                            w.eq(PurchaseDetailEntity::getPurchaseId, key)
                                    .or().eq(PurchaseDetailEntity::getSkuId, key)
                                    .or().like(PurchaseDetailEntity::getSkuNum, key);
                        })
        );

        return new PageUtils(page);
    }

}