package com.example.blog_system.Controller;

import com.example.blog_system.Common.AjaxResult;
import com.example.blog_system.Common.ApplicationVariable;
import com.example.blog_system.Common.PasswordTools;
import com.example.blog_system.Model.User;
import com.example.blog_system.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserControllerTest {
    @Autowired
    UserService userService;
    @Test
    void login() {
         String username = "张政";
         String password = "123456";
        //1.判空
        if(!StringUtils.hasLength(username) || !StringUtils.hasLength(password))
        {
            return;
        }
        //2.进行判断
        User user = userService.login(username);
        if(user==null || !StringUtils.hasLength(user.getUsername()))
        {
            return;
        }
        //判断密码是否正确 md5+salt
        if(PasswordTools.verify(password,user.getPassword()))
        {
            System.out.println("密码错误");
        }
        //3.创建session

    }
}