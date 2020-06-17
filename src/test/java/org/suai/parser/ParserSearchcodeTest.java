package org.suai.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.suai.Example;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ParserSearchcodeTest {

    ParserSearchcode parserSearchcode;

    @BeforeEach
    void init() {
        parserSearchcode = new ParserSearchcode();
    }

    @Test
    void findExample() throws ParseException {
        String funcName = "fopen";
        ArrayList<Example> examples = parserSearchcode.findExample(funcName);
        assertAll("Should return ArrayList with several Example",
                () -> assertTrue(examples.size() > 1),
                () -> assertTrue(examples.get(0).getSource().contains("github.com") ||
                        examples.get(0).getSource().contains("gitlab.com") ||
                        examples.get(0).getSource().contains("bitbucket.org")),
                () -> assertTrue(examples.get(0).getCode().contains(funcName)));
    }
}