package org.suai.parser;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import org.suai.Example;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parser {
    private static void downloadWebpage(URL url, String fileName) {
        InputStream is = null;
        BufferedReader br;
        String line;
        BufferedWriter bw;

        try {
            is = url.openStream();  //  IOException
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8));
            int i = 0;
            while ((line = br.readLine()) != null) {
                bw.write(line + "\n");
                System.out.println(i + ":   " + line);
                i++;
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        System.out.println("Webpage " + url.toString() + " downloaded successfully!");
    }

    public static Example parserCplusplus(String funcName) {
        URL url = null;
        try {
            url = new URL("http://www.cplusplus.com/" + funcName);
            System.out.println(url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
//            fis = new FileInputStream("D:\\shared\\JavaPGM\\4thYear\\code-recommendation-system\\code-recommendation-system\\cplusplus_fgets.html");
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
                return new Example("Example is not found!");
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
        return new Example(out.toString());
    }

//    public static Example parserGithub(String funcName) {
//        Github github = new RtGithub();
//    }
}
