package com.example.blog_system.Model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User  implements Serializable {
    private int id;
    private String username;
    private String password;
    private String photo;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;
    private int state;
}
