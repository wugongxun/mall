package com.wgx.mall.member.dao;

import com.wgx.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:36:46
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
