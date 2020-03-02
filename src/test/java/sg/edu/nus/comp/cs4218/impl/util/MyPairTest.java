package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyPairTest {
    @Test
    void testGetKeyAndGetValueWithAValidKeyAndValidValueShouldRunSuccessfully() {
        MyPair<String, String> pair = new MyPair<>("Key", "Value");
        assertEquals("Key", pair.getKey());
        assertEquals("Value", pair.getValue());
    }

    @Test
    void testToStringWithAValidKeyAndValidValueShouldRunSuccessfully() {
        MyPair<String, String> pair = new MyPair<>("1", "2");
        assertNotNull(pair.toString());
        assertEquals("(1,2)", pair.toString());
    }

    @Test
    void testEqualsAndHashCodeWithAnotherSimilarPairObjShouldRunSuccessfully() {
        MyPair<String, String> pair = new MyPair<>("1", "2");
        boolean result = pair.equals(pair);
        assertTrue(result);
        assertEquals(pair.hashCode(), pair.hashCode());
    }

    @Test
    void testEqualsAndHashCodeWithAnotherDifferentPairObjWithSimilarKeyAndValueShouldRunSuccessfully() {
        MyPair<String, String> pair = new MyPair<>("1", "2");
        MyPair<String, String> anotherPair = new MyPair<>("1", "2");
        boolean result = pair.equals(anotherPair);
        assertTrue(result);
        assertEquals(pair.hashCode(), anotherPair.hashCode());
    }

    @Test
    void testEqualsAndHashCodeWithAnotherDifferentPairObjShouldRunSuccessfully() {
        MyPair<String, String> pair = new MyPair<>("1", "2");
        MyPair<String, String> anotherPair = new MyPair<>("a11", "12");
        boolean result = pair.equals(anotherPair);
        assertFalse(result);
        assertNotEquals(pair.hashCode(), anotherPair.hashCode());
    }

    @Test
    void testEqualsAndHashCodeWithAnotherObjShouldRunSuccessfully() {
        MyPair<String, String> pair = new MyPair<>("1", "2");
        Object obj = new Object();
        boolean result = pair.equals(obj);
        assertFalse(result);
        assertNotEquals(pair.hashCode(), obj.hashCode());
    }
}
