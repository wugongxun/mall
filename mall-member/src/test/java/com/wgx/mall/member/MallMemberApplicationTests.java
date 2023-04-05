package com.wgx.mall.member;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.PropertyNamingStrategy;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.wgx.common.to.SocialUserTo;
import com.wgx.common.utils.R;
import com.wgx.mall.member.entity.MemberEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

//@SpringBootTest
class MallMemberApplicationTests {

    @Test
    void contextLoads() {
//        SocialUserTo socialUserTo = new Gson().fromJson("{\"access_token\":\"123456\"}", SocialUserTo.class);
        String s = PropertyNamingStrategy.snakeToCamel("{\"access_token\":\"123456\"}");
        System.out.println(s);
        SocialUserTo socialUserTo = JSON.parseObject(s, SocialUserTo.class);
        System.out.println(socialUserTo.getAccessToken());

    }

}
