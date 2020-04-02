package org.suai.handler;

import org.suai.Example;
import org.suai.parser.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class GetCodeHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        String funcName = parameterMap.get("func")[0];
        System.out.println("Request: " + funcName);
        ArrayList<Example> examples = new ArrayList<>();
        ArrayList<Parser> parsers = initParsers();
        try {
            for (int i = 0; i < parsers.size(); i++) {
                examples.add(parsers.get(i).findExample(funcName));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PrintWriter out = resp.getWriter();
        out.print("Example of usage " + funcName + "\n");
        for (int i = 0; i < examples.size(); i++) {
            out.print(examples.get(i));
            out.print("\n// -----------------------------------------------------------------\n");
        }
    }

    private ArrayList<Parser> initParsers() {
        ArrayList<Parser> parsers = new ArrayList<>();
        parsers.add(new ParserCppreference());
        parsers.add(new ParserCplusplus());
//        parsers.add(new ParserGithub());
        parsers.add(new ParserSearchcode());

        return parsers;
    }
}
