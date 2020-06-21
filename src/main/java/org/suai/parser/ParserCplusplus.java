package org.suai.parser;

import org.suai.Example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserCplusplus implements Parser {

    @Override
    public ArrayList<Example> findExample(String funcName) throws ParseException {
//        System.out.println("Parser Cplusplus");

        HttpURLConnection connection = null;
        BufferedReader reader;
        String line;
        String startPattern = "<td class=\"source\"><pre><code>(.*)";
        String endPattern = "(.*)</code></pre></td>(.*)";
        StringBuilder out = new StringBuilder();
        ArrayList<Example> examples = new ArrayList<>();
        String urlString = "https://www.cplusplus.com/" + funcName;
        int stringCounter = 0;

        ArrayList<String> response = null;
        try {
            response = getResponse(urlString, false);
        } catch (ParseException e) {
            throw new ParseException("cplusplus.com");
        }

        Pattern p_start = Pattern.compile(startPattern, Pattern.CASE_INSENSITIVE);
        Pattern p_end = Pattern.compile(endPattern, Pattern.CASE_INSENSITIVE);
        Matcher m;

        boolean findFlag = false;
        for(int i = 0; i < response.size() && !findFlag; i++) {
            line = response.get(i);
            m = p_start.matcher(line);
            stringCounter++;
            while (m.find()) {
                m = p_end.matcher(line);
                while(!m.find()){
                    i++;
                    String originalLine = response.get(i);
                    line = originalLine.replaceAll("\\<[^>]*>",""); // exclude html-tags
                    line = line.replaceAll("&lt;", "<").replaceAll("&gt;", ">"); // for #include
                    out.append(line);
                    out.append("\n");
                    m = p_end.matcher(originalLine);
                }
                findFlag = true;
                examples.add(new Example(urlString, out.toString()));
            }
        }
        if (!findFlag) {
            throw new ParseException("cplusplus.com");
        }

        return examples;
    }
}
