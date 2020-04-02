package org.suai.parser;

import org.suai.Example;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserCplusplus implements Parser {

    @Override
    public Example findExample(String funcName) throws ParseException {
        URL url = null;
        try {
            url = new URL("http://www.cplusplus.com/" + funcName);
//            System.out.println(url.toString());
        } catch (MalformedURLException e) {
            throw new ParseException(e.getMessage());
        }
        InputStream is = null;
        FileInputStream fis = null;
        BufferedReader br;
        String line;
        String startPattern = "<td class=\"source\"><pre><code><cite>(.*)";
        String endPattern = "(.*)</code></pre></td>(.*)";
        StringBuilder out = new StringBuilder();
        int stringCounter = 0;
        try {
            is = url.openStream();  // throws an IOException
//            fis = new FileInputStream("cplusplus_fgets.html");
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
                        line = line.replaceAll("&lt;", "<").replaceAll("&gt;", ">"); // for libraries
                        out.append(line);
                        out.append("\n");
                        m = p_end.matcher(originalLine);
                    }
                    findFlag = true;
                }
            }
            if (!findFlag) {
                return new Example("Example is not found!", "cplusplus.com");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        return new Example(out.toString(), "cplusplus.com");
    }

    /*private String getFuncURL(String funcName) throws ParseException {
        URL url = null;
        try {
            url = new URL("http://cplusplus.com/search.do?q=" + funcName + "&kwr=no");
        } catch (MalformedURLException e) {
            throw new ParseException(e.getMessage());
        }
        //<div class="gs-bidi-start-align gs-visibleUrl gs-visibleUrl-long" dir="ltr" style="word-break:break-all;">www.cplusplus.com/reference/cstring/<b>strcmp</b>/</div>
        InputStream is = null;
        FileInputStream fis = null;
        BufferedReader br;
        String line;
        String startPattern = "<div class=\"gs-bidi-start-align gs-visibleUrl gs-visibleUrl-long\" dir=\"ltr\" style=\"word-break:break-all;\">(.*)";
        String endPattern = "(.*)</code></pre></td>(.*)";
        StringBuilder out = new StringBuilder();
        int stringCounter = 0;
        try {
            is = url.openStream();  // throws an IOException
//            fis = new FileInputStream("cplusplus_fgets.html");
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
                        line = line.replaceAll("&lt;", "<").replaceAll("&gt;", ">"); // for libraries
                        out.append(line);
                        out.append("\n");
                        m = p_end.matcher(originalLine);
                    }
                    findFlag = true;
                }
            }
            if (!findFlag) {
//                return new Example("Example is not found!");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
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
//        return new Example(out.toString());
        return null;
    }*/
}
