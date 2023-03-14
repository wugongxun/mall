package com.wgx.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wgx.common.utils.PageUtils;
import com.wgx.mall.ware.entity.PurchaseEntity;
import com.wgx.mall.ware.vo.DoneVo;
import com.wgx.mall.ware.vo.MergeVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:41:43
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils unreceiveList(Map<String, Object> params);

    void merge(MergeVo mergeVo);

    void received(List<Long> purchaseIds);

    void done(DoneVo doneVo);
}

