package org.suai.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.suai.Example;

import java.util.ArrayList;

public class ParserSearchcode implements Parser {

    @Override
    public ArrayList<Example> findExample(String funcName) throws ParseException {
//        System.out.println("Parser Searchcode");

        ArrayList<Example> examples = new ArrayList<>();
        int numbOfResults = 20;
        // search code examples on C with lines of code 10<len<200
        String urlSearch = "https://searchcode.com/api/codesearch_I/?q=" + funcName + "&per_page=" + numbOfResults + "&lan=28&loc=10&loc2=200";
        ArrayList<String> response = getResponse(urlSearch, false);
        String responseString = String.join("\n", response);
        JSONObject jo = new JSONObject(responseString);
        JSONArray results = jo.getJSONArray("results");
        if (results.isEmpty()) {
            throw new ParseException("searchcode.com");
        }
        for (int i = 0; i < results.length(); i++) {
            JSONObject o = results.getJSONObject(i);
            ////////////////// Get full file of source code
            int id = o.getInt("id");
//          System.out.println("Searchcode ID: " + id + "\n");
            StringBuilder exampleBuilder = new StringBuilder();
            ArrayList<String> responseAnswer = getResponse("https://searchcode.com/api/result/" + id + "/", false);
            StringBuilder codeLine = new StringBuilder();
            for(int j = 0; j < responseAnswer.size(); j++) {
                codeLine.append(responseAnswer.get(j));
            }
            JSONObject codeObj = new JSONObject(codeLine.toString());
            exampleBuilder.append(codeObj.getString("code"));
            exampleBuilder.append("\n");
            ///////////////// Get several lines of source code containing funcName
                /*JSONObject lines = o.getJSONObject("lines");
                StringBuilder exampleBuilder = new StringBuilder();
                Iterator<String> keys = lines.keys();
                while (keys.hasNext()) {
                    exampleBuilder.append(lines.getString(keys.next()));
                    exampleBuilder.append("\n");
                }*/
            ////////////
            examples.add(new Example(o.getString("repo"), exampleBuilder.toString()));
        }
        return examples;
    }
}
