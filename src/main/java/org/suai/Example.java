package org.suai;

import java.util.ArrayList;

public class Example {
    ArrayList<String> list;

    public Example() {
        list = new ArrayList<>();
    }

    public Example(String initExample) {
        list = new ArrayList<>();
        list.add(initExample);
    }

    public void addExample(String example) {
        list.add(example);
    }

    public ArrayList<String> getList() {
        return list;
    }
}
