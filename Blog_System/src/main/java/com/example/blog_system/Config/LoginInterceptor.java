package com.example.blog_system.Config;

import com.example.blog_system.Common.ApplicationVariable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if(session!=null && session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO)!=null)
        {
            return true;
        }
        //未登录
        response.sendRedirect("/login.html");
        return false;
    }
}
