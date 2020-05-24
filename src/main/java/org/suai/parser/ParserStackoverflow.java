package org.suai.parser;
import org.suai.Example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.*;

public class ParserStackoverflow implements Parser {
    @Override
    public ArrayList<Example> findExample(String funcName) throws ParseException {
        ArrayList<Example> examples = new ArrayList<>();

        String key = "6T)svt*aUTaibpaVcYCxjA(("; // Auth key

        String urlSearch = "https://api.stackexchange.com/2.2/search/advanced?q=" + funcName +
                "&order=desc&sort=votes&accepted=True&closed=True&migrated=False&tagged=c&site=stackoverflow&filter=!9Z(-wzu0T&key=" + key;
        ArrayList<String> response = null;
        try {
            response = getResponse(urlSearch, true);
        } catch (ParseException e) {
            throw new ParseException("stackoverflow.com");
        }

        JSONArray results = new JSONObject(response.get(0)).getJSONArray("items");
        boolean isFound = false;
        for (int i = 0; i < results.length(); i++) {
            JSONObject o = results.getJSONObject(i);
            int acceptedAnswerId = o.getInt("accepted_answer_id");
            String urlAnswer = "https://api.stackexchange.com/2.2/answers/" + acceptedAnswerId +
                    "?order=desc&sort=activity&site=stackoverflow&filter=!9Z(-wzu0T&key=" + key;
            ArrayList<String> responseAnswer = getResponse(urlAnswer, true);
            JSONArray answerJson = new JSONObject(responseAnswer.get(0)).getJSONArray("items");
            String answer = answerJson.getJSONObject(0).getString("body");
            // Parse answer
            Pattern pattern = Pattern.compile("<pre><code>(.+?)</code>(.*)", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(answer);

            if (answer.contains("<code>")) {
                isFound = true;
                String code = null;
                while (matcher.find()) { // for answers that include several code examples
                    code = matcher.group(1);
                    if (code.contains("\n") && code.contains(funcName)) { // check is this a multiline code or just highlighting
                        code = code.replaceAll("&lt;", "<").replaceAll("&gt;", ">"); // for libraries
                        examples.add(new Example(o.getString("link"), code));
                        break;
                    }
                }
            }
        }
        if (!isFound) {
            throw new ParseException("stackoverflow.com");
        }

        return examples;
    }
}
