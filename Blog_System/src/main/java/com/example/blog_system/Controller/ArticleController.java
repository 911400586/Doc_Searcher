package com.example.blog_system.Controller;

import com.example.blog_system.Common.AjaxResult;
import com.example.blog_system.Common.ApplicationVariable;
import com.example.blog_system.Common.StringTools;
import com.example.blog_system.Common.UserSessionTools;
import com.example.blog_system.Model.Article;
import com.example.blog_system.Model.User;
import com.example.blog_system.Model.Vo.ArticleVo;
import com.example.blog_system.Service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    ArticleService articleService;
    @RequestMapping("/add")
    public AjaxResult add(Article article, HttpServletRequest request)
    {
        //1.判空
        if(article==null || !StringUtils.hasLength(article.getContent()) || !StringUtils.hasLength(article.getTitle()))
        {
            return AjaxResult.fail(-1,"文章内容或者标题为空");
        }
        //2.添加
        HttpSession session = request.getSession(false);
        User user = (User)session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        int uid = user.getId();
        article.setUid(uid);
        int code = articleService.add(article);
        if(code==1)
        {
            //添加成功
            return AjaxResult.success(1);
        }
        //失败
        return AjaxResult.fail(-2,"文章添加失败!");
    }
    @RequestMapping("/getArticleById")
    public AjaxResult get(Integer id,HttpServletRequest request)
    {

        if(id==null || id<0)
        {
            return AjaxResult.fail(-2,"参数异常!");
        }
        User user = UserSessionTools.getLoginUser(request);
        //防止其它用户拿到他人文章
         Article article =  articleService.getArticleById(id,user.getId());
         if(article==null)
         {
             return AjaxResult.fail(-1,"非法请求");
         }
         return AjaxResult.success(article);
    }
    @RequestMapping("/update")
    public AjaxResult update(Article article,HttpServletRequest request)
    {
        //1.判空
        if(article==null || article.getId()<=0 || !StringUtils.hasLength(article.getTitle()) || !StringUtils.hasLength(article.getContent()))
        {
            return AjaxResult.fail(-1,"内容或标题不能为空");
        }
        //2.获取登录用户的id，填充到 articleinfo 对象中（修改是要验证权限）
        User user = UserSessionTools.getLoginUser(request);
        article.setUid(user.getId());
        article.setUpdatetime(LocalDateTime.now());
        //3.进行更新和校验
        int num = articleService.updateById(article);
        if(num!=1)
        {
            return AjaxResult.fail(-1,"非法请求");
        }
        return AjaxResult.success(1);
    }
    @RequestMapping("/detail")
    public AjaxResult detail(Integer id)
    {
        if(id==null || id<=0)
        {
            return AjaxResult.fail(-1,"非法请求");
        }
        ArticleVo vo = articleService.getArticle(id);
        if(vo==null)
        {
            return AjaxResult.fail(-1,"无该文章");
        }
        return AjaxResult.success(vo);
    }
    @RequestMapping("/count")
   public AjaxResult count(Integer id)
    {
        if(id==null || id<=0)
        {
            return AjaxResult.fail(-1,"非法请求");
        }
        int num =  articleService.count(id);
        if(num==1)
        {
            return AjaxResult.success(1);
        }
        return AjaxResult.fail(-1,"非法请求");
    }
    @RequestMapping("/mylist")
    public AjaxResult myList(HttpServletRequest request)
    {
        User user = UserSessionTools.getLoginUser(request);
        List<Article> list = articleService.getArticleByUid(user.getId());
        //生成摘要
        for(Article item:list)
        {
            //去除markdown关键字
            String content =  StringTools.removeMarkdownKeywords(item.getContent());
            //截取150字
            content = content.substring(0,content.length()>=150?150:content.length());
            item.setContent(content);
        }
        return AjaxResult.success(list);
    }
    @RequestMapping("/mainlist")
    public AjaxResult mainList(Integer pageSize,Integer index)
    {
        if(pageSize==null || pageSize<=0 || index==null || index<=0)
        {
            return AjaxResult.fail(-1,"参数错误");
        }
        int offset = (index-1)*pageSize;
        List<Article> list = articleService.getListByPage(pageSize,offset);
        //生成摘要
        for(Article item:list)
        {
            //去除markdown关键字
            String content =  StringTools.removeMarkdownKeywords(item.getContent());
            //截取50字
            content = content.substring(0,content.length()>=50?50:content.length());
            item.setContent(content);
        }
        return AjaxResult.success(list);
    }
    @RequestMapping("/countnum")
    public AjaxResult articleCount()
    {

        return AjaxResult.success(articleService.getCount());
    }
    @RequestMapping("/del")
    public AjaxResult del(Integer id,HttpServletRequest request)
    {
        if(id==null && id<=0)
        {
            return AjaxResult.fail(-1,"参数异常");
        }
        User user = UserSessionTools.getLoginUser(request);
        int num = articleService.del(id,user.getId());
        if(num!=1)
        {
            return AjaxResult.fail(-1,"参数异常");
        }
        return AjaxResult.success(1);

    }
}
