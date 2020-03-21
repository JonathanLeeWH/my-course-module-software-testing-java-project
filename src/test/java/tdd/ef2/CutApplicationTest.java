package tdd.ef2;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.app.CutApplication;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class CutApplicationTest {

    private static final String ERR_OUT_RANGE = "out of range error";
    private static final String ERR_INVALID_RANGE = "invalid range error";
    private static String folderName = "src/test/java/tdd/util/dummyTestFolder/CutTestFolder";
    private static String fileNameTest = "test.txt";
    private static String fileNameNames = "course.txt";
    private static String subDirName = "subDir";
    private static String fileNameNotExist = "notExist.txt";
    private static String fileNameEmpty1 = "empty1.txt";
    private static String cutPrefix = "cut: ";
    private final CutInterface app = new CutApplication();
    private OutputStream outputStream = null;

    @Test
    void testCutTwoCharactersFromFile() {
        String expectResult = "Ts";
        assertDoesNotThrow(() -> {
            String realResult = app.cutFromFiles(true, false, false, 1, 8, folderName + CHAR_FILE_SEP + fileNameTest);
            assertEquals(expectResult, realResult);
        });
    }

    /**
     * Original expectedResult is sT.
     * New expectedResult is Ts.
     * Reason: As stated in assumption file, our program will retrieve characters based on
     * the position inside the file or InputStream regardless of the reverse ordering.
     */
    @Test
    void testCutTwoCharactersInReverseOrderFromFile() {
        String expectResult = "Ts";
        assertDoesNotThrow(() -> {
            String realResult = app.cutFromFiles(true, false, false, 8, 1, folderName + CHAR_FILE_SEP + fileNameTest);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testCutRangeOfCharactersFromFile() {
        String expectResult = "Today is";
        assertDoesNotThrow(() -> {
            String realResult = app.cutFromFiles(true, false, true, 1, 8, folderName + CHAR_FILE_SEP + fileNameTest);
            assertEquals(expectResult, realResult);
        });
    }

    /**
     * This test cases has been disabled as no index can be less than or equal to 0. (Stated in assumption)
     * It should throw exception.
     */
    @Test
    @Disabled
    void testCutSingleCharactersFromFile() {
        String expectResult = "s";
        assertDoesNotThrow(() -> {
            String realResult = app.cutFromFiles(true, false, false, 8, 0, folderName + CHAR_FILE_SEP + fileNameTest);
            assertEquals(expectResult, realResult);
        });
    }

    /**
     * This test cases has been disabled as no index can be less than or equal to 0. (Stated in assumption)
     * It should throw exception.
     */
    @Test
    @Disabled
    void testCutSingleBytesFromStdin() {
        String original = "bad";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "a";
        assertDoesNotThrow(() -> {
            String realResult = app.cutFromStdin(false, true, false, 2, 0, stdin);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testCutTwoBytesFromStdin() {
        String original = "baz";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "az";
        assertDoesNotThrow(() -> {
            String realResult = app.cutFromStdin(false, true, false, 2, 3, stdin);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testCutRangeOfBytesFromStdin() {
        String original = "bazzzz";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "azzzz";
        assertDoesNotThrow(() -> {
            String realResult = app.cutFromStdin(false, true, true, 2, 6, stdin);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testRunWithOneFile() {
        String expectResult = "Ts" + STRING_NEWLINE;
        String[] args = {"-c", "1,8", folderName + CHAR_FILE_SEP + fileNameTest};
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, System.in, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithTwoFile() {
        String expectResult = "Today is" + STRING_NEWLINE + "Cristina" + STRING_NEWLINE + "Software" + STRING_NEWLINE;
        String[] args = {"-c", "1-8", folderName + CHAR_FILE_SEP + fileNameTest, folderName + CHAR_FILE_SEP + fileNameNames};
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, System.in, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithFilesAndStdin() {
        String original = "bazzz";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "od" + STRING_NEWLINE + "az" + STRING_NEWLINE;
        String[] args = {"-c", "2-3", folderName + CHAR_FILE_SEP + fileNameTest, "-"};
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, stdin, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithoutSpecifiedFile() {
        String original = "baz";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "az" + STRING_NEWLINE;
        String[] args = {"-c", "2-3"};
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, stdin, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithSingleIndex() {
        String expectResult = "o" + STRING_NEWLINE;
        String[] args = {"-c", "2", folderName + CHAR_FILE_SEP + fileNameTest};
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, System.in, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithClosedOutputStream() {
        String[] args = {"-b", "1-8", folderName + CHAR_FILE_SEP + fileNameTest};
        Throwable thrown = assertThrows(CutException.class, () -> {
            outputStream = new FileOutputStream(new File(folderName + CHAR_FILE_SEP + fileNameEmpty1));
            IOUtils.closeOutputStream(outputStream);
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), cutPrefix + ERR_WRITE_STREAM);
    }

    /**
     * This test case is disabled.
     * Reason: As stated in assumption, our program will take care of index that are out of range and
     * retrieve the last character position in on a particular line.
     */
    @Test
    @Disabled
    void testRunCharacterIndexOutOfRange() {
        String original = "baz";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String[] args = {"-c", "1-8", "-"};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(CutException.class, () -> {
            app.run(args, stdin, outputStream);
        });
        assertEquals(thrown.getMessage(), cutPrefix + ERR_OUT_RANGE);
    }

    /**
     * This test case is disabled.
     * Reason: As stated in assumption, our program will take care of index that are out of range and
     * retrieve the last character position in on a particular line.
     */
    @Test
    @Disabled
    void testRunByteIndexOutOfRange() {
        String[] args = {"-b", "1-20", folderName + CHAR_FILE_SEP + fileNameTest};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(CutException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), cutPrefix + ERR_OUT_RANGE);
    }

    /**
     * I remove the prefix cut in the actual message and change to throw exception instead of CutException
     * Reason: In our implementation, any exception throw in cutFromStdin
     * will throw Exception as the run method will then throw again with cut exception.
     */
    @Test
    void testCutWithNullIStream() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.cutFromStdin(false, false, false, 1, 8, null);
        });
        assertEquals(thrown.getMessage(), ERR_NULL_STREAMS);
    }

    /**
     * I remove the prefix cut in the actual message and change to throw exception instead of CutException
     * Reason: In our implementation, any exception throw in cutFromFiles
     * will throw Exception as the run method will then throw again with cut exception.
     */
    @Test
    void testCutWithNotExistFileName() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.cutFromFiles(false, false, false, 1, 8, folderName + CHAR_FILE_SEP + fileNameNotExist);
        });
        assertEquals(thrown.getMessage(), ERR_FILE_NOT_FOUND);
    }

    /**
     * I remove the prefix cut in the actual message and change to throw exception instead of CutException
     * Reason: In our implementation, any exception throw in cutFromFiles
     * will throw Exception as the run method will then throw again with cut exception.
     */
    @Test
    void testCutWithNullFileName() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.cutFromFiles(false, false, false, 1, 8, null);
        });
        assertEquals(thrown.getMessage(), ERR_NULL_ARGS);
    }

    /**
     * I remove the prefix cut in the actual message.
     * Reason: In our implementation, any exception throw in cutFromFiles
     * will throw Exception as the run method will then throw again with cut exception.
     */
    @Test
    void testCutWithDirectoryName() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.cutFromFiles(false, false, false, 1, 8, folderName + CHAR_FILE_SEP + subDirName);
        });
        assertEquals(thrown.getMessage(), ERR_IS_DIR);
    }

    @Test
    void testRunWithNullArg() {
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(CutException.class, () -> {
            app.run(null, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), cutPrefix + ERR_NULL_ARGS);
    }

    /**
     * I change the actual message to ERR_NO_OSTREAM instead of ERR_NULL_STREAM.
     * Reason: It is more ideal to provide a more friendly error message to indicate
     * that no output stream has been provided rather than the general null pointer excpetion.
     */
    @Test
    void testRunWithNullOStream() {
        String[] args = {folderName + CHAR_FILE_SEP + fileNameTest};
        Throwable thrown = assertThrows(CutException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), cutPrefix + ERR_NO_OSTREAM);
    }

    /**
     * I change the exception message to use ERR_MISSING_ARG instead of ERR_NO_ARGS
     * Reason: To provide a more friendly exception message for users.
     */
    @Test
    void testRunWithLessThanTwoArgs() {
        String[] args = {"-b"};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(CutException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), cutPrefix + ERR_MISSING_ARG);
    }

    /**
     * I change the type of xception message to use illegal flag message.
     * Reason: This is to allow user to provide more details on what flag arguments
     * is considered to be illegal.
     */
    @Test
    void testRunWithInvalidFlag() {
        String[] args = {"-p", "8-2"};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(CutException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), cutPrefix + ILLEGAL_FLAG_MSG + "p");
    }

    /**
     * This test case has been disabled.
     * Reason: As stated in assumption, our program is able to handle range in which
     * the starting index is higher than the ending index.
     */
    @Test
    @Disabled
    void testRunWithInvalidRange() {
        String[] args = {"-c", "8-2"};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(CutException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), cutPrefix + ERR_INVALID_RANGE);
    }

    /**
     * I use a different error message such as ERR_LESS_THAN_ZERO than ERR_OUT_RANGE_ERROR
     * Reason: To provide a more friendly exception message.
     */
    @Test
    void testRunWithInvalidNumber() {
        String[] args = {"-c", "0", folderName + CHAR_FILE_SEP + fileNameTest};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(CutException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), cutPrefix + ERR_LESS_THAN_ZERO);
    }
}