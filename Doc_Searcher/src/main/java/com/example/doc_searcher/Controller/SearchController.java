package com.example.doc_searcher.Controller;

import com.example.doc_searcher.search.Parse;
import com.example.doc_searcher.search.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SearchController {
    @Resource
    private Searcher searcher;
    @ResponseBody
    @RequestMapping("/searcher")
    public Object Search(String query)
    {
        if (!searcher.isRun)
        {
            synchronized (searcher)
            {
                if(!searcher.isRun)
                {
                    searcher.run();
                    searcher.isRun = true;
                }
            }
        }
        return searcher.getList(query);
    }
    @RequestMapping("/index")
    public Object Index()
    {
        return "/index.html";
    }
}
