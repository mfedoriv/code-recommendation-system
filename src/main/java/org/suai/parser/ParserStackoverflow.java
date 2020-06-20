package org.suai.parser;

import org.suai.Example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.suai.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        //        String key = "6T)svt*aUTaibpaVcYCxjA(("; // Old Auth key
        String key = "EPsz6N*vmgv)QZ9Flb2XBA(("; // Auth key (This is not considered a secret!)
        String filter = "!*1ScCIwlbiqdYGrs2eRmgiIODFQSC.SIIJfIF(JuP";
//
///2.2/search/advanced?order=desc&sort=relevance&q=printf&accepted=True&nottagged=c++&tagged=c;printf&site=stackoverflow&filter=!*1ScCIwlbiqdYGrs2eRmgiIODFQSC.SIIJfIF(JuP
//        !9Z(-wzu0T //Old filter
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
//            /2.2/answers/2524675?order=desc&sort=activity&site=stackoverflow&filter=!B96E4.(oV2Gz(eNZJuonUmqvxKo*65
//            !9Z(-wzu0T // old filter
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


    public ArrayList<Example> findExampleFromFile(String funcName) throws ParseException {
        ArrayList<Example> examples = new ArrayList<>();

            ArrayList<String> responseAnswer = new ArrayList<>();
            responseAnswer.add(Utils.getDataFromFile("stackoverflow_ansID_1716621.json"));
            ////////////////////
            JSONArray answerJson = new JSONObject(responseAnswer.get(0)).getJSONArray("items");
            String answer = answerJson.getJSONObject(0).getString("body");
            // Parse answer
            Pattern pattern = Pattern.compile("<code>(.+?)</code>", Pattern.DOTALL);
//            Pattern pattern1 = Pattern.compile("(\\W*)printf(\\s*)\\((.*)", Pattern.DOTALL);
//            String examp = "printf(\"Buffered, will be flushed\");\nfflush(stdout); // Will now print everything in the stdout buffer\n";
//            String examp2 = "fprintf(stderr, \"I will be printed immediately\");\n";
//            System.out.println(examp.matches("(\\W*)printf(\\s*)\\((.*)"));
//            System.out.println(examp2.matches("(\\W*)printf(\\s*)\\((.*)"));
            Matcher matcher = pattern.matcher(answer);
            boolean isFound = false;
            ArrayList<String> codes = new ArrayList<>();
            if (answer.contains("<code>")) {
                while (matcher.find()) {
                    String code = matcher.group(1);
                    // Can't understand what's wrong in this regexp that describes function with not word at left
                    // (to exclude functions that start differently: printf, fprintf),
                    // bracket at right and and the other things in the end. In IDEA regexp validator it works
                    // but in code 'funcMatcher.matches()' don't
                    Matcher funcMatcher = Pattern.compile("(\\W*)" + funcName.toLowerCase() + "(\\s*)\\((.*)").matcher(code);
                    if (funcMatcher.find()) {
                        isFound = true;
                        codes.add(matcher.group(1));
                    }
                }
            }
        codes.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) { //descending order
                return o2.length() - o1.length();
            }
        });
            examples.add(new Example("src", codes.get(0)));
        System.out.println(examples);
        if (!isFound) {
            throw new ParseException("stackoverflow.com");
        }

        return examples;
    }
}
