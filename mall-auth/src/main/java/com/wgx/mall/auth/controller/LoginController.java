package com.wgx.mall.auth.controller;

import com.wgx.common.constants.AuthConstant;
import com.wgx.common.exception.ExceptionCode;
import com.wgx.common.utils.R;
import com.wgx.mall.auth.feign.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author wgx
 * @since 2023/3/25 17:19
 */
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private OtherService otherService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        String cacheCode = stringRedisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + phone);
        long cacheTime = -1;
        if (StringUtils.hasLength(cacheCode)) {
            cacheTime = Long.parseLong(cacheCode.split("_")[1]);
        }
        if (cacheTime == -1 || System.currentTimeMillis() - cacheTime > 60 * 1000) {
            String code = UUID.randomUUID().toString().substring(0, 5);
            stringRedisTemplate.opsForValue().set(AuthConstant.SMS_CODE_CACHE_PREFIX + phone,
                    code + "_" + System.currentTimeMillis(),
                    10, TimeUnit.MINUTES);

            R send = otherService.send(phone, code);
            return send;
        } else {
            return R.error(ExceptionCode.SMS_CODE_EXCEPTION);
        }
    }

}
