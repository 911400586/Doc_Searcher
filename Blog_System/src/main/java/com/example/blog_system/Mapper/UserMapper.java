package com.example.blog_system.Mapper;

import com.example.blog_system.Model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public int register(User user);
    public User login(String username);

}
