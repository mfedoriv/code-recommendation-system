package com.company.handler;

import com.company.parser.Parser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class GetCodeHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        String funcName = parameterMap.get("func")[0];
        System.out.println("Request: " + funcName);

        URL url = null;
        try {
            url = new URL("http://www.cplusplus.com/" + funcName);
            System.out.println(url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String filename = "cplusplus_"+ funcName +".html";
        String example = "Not found!";
        if (url != null) {
            example = Parser.parseData(url);
        }
        PrintWriter out = resp.getWriter();
        out.print("Example of usage " + funcName + "\n" + example);
    }
}
