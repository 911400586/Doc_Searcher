package com.example.blog_system.Controller;

import com.example.blog_system.Common.AjaxResult;
import com.example.blog_system.Common.ApplicationVariable;
import com.example.blog_system.Common.PasswordTools;
import com.example.blog_system.Common.UserSessionTools;
import com.example.blog_system.Model.User;
import com.example.blog_system.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/reg")
    public AjaxResult Register(User user)
    {
        //1.判空
        if(user==null || !StringUtils.hasLength(user.getUsername()) || !StringUtils.hasLength(user.getPassword()))
        {
            return AjaxResult.fail(-1,"参数异常");
        }
        //2.操作数据库
        User user1 = userService.getUserByName(user.getUsername());
        if(user1!=null && user1.getUsername().equals(user.getUsername()))
        {
            return AjaxResult.fail(-2,"该用户已注册!");
        }
        user.setPassword(PasswordTools.Md5AndSalt(user.getPassword()));
       int num =  userService.register(user);
       if(num==1)
       {
           return AjaxResult.success(1);
       }
       return AjaxResult.fail(-1,"对不起,注册失败!");
    }
    @RequestMapping("/login")
    public AjaxResult Login(String username, String password, HttpServletRequest request)
    {
        //1.判空
        if(!StringUtils.hasLength(username) || !StringUtils.hasLength(password))
        {
            return AjaxResult.fail(-1,"参数异常");
        }
        //2.进行判断
        User user = userService.login(username);
        if(user==null || !StringUtils.hasLength(user.getUsername()))
        {
            if(user==null)
            {
                return AjaxResult.fail(-3,"参数异常");
            }
            if(!StringUtils.hasLength(user.getUsername()))
            {
                return AjaxResult.fail(-4,"参数异常");
            }

        }
        //判断密码是否正确 md5+salt
        if(!PasswordTools.verify(password,user.getPassword()))
        {
            return AjaxResult.fail(-1,"密码错误");
        }
        //3.创建session
//        HttpSession session = request.getSession();
//        session.setAttribute(ApplicationVariable.SESSION_KEY_USERINFO,user);
        HttpSession session = request.getSession();
        session.setAttribute(ApplicationVariable.SESSION_KEY_USERINFO,user);
        return AjaxResult.success(1);
    }
    @RequestMapping("/logout")
    public AjaxResult Logout(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        session.removeAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        return AjaxResult.success(1,"注销成功!");
    }
    @RequestMapping("/islogin")
    public AjaxResult islogin(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if(session!=null && session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO)!=null)
        {
            return AjaxResult.success(1);
        }
        //未登录
        return AjaxResult.success(-1);
    }
}
