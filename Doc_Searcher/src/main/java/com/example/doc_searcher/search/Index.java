package com.example.doc_searcher.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.dat.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Index {
    //索引存放位置
    @Value("${html.source.save}")
    public String path;
    @Resource
    ObjectMapper mapper;
    //正排索引 list的size当做doc的id
    ArrayList<DocInfo> docInfoArrayList = new ArrayList<>();
    //倒排索引
    HashMap<String,ArrayList<Weight>> InvertMap = new HashMap<>();
//    public DocInfo getDocById(int id)
//    {
//        return docInfoArrayList.get(id);
//    }
//    public ArrayList<Weight> getInvertedByWord(String word)
//    {
//        return InvertMap.get(word);
//    }
    public void AddDoc(String title,String url,String content)
    {

        //1.构建正排索引
        int DocId = SetIndex(title,url,content);
        //2.构建倒排索引
        SetInvertIndex(DocId);

    }
    //1.构建正排索引
    public int SetIndex(String title,String url,String content)
    {
        DocInfo info = new DocInfo();
        info.setTitle(title);
        info.setUrl(url);
        info.setContent(content);
        info.setId(docInfoArrayList.size());
        docInfoArrayList.add(info);
        return docInfoArrayList.size()-1;
    }
    public void SetInvertIndex(int DocId)
    {
        //获取DocInfo
        DocInfo info = docInfoArrayList.get(DocId);
        //记录title和content的出现次数
        class Count{
            int title;
            int content;
        }
        HashMap<String,Count> CountMap = new HashMap<>();
        //统计标题
        List<Term> Terms = ToAnalysis.parse(info.getTitle()).getTerms();
        for(Term term:Terms)
        {
            if(!CountMap.containsKey(term.getName()))
            {
                Count count = new Count();
                count.content = 0;
                count.title = 1;
                CountMap.put(term.getName(),count);
            }
            else{
                CountMap.get(term.getName()).title++;
            }
        }
        //统计内容
        Terms = ToAnalysis.parse(info.getContent()).getTerms();
        for(Term term:Terms)
        {
            if(!CountMap.containsKey(term.getName()))
            {
                Count count = new Count();
                count.content = 1;
                count.title = 0;
                CountMap.put(term.getName(),count);
            }
            else{
                CountMap.get(term.getName()).content++;
            }
        }

        //计算权重 构建倒排索引
        for(Map.Entry<String,Count> i:CountMap.entrySet())
        {
            String key = i.getKey();
            Count value = i.getValue();
            int weight = value.title*10+value.content*2;
            Weight weight1 = new Weight();
            weight1.setDocId(DocId);
            weight1.setValue(weight);
            if(InvertMap.containsKey(key))
            {
                InvertMap.get(key).add(weight1);
            }
            else{
                ArrayList<Weight> weightArrayList = new ArrayList<>();
                weightArrayList.add(weight1);
                InvertMap.put(key,weightArrayList);
            }
        }

    }
    @SneakyThrows
    public void Save()
    {
       String IndexPath = path+"/Index.json";
       String InvertIndex = path+"/InvertIndex.json";
       File file1 = new File(IndexPath);
       File file2 = new File(InvertIndex);
       if(!file1.exists())
       {
           file1.createNewFile();
       }
       if(!file2.exists())
       {
           file2.createNewFile();
       }
       //将数据转换成json格式
        try(FileWriter writer = new FileWriter(file1); FileWriter writer1 = new FileWriter(file2))
        {
            mapper.writeValue(writer,docInfoArrayList);
            mapper.writeValue(writer1,InvertMap);
        }

    }
    @SneakyThrows
    public void Load()
    {
        String IndexPath = path+"/Index.json";
        String InvertIndex = path+"/InvertIndex.json";
        File file1 = new File(IndexPath);
        File file2 = new File(InvertIndex);

        //将数据转换成json格式
        try(FileReader reader = new FileReader(file1); FileReader reader1= new FileReader(file2))
        {
            mapper.readValue(reader, new TypeReference<ArrayList<DocInfo>>(){});
            mapper.readValue(reader1, new TypeReference<HashMap<String,ArrayList<Weight>>>(){});
        }
    }
}
