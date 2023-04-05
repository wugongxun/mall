package com.wgx.mall.member.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.common.exception.ExceptionCode;
import com.wgx.common.to.SocialUserTo;
import com.wgx.common.utils.R;
import com.wgx.mall.member.to.LoginTo;
import com.wgx.mall.member.to.RegisterTo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
                            .or()
                            .eq(MemberEntity::getMobile, registerTo.getUsername())
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

        memberEntity.setPassword(passwordEncoder.encode(registerTo.getPassword()));
        this.baseMapper.insert(memberEntity);
        return R.ok("注册成功");
    }

    @Override
    public R<MemberEntity> login(LoginTo loginTo) {

        MemberEntity member = this.getOne(
                Wrappers.lambdaQuery(MemberEntity.class)
                        .eq(MemberEntity::getUsername, loginTo.getAccount())
                        .or()
                        .eq(MemberEntity::getMobile, loginTo.getAccount())
        );
        if (member == null || !passwordEncoder.matches(loginTo.getPassword(), member.getPassword())) {
            return R.error(ExceptionCode.ACCOUNT_OR_PASSWORD_ERROR);
        }
        return R.ok("登录成功").setData(member);
    }

    @Override
    public R<MemberEntity> socialLogin(SocialUserTo socialUserTo) {
        MemberEntity member = getOne(
                Wrappers.lambdaQuery(MemberEntity.class)
                        .eq(MemberEntity::getSocialUid, socialUserTo.getId())
        );
        if (member != null) {
            member.setAccessToken(socialUserTo.getAccessToken())
                    .setExpiration(socialUserTo.getExpiration());
            updateById(member);
            return R.ok().putDate(member);
        } else {
            MemberEntity newMember = new MemberEntity();
            newMember.setSocialUid(socialUserTo.getId())
                    .setAccessToken(socialUserTo.getAccessToken())
                    .setExpiration(socialUserTo.getExpiration())
                    .setUsername(socialUserTo.getLogin())
                    .setLevelId(1l)
                    .setCreateTime(new Date())
                    .setNickname(socialUserTo.getName())
                    .setHeader(socialUserTo.getAvatarUrl());
            save(newMember);
            return R.ok().putDate(newMember);
        }
    }
}