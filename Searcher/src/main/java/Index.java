import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import javax.print.Doc;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class DocInfo{
    private String title;
    private String url;
    private String content;
    private  int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
class Weight {

    //通过id可以获取文档相关信息
    private int id;
    //权重
    private int weight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
public class Index {

    //private static String path = "D:/resource/index";
    private static String path = "/root/resource/index";
    //public static FileWriter fw;
    public Index(){

    }

    //正排索引
    private ArrayList<DocInfo> fowardIndex = new ArrayList<>();
    //倒排索引
    private HashMap<String,ArrayList<Weight>> inveredIndex = new HashMap<>();
   //通过正排id获取
    public DocInfo GetDocInfoById(int id)
    {
        return fowardIndex.get(id);
    }
    //通过分词获取倒排拉链
    public ArrayList<Weight> GetListByString(String query)
    {
        return inveredIndex.get(query);
    }
    //建立正排
    public void show()
    {
        for(DocInfo doc : fowardIndex)
        {
            System.out.println(doc.getUrl());
        }
    }
    private synchronized DocInfo SetForward(String title,String url,String content)
    {
      DocInfo info = new DocInfo();
      info.setTitle(title);
      info.setContent(content);
      info.setUrl(url);
      info.setId(fowardIndex.size());
      fowardIndex.add(info);
        return fowardIndex.get(fowardIndex.size()-1);
    }
    //建立倒排
    private void SetInvered(DocInfo doc)
    {
        // 构造 Weight 对象, 并更新倒排索引.
        // 此处 "权重" 简单粗暴的认为词出现的次数.
        // weight = 10 * 这个词标题中出现的次数 + 1 * 这个词正文中出现的次数
        // 核心流程:
        // 1. 对标题进行分词
        // 2. 遍历分词结果, 统计标题中每个词出现的次数
        // 3. 对正文进行分词
        // 4. 遍历分词结果, 统计正文中每个词出现的次数
        // 5. 把以上内容都整理到一个 HashMap 中
        // 6. 遍历 HashMap, 可以得到 词 -> 权重 这样的映射关系, 更新到倒排索引中
        // 这个类用于辅助统计词出现的次数
        class cnt{
            public int titlecnt;
            public int contentcnt;
            public cnt(int titlecnt,int contentcnt)
            {
                this.titlecnt = titlecnt;
                this.contentcnt = contentcnt;
            }
        }
        HashMap<String,cnt> Cnt = new HashMap<>();
       //1.对标题进行分词 统计词频
        List<Term> terms = ToAnalysis.parse(doc.getTitle()).getTerms();
        for(Term term:terms)
        {
            if(!Cnt.containsKey(term.getName()))
            {
               Cnt.put(term.getName(),new cnt(1,0));
            }
            else
            {
                Cnt.get(term.getName()).titlecnt++;
            }
        }
        //2.对内容进行分词,统计词频
        terms = ToAnalysis.parse(doc.getContent()).getTerms();
        for(Term term:terms)
        {
            if(!Cnt.containsKey(term.getName()))
            {
                Cnt.put(term.getName(),new cnt(0,1));
            }
            else{
                Cnt.get(term.getName()).contentcnt++;
            }
        }
        //建倒排拉链
        for (HashMap.Entry<String, cnt> entry : Cnt.entrySet()) {
            // 6. 遍历 HashMap, 可以得到 词 -> 权重 这样的映射关系, 更新到倒排索引中
            Weight weight = new Weight();
            weight.setId(doc.getId());
            weight.setWeight(entry.getValue().titlecnt * 10
                    + entry.getValue().contentcnt);
            // weight.setWord(entry.getKey());
            // 这个逻辑也可以使用 Map.putIfAbsent. 此处为了直观, 还是直接用 get 吧
            synchronized (this) {
                ArrayList<Weight> invertedList = inveredIndex.get(entry.getKey());
                if (invertedList == null) {
                    invertedList = new ArrayList<>();
                    inveredIndex.put(entry.getKey(), invertedList);
                }
                invertedList.add(weight);
            }
        }

    }
    public DocInfo getDocInfo(int docId)
    {
        return fowardIndex.get(docId);
    }
    public ArrayList<Weight> getInvered(String term)
    {
        return inveredIndex.get(term);
    }
    //建立索引
    public void AddDocIndex(String title,String url,String content)
    {


        //1.先建立正排
       DocInfo doc = SetForward(title,url,content);
        //2.后建立倒排
        SetInvered(doc);
        //System.out.println("title:"+doc.getTitle()+" url:"+doc.getUrl());
    }
    //将正排索引,倒排索引保存到磁盘中 格式为json
    public void save()
    {
        ObjectMapper mapper = new ObjectMapper();
        //1.正排保存在root/forward.dat,倒排保存在root/invered.dat
        File path1 = new File(path+"/forward.dat");
        File path2 = new File(path+"/invered.dat");
        if(!path1.exists())
        {
            try {
                path1.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            //存在 删除在创建
            path1.delete();
            try {
                path1.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(!path2.exists())
        {
            try {
                path2.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            path2.delete();
            try {
                path2.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            mapper.writeValue(path1,fowardIndex);
            mapper.writeValue(path2,inveredIndex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //将正排索引,倒排索引加载到内存中
    public void load()
    {
        long beg;
//        try {
//             beg = System.currentTimeMillis();
//            fw.write("索引正在加载\n");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        ObjectMapper mapper = new ObjectMapper();
        File path1 = new File(path+"/forward.dat");
        File path2 = new File(path+"/invered.dat");
        try {
            fowardIndex = mapper.readValue(path1, new TypeReference<ArrayList<DocInfo>>() {});
            inveredIndex = mapper.readValue(path2, new TypeReference<HashMap<String, ArrayList<Weight>>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        try {
//            fw.write("索引加载完成!");
//            long end = System.currentTimeMillis();
//            fw.write("时间: " + (end - beg)+'\n');
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}
