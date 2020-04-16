package org.suai.parser;

import org.suai.Example;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserCppreference implements Parser {

    private String getFuncURL(String funcName) throws ParseException {
//        https://en.cppreference.com/w/Special:Search/strcmp
        URL url = null;
        try {
            url = new URL("https://en.cppreference.com/w/Special:Search/" + funcName);
//            System.out.println(url.toString());
        } catch (MalformedURLException e) {
            throw new ParseException(e.getMessage());
        }
//        <li><div class='mw-search-result-heading'><a href="/w/c/string/byte/strcmp" title="c/string/byte/strcmp">strcmp</a></div>
        InputStream is = null;
        FileInputStream fis = null;
        BufferedReader br;
        String line;
        String startPattern = "<li><div class='mw-search-result-heading'><a href=\"/w/c/(.*)";
        StringBuilder out = new StringBuilder();
        try {
            is = url.openStream();  // throws an IOException
//            fis = new FileInputStream("cppreference_search_strcmp.html");
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//            br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));

            Pattern p_start = Pattern.compile(startPattern, Pattern.CASE_INSENSITIVE);
            Matcher m;

            while ((line = br.readLine()) != null) {
                m = p_start.matcher(line);
                if (m.find()) {
                    line = line.replaceAll("<li>(.*)href=\"", "").replaceAll("\" title=(.*)</div>", ""); //delete all except URI
//                    System.out.println(line);
                    break;
                }
            }
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return "https://en.cppreference.com" + line;
    }

    @Override
    public Example findExample(String funcName) throws ParseException {
       String funcURL = getFuncURL(funcName);
        URL url = null;
        try {
            url = new URL(funcURL);
//            System.out.println(url.toString());
        } catch (MalformedURLException e) {
            throw new ParseException(e.getMessage());
        }
//<div class="gs-bidi-start-align gs-visibleUrl gs-visibleUrl-long" dir="ltr" style="word-break:break-all;">www.cplusplus.com/reference/cstring/<b>strcmp</b>/</div>
        InputStream is = null;
        FileInputStream fis = null;
        BufferedReader br;
        String line;
        String startPattern = "<div dir=\"ltr\" (.*)c source-c\"><pre class=\"de1\"><span class=\"co2\">(.*)";
        String endPattern = "(.*)</span></pre></div></div>";
        StringBuilder out = new StringBuilder();
        int stringCounter = 0;
        try {
            is = url.openStream();  // throws an IOException
//            fis = new FileInputStream("cppreference_strcmp.html");
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//            br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));

            Pattern p_start = Pattern.compile(startPattern, Pattern.CASE_INSENSITIVE);
            Pattern p_end = Pattern.compile(endPattern, Pattern.CASE_INSENSITIVE);
            Matcher m;

            boolean findFlag = false;
            while ((line = br.readLine()) != null && !findFlag) {
                m = p_start.matcher(line);
                stringCounter++;
                while (m.find()) {
//                    String value = m.group(1);
//                    out.append(line);
                    m = p_end.matcher(line);
                    while(!m.find()){
                        String originalLine = br.readLine();
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
                throw new ParseException("Example is not found!");
            }
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
//        System.out.println("Done!");
        return new Example(out.toString(), "cppreference.com");
    }
}
