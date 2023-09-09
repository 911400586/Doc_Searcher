package com.example.blog_system.Common;

import org.springframework.util.DigestUtils;
import sun.security.provider.MD5;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PasswordTools {
    public static String Md5AndSalt(String password)
    {
        //1.生成随机数(salt)
        String salt = UUID.randomUUID().toString().replaceAll("-","");
        //2.将pssword和salt用md5加密,生成新的密码
        String newPassword = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        //3.将salt和新密码用$进行合并
        return salt+'$'+newPassword;
    }
    public static String encrypt(String password, String salt) {
        // 1.使用(盐值+明文密码)得到加密的密码
        String finalPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes());
        // 2.将盐值和加密的密码共同返回（合并盐值和加密密码）
        String dbPassword = salt + "$" + finalPassword;
        return dbPassword;
    }

    public static boolean verify(String password,String saltpassowrd)
    {
        if(password==null ||password.equals("") || saltpassowrd==null || saltpassowrd.equals("")|| saltpassowrd.length()!=65)
        {
            return false;
        }
        //1.取出salt
        String[] arry = saltpassowrd.split("\\$");
        String salt = arry[0];
        String checkpassword = encrypt(password,salt);

        if(checkpassword.equals(saltpassowrd))
        {
            return true;
        }
        return false;
    }

}
