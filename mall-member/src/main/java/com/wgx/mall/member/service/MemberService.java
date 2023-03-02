package com.wgx.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wgx.common.utils.PageUtils;
import com.wgx.mall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:36:46
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

