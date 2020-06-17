package org.suai.analyser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.suai.Example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class  AnalyserTest {

    Analyser analyser;

    @BeforeEach
    void init() {
        analyser = new Analyser();
    }

    @Test
    void analyseCplusplus() {
        Example example = new Example("cplusplus.com/someuri", "some code with fopen");
        assertEquals(1000, analyser.analyse(example, "fopen").getRating());
    }

    @Test
    void analyseCppreference() {
        Example example = new Example("cppreference.com/someuri", "some code with fopen");
        assertEquals(1000, analyser.analyse(example, "fopen").getRating());
    }

    @Test
    void analyseStackoverflow() {
        Example example = new Example("stackoverflow.com/someuri", "some code with fopen", 666);
        assertEquals(666, analyser.analyse(example, "fopen").getRating());
    }

    @Test
    void analyseSearchcode() {
        String path = "src/test/resources/codeForAnalyse.txt";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Example example = new Example("github.com/someuri", sb.toString());
        int size = 33 - 5; // without empty strings and comments
        int n = size - 25;
        int maxNesting = 3;
        int k = maxNesting - 2;

        double sizeRating = 500 * Math.pow(0.95, n);
        double nestingRating = 500 * Math.pow(0.7, k);
        double scaleCoef = 0.7;
        int rating = (int)Math.round((sizeRating + nestingRating) * scaleCoef);

        assertEquals(rating, analyser.analyse(example, "fopen").getRating());
    }
}