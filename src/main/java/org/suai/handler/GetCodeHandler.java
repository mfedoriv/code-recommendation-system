package org.suai.handler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.suai.Example;
import org.suai.Utils;
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
        String funcName;
        try (PrintWriter out = resp.getWriter()) {
            if (req.getAttribute("func") != null) {
                funcName = (String)req.getAttribute("func");
            }
            else {
                Map<String, String[]> parameterMap = req.getParameterMap();

                if (parameterMap.isEmpty()){
                    resp.getWriter().print("Please send the name of the C function in the func parameter. " +
                            "For example http://localhost:8080/getcode?func=fopen");
                    throw new ParseException("Empty parameters");
                } else {
                    funcName = parameterMap.get("func")[0];
                }
            }

            System.out.println("Request: " + funcName);
            ArrayList<Example> examples = new ArrayList<>();
            ArrayList<Parser> parsers = initParsers();
            for (Parser parser : parsers) {
                try {
                    examples.addAll(parser.findExample(funcName.toLowerCase()));
                } catch (ParseException e) {
                    System.out.println("Example of usage "+ funcName.toUpperCase() + " not found on the site: " + e.getMessage());
                }
            }


            if (req.getAttribute("printType") == "text") {
                if (examples.isEmpty()) {
                    out.print("Examples of usage <b>" + funcName.toUpperCase() + "</b> not found!<br>" +
                            "Try to change your request.");
                } else {
                    // Print as text
                    out.print("// Examples of usage <b>" + funcName + "</b><br><br>");
                    for (Example example : examples) {
                        out.print("Source: <a href=\"" + example.getSource() + "\" target=\"_blank\">" + example.getSource() + "</a><br>");
                        out.print("Rating: " + example.getRating() + "<br><br>");
                        String escaped = Utils.escapeHTML(example.getCode());
                        out.print(escaped);
                        out.print("<br><br>// #####################################################################################<br><br>");
                    }
                }
            } else {
                // Print as JSON format
                JSONArray results = new JSONArray();
                for (int i = 0; i < examples.size(); i++) {
                    results.put(examples.get(i).toJSONObject());
                }
                out.print(results); // JSONArray
            }

        } catch (IOException | ParseException e) {
            System.out.println("Error! " + e.getMessage());
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
