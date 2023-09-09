package com.example.blog_system.Common;

import com.example.blog_system.Common.AjaxResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAop {
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e){
        return AjaxResult.fail(-1,e.getMessage());
    }
}
