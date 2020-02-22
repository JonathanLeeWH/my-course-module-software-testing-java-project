package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.EchoException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class EchoApplicationTest {

    private static final String TEST_STRING_1 = "hello1";
    private static final String TEST_STRING_2 = "hello2";
    private static final String TEST_STRING_3 = "A B C";
    private static final String TEST_STRING_4 = "A*B*C";
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
            echoApplication.run(new String[]{}, mock(InputStream.class), null);
        });

        assertEquals(new EchoException(ERR_NO_OSTREAM).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input args is null.
     * Expected: Throws EchoException ERR_NULL_ARGS
     */
    @Test
    void runWhenInputArgsIsNullShouldThrowEchoException() {
        Exception exception = assertThrows(EchoException.class, () -> {
            echoApplication.run(null, mock(InputStream.class), outputStream);
        });

        assertEquals(new EchoException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input args is empty.
     * For example: echo
     * Expected: Outputstream should contain a new line (STRING_NEWLINE)
     */
    @Test
    void runWhenInputArgsIsEmptyShouldWriteNewLine() throws EchoException {
        String[] inputArgs = {};

        echoApplication.run(inputArgs, mock(InputStream.class), outputStream);

        assertEquals(STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests run method when input args contain only one element.
     * For example: echo hello1
     * Expected: Outputstream should contain the element terminated by a new line.
     */
    @Test
    void runWhenInputArgsContainsOneElementShouldWriteTheElementWithNewLine() throws EchoException {
        String[] inputArgs = {TEST_STRING_1};
        String expected = TEST_STRING_1 + STRING_NEWLINE;

        echoApplication.run(inputArgs, mock(InputStream.class), outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests run method when input args contain only two elements.
     * For example: echo hello1 hello2
     * Expected: Outputstream should contain the two elements separated by space and terminated by a new line.
     */
    @Test
    void runWhenInputArgsContainsTwoElementsShouldWriteTheTwoElementsSepBySpaceWithNewLine() throws EchoException {
        String[] inputArgs = {TEST_STRING_1, TEST_STRING_2};
        String expected = TEST_STRING_1 + WHITE_SPACE + TEST_STRING_2 + STRING_NEWLINE;

        echoApplication.run(inputArgs, mock(InputStream.class), outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests run method when input args contain only one element with the content of the element separated by space.
     * For example: echo A B C
     * Expected: Outputstream should write A B C terminated by a new line.
     */
    @Test
    void runWhenInputArgsContainsOneElementContentSepBySpaceShouldWriteTheElementContentSepBySpaceWithNewLine() throws EchoException {
        String[] inputArgs = {TEST_STRING_3};
        String expected = TEST_STRING_3 + STRING_NEWLINE;

        echoApplication.run(inputArgs, mock(InputStream.class), outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests run method when input args contain only one element with the content of the element separated by *.
     * For example: echo A*B*C
     * Expected: Outputstream should write A*B*C terminated by a new line.
     */
    @Test
    void runWhenInputArgsContainsOneElementContentSepByAsteriskShouldWriteTheElementContentSepByAsteriskWithNewLine() throws EchoException {
        String[] inputArgs = {TEST_STRING_4};
        String expected = TEST_STRING_4 + STRING_NEWLINE;

        echoApplication.run(inputArgs, mock(InputStream.class), outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests run method when IOException occurs.
     * Expected: Throws EchoException with ERR_IO_EXCEPTION
     */
    @Test
    void runWhenIOExceptionOccursShouldThrowEchoException() throws IOException {
        String[] inputArgs = {};
        try (OutputStream mockOutputStream = mock(OutputStream.class)) {
            doThrow(IOException.class).when(mockOutputStream).write(any(byte[].class));
            EchoException exception = assertThrows(EchoException.class, () -> {
                echoApplication.run(inputArgs, mock(InputStream.class), mockOutputStream);
            });
            assertEquals(new EchoException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
        }
    }
}