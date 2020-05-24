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

public class ParserClass {
    public static void downloadWebpage(String urlString, String fileName) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try(
                BufferedReader reader =  new BufferedReader(new InputStreamReader(url.openStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        )  {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }
            System.out.println("Webpage " + url.toString() + " downloaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
