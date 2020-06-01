package org.suai;

import org.json.JSONObject;

import java.util.ArrayList;

public class Example implements Comparable<Example> {
    private String source;
    private int rating;
    private String code;

    public Example(String src, String codeEx) {
        source = src;
        rating = 0;
        code = codeEx;
    }

    public String getCode() {
        return code;
    }

    public String getSource() {
        return source;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("// Source of example: ").append(source).append("\n");
        sb.append("// Rating: ").append(rating).append("\n");
        if (code == null) {
            sb.append("// Sorry. Can't find code.\n");
        } else {
            sb.append(code).append("\n");
        }
        return sb.toString();
    }

    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("source", source);
        jo.put("rating", rating);
        jo.put("code", code);
        return jo;
    }

    @Override
    public int compareTo(Example ex) {
        int compareRating = ex.getRating();
        // descending order
        return compareRating - this.rating;
    }
}
