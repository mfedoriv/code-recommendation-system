package org.suai;

import org.json.JSONArray;
import org.json.JSONObject;
import org.suai.parser.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;
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
//        Example example = null;
//        ParserCplusplus parser = new ParserCplusplus();
//        try {
//            example = parser.findExample("strcmp");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (example != null) {
//            System.out.println(example);
//        }

        /////////////////

//        System.out.println(new JSONObject(example));
//        System.out.println(new JSONArray(example.getList()));

        /*PrintStream out = System.out;
        out.print("{\n");
        out.print("\"results\":[\n");
        for (int i = 0; i < examples.size(); i++) {
            out.print(examples.get(i).toJSONObject());
            if (i != examples.size() - 1) {
                out.print(",");
            }
            out.print("\n");
        }
        out.print("]\n}");
//        System.out.println(example.toJSONObject());*/

        //////////////////////////
        /*Example ex1 = new Example("github.com", "somecodeline1\ncodeline2\ncodeline3");
        ex1.setRating(10);
        Example ex2 = new Example("github.com", "somecodeline1\ncodeline2\ncodeline3\ncodeline4");
        ex2.setRating(5);
        Example ex3 = new Example("github.com", "somecodeline1\ncodeline2\ncodeline3\ncodeline4\ncodeline5\ncodeline6");
        ex3.setRating(7);
        ArrayList<Example> examples = new ArrayList<>();
        examples.add(ex1);
        examples.add(ex2);
        examples.add(ex3);
        System.out.println(examples);
        Collections.sort(examples);
        System.out.println(examples);*/

//        ParserClass.downloadWebpage("https://www.cplusplus.com/", "cplusplus.html");
        //////////////////////////
//        int acceptedAnswerId = 1716621;
//        String key = "EPsz6N*vmgv)QZ9Flb2XBA(("; // Auth key
//        String urlAnswer = "https://api.stackexchange.com/2.2/answers/" + acceptedAnswerId +
//                "?order=desc&sort=activity&site=stackoverflow&filter=!B96E4.(oV2Gz(eNZJuonUmqvxKo*65&key=" + key;
//        ArrayList<String> data = new ArrayList<>();
//        try (FileWriter writer = new FileWriter("stackoverflow_ansID_" + acceptedAnswerId + ".json")){
//            data = new ParserStackoverflow().getResponse(urlAnswer, true);
//            for(String str: data) {
//                writer.write(str + System.lineSeparator());
//            }
//        } catch (ParseException | IOException e) {
//            e.printStackTrace();
//        }

        /////////////////////////////////
//        ParserStackoverflow ps = new ParserStackoverflow();
//        try {
//            ps.findExampleFromFile("printf");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

////////////////////////////////

        ParserCplusplus p = new ParserCplusplus();
        System.out.println(p.getClass().getName());
        System.out.println(p.getClass().getSimpleName());

        Properties properties = new Properties();
//        properties.setProperty(p.getClass().getSimpleName() + "_enabled", "True");
//        System.out.println(properties.get(p.getClass().getSimpleName() + "_enabled"));
//        try {
//            properties.store(new FileWriter("coderec.properties"), "Configuration file of Code Recommendation System.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            properties.load(new FileReader("coderec.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(properties.get(p.getClass().getSimpleName() + "_enabled"));
        System.out.println(properties.toString());
        System.out.println(properties.getProperty("hello"));
    }
}
