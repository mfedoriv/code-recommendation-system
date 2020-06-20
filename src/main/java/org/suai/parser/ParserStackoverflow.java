package org.suai.parser;

import org.suai.Example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.regex.*;

public class ParserStackoverflow implements Parser {
    @Override
    public ArrayList<Example> findExample(String funcName) throws ParseException {
//        System.out.println("Parser Stackoverflow");

        ArrayList<Example> examples = new ArrayList<>();

        Preferences prefs = Preferences.userRoot().node("CodeRecSystem");
        Properties properties = new Properties();
        String access_token = prefs.get("token", null); // This is unique for every user and must keep secret!
        if (access_token == null) {
            try {
                properties.load(getClass().getResourceAsStream("/coderec.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            access_token = properties.getProperty("token", "");
            if (access_token.length() <= 0) {
                throw new ParseException("stackoverflow.com. Error! Can't find StackOverflow token!" +
                        "You must set your token in coderec.propreties file before first launch.");
            }
            prefs.put("token", access_token);
        }
        String key = "EPsz6N*vmgv)QZ9Flb2XBA(("; // Auth key (This is not considered a secret!)
        String filter = "!*1ScCIwlbiqdYGrs2eRmgiIODFQSC.SIIJfIF(JuP";

        ArrayList<String> urlSearches = new ArrayList<>();
        // with 'funcName' tag for better search
        int numbOfResults = 8;
        urlSearches.add("https://api.stackexchange.com/2.2/search/advanced?q=" + funcName +
                "&order=desc&sort=relevance&pagesize=" + numbOfResults + "&accepted=True&site=stackoverflow&filter=" +
                filter + "&nottagged=c%2B%2B&tagged=c%3B" + funcName + "&key=" + key + "&access_token=" + access_token);
        // without 'funcName' tag
        urlSearches.add("https://api.stackexchange.com/2.2/search/advanced?q=" + funcName +
                "&order=desc&sort=relevance&pagesize=" + numbOfResults + "&accepted=True&site=stackoverflow&filter=" +
                filter + "&nottagged=c%2B%2B&tagged=c&key=" + key + "&access_token=" + access_token);
        boolean isFound = false;
        for (String urlSearch: urlSearches) {
            ArrayList<String> response = null;
            try {
                response = getResponse(urlSearch, true);
            } catch (ParseException e) {
                throw new ParseException("stackoverflow.com");
            }
            JSONArray results = new JSONObject(response.get(0)).getJSONArray("items");
            for (int i = 0; i < results.length(); i++) {
                JSONObject o = results.getJSONObject(i);
                int acceptedAnswerId = o.getInt("accepted_answer_id");
                String urlAnswer = "https://api.stackexchange.com/2.2/answers/" + acceptedAnswerId +
                        "?order=desc&sort=activity&site=stackoverflow&filter=!-Kh(ZYuip9YGwltuOUoudmWLcrmMYLnp*&key="
                        + key + "&access_token=" + access_token;
                ArrayList<String> responseAnswer = getResponse(urlAnswer, true);
                JSONArray answerJson = new JSONObject(responseAnswer.get(0)).getJSONArray("items");
                String answer = answerJson.getJSONObject(0).getString("body");
                // Parse answer
                Pattern pattern = Pattern.compile("<code>(.+?)</code>", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(answer);
                ArrayList<String> codes = new ArrayList<>();
                if (answer.contains("<code>")) {
                    while (matcher.find()) {
                        String code = matcher.group(1);
                        // Can't understand what's wrong in this regexp that describes function with not word at left
                        // (to exclude functions that start differently: printf, fprintf),
                        // bracket at right and and the other things in the end. In IDEA regexp validator it works
                        // but in code 'funcMatcher.matches()' don't
                        // EDIT: search can be better with \b, but need to test
                        Matcher funcMatcher = Pattern.compile("(\\W*)" + funcName.toLowerCase() + "(\\s*)\\((.*)").matcher(code);
                        if (funcMatcher.find()) {
                            isFound = true;
                            codes.add(matcher.group(1));
                        }
                    }
                }
                if (!codes.isEmpty()) {
                    // choose biggest part of code from answer if there was more than one
                    codes.sort(new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) { //descending order
                            return o2.length() - o1.length();
                        }
                    });
                    // return only answers with score > 0
                    int score = o.getInt("score");
                    if (score > 0){
                        Example ex = new Example(o.getString("link"), codes.get(0));
                        ex.setRating(score);
                        examples.add(ex);
                    }
                }
            }
            if (isFound) {
                break;
            }
        }

        if (!isFound) {
            throw new ParseException("stackoverflow.com");
        }
        return examples;
    }
}
