package org.suai.parser;

import org.suai.Example;

public interface Parser {
    public Example findExample(String funcName) throws ParseException;
}
