package org.suai;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void escapeHTML() {
        String unescaped = "Hello &\"'<>\n";
        String escaped = "Hello&nbsp;&nbsp;&#38;&#34;&#39;&#60;&#62;<br>";
        assertEquals(escaped, Utils.escapeHTML(unescaped));
    }

    @Test
    void getDataFromFile() {
        assertEquals("data", Utils.getDataFromFile("/dataForUtilsTest.txt"));
    }

    @Test
    void writeDataToFile() {
        String dataToWrite = "new data";
        Utils.writeDataToFile("/dataForUtilsTest.txt", dataToWrite);
        assertEquals(dataToWrite, Utils.getDataFromFile("/dataForUtilsTest.txt"));
        Utils.writeDataToFile("/dataForUtilsTest.txt", "data");
    }
}