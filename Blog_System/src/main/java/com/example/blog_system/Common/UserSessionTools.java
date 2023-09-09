package com.example.blog_system.Common;

import com.example.blog_system.Model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserSessionTools {
    public static User getLoginUser(HttpServletRequest request)
    {
        HttpSession session= request.getSession(false);
        return (User)session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
    }
}
