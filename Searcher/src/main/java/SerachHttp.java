import com.fasterxml.jackson.databind.ObjectMapper;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/search")
public class SerachHttp extends HttpServlet {
    ObjectMapper mapper = new ObjectMapper();
    DocSearcher docSearcher = DocSearcher.getDocSearcher();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.监测query是否合法

       String query = req.getParameter("query");
       if(query.equals(""))
     {
         resp.setContentType("text/html");
         resp.getWriter().write("<h>该请求非法!,请重新输入</h>");
         return;
      }
      List<Result> list = docSearcher.searcher(query);
       resp.setContentType("application/json");
       mapper.writeValue(resp.getWriter(),list);
    }
}
