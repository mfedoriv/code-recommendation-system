package org.suai;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {

    Example example;

    @BeforeEach
    void init() {
        example = new Example("source.com","some code", 500);
    }

    @Test
    void getCode() {
        assertEquals("some code", example.getCode());
    }

    @Test
    void setCode() {
        String code = "another code";
        example.setCode(code);
        assertEquals(code, example.getCode());
    }

    @Test
    void getSource() {
        assertEquals("source.com", example.getSource());
    }

    @Test
    void getRating() {
        assertEquals(500, example.getRating());
    }

    @Test
    void setRating() {
        int newRating = 800;
        example.setRating(newRating);
        assertEquals(newRating, example.getRating());
    }

    @Test
    void testToString() {
        String exToString = example.toString();
        assertAll("Example.toString should contain Rating, Source and code.",
                () -> assertTrue(exToString.contains("Rating")),
                () -> assertTrue(exToString.contains("Source")),
                () -> assertTrue(exToString.contains("some code")));
    }

    @Test
    void toJSONObject() {
        JSONObject exJSON = example.toJSONObject();
        assertAll("Should return JSONObject of Example with fields: rating, source, code.",
                () -> assertEquals("source.com", exJSON.getString("source")),
                () -> assertEquals(500, exJSON.getInt("rating")),
                () -> assertEquals("some code", exJSON.getString("code")));
    }

    @Test
    void compareTo() {
        Example example1 = new Example("source.com", "some code code", 600);
        assertEquals(100, example.compareTo(example1));
    }

    @Test
    void testEquals() {
        Example example1 = new Example("source.com", "some code", 500);
        assertTrue(example.equals(example1));
    }

    @Test
    void testEqualsDifferentSource() {
        Example example1 = new Example("different.com", "some code", 500);
        assertTrue(example.equals(example1));
    }

    @Test
    void testEqualsDifferentRating() {
        Example example1 = new Example("source.com", "some code", 600);
        assertFalse(example.equals(example1));
    }

    @Test
    void testEqualsDifferentCode() {
        Example example1 = new Example("source.com", "different code", 500);
        assertFalse(example.equals(example1));
    }

    @Test
    void testHashCode() {
        Example example1 = new Example(example);
        assertEquals(example.hashCode(), example1.hashCode());
    }


}