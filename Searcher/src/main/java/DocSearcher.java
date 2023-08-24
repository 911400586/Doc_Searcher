import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class DocSearcher {
    private static Index index = new Index();
    private static DocSearcher docSearcher = new DocSearcher();

    private DocSearcher(){

    }

    public static DocSearcher getDocSearcher() {
        return docSearcher;
    }

    static
    {
        index.load();
    }
    public List<Result> searcher(String query)
    {
//        try {
//            index.fw.write("searcher:正在进行分词\n");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        //1.进行分词
          List<Term> terms = ToAnalysis.parse(query).getTerms();
        //2.通过分词拿到倒排拉链
        ArrayList<Weight> list = new ArrayList<>();
        int i =1;
        for(Term term:terms)
        {
           list.addAll(index.getInvered(term.getName()));


        }
//        try {
//            index.fw.write("searcher：拿到倒排拉链\n");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        //3.排序
        list.sort(new Comparator<Weight>() {
            @Override
            public int compare(Weight o1, Weight o2) {
                return -(o1.getWeight()-o2.getWeight());
            }
        });
        List<Result> lt = new ArrayList<>();
        //4.构建result
        for(Weight weight:list)
        {
            DocInfo info = index.getDocInfo(weight.getId());
            Result  result = new Result();
            result.setTitle(info.getTitle());
            result.setUrl(info.getUrl());
            result.setDesc(GetDesc(info.getContent(),terms));
            lt.add(result);
        }
        return lt;
    }
    private String GetDesc(String content, List<Term> terms) {
        // 先找一下 word 在 content 中第一次出现的位置
        // 由于此时的 word 已经是全小写了, 需要把原来正文也转成小写
        // 进行全字匹配. 要求待查找结果必须是个独立的单词.
        // 用分词结果中的第一个在描述能找到的词, 作为位置的中心
        int firstPos = -1;
        String firstWord = "";
        for (Term term : terms) {
             firstWord = term.getName();
            firstPos = content.toLowerCase().indexOf(" " + firstWord + " ");
            if (firstPos > 0) {
                break;
            }
        }
        // 如果所有的分词结果在正文中都不存在, 则直接返回空的描述
        if (firstPos == -1) {
            return "";
        }

        String desc = "";
        int descBeg = firstPos < 50 ? 0 : firstPos - 50;
        if (descBeg + 150 > content.length()) {
            desc = content.substring(descBeg);
        } else {
            // 正文长度充足, 在最后加上 ...
            desc = content.substring(descBeg, descBeg + 150) + "...";
        }
        //用<i></i>将分词标红 可能有多个分词
        for (Term term : terms) {
            String word = term.getName();
            desc = desc.replaceAll("(?i)" + " " + word + " ", " <i>" + word + "</i> ");
        }

        return desc;
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DocSearcher docSearcher = new DocSearcher();
        while (true) {
            System.out.print("-> ");
            String query = scanner.next();
            List<Result> results = docSearcher.searcher(query);
            for (Result result : results) {
                System.out.println("================================");
                System.out.println(result.getUrl());
            }
        }
    }

}
