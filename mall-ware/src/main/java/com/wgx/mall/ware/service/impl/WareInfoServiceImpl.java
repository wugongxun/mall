package com.wgx.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.ware.dao.WareInfoDao;
import com.wgx.mall.ware.entity.WareInfoEntity;
import com.wgx.mall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                Wrappers.lambdaQuery(WareInfoEntity.class)
                        .and(StringUtils.hasLength(key), w -> {
                            w.eq(WareInfoEntity::getId, key)
                                    .or().like(WareInfoEntity::getName, key)
                                    .or().like(WareInfoEntity::getAddress, key)
                                    .or().like(WareInfoEntity::getAreacode, key);
                        })
        );

        return new PageUtils(page);
    }

}