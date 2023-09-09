package com.example.blog_system.Service;

import com.example.blog_system.Mapper.UserMapper;
import com.example.blog_system.Model.Article;
import com.example.blog_system.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    public int register(User user)
    {
        return userMapper.register(user);
    }
    public User login(String username)
    {
        return userMapper.login(username);
    }
    public User getUserByName(String username)
    {
        return userMapper.login(username);
    }

}
