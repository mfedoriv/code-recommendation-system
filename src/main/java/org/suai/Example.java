package org.suai;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Example {
    private ArrayList<String> list;
    private String source;

    public Example() {
        list = new ArrayList<>();
        source = null;
    }

    public Example(String src) {
        list = new ArrayList<>();
        source = src;
    }

    public Example(String src, String initExample) {
        list = new ArrayList<>();
        list.add(initExample);
        source = src;
    }

    public void addExample(String example) {
        list.add(example);
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setSource(String src) {
        source = src;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("// Source of examples: ").append(source).append("\n");
        if (list.isEmpty()) {
            sb.append("// Sorry. Can't find examples.");
        } else {
            for (int i = 0; i < list.size(); i++) {
                sb.append("// Example ").append(i + 1).append("\n").append(list.get(i)).append("\n");
                sb.append("// -------------------------------------------------------------- \n");
            }
        }
        return sb.toString();
    }

    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("source", source);
        jo.put("examples", new JSONArray(list));
        return jo;
    }
}
