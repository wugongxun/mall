package com.wgx.mall.auth.controller;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.PropertyNamingStrategy;
import com.alibaba.fastjson2.TypeReference;
import com.wgx.common.constants.AuthConstant;
import com.wgx.common.exception.ExceptionCode;
import com.wgx.common.to.MemberTo;
import com.wgx.common.to.SocialUserTo;
import com.wgx.common.utils.R;
import com.wgx.mall.auth.feign.MemberService;
import com.wgx.mall.auth.feign.OtherService;
import com.wgx.mall.auth.vo.UserLoginVo;
import com.wgx.mall.auth.vo.UserRegisterVo;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Value("${gitee.oauth.clientid}")
    public String clientId;
    @Value("${gitee.oauth.clientsecret}")
    public String clientSecret;
    @Value("${gitee.oauth.callback}")
    public String callbackUrl;

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

    @PostMapping("/login")
    public String login(@Valid UserLoginVo loginVo, RedirectAttributes redirectAttributes, HttpSession session) {
        R<MemberTo> r = memberService.login(loginVo);
        if (r.getCode() == 0) {
            session.setAttribute(AuthConstant.LOGIN_USER, r.getData(new TypeReference<MemberTo>() {}));
            return "redirect:http://www.mall.com";
        } else {
            redirectAttributes.addFlashAttribute("errors", r.getMsg());
            return "redirect:http://www.auth.mall.com/login.html";
        }
    }

    @GetMapping("/giteeLogin")
    public String giteeLogin() {
        String s = "https://gitee.com/oauth/authorize?client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(callbackUrl) +
                "&response_type=code" +
                "&scope=user_info%20emails";
        return "redirect:" + s;
    }

    @GetMapping("/callback")
    public String callback(String code, RedirectAttributes redirectAttributes, HttpSession session) throws IOException {
        String url = "https://gitee.com/oauth/token?grant_type=authorization_code" +
                "&code=" + code +
                "&client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(callbackUrl) +
                "&client_secret=" + clientSecret;
        HttpPost httpPost = new HttpPost(url);
        HttpClient httpClient = HttpClients.createDefault();
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();


        String res = EntityUtils.toString(entity, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(res);
        String accessToken = (String) jsonObject.get("access_token");
        HttpGet httpGet = new HttpGet("https://gitee.com/api/v5/user?access_token=" + accessToken);
        HttpResponse userRes = httpClient.execute(httpGet);

        httpPost.releaseConnection();

        String snakeToCamel = PropertyNamingStrategy.snakeToCamel(EntityUtils.toString(userRes.getEntity(), "UTF-8"));
        SocialUserTo socialUserTo = JSONObject.parseObject(snakeToCamel, SocialUserTo.class);
        socialUserTo.setAccessToken(accessToken);
        LocalDateTime ex = LocalDateTime.now().plusDays(1);
        socialUserTo.setExpiration(Date.from(ex.toInstant(ex.atZone(ZoneId.systemDefault()).getOffset())));


        R r = memberService.socialLogin(socialUserTo);
        if (r.getCode() == 0) {
            session.setAttribute(AuthConstant.LOGIN_USER, r.getData(new TypeReference<MemberTo>() {}));
            return "redirect:http://mall.com";
        } else {
            redirectAttributes.addFlashAttribute("errors", r.getMsg());
            return "redirect:http://www.auth.mall.com/login.html";
        }
    }

}
