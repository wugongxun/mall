package com.wgx.mall.coupon.service.impl;

import com.wgx.common.to.MemberPrice;
import com.wgx.common.to.SkuReductionTo;
import com.wgx.mall.coupon.entity.MemberPriceEntity;
import com.wgx.mall.coupon.entity.SkuLadderEntity;
import com.wgx.mall.coupon.service.MemberPriceService;
import com.wgx.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.coupon.dao.SkuFullReductionDao;
import com.wgx.mall.coupon.entity.SkuFullReductionEntity;
import com.wgx.mall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveInfo(SkuReductionTo skuReductionTo) {
        //保存满减打折
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId())
                .setFullCount(skuReductionTo.getFullCount())
                .setDiscount(skuReductionTo.getDiscount())
                .setAddOther(skuReductionTo.getCountStatus());

        if (skuReductionTo.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }

        //保存SkuFullReductionEntity
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        if (skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
            this.save(skuFullReductionEntity);
        }

        //保存会员价格
        List<MemberPrice> memberPrices = skuReductionTo.getMemberPrice();
        if (memberPrices != null && !memberPrices.isEmpty()) {
            List<MemberPriceEntity> memberPriceEntities = memberPrices.stream().map(memberPrice -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setSkuId(skuReductionTo.getSkuId())
                        .setMemberLevelId(memberPrice.getId())
                        .setMemberLevelName(memberPrice.getName())
                        .setMemberPrice(memberPrice.getPrice())
                        .setAddOther(1);
                return memberPriceEntity;
            }).filter(memberPriceEntity -> memberPriceEntity.getMemberPrice().compareTo(BigDecimal.ZERO) == 1).collect(Collectors.toList());
            memberPriceService.saveBatch(memberPriceEntities);
        }
    }

}