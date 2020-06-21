package org.suai.handler;

import org.json.JSONArray;
import org.suai.Utils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class SearchHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        String query = req.getParameter("q");
        String url = "localhost:8080/getcode?func=" + query;
        String responseEx;
        JSONArray examples;
        try (PrintWriter out = resp.getWriter()) {
            resp.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            resp.setContentType("charset=UTF-8");
            BufferedReader br  = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Search.html")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("STYLE")) {
                    out.print(Utils.getDataFromFile("/style.css"));
                }
                if (line.contains("RESULT_OF_SEARCH")){
                    if (query != null) {
                        req.setAttribute("func", query);
                        req.setAttribute("printType", "text");
                        RequestDispatcher rd = req.getRequestDispatcher("getcode");
                        rd.include(req, resp);
                    }
                    line = " ";
                }
                out.println(line);
            }
            br.close();
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }
}
