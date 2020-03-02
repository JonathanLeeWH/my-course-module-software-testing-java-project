package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GrepArgsParserTest {
    private GrepArgsParser grepArgsParser;
    private static final char FLAG_IS_INVERT = 'v';
    private static final String VALID_NON_FLAG_1 = "hello";
    private static final String VALID_NON_FLAG_2 = "hello2";

    @BeforeEach
    void setUp() {
        grepArgsParser = new GrepArgsParser();
    }

    @Test
    void testIsInvertWhenFlagsContainVFlagShouldReturnTrue() {
        grepArgsParser.flags.add(FLAG_IS_INVERT);
        assertTrue(grepArgsParser.isInvert());
    }
    @Test
    void testGetFileNamesWhenNonFlagArgsIsEmptyShouldReturnNull() {
        assertTrue(grepArgsParser.nonFlagArgs.isEmpty());
        assertNull(grepArgsParser.getFileNames());
    }

    @Test
    void testGetPatternShouldReturnNullWhenNonFlagArgsIsNotEmpty() {
        grepArgsParser.nonFlagArgs.addAll(Arrays.asList(VALID_NON_FLAG_1, VALID_NON_FLAG_2));
        assertNull(grepArgsParser.getPattern());
    }
}
