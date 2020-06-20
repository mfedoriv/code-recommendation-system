package org.suai.parser;

import org.suai.Example;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserCppreference implements Parser {

    private String getFuncURL(String funcName) throws ParseException {
//        https://en.cppreference.com/w/Special:Search/strcmp
        //"https://en.cppreference.com/w/Special:Search/" + funcName
        ArrayList<String> response = getResponse("https://en.cppreference.com/w/Special:Search/" + funcName, false);
        String line = null;
        String startPattern = "<li><div class='mw-search-result-heading'><a href=\"/w/c/(.*)";

        Pattern p_start = Pattern.compile(startPattern, Pattern.CASE_INSENSITIVE);
        Matcher m;
        boolean isFind = false;
        for(int i = 0; i < response.size(); i++) {
            line = response.get(i);
            m = p_start.matcher(line);
            if (m.find()) {
                isFind = true;
                line = line.replaceAll("<li>(.*)href=\"", "").replaceAll("\" title=(.*)</div>", ""); //delete all except URI
//                    System.out.println(line);
                break;
            }
        }
        if (!isFind) {
            throw new ParseException("cppreference.com");
        }

        return "https://en.cppreference.com" + line;
    }

    @Override
    public ArrayList<Example> findExample(String funcName) throws ParseException {
//        System.out.println("Parser Cppreference");

        String funcURL = getFuncURL(funcName);
        ArrayList<String> response = getResponse(funcURL, false);

        String line;
        String startPattern = "<div dir=\"ltr\" (.*)c source-c\"><pre class=\"de1\"><span class=\"co2\">(.*)";
        String endPattern = "(.*)</span></pre></div></div>";
        StringBuilder out = new StringBuilder();
        ArrayList<Example> examples = new ArrayList<>();

        Pattern p_start = Pattern.compile(startPattern, Pattern.CASE_INSENSITIVE);
        Pattern p_end = Pattern.compile(endPattern, Pattern.CASE_INSENSITIVE);
        Matcher m;

        boolean findFlag = false;
        for(int i = 0; i < response.size() && !findFlag; i++) {
            line = response.get(i);
            m = p_start.matcher(line);
            while (m.find()) {
                m = p_end.matcher(line);
                while(!m.find()){
                    i++;
                    String originalLine = response.get(i);
                    line = originalLine.replaceAll("\\<[^>]*>",""); // exclude html-tags
                    // replace html symbols
                    line = line.replaceAll("&lt;", "<").replaceAll("&gt;", ">"); // for libraries
                    line = line.replaceAll("&#160;", "").replaceAll("&#40;", "(").replaceAll("&#41;", ")");
                    line = line.replaceAll("&#123;", "{").replaceAll("&#125;", "}").replaceAll("&quot;", "\"");
                    line = line.replaceAll("&#91;", "[").replaceAll("&#93;", "]");
                    out.append(line);
                    out.append("\n");
                    m = p_end.matcher(originalLine);
                }
                findFlag = true;
            }
        }
        if (!findFlag) {
            throw new ParseException("cppreference.com");
        }
        examples.add(new Example(funcURL, out.toString()));
        return examples;
    }
}
