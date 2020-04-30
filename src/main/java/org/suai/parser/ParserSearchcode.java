package org.suai.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.suai.Example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class ParserSearchcode implements Parser {

    @Override
    public Example findExample(String funcName) throws ParseException {
        Example example = new Example("searchcode.com");
        try {
            // search code examples on C/C++ with lines of code 20<len<30
            String urlSearch = "https://searchcode.com/api/codesearch_I/?q=" + funcName + "&per_page=100lan=16&lan=28&loc=20&loc2=30";
            String response = getResponse(urlSearch, false);

            JSONObject jo = new JSONObject(response);
            JSONArray results = jo.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject o = results.getJSONObject(i);
                ////////////////// Get full file of source code
                int id = o.getInt("id");
//                System.out.println("Searchcode ID: " + id + "\n");
                StringBuilder exampleBuilder = new StringBuilder();
                URL codeURL = new URL("https://searchcode.com/api/result/" + id + "/");
                HttpURLConnection codeConnection = (HttpURLConnection) codeURL.openConnection();
                BufferedReader codeReader = new BufferedReader(new InputStreamReader(codeConnection.getInputStream()));
                String line = null;
                StringBuilder codeLine = new StringBuilder();
                while ((line = codeReader.readLine()) != null) {
                    codeLine.append(line);
                }
                JSONObject codeObj = new JSONObject(codeLine.toString());
                exampleBuilder.append(codeObj.getString("code"));
                exampleBuilder.append("\n");
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
        }
        return example;
    }
}
