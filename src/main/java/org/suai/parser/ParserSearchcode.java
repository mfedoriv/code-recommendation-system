package org.suai.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.suai.Example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ParserSearchcode implements Parser {

    @Override
    public Example findExample(String funcName) throws ParseException {
        HttpURLConnection connection = null;
        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();
        Example example = new Example("searchcode.com");
        try {
            // search code examples on C/C++ with lines of code 20<len<30
            URL url = new URL("https://searchcode.com/api/codesearch_I/?q=" + funcName + "&per_page=100lan=16&lan=28&loc=20&loc2=30");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            int status = connection.getResponseCode();
            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }
            JSONObject jo = new JSONObject(responseContent.toString());
            JSONArray results = jo.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject o = results.getJSONObject(i);
                ////////////////// Get full file of source code
                int id = o.getInt("id");
                StringBuilder exampleBuilder = new StringBuilder();
                URL codeURL = new URL("https://searchcode.com/api/result/" + id + "/");
                HttpURLConnection codeConnection = (HttpURLConnection) codeURL.openConnection();
                BufferedReader codeReader = new BufferedReader(new InputStreamReader(codeConnection.getInputStream()));
                String codeLine;
                while ((codeLine = codeReader.readLine()) != null) {
                    JSONObject codeObj = new JSONObject(codeLine);
                    exampleBuilder.append(codeObj.getString("code"));
                    exampleBuilder.append("\n");
                }
                codeReader.close();
                ///////////////// Get several lines of source code containing funcName
                /*JSONObject lines = o.getJSONObject("lines");
                StringBuilder exampleBuilder = new StringBuilder();
                Iterator<String> keys = lines.keys();
                while (keys.hasNext()) {
                    exampleBuilder.append(lines.getString(keys.next()));
                    exampleBuilder.append("\n");
                }*/
                ////////////
                example.addExample(exampleBuilder.toString());
            }
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return example;
    }
}
