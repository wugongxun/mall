package com.wgx.mall.cart.interceptor;

import com.wgx.common.constants.AuthConstant;
import com.wgx.common.constants.CartConstant;
import com.wgx.common.to.MemberTo;
import com.wgx.common.to.UserInfoTo;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author wgx
 * @since 2023/4/1 16:01
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberTo loginUser = (MemberTo) session.getAttribute(AuthConstant.LOGIN_USER);
        if (loginUser != null) {
            userInfoTo.setUserId(loginUser.getId());
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            Arrays.stream(cookies).forEach(cookie -> {
                if (CartConstant.TEMP_USER_COOKIE_NAME.equals(cookie.getName())) {
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            });
        }

        if (!StringUtils.hasLength(userInfoTo.getUserKey())) {
            userInfoTo.setUserKey(UUID.randomUUID().toString());
        }

        threadLocal.set(userInfoTo);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        UserInfoTo userInfoTo = threadLocal.get();

        if (!userInfoTo.getTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("mall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }

    }
}
