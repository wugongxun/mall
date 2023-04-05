package com.wgx.mall.order.interceptor;

import com.wgx.common.constants.AuthConstant;
import com.wgx.common.to.MemberTo;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author wgx
 * @since 2023/4/5 14:54
 */
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberTo loginUser = (MemberTo) session.getAttribute(AuthConstant.LOGIN_USER);
        if (loginUser != null) {
            threadLocal.set(loginUser);
            return true;
        }
        response.sendRedirect("http://auth.mall.com/login.html");
        return false;
    }
}
