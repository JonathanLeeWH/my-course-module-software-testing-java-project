package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class WcApplicationIT {
    private WcApplication wcApplication;
    private String[] defaultWcArgs;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "11"+  System.lineSeparator() +
            "1 test 1 2" +  System.lineSeparator() + "5" + System.lineSeparator() + "+";
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    void setUp() {
        wcApplication = new WcApplication();
        defaultWcArgs = Collections.singletonList("-cw").toArray(new String[1]);
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
    void testWcApplicationAndWcArgumentWithNullOutputStreamShouldThrowWcException() {
        Throwable thrown = assertThrows(WcException.class, () -> wcApplication.run(defaultWcArgs, ourTestStdin, null));
        assertEquals(thrown.getMessage(), WcApplication.COMMAND + ": " + ERR_NULL_STREAMS);
    }

    // Positive test cases
    @Test
    void testWcApplicationAndWcArgumentWithMultipleFilesShouldRunSuccessfully() throws WcException {
        wcApplication.run(Arrays.asList("-c", testFile3.toFile().getPath()).toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = String.format(" %7d", 60) + " " + testFile3.toFile().getPath() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testWcApplicationAndWcArgumentWithNoFilesShouldRunSuccessfully() throws WcException {
        wcApplication.run(defaultWcArgs, ourTestStdin, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }
}
