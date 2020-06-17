package org.suai.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.suai.Example;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ParserCppreferenceTest {

    ParserCppreference parserCppreference;

    @BeforeEach
    void init() {
        parserCppreference = new ParserCppreference();
    }

    @Test
    void findExampleTestException() {
        assertThrows(ParseException.class, () -> parserCppreference.findExample("incorrect input"));
    }

    @Test
    void findExample() throws ParseException {
        String funcName = "fopen";
        ArrayList<Example> examples = parserCppreference.findExample(funcName);
        assertAll("Should return ArrayList with one Example",
                () -> assertEquals(1, examples.size()),
                () -> assertTrue(examples.get(0).getSource().contains("cppreference.com")),
                () -> assertTrue(examples.get(0).getCode().contains(funcName)));
    }
}