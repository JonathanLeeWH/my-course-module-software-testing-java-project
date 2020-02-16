package test.sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.app.CutApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class CutApplicationTest {
    private CutApplication cutApplication;
    private String[] defaultCutArgs;
    private InputStream stdin;
    private OutputStream outputStream;

    @BeforeEach
    public void setUp() {
        cutApplication = new CutApplication();
        stdin = System.in;
        defaultCutArgs = Arrays.asList("-c","8").toArray(new String[1]);
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test cut application with run().
     */

    @Test
    void testRunNullArgs() {
       Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(null, stdin, outputStream));
       assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_NULL_ARGS);
    }

    @Test
    void testRunNullOutputStream() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(defaultCutArgs, stdin, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_NO_OSTREAM);
    }

    @Test
    void testRunSuccess() throws CutException {
        //Use a sample test file.
        cutApplication.run(Arrays.asList("-c", "6", "README.md").toArray(new String[3]), stdin, outputStream);
        assertEquals("2\na\nr\n", outputStream.toString());
    }

    /**
     * Test cut application using cutFromFiles()
     */
    // Erronorous Test cases (13 cases)
    @Test
    void testCutFromFilesWithoutAnyPosFlags() { }

    @Test
    void testCutFromFilesUsingByteAndCharPos() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndASingleFileWithStartNumLessThanZero() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndASingleFileWithEndNumLessThanZero() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndASingleFileWithStartNumIsNotAInteger() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndASingleFileWithEndNumIsNotAInteger() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndASingleFileWithNumLessThanZero() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndASingleFileWithNumIsNotAInteger() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndASingleFileWithFileNotFoundInDir() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndMultipleFilesWithAtLeastOneFileNotFoundInDir() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndASingleFileWithInvalidFilename() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndMultipleFilesWithAtLeastOneInvalidFilename() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndNullFile() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(false, false, false, 1, 2, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_GENERAL);
    }

    // Single Test cases (1 case)
    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndEmptyFilenameString() { }

    // Positive test cases (50 cases)
    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumUnsortedAndValidSingleFileWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSingleFile() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumUnsortedAndValidSingleFileWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSingleFile() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumUnsortedAndValidDistinctFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidDistinctFiles() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumUnsortedAndValidDistinctFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidDistinctFiles() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumUnsortedAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndFileIsDash() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumUnsortedAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndFileIsDash() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumUnsortedAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSimilarFiles() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumUnsortedAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSimilarFiles() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumUnsortedAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndHavingDashBetweenMultipleFiles() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumUnsortedAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndHavingDashBetweenMultipleFiles() { }


    /**
     * Test cut application using cutFromStdin()
     */
    // Erronorous Test cases (9 cases)
    @Test
    void testCutFromStdinWithoutAnyPosFlags() { }

    @Test
    void testCutFromStdinUsingByteAndCharPos() { }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLessThanZero() { }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumLessThanZero() { }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumIsNotAInteger() { }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumIsNotAInteger() { }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithNumLessThanZero() { }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithNumIsNotAInteger() { }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndNullInputStream() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromStdin(false, false, false, 1, 2, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_NULL_STREAMS);
    }

    // Single Test cases (1 case)
    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithAnEmptyInputStream() { }

    // Positive Test cases (10 cases)
    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumUnsortedAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStream() { }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumUnsortedAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAndSingleNumAndValidInputStream() { }
}