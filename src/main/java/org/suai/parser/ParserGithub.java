package org.suai.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.suai.Example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ParserGithub implements Parser {
    @Override
    public ArrayList<Example> findExample(String funcName) throws ParseException {
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
        StringBuilder responseContent = new StringBuilder();
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
        return null;
    }
}
