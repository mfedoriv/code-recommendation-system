package org.suai;

import org.suai.parser.ParseException;
import org.suai.parser.Parser;

public class test {
    public static void main(String[] args) {
//        System.out.println(Parser.parseData());
        Example example;
        try {
            example = Parser.parserGithub("fopen");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
