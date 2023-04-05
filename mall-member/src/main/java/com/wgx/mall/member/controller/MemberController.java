package com.wgx.mall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.wgx.common.to.SocialUserTo;
import com.wgx.mall.member.to.LoginTo;
import com.wgx.mall.member.to.RegisterTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wgx.mall.member.entity.MemberEntity;
import com.wgx.mall.member.service.MemberService;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.R;



/**
 * 会员
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:36:46
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;


    /**
     * 社交登录
     */
    @PostMapping("/socialLogin")
    public R<MemberEntity> socialLogin(@RequestBody SocialUserTo socialUserTo) {
        try {
            return memberService.socialLogin(socialUserTo);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("登录失败");
        }
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public R<MemberEntity> login(@RequestBody LoginTo loginTo) {
        return memberService.login(loginTo);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public R register(@RequestBody RegisterTo registerTo) {
        return memberService.register(registerTo);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
