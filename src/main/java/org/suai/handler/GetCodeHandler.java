package org.suai.handler;

import org.json.JSONArray;
import org.suai.Example;
import org.suai.Utils;
import org.suai.analyser.Analyser;
import org.suai.parser.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.prefs.Preferences;

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

            HashSet<Example> examplesSet = new HashSet<>(); // to exclude duplicate elements
            Analyser analyser = new Analyser();

//            System.out.println("Analyse " + examples.size() + " examples.");
            for (int i = 0; i < examples.size(); i++) {
//                System.out.println((i+1) + ". example");
                Example exAnalysed = analyser.analyse(examples.get(i), funcName);
                if (exAnalysed.getCode() != null) {
                    examplesSet.add(exAnalysed);
                }
            }
            examples = new ArrayList<>(examplesSet);
            examples.sort(new Comparator<Example>() {
                @Override
                public int compare(Example o1, Example o2) { //descending order
                    return o2.getRating() - o1.getRating();
                }
            });

            if (req.getAttribute("printType") == "text") {
                if (examples.isEmpty()) {
                    out.print("Examples of usage <b>" + funcName.toUpperCase() + "</b> not found!<br>" +
                            "Try to change your request or add more sites to search on Settings page " +
                            "if you are looking for functions from 3rd parties libraries.");
                } else {
                    // Print as text
                    out.print("// Examples of usage <b>" + funcName + "</b><br><br>");
                    int numb = 1;
                    for (Example example : examples) {
                        out.print("Example " + numb + "/" + examples.size() + "<br>");
                        numb++;
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
        Preferences prefs = Preferences.userRoot().node("CodeRecSystem");

        if (prefs.getBoolean("ParserCplusplus_enabled", false)) {
            parsers.add(new ParserCplusplus());
        }
        if (prefs.getBoolean("ParserCppreference_enabled", false)) {
            parsers.add(new ParserCppreference());
        }
        if (prefs.getBoolean("ParserStackoverflow_enabled", false)) {
            parsers.add(new ParserStackoverflow());
        }
        if (prefs.getBoolean("ParserSearchcode_enabled", false)) {
            parsers.add(new ParserSearchcode());
        }

        return parsers;
    }
}
