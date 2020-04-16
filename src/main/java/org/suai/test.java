package org.suai;

import org.suai.parser.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        ///////
//        Example example = null;
//        ParserSearchcode parser = new ParserSearchcode();
//        try {
//            example = parser.findExample("strcmp");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (example != null){
//            System.out.println(example);
//        }
        /////////////////
//        Example example = null;
//        ParserStackoverflow parser = new ParserStackoverflow();
//        try {
//            example = parser.findExample("strcmp");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (example != null) {
//            System.out.println(example);
//        }
        ///////////////////////
        Example example = null;
        ParserCplusplus parser = new ParserCplusplus();
        try {
            example = parser.findExample("strcmp");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (example != null) {
            System.out.println(example);
        }
    }
}
