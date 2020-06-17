package org.suai.parser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.suai.Example;

import java.util.ArrayList;

class ParserCplusplusTest {

    ParserCplusplus parserCplusplus;

    @BeforeEach
    void init() {
        parserCplusplus = new ParserCplusplus();
    }

    @Test
    void findExampleTestException() {
        assertThrows(ParseException.class, () -> parserCplusplus.findExample("incorrect input"));
    }

    @Test
    void findExample() throws ParseException {
        String funcName = "fopen";
        ArrayList<Example> examples = parserCplusplus.findExample(funcName);
        assertAll("Should return ArrayList with one Example",
                () -> assertEquals(1, examples.size()),
                () -> assertTrue(examples.get(0).getSource().contains("cplusplus.com")),
                () -> assertTrue(examples.get(0).getCode().contains(funcName)));
    }
}