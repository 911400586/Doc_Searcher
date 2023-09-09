package com.example.doc_searcher.search;

import lombok.SneakyThrows;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;

//解析资源 负责读取 html 文档, 制作并生成索引数据(输出到文件中)
@Component
public class Parse {
    //未加工数据存放位置
    @Value("${html.source.root}")
    private String rawSource;
    //官网url(部分)
    @Value("${html.source.path}")
    private String root;
    //加工数据存放位置
    @Value("${html.source.save}")
    public String sava;
    @Resource
    private Index index;
    //用于上锁
    Object object = new Object();
//    public void run()
//    {
//        long beg =System.currentTimeMillis();
//        //1.获取raw路径下所有的*.html的路径
//        ArrayList<File> FileList = new ArrayList<>();
//        EnumFile(rawSource,FileList);
//        //2.提取出每一个文件的title,url,content.封装成DocInfo
//        for(File file:FileList)
//        {
//            ParseHtml(file);
//        }
//        long end = System.currentTimeMillis();
//        System.out.printf("索引构建完毕!耗时：%d",end-beg);
//        //3.存储索引
//        index.Save();
//    }
    //多线程建立索引
    @SneakyThrows
    public void run()
    {
        long beg = System.currentTimeMillis();
        //1.获取raw路径下所有的*.html的路径
        ArrayList<File> FileList = new ArrayList<>();
        EnumFile(rawSource,FileList);
        //2.提取出每一个文件的title,url,content.封装成DocInfo
        //使用线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(18);
        CountDownLatch latch = new CountDownLatch(FileList.size());
        for(File file:FileList)
        {
            fixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    ParseHtml(file);
                    latch.countDown();
                }
            });
        }
        //阻塞等待所有任务完成
        latch.await();
        //关闭线程池
        fixedThreadPool.shutdown();
        long end = System.currentTimeMillis();
        System.out.printf("索引构建完毕!耗时：%d\n",end-beg);
        //3.存储索引
        index.Save();
    }
    public DocInfo getDocById(int id)
    {
        return index.docInfoArrayList.get(id);
    }
    public ArrayList<Weight> getInvertedByWord(String word)
    {
        return index.InvertMap.get(word);
    }
    public void ParseHtml(File file)
    {
        DocInfo docInfo = new DocInfo();
        //1.获取title(文件名)
        String title = ParseTile(file);
        //2.获取url 需要映射到官网的url
        String url = ParseUrl(file);
        //3.去除文章中的html关键字
        String content = ParseContent(file);
        //4.构建索引
        //临界区需要上锁
        synchronized(object)
        {
            index.AddDoc(title,url,content);
        }
    }
    @SneakyThrows
    public String ParseContent(File file)
    {
        //1.将html中的关键字去除
        try(BufferedReader reader = new BufferedReader(new FileReader(file),1024*1024))
        {
            //开关
            boolean flag = true;
            StringBuffer buffer = new StringBuffer();
            while(true)
            {
                int ret = reader.read();
                //读到文件末尾
                if(ret==-1)
                {
                    break;
                }
                char c = (char)ret;
                if(flag==true)
                {
                    if(c=='<')
                    {
                        flag = false;
                        continue;
                    }
                    if(c=='\n' ||c=='\r')
                    {
                        //将换行替换成空格
                        c = ' ';
                    }
                    buffer.append(c);
                }
                else{
                    if(c=='>')
                    {
                        flag = true;
                    }
                }
            }
            return buffer.toString();
        }
    }
    @SneakyThrows
    public String ParseUrl(File file)
    {
        String part_path = file.getCanonicalPath().substring(rawSource.length());
        String url = root+part_path;
        url =  url.replaceAll("\\\\","/");
        return url;
    }
    public String ParseTile(File file)
    {
        return file.getName();
    }
    @SneakyThrows
    public void EnumFile(String path, ArrayList<File> list)
    {
        File rootPath = new File(path);
        //获取rootPath下所有的文件/目录
        File[] files = rootPath.listFiles();
        for(File file:files)
        {
            if(file.isFile() && file.getName().endsWith(".html"))
            {
                list.add(file);
            }
            else if(file.isDirectory())
            {
                EnumFile(file.getCanonicalPath(),list);
            }
            else{
                // nothing
            }
        }
    }
}
