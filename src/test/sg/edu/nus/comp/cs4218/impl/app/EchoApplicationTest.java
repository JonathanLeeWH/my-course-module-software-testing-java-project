package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.EchoException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class EchoApplicationTest {

    private static final String TEST_STRING_1 = "hello1";
    private static final String TEST_STRING_2 = "hello2";
    private static final String WHITE_SPACE = " ";

    private EchoApplication echoApplication;
    private OutputStream outputStream;

    @BeforeEach
    void setUp() {
        echoApplication = new EchoApplication();
        outputStream = new ByteArrayOutputStream();
    }

    /**
     * Tests constructResult method when input String Array args is null.
     * Expected: Throws EchoException with ERR_NULL_ARGS
     */
    @Test
    void constructResultWhenInputArgsIsNullThrowsEchoException() {
        Exception exception = assertThrows(EchoException.class, () -> {
            echoApplication.constructResult(null);
        });

        assertEquals(new EchoException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests constructResult method when input String Array args is empty.
     * Expected: A String containing a new line (STRING_NEWLINE)
     */
    @Test
    void constructResultWhenInputArgsIsEmptyShouldReturnStringWithNewLineOnly() throws EchoException {
        String[] inputArgs = {};

        assertEquals(STRING_NEWLINE, echoApplication.constructResult(inputArgs));
    }

    /**
     * Tests constructResult method when input String Array args has only one element.
     * Expected: Return a String with the element and terminates by a new line.
     */
    @Test
    void constructResultWhenInputArgsContainsOneElementShouldReturnTheElementTerminatedByANewLine() throws EchoException {
        String[] inputArgs = {TEST_STRING_1};
        String expected = TEST_STRING_1 + STRING_NEWLINE;

        assertEquals(expected, echoApplication.constructResult(inputArgs));
    }

    /**
     * Tests constructResult method when input String Array args has two elements.
     * Expected: Return a String with the two elements separated by spaces and terminates by a new line.
     */
    @Test
    void constructResultWhenInputArgsContainsTwoElementsShouldReturnTheTwoElementSepBySpaceTerminatedByANewLine() throws EchoException {
        String[] inputArgs = {TEST_STRING_1, TEST_STRING_2};
        String expected = TEST_STRING_1 + WHITE_SPACE + TEST_STRING_2 + STRING_NEWLINE;

        assertEquals(expected, echoApplication.constructResult(inputArgs));
    }

    /**
     * Tests run method when no outputstream is null.
     * In this case, we can also set inputstream to System.in as it is not used and for simplicity.
     * Expected: Throws EchoException with ERR_NO_OSTREAM
     */
    @Test
    void runWhenInputOutputStreamIsNullShouldThrowEchoException() {
        Exception exception = assertThrows(EchoException.class, () -> {
            echoApplication.run(new String[]{}, System.in, null);
        });

        assertEquals(new EchoException(ERR_NO_OSTREAM).getMessage(), exception.getMessage());
    }
}