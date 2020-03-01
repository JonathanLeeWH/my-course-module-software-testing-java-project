package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class SortApplicationIT {
    private SortApplication sortApplication;
    private String[] defaultSortArgs;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "11"+  System.lineSeparator() +
            "1 test 1 2" +  System.lineSeparator() + "5" + System.lineSeparator() + "+";
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    void setUp() {
        sortApplication = new SortApplication();
        defaultSortArgs = Collections.singletonList("-nr").toArray(new String[1]);
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    /**
     * Test cases with run().
     */
    // Error test cases
    @Test
    void testRunAndParseWithNullOutputStreamShouldThrowSortException() {
        Throwable thrown = assertThrows(SortException.class, () -> sortApplication.run(defaultSortArgs, ourTestStdin, null));
        assertEquals(thrown.getMessage(), SortApplication.COMMAND + ": " + ERR_NULL_STREAMS);
    }

    // Positive test cases
    @Test
    void testRunAndParseWithMultipleFilesShouldRunSuccessfully() throws SortException {
        sortApplication.run(Arrays.asList("-n", testFile3.toFile().getPath()).toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "001, 010" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() +
                "2, 3" + System.lineSeparator() + "21, 4" + System.lineSeparator() + "22, 41" + System.lineSeparator() +
                "51, 15" + System.lineSeparator() + "551, 1200" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testRunAndParseWithNoFilesShouldRunSuccessfully() throws SortException {
        sortApplication.run(defaultSortArgs, ourTestStdin, ourTestStdout);
        String expectedResult = "11" + System.lineSeparator() + "5" + System.lineSeparator() + "1 test 1 2" +
                System.lineSeparator() + "+" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }
}
