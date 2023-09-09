package com.example.blog_system.Common;

import lombok.Data;

import java.io.Serializable;
@Data
public class AjaxResult implements Serializable {
    //状态码
    private int code;
    //状态码信息
    private String msg;
    //数据信息
    //成功返回
    private Object data;
    public static AjaxResult success(Object data)
    {
        AjaxResult result = new AjaxResult();
        result.setData(data);
        result.setCode(200);
        result.setMsg("success");
        return result;
    }
    public static AjaxResult success(Object data,String msg)
    {
        AjaxResult result = new AjaxResult();
        result.setData(data);
        result.setMsg(msg);
        result.setCode(200);
        return result;
    }
    //失败返回
    public static AjaxResult fail(int code,String msg)
    {
        AjaxResult result = new AjaxResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData("");
        return result;
    }
    public static AjaxResult fail(int code,String msg,Object data)
    {
        AjaxResult result = new AjaxResult();
        result.setData(data);
        result.setMsg(msg);
        result.setCode(code);
        return result;
    }

}
