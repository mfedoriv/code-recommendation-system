package org.suai;

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
        assertEquals("data", Utils.getDataFromFile("src/test/resources/dataForUtilsTest.txt"));
    }

    @Test
    void writeDataToFile() {
        String dataToWrite = "new data";
        Utils.writeDataToFile("src/test/resources/dataForUtilsTest.txt", dataToWrite);
        assertEquals(dataToWrite, Utils.getDataFromFile("src/test/resources/dataForUtilsTest.txt"));
        Utils.writeDataToFile("src/test/resources/dataForUtilsTest.txt", "data");
    }

    @Test
    void downloadWebpage() {
        String url = "http://cplusplus.com/reference/cstdio/fopen/";
        Utils.downloadWebpage(url, "src/test/resources/fopen.html");
        assertTrue(Utils.getDataFromFile("src/test/resources/fopen.html").contains("cplusplus.com"));
        File file = new File("src/test/resources/fopen.html");
        file.delete();
    }
}