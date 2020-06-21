package org.suai;

import org.json.JSONObject;

import java.util.Objects;

public class Example implements Comparable<Example> {

    private String source;
    private int rating;
    private String code;

    public Example(String source) {
        this.source = source;
        this.rating = 0;
    }

    public Example(String src, String codeEx) {
        this.source = src;
        this.rating = 0;
        this.code = codeEx;
    }

    public Example(String source,  String code, int rating) {
        this.source = source;
        this.rating = rating;
        this.code = code;
    }

    public Example(JSONObject example) {
        this.source = example.getString("source");
        this.rating = example.getInt("rating");
        this.code = example.getString("code");
    }

    public Example(Example example) {
        this.source = example.getSource();
        this.code = example.getCode();
        this.rating = example.getRating();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    // equals() and hashCode() are implemented so as to prevent being in the list of Examples with the same code
    // and rating, but different sources, i.e. if the sources are different and everything else is the same, the
    // Examples are considered equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Example example = (Example) o;
        return rating == example.rating &&
                Objects.equals(code, example.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rating, code);
    }
}
