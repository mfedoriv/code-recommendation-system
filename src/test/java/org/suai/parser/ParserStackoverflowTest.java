package org.suai.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.suai.Example;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ParserStackoverflowTest {

    ParserStackoverflow parserStackoverflow;

    @BeforeEach
    void init() {
        parserStackoverflow = new ParserStackoverflow();
    }

//    @Disabled("Don't know how to text this method without placing my token here")
    @Test
    void findExample() throws ParseException {
        String funcName = "fopen";
        ArrayList<Example> examples = parserStackoverflow.findExample(funcName);
        assertAll("Should return ArrayList with several Examples",
                () -> assertTrue(examples.size() > 1),
                () -> assertTrue(examples.get(0).getSource().contains("stackoverflow.com")),
                () -> assertTrue(examples.get(0).getCode().contains(funcName)));
    }
}