package org.suai.handler;

import org.suai.Example;
import org.suai.parser.Parser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class GetCodeHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        String funcName = parameterMap.get("func")[0];
        System.out.println("Request: " + funcName);

//        String filename = "cplusplus_"+ funcName +".html";
        Example example = Parser.parserCplusplus(funcName);
        PrintWriter out = resp.getWriter();
        out.print("Example of usage " + funcName + "\n" + example.getList().get(0).toString());
    }
}
