package org.suai.parser;

//import com.jcabi.github.Content;
//import com.jcabi.github.Github;
//import com.jcabi.github.RtGithub;
//import com.jcabi.github.Search;
import org.json.JSONArray;
import org.json.JSONObject;
import org.suai.Example;

import java.io.*;
import java.net.HttpURLConnection;
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

    public static Example parserGithub(String funcName) throws ParseException {
        String oauthToken = null;
        try(BufferedReader br = new BufferedReader(new FileReader("src/main/java/org/suai/github_token.txt"))) {
            for(String line; (line = br.readLine()) != null; ) {
                line = line.trim();
                if (line.charAt(0) == '#') {
                    continue;
                }
                oauthToken = line;
                break;
            }
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        }
        if (oauthToken == null) {
            throw new ParseException("Can't find your OAuth GitHub Token! Please insert it in file github_token.txt");
        }
//        https://api.github.com/search/code?q={query}{&page,per_page,sort,order}
        HttpURLConnection connection = null;
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        try {
            URL url = new URL("https://api.github.com/search/code?q=" + funcName + "+in:file+language:C++");
//            URL url = new URL("https://api.github.com");
//            URL url = new URL("https://jsonplaceholder.typicode.com/posts");
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("Authorization", "token " + oauthToken);
            int status = connection.getResponseCode();
            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }
            JSONObject jo = new JSONObject(responseContent.toString());
            JSONArray items = jo.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                System.out.println(items.get(i));
            }


        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

//        Github github = new RtGithub(oauthToken);
//        Iterable<Content> list = null;
//        try {
//            list = github.search().codes(funcName + "language:C++", "best match", Search.Order.DESC);
//        } catch (IOException e) {
//            throw new ParseException(e.getMessage());
//        }
//        for (Content o: list) {
//            System.out.println(o.toString());
//        }
        return null;
    }

}
