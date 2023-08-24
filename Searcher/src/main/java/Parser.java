import javax.xml.crypto.Data;
import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Parser {
    //private static String path = "D:/resource/docs/api";
    private static String path = "/root/resource/docs/api";
    private static Index index = new Index();
    //索引实例
    public static void main(String[] args) throws IOException {
       Parser parser = new Parser();
        //parser.show();
        try {
            parser.runByThreadPool();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void runByThreadPool() throws InterruptedException {
        System.out.println("开始解析!");
        long beg = System.currentTimeMillis();
        // 1. 枚举出这个目录下的所有文件
        ArrayList<File> fileList = new ArrayList<>();
        EnumFile(path, fileList);
        System.out.println("共需要处理 " + fileList.size() + " 个文档!");
        //4个线程同时完成后,才算完成
        CountDownLatch latch = new CountDownLatch(fileList.size());
        //线程池
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (File f : fileList) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("解析 " + f.getAbsolutePath());
                    ParseHtml(f);
                    latch.countDown();
                }
            });
        }
        latch.await();
        // 所有任务都结束之后, 就可以销毁线程池了. 否则进程无法正常退出!
        executorService.shutdown();
        System.out.println("解析完成! 开始保存索引!");
        index.save();
        long end = System.currentTimeMillis();
        System.out.println("保存索引完成! 时间: " + (end - beg));
    }
//    public void run()
//    {
//        System.out.println("开始解析");
//        long start = System.currentTimeMillis();
//        //1.获取文件集合
//        List<File> fileList = new ArrayList<>();
//        EnumFile(path,fileList);
//        System.out.println("EnumFile 完成!");
//        //2.解析每一个文件
//        for(File file:fileList)
//        {
//            ParseHtml(file);
//        }
//        long end = System.currentTimeMillis();
//        System.out.println("解析完成!总花费时间: " + (end-start));
//        index.save();
//    }
    public void EnumFile(String RootPath,List<File> fileList)
    {
        File root = new File(RootPath);
        File[] list = root.listFiles();
        for(File file:list)
        {
            if(file.isDirectory())
            {
                EnumFile(file.getAbsolutePath(),fileList);
            }
            else{
                if(file.getAbsolutePath().endsWith(".html"))
                {
                    fileList.add(file);
                }
            }
        }
    }
    public void show()
    {
        System.out.println("加载中");
        index.load();
        System.out.println("加载成功");
        System.out.println("打印正排索引中的url");
        index.show();
    }
    public void ParseHtml(File file)
    {
        String title = ParseTitle(file);
        String content = parseContentByRe(file);
        String url = ParseUrl(file);
        System.out.println(url);
        //构建实例 存储到本地
        index.AddDocIndex(title,url,content);
    }
    public String ParseTitle(File file)
    {
        //1.将文件名当做标题
        String title = file.getName();
        return title;
    }
    private String readFile(File f) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    public String parseContentByRe(File f) {
        // 1. 先把整个文件内容都读取出来
        String content = readFile(f);
        // 2. 使用正则替换掉 <script> 标签
        content = content.replaceAll("<script.*?>(.*?)</script>", " ");
        // 3. 使用正则替换掉其他标签
        content = content.replaceAll("<.*?>", " ");
        // 4. 多个空格合并成一个
        content = content.replaceAll("\\s+", " ");
        return content;
    }

//    public String ParseContent(File file) {
//        //1.读取文件内容,去除标签和换行
//        try {
//
//            InputStream in = new FileInputStream(file);
//            BufferedReader re = new BufferedReader(new InputStreamReader(in, "ASCII"));
//            boolean flag = true;//判断是否是有效内容
//            StringBuffer buffer = new StringBuffer();
//            while (true) {
//                int ret = re.read();
//                if (ret == -1) {
//                    break;
//                }
//                char c = (char) ret;
//                if (flag) {
//                    if (c == '<') {
//                        flag = false;
//                        continue;
//                    }
//                    if (c == '\n' || c == '\r') {
//                        c = ' ';
//                    }
//                    buffer.append(c);
//                } else {
//                    if (c == '>') {
//                        flag = true;
//                    }
//                }
//            }
//            re.close();
//            return buffer.toString();
//
//        } catch (FileNotFoundException | UnsupportedEncodingException e) {
//           e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    public String ParseUrl(File file)
    {

        String part1 = "https://docs.oracle.com/javase/8/docs/api";

        String part2 = file.getAbsolutePath().substring(path.length());
        return (part1 + part2).replaceAll("\\\\","/");
    }
}
