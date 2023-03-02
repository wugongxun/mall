package com.wgx.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wgx.common.utils.PageUtils;
import com.wgx.mall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:41:43
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

