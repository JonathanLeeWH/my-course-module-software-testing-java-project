package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RmArgsParserTest {

    private RmArgsParser rmArgsParser;
    private static final char FLAG_IS_RECURSIVE = 'r';
    private static final char FLAG_WITH_FOLDER = 'd';
    private static final String VALID_NON_FLAG_1 = "hello";
    private static final String VALID_NON_FLAG_2 = "hello2";

    @BeforeEach
    void setUp() {
        rmArgsParser = new RmArgsParser();
    }

    /**
     * Tests isEmptyFolder method when flags contain d flag (FLAG_WITH_FOLDER).
     * Expected: Returns true.
     */
    @Test
    void testIsEmptyFolderWhenFlagsContainDFlagShouldReturnTrue() {
        rmArgsParser.flags.add(FLAG_WITH_FOLDER);
        assertTrue(rmArgsParser.isEmptyFolder());
    }

    /**
     * Tests isEmptyFolder method when flags does not contain d flag (FLAG_WITH_FOLDER).
     * Expected: Returns false.
     */
    @Test
    void testIsEmptyFolderWhenFlagsDoesNotContainDFlagShouldReturnFalse() {
        assertFalse(rmArgsParser.isEmptyFolder());
    }

    /**
     * Tests isRecursive method when flags contain r flag (FLAG_IS_RECURSIVE).
     * Expected: Returns true.
     */
    @Test
    void testIsRecursiveWhenFlagsContainRFlagShouldReturnTrue() {
        rmArgsParser.flags.add(FLAG_IS_RECURSIVE);
        assertTrue(rmArgsParser.isRecursive());
    }

    /**
     * Tests isRecursive method when flags does not contain r flag (FLAG_IS_RECURSIVE).
     * Expected: Returns false.
     */
    @Test
    void testIsRecursiveWhenFlagsDoesNotContainRFlagShouldReturnFalse() {
        assertFalse(rmArgsParser.isRecursive());
    }

    /**
     * Tests getFileNames method when nonFlagArgs is empty.
     * Expected: Returns an empty list.
     */
    @Test
    void testGetFileNamesWhenNonFlagArgsIsEmptyShouldReturnAnEmptyList() {
        assertTrue(rmArgsParser.nonFlagArgs.isEmpty());
        assertTrue(rmArgsParser.getFileNames().isEmpty());
    }

    /**
     * Tests getFileNames method when nonFlagArgs is non empty.
     * Expected: Returns a non empty list.
     */
    @Test
    void testGetFileNamesWhenNonFlagArgsIsNonEmptyShouldReturnANonEmptyList() {
        rmArgsParser.nonFlagArgs.addAll(Arrays.asList(VALID_NON_FLAG_1, VALID_NON_FLAG_2));
        assertFalse(rmArgsParser.nonFlagArgs.isEmpty());
        assertFalse(rmArgsParser.getFileNames().isEmpty());
    }
}