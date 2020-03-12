package com.company.parser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parser {
    public static void downloadWebpage(URL url, String fileName) {
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

    public static String parseData(URL url) {
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

            boolean findcheck = false;
            while ((line = br.readLine()) != null && !findcheck) {
                m = p_start.matcher(line);
                stringCounter++;
                while (m.find()) {
//                    String value = m.group(1);
//                    out.append(line);
                    m = p_end.matcher(line);
                    while(!m.find()){
                        line = br.readLine();
                        out.append(line.replaceAll("\\<[^>]*>",""));
                        out.append("\n");
                        m = p_end.matcher(line);
                    }
                    findcheck = true;
                }
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
        return out.toString();
    }
}
