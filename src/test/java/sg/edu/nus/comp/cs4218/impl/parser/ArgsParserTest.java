package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

class ArgsParserTest {

    private static final String ILLEGAL_FLAG = "z";
    private static final Character VALID_FLAG_1 = 'a';
    private static final Character VALID_FLAG_2 = 'b';
    private static final String VALID_NON_FLAG_1 = "hello";

    private ArgsParser argsParser;

    @BeforeEach
    void setUp() {
        argsParser = new ArgsParser();
        argsParser.legalFlags.addAll(Arrays.asList(VALID_FLAG_1, VALID_FLAG_2));
    }

    /**
     * Tests parse method when there is at least one illegal flag.
     * Since we test only public methods, we do not test validateArgs directly.
     * For example: If the list of args passed contains an illegal flag, in this case, -z is treated as an invalid flag.
     * Expected: Throws InvalidArgsException
     */
    @Test
    void testParseWhenAtLeastOneIllegalFlagShouldThrowInvalidArgsException() throws InvalidArgsException {
        String[] argsList = {CHAR_FLAG_PREFIX + ILLEGAL_FLAG};
        InvalidArgsException exception = assertThrows(InvalidArgsException.class, () -> {
            argsParser.parse(argsList);
        });

        assertEquals(new InvalidArgsException(ILLEGAL_FLAG_MSG + ILLEGAL_FLAG).getMessage(), exception.getMessage());
    }

    /**
     * Test parse method when there is no illegal flag and empty arg list
     * Expected: Flags and nonFlagArgs fields are empty.
     */
    @Test
    void testParseWhenNoIllegalFlagAndEmptyArgList() throws InvalidArgsException {
        String[] emptyArgsList = {};
        argsParser.parse(emptyArgsList);
        assertTrue(argsParser.flags.isEmpty());
        assertTrue(argsParser.nonFlagArgs.isEmpty());
    }

    /**
     * Tests parse method when there no illegal flag and non empty arg list with flags and non flags.
     * Expected: Flags and nonFlags fields are non empty.
     */
    @Test
    void testParseWhenNoIllegalFlagAndNonEmptyArgListWithFlagsAndNonFlags() throws InvalidArgsException {
        String[] argsList = {CHAR_FLAG_PREFIX + VALID_FLAG_1.toString(), CHAR_FLAG_PREFIX + VALID_FLAG_2.toString(), VALID_NON_FLAG_1};
        argsParser.parse(argsList);
        assertFalse(argsParser.flags.isEmpty());
        assertFalse(argsParser.nonFlagArgs.isEmpty());
    }

    /**
     * Tests parse method when there no illegal flag and non empty arg list with non flags only.
     * Expected: Flags field is empty and nonFlags field is non empty.
     */
    @Test
    void testParseWhenNoIllegalFlagAndNonEmptyArgListWithNonFlagOnly() throws InvalidArgsException {
        String[] argsList = {VALID_NON_FLAG_1};
        argsParser.parse(argsList);
        assertTrue(argsParser.flags.isEmpty());
        assertFalse(argsParser.nonFlagArgs.isEmpty());
    }

    /**
     * Tests parse method when there no illegal flag and non empty arg list with flags only.
     * Expected: Flags field is empty and nonFlags field is non empty.
     */
    @Test
    void testParseWhenNoIllegalFlagAndNonEmptyArgListWithFlagOnly() throws InvalidArgsException {
        String[] argsList = {CHAR_FLAG_PREFIX + VALID_FLAG_1.toString()};
        argsParser.parse(argsList);
        assertFalse(argsParser.flags.isEmpty());
        assertTrue(argsParser.nonFlagArgs.isEmpty());
    }
}