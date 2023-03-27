package com.wgx.mall.member.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.common.utils.R;
import com.wgx.mall.member.to.RegisterTo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.member.dao.MemberDao;
import com.wgx.mall.member.entity.MemberEntity;
import com.wgx.mall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R register(RegisterTo registerTo) {
        //检查用户名和手机号是否唯一
        try {
            MemberEntity one = this.baseMapper.selectOne(
                    Wrappers.lambdaQuery(MemberEntity.class)
                            .eq(MemberEntity::getMobile, registerTo.getPhone())
                            .or()
                            .eq(MemberEntity::getUsername, registerTo.getUsername())
            );
            if (one != null) {
                return R.error("用户名或者手机号已注册");
            }
        } catch (Exception e) {
            return R.error("用户名或者手机号已注册");
        }


        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setLevelId(1l);
        memberEntity.setUsername(registerTo.getUsername());
        memberEntity.setMobile(registerTo.getPhone());
        //加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberEntity.setPassword(passwordEncoder.encode(registerTo.getPassword()));
        this.baseMapper.insert(memberEntity);
        return R.ok("注册成功");
    }
}