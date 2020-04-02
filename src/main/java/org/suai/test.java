package org.suai;

import org.suai.parser.ParseException;
import org.suai.parser.ParserClass;
import org.suai.parser.ParserCppreference;
import org.suai.parser.ParserSearchcode;

import java.net.MalformedURLException;
import java.net.URL;

public class test {
    public static void main(String[] args) {
//        System.out.println(Parser.parseData());

//        Example example;
//        URL url = null;
//        try {
//            url = new URL("http://cplusplus.com/search.do?q=strcmp&kwr=no");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
        /////////////
//        URL url = null;
//        try {
//            url = new URL("https://en.cppreference.com/w/c/string/byte/strcmp");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        ParserClass.downloadWebpage(url, "cppreference_strcmp.html");
        Example example = null;
        ParserSearchcode parser = new ParserSearchcode();
        try {
            example = parser.findExample("strcmp");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (example != null){
            System.out.println(example);
        }
    }
}
