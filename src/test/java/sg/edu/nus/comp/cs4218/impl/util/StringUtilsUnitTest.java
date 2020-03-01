package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsUnitTest {

    private static final String NULL_STRING = null;
    private static final String EMPTY_STRING = "";
    private static final String SPACE_STRING = "  ";
    private static final String SPECIAL_CHAR= "\n\t\n\t\t";
    private static final String SPECIAL_CHAR_SP = "\n    \n\t";
    private static final String NORMAL_STRING = "This is normal string.";

    private static final String[] EMPTY_STR_ARR = new String[0];

    /*******************************************************************
     isBlank method test
     ******************************************************************/

    @Test
    void isBlankNullStringTest() {
        assertTrue(StringUtils.isBlank(NULL_STRING));
    }

    @Test
    void isBlankEmptyStringTest() {
        assertTrue(StringUtils.isBlank(EMPTY_STRING));
    }

    @Test
    void isBlankStringAllWhitespace1Test() {
        assertTrue(StringUtils.isBlank(SPACE_STRING));
    }

    @Test
    void isBlankStringAllWhitespace2Test() {
        assertTrue(StringUtils.isBlank(SPECIAL_CHAR));
    }

    @Test
    void isBlankStringAllWhitespace3Test() {
        assertTrue(StringUtils.isBlank(SPECIAL_CHAR_SP));
    }

    @Test
    void isBlankNormalStringTest() {
        assertFalse(StringUtils.isBlank(NORMAL_STRING));
    }


    /*******************************************************************
     multiplyChar method test within StringUtils
     ******************************************************************/

    @Test
    void multiplyCharNumericalCharPositiveNumTest1() {
        String actualOutput = StringUtils.multiplyChar('0', 3);
        assertEquals(actualOutput, "000");
    }

    @Test
    void multiplyCharNumericalCharPositiveNumTest2() {
        String actualOutput = StringUtils.multiplyChar('5', 2);
        assertEquals(actualOutput, "55");
    }

    @Test
    void multiplyCharNumericalCharPositiveNumTest3() {
        String actualOutput = StringUtils.multiplyChar('9', 1);
        assertEquals(actualOutput, "9");
    }

    @Test
    void multiplyCharNumericalCharNegativeNumTest() {
        String actualOutput = StringUtils.multiplyChar('1', -1);
        assertEquals(actualOutput, EMPTY_STRING);
    }

    @Test
    void multiplyCharNumericalCharZeroNumTest() {
        String actualOutput = StringUtils.multiplyChar('5', 0);
        assertEquals(actualOutput, EMPTY_STRING);
    }
    @Test
    void multiplyCharSmallAlphabeticalCharPositiveNumTest1() {
        String actualOutput = StringUtils.multiplyChar('a', 3);
        assertEquals(actualOutput, "aaa");
    }

    @Test
    void multiplyCharSmallAlphabeticalCharPositiveNumTest2() {
        String actualOutput = StringUtils.multiplyChar('b', 2);
        assertEquals(actualOutput, "bb");
    }

    @Test
    void multiplyCharCapitalAlphabeticalCharPositiveNumTest1() {
        String actualOutput = StringUtils.multiplyChar('A', 5);
        assertEquals(actualOutput, "AAAAA");
    }

    @Test
    void multiplyCharCapitalAlphabeticalCharPositiveNumTest2() {
        String actualOutput = StringUtils.multiplyChar('B', 6);
        assertEquals(actualOutput, "BBBBBB");
    }

    @Test
    void multiplyCharCapitalAlphabeticalCharPositiveNumTest3() {
        String actualOutput = StringUtils.multiplyChar('G', 4);
        assertEquals(actualOutput, "GGGG");
    }

    @Test
    void multiplyCharWhitespaceCharPositiveNumTest1() {
        String actualOutput = StringUtils.multiplyChar(' ', 4);
        assertEquals(actualOutput, "    ");
    }

    @Test
    void multiplyCharWhitespaceCharPositiveNumTest2() {
        String actualOutput = StringUtils.multiplyChar('\n', 2);
        assertEquals(actualOutput, "\n\n");
    }

    @Test
    void multiplyCharWhitespaceCharPositiveNumTest3() {
        String actualOutput = StringUtils.multiplyChar('\t', 2);
        assertEquals(actualOutput, "\t\t");
    }

    @Test
    void multiplyCharOtherCharPositiveNumTest1() {
        String actualOutput = StringUtils.multiplyChar(':', 4);
        assertEquals(actualOutput, "::::");
    }

    @Test
    void multiplyCharOtherCharPositiveNumTest2() {
        String actualOutput = StringUtils.multiplyChar(',', 3);
        assertEquals(actualOutput, ",,,");
    }

    @Test
    void multiplyCharOtherCharPositiveNumTest3() {
        String actualOutput = StringUtils.multiplyChar('*', 2);
        assertEquals(actualOutput, "**");
    }

    /*******************************************************************
     Tokenize method test within StringUtils
     ******************************************************************/

    @Test
    void tokenizeEmptyStringTest() {
        String[] actualOutput = StringUtils.tokenize(EMPTY_STRING);
        assertArrayEquals(actualOutput, EMPTY_STR_ARR);
    }

    @Test
    void tokenizeSingleSpaceTest() {
        String[] actualOutput = StringUtils.tokenize(" ");
        assertArrayEquals(actualOutput, EMPTY_STR_ARR);
    }

    @Test
    void tokenizeMultipleNewlineCharTest() {
        String[] actualOutput = StringUtils.tokenize("\n\n\n");
        assertArrayEquals(actualOutput, EMPTY_STR_ARR);
    }

    @Test
    void tokenizeMultipleWhitespaceCharTest() {
        String[] actualOutput = StringUtils.tokenize("\n\t ");
        assertArrayEquals(actualOutput, EMPTY_STR_ARR);
    }

    @Test
    void tokenizeNoWhitespaceTest() {
        String[] actualOutput = StringUtils.tokenize("wordNoSpace");
        List<String> result = new ArrayList<String>();
        result.add("wordNoSpace");
        String[] expectedOutput = result.toArray(new String[0]);
        assertArrayEquals(actualOutput, expectedOutput);
    }

    @Test
    void tokenizeSingleWhitespaceTest() {
        String[] actualOutput = StringUtils.tokenize("word oneSpace");
        List<String> result = new ArrayList<String>();
        result.add("word");
        result.add("oneSpace");
        String[] expectedOutput = result.toArray(new String[0]);
        assertArrayEquals(actualOutput, expectedOutput);
    }

    @Test
    void tokenizeMultipleWhitespaceTest() {
        String[] actualOutput = StringUtils.tokenize("string with many spaces");
        List<String> result = new ArrayList<String>();
        result.add("string");
        result.add("with");
        result.add("many");
        result.add("spaces");
        String[] expectedOutput = result.toArray(new String[0]);
        assertArrayEquals(actualOutput, expectedOutput);
    }

    @Test
    void tokenizeLeadingSpaceAndTrailingNewlineTest() {
        String[] actualOutput = StringUtils.tokenize("  two leading spaces and a newline\n");
        List<String> result = new ArrayList<String>();
        result.add("two");
        result.add("leading");
        result.add("spaces");
        result.add("and");
        result.add("a");
        result.add("newline");
        String[] expectedOutput = result.toArray(new String[0]);
        assertArrayEquals(actualOutput, expectedOutput);
    }
}