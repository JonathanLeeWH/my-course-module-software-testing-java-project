package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTest {

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
    void testIsBlankNullStringTest() {
        assertTrue(StringUtils.isBlank(NULL_STRING));
    }

    @Test
    void testIsBlankEmptyStringTest() {
        assertTrue(StringUtils.isBlank(EMPTY_STRING));
    }

    @Test
    void testIsBlankStringAllWhitespace1Test() {
        assertTrue(StringUtils.isBlank(SPACE_STRING));
    }

    @Test
    void testIsBlankStringAllWhitespace2Test() {
        assertTrue(StringUtils.isBlank(SPECIAL_CHAR));
    }

    @Test
    void testIsBlankStringAllWhitespace3Test() {
        assertTrue(StringUtils.isBlank(SPECIAL_CHAR_SP));
    }

    @Test
    void testIsBlankNormalStringTest() {
        assertFalse(StringUtils.isBlank(NORMAL_STRING));
    }


    /*******************************************************************
     multiplyChar method test within StringUtils
     ******************************************************************/

    @Test
    void testMultiplyCharNumericalCharPositiveNumTest1() {
        String actualOutput = StringUtils.multiplyChar('0', 3);
        assertEquals(actualOutput, "000");
    }

    @Test
    void testMultiplyCharNumericalCharPositiveNumTest2() {
        String actualOutput = StringUtils.multiplyChar('5', 2);
        assertEquals(actualOutput, "55");
    }

    @Test
    void testMultiplyCharNumericalCharPositiveNumTest3() {
        String actualOutput = StringUtils.multiplyChar('9', 1);
        assertEquals(actualOutput, "9");
    }

    @Test
    void testMultiplyCharNumericalCharNegativeNumTest() {
        String actualOutput = StringUtils.multiplyChar('1', -1);
        assertEquals(actualOutput, EMPTY_STRING);
    }

    @Test
    void testMultiplyCharNumericalCharZeroNumTest() {
        String actualOutput = StringUtils.multiplyChar('5', 0);
        assertEquals(actualOutput, EMPTY_STRING);
    }
    @Test
    void testMultiplyCharSmallAlphabeticalCharPositiveNumTest1() {
        String actualOutput = StringUtils.multiplyChar('a', 3);
        assertEquals(actualOutput, "aaa");
    }

    @Test
    void testMultiplyCharSmallAlphabeticalCharPositiveNumTest2() {
        String actualOutput = StringUtils.multiplyChar('b', 2);
        assertEquals(actualOutput, "bb");
    }

    @Test
    void testMultiplyCharCapitalAlphabeticalCharPositiveNumTest1() {
        String actualOutput = StringUtils.multiplyChar('A', 5);
        assertEquals(actualOutput, "AAAAA");
    }

    @Test
    void testMultiplyCharCapitalAlphabeticalCharPositiveNumTest2() {
        String actualOutput = StringUtils.multiplyChar('B', 6);
        assertEquals(actualOutput, "BBBBBB");
    }

    @Test
    void testMultiplyCharCapitalAlphabeticalCharPositiveNumTest3() {
        String actualOutput = StringUtils.multiplyChar('G', 4);
        assertEquals(actualOutput, "GGGG");
    }

    @Test
    void testMultiplyCharWhitespaceCharPositiveNumTest1() {
        String actualOutput = StringUtils.multiplyChar(' ', 4);
        assertEquals(actualOutput, "    ");
    }

    @Test
    void testMultiplyCharWhitespaceCharPositiveNumTest2() {
        String actualOutput = StringUtils.multiplyChar('\n', 2);
        assertEquals(actualOutput, "\n\n");
    }

    @Test
    void testMultiplyCharWhitespaceCharPositiveNumTest3() {
        String actualOutput = StringUtils.multiplyChar('\t', 2);
        assertEquals(actualOutput, "\t\t");
    }

    @Test
    void testMultiplyCharOtherCharPositiveNumTest1() {
        String actualOutput = StringUtils.multiplyChar(':', 4);
        assertEquals(actualOutput, "::::");
    }

    @Test
    void testMultiplyCharOtherCharPositiveNumTest2() {
        String actualOutput = StringUtils.multiplyChar(',', 3);
        assertEquals(actualOutput, ",,,");
    }

    @Test
    void testMultiplyCharOtherCharPositiveNumTest3() {
        String actualOutput = StringUtils.multiplyChar('*', 2);
        assertEquals(actualOutput, "**");
    }

    /*******************************************************************
     Tokenize method test within StringUtils
     ******************************************************************/

    @Test
    void testTokenizeEmptyStringTest() {
        String[] actualOutput = StringUtils.tokenize(EMPTY_STRING);
        assertArrayEquals(actualOutput, EMPTY_STR_ARR);
    }

    @Test
    void testTokenizeSingleSpaceTest() {
        String[] actualOutput = StringUtils.tokenize(" ");
        assertArrayEquals(actualOutput, EMPTY_STR_ARR);
    }

    @Test
    void testTokenizeMultipleNewlineCharTest() {
        String[] actualOutput = StringUtils.tokenize("\n\n\n");
        assertArrayEquals(actualOutput, EMPTY_STR_ARR);
    }

    @Test
    void testTokenizeMultipleWhitespaceCharTest() {
        String[] actualOutput = StringUtils.tokenize("\n\t ");
        assertArrayEquals(actualOutput, EMPTY_STR_ARR);
    }

    @Test
    void testTokenizeNoWhitespaceTest() {
        String[] actualOutput = StringUtils.tokenize("wordNoSpace");
        List<String> result = new ArrayList<String>();
        result.add("wordNoSpace");
        String[] expectedOutput = result.toArray(new String[0]);
        assertArrayEquals(actualOutput, expectedOutput);
    }

    @Test
    void testTokenizeSingleWhitespaceTest() {
        String[] actualOutput = StringUtils.tokenize("word oneSpace");
        List<String> result = new ArrayList<String>();
        result.add("word");
        result.add("oneSpace");
        String[] expectedOutput = result.toArray(new String[0]);
        assertArrayEquals(actualOutput, expectedOutput);
    }

    @Test
    void testTokenizeMultipleWhitespaceTest() {
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
    void testTokenizeLeadingSpaceAndTrailingNewlineTest() {
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

    /*******************************************************************
     Tokenize method test within StringUtils
     ******************************************************************/

    /**
     * Tests isNumber method when input is empty.
     * Expected: Returns false.
     */
    @Test
    void testIsNumberWhenInputIsEmptyShouldReturnFalse() {
        assertFalse(StringUtils.isNumber(EMPTY_STRING));
    }

    /**
     * Tests isNumber method when input is has extraneous character like whitespace as specified in BigInteger Javadocs.
     * Expected: Returns false.
     */
    @Test
    void testIsNumberWhenInputHasExtraneousCharacterLikeWhitespaceShouldReturnFalse() {
        assertFalse(StringUtils.isNumber("1 2"));
    }

    /**
     * Tests isNumber method when input string cannot be represented as a number.
     * Expected: Returns true.
     */
    @Test
    void testIsNumberWhenInputCannotBeRepresentedAsANumberShouldReturnTrue() {
        assertFalse(StringUtils.isNumber("hello"));
    }

    /**
     * Tests isNumber method when input is has no extraneous character like whitespace as specified in BigInteger Javadocs.
     * Expected: Returns true.
     */
    @Test
    void testIsNumberWhenInputHasNoExtraneousCharacterShouldReturnTrue() {
        assertTrue(StringUtils.isNumber("1"));
    }
}