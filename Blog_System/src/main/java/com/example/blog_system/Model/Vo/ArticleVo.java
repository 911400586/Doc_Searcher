package com.example.blog_system.Model.Vo;

import com.example.blog_system.Model.Article;
import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleVo extends Article  implements Serializable {
    private String username;
}
