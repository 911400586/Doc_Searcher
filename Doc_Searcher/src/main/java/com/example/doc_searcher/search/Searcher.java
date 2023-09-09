package com.example.doc_searcher.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.prism.shader.Solid_TextureRGB_AlphaTest_Loader;
import lombok.SneakyThrows;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@Component
public class Searcher {
    @Resource
    Parse parse;
    @Value("${stopPath}")
    String stopPath;
    public static boolean isRun = false;
    private HashSet<String> stopHash = new HashSet<>();
    //启动
    public void run()
    {
        parse.run();
        //加载暂停次表
        loadStopHash();
    }
    @SneakyThrows
    public void loadStopHash()
    {
        File file1 = new File(stopPath);
        if(!file1.exists())
        {
            return;
        }
        try(InputStream inputStream = new FileInputStream(file1))
        {
            Scanner scanner = new Scanner(inputStream);
            //去掉其中的\n
            while (scanner.hasNext())
            {
                String word = scanner.nextLine();
                stopHash.add(word);
            }
        }
        System.out.println("暂停次加载成功!");
    }
    public List<Result> getList(String word)
    {
        //1.对前端传来的word进行分词,获取倒排拉链
        ArrayList<ArrayList<Weight>> weightArrayList = new ArrayList<>();
        List<Term> terms = ToAnalysis.parse(word).getTerms();
        for(Term term:terms)
        {
            //判断是否是暂停词
            if(stopHash.contains(term.getName()))
            {
                continue;
            }
            ArrayList<Weight> list = parse.getInvertedByWord(term.getName());
            if(list==null)
            {
                continue;
            }
            weightArrayList.add(parse.getInvertedByWord(term.getName()));
        }
        //2.将获取的拉链进行排序
            for(ArrayList<Weight> list:weightArrayList)
            {
                //按照id进行排序
                list.sort(new Comparator<Weight>() {
                    @Override
                    public int compare(Weight o1, Weight o2) {
                        return o1.getDocId()-o2.getDocId();
                    }
                });
            }
            //3.权值合并
            ArrayList<Weight> list = Merge(weightArrayList);
//        weightArrayList.sort((Weight o1, Weight o2)->{
//            return o1.getValue()-o2.getValue();
//        });
        //4.排序 按照权重
        list.sort(new Comparator<Weight>() {
            @Override
            public int compare(Weight o1, Weight o2) {
                return o2.getValue()-o1.getValue();
            }
        });
        //5.构建返回值
        ArrayList<Result> resultArrayList = new ArrayList<>();
        for(Weight weight:list)
        {
            //获取doc
            DocInfo docInfo = parse.getDocById(weight.getDocId());
            String title = docInfo.getTitle();
            String url = docInfo.getUrl();
            String content = docInfo.getContent();
            //获取摘要
            String desc = Desc(terms,content);
            Result result = new Result();
            result.setTitle(title);
            result.setUrl(url);
            result.setDesc(desc);
            resultArrayList.add(result);
        }
        return resultArrayList;
    }
    public ArrayList<Weight> Merge(ArrayList<ArrayList<Weight>> source)
    {
        class Pos{
            int row;
            int col;
            public Pos(int row,int col)
            {
                this.row =row;
                this.col =col;
            }
        }
        //队列
        Queue<Pos> queue = new LinkedList<>();
        ArrayList<Weight> target = new ArrayList<>();
        //将source每一行的第一列坐标添加到queue中
        for(int i = 0;i<source.size();i++)
        {
            Pos pos = new Pos(i,0);
            queue.offer(pos);
        }
        //进行合并
        while(!queue.isEmpty())
        {
            Pos pos = queue.poll();
            Weight curWeight = source.get(pos.row).get(pos.col);
            if(target.size()!=0)
            {
                //判断DocId是否相同
                //获取target刚加入进去的元素
                Weight oldWeight = target.get(target.size()-1);
                if(oldWeight.getDocId()==curWeight.getDocId())
                {
                    //进行合并
                    oldWeight.setValue(oldWeight.getValue()+curWeight.getValue());
                }
                else{
                    //直接加入
                    target.add(curWeight);
                }
            }
            else{
                //直接加入
                target.add(curWeight);
            }
            //判断是否走到当前行的末尾
            if(pos.col+1==source.get(pos.row).size())
            {
                continue;
            }
            queue.offer(new Pos(pos.row, pos.col+1));
        }
        return target;
    }
    public String Desc(List<Term> terms,String content)
    {
        int firstPos = -1;
        for(Term term:terms)
        {
            String word = term.getName();
            firstPos = content.toLowerCase().indexOf(word);
            if(firstPos!=-1)
            {
                break;
            }
        }
        if(firstPos==-1)
        {
            return "****";
        }
        //取关键词的前50个和后100个
        int start = firstPos>50?firstPos-50:0;
        String desc = "";
        if(firstPos+100>content.length())
        {
            desc = content.substring(start);
        }
        else{
            desc = content.substring(start,firstPos+100)+"......";
        }
        //将关键词标红
        for(Term term:terms)
        {
           String word = term.getName();
           //(?!)表示忽略大小写
          desc =  desc.replaceAll("(?i)"+word+" ","<i>"+word+" </i>");
        }
        return desc;
    }
}
