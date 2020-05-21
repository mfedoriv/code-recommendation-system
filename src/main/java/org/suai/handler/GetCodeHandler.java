package org.suai.handler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.suai.Example;
import org.suai.parser.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class GetCodeHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        Map<String, String[]> parameterMap = req.getParameterMap();
        String funcName = parameterMap.get("func")[0];
        System.out.println("Request: " + funcName);
        ArrayList<Example> examples = new ArrayList<>();
        ArrayList<Parser> parsers = initParsers();
        try {
            for (Parser parser : parsers) {
                examples.add(parser.findExample(funcName));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try (PrintWriter out = resp.getWriter()) {
            // Print as text
            /*out.print("// Example of usage " + funcName + "\n");
            for (Example example : examples) {
                out.print(example);
                out.print("\n// ##############################################################\n");
            }*/
            // Print as JSON format
            JSONArray results = new JSONArray();
            for (int i = 0; i < examples.size(); i++) {
                results.put(examples.get(i).toJSONObject());
            }
            out.print(results); // JSONArray
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<Parser> initParsers() {
        ArrayList<Parser> parsers = new ArrayList<>();
        parsers.add(new ParserCppreference());
        parsers.add(new ParserCplusplus());
//        parsers.add(new ParserGithub());
        parsers.add(new ParserStackoverflow());
//        parsers.add(new ParserSearchcode());


        return parsers;
    }
}
