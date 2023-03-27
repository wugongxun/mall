package com.wgx.mall.auth.controller;

import com.wgx.common.constants.AuthConstant;
import com.wgx.common.exception.ExceptionCode;
import com.wgx.common.utils.R;
import com.wgx.mall.auth.feign.MemberService;
import com.wgx.mall.auth.feign.OtherService;
import com.wgx.mall.auth.vo.UserRegisterVo;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wgx
 * @since 2023/3/25 17:19
 */
@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private OtherService otherService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberService memberService;

    @GetMapping("/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        String cacheCode = stringRedisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + phone);
        long cacheTime = -1;
        if (StringUtils.hasLength(cacheCode)) {
            cacheTime = Long.parseLong(cacheCode.split("_")[1]);
        }
        if (cacheTime == -1 || System.currentTimeMillis() - cacheTime > 60 * 1000) {
            String code = RandomStringUtils.random(6, false, true);
            stringRedisTemplate.opsForValue().set(AuthConstant.SMS_CODE_CACHE_PREFIX + phone,
                    code + "_" + System.currentTimeMillis(),
                    10, TimeUnit.MINUTES);

            R send = otherService.send(phone, code);
            return send;
        } else {
            return R.error(ExceptionCode.SMS_CODE_EXCEPTION);
        }
    }

    //TODO 分布式session
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo registerVo, BindingResult result, RedirectAttributes redirectAttributes) {
        Map<String, String> errors = new HashMap<>();
        if (result.hasErrors()) {
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        } else {
            String cacheCode = stringRedisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + registerVo.getPhone());
            if (StringUtils.hasLength(cacheCode) && cacheCode.split("_")[0].equals(registerVo.getCode())) {
                stringRedisTemplate.delete(AuthConstant.SMS_CODE_CACHE_PREFIX + registerVo.getPhone());
                //注册
                R r = memberService.register(registerVo);
                if (r.getCode() == 0) {
                    return "redirect:http://www.auth.mall.com/login.html";
                } else {
                    errors.put("username", r.getMsg());
                    errors.put("phone", r.getMsg());
                }
            } else {
                errors.put("code", "验证码错误");
            }
        }
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:http://www.auth.mall.com/register.html";
    }

}
