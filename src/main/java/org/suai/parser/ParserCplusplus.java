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
//        InputStream is = null;
//        FileInputStream fis = null;
//        BufferedReader br;
//        String line;
        HttpURLConnection connection = null;
        BufferedReader reader;
        String line;
        String startPattern = "<td class=\"source\"><pre><code>(.*)";
        String endPattern = "(.*)</code></pre></td>(.*)";
        StringBuilder out = new StringBuilder();
        ArrayList<Example> examples = new ArrayList<>();
        int stringCounter = 0;
        try {
//            URL url = new URL("http://www.cplusplus.com/" + funcName);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36");
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(5000);

            // Check for redirect
            URL resourceUrl, base, next;
            Map<String, Integer> visited;
            String location;
            int times;
            String url = "http://www.cplusplus.com/" + funcName;
            visited = new HashMap<>();

            while (true)
            {
                // lambda expression. Don't know what it is and what happens there... but it works!
                times = visited.compute(url, (key, count) -> count == null ? 1 : count + 1);

                if (times > 3)
                    throw new ParseException("Stuck in redirect loop");

                resourceUrl = new URL(url);
                connection = (HttpURLConnection) resourceUrl.openConnection();

                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setInstanceFollowRedirects(false);   // Make the logic below easier to detect redirections
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36");

                switch (connection.getResponseCode())
                {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                        location = connection.getHeaderField("Location");
                        location = URLDecoder.decode(location, "UTF-8");
                        base = new URL(url);
                        next = new URL(base, location);  // Deal with relative URLs
                        url = next.toExternalForm();
                        continue;
                }

                break;
            }
            int status = connection.getResponseCode();
            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }

            Pattern p_start = Pattern.compile(startPattern, Pattern.CASE_INSENSITIVE);
            Pattern p_end = Pattern.compile(endPattern, Pattern.CASE_INSENSITIVE);
            Matcher m;


            boolean findFlag = false;
            while ((line = reader.readLine()) != null && !findFlag) {
                m = p_start.matcher(line);
                stringCounter++;
                while (m.find()) {
//                    String value = m.group(1);
//                    out.append(line);
                    m = p_end.matcher(line);
                    while(!m.find()){
                        String originalLine = reader.readLine();
                        line = originalLine.replaceAll("\\<[^>]*>",""); // exclude html-tags
                        line = line.replaceAll("&lt;", "<").replaceAll("&gt;", ">"); // for #include
                        out.append(line);
                        out.append("\n");
                        m = p_end.matcher(originalLine);
                    }
                    findFlag = true;
                    examples.add(new Example(url, out.toString()));
                }
            }
            if (!findFlag) {
                throw new ParseException("Example is not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
//        System.out.println("Done!");

        return examples;
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
