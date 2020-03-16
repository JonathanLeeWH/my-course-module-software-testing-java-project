package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.parser.ArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

public class WcArgumentsTest {
    private WcArguments wcArguments;
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");

    @BeforeEach
    public void setUp() {
        wcArguments = new WcArguments();
    }

    // Error test cases
    @Test
    public void testParseWithNullArgsShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcArguments.parse(null));
        assertEquals(thrown.getMessage(), ERR_NULL_ARGS);
    }

    @Test
    public void testParseWithInvalidArgsShouldThrowSortException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcArguments.parse("-x"));
        assertEquals(thrown.getMessage(), ERR_INVALID_FLAG);
    }

    // Positive test cases
    @Test
    public void testParseWithNoArgsShouldRunSuccessfully() throws Exception {
        wcArguments.parse();
        // For WcApplication, it is okay to have no flag args declared. Shell will
        // presume that you want -clw.
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithEmptyArgShouldRunSuccessfully() throws Exception {
        wcArguments.parse("");
        // For WcApplication, it is okay to have no flag args declared. Shell will
        // presume that you want -clw.
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }


    @Test
    public void testParseWithIsBytesOnlyShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c");
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsLinesOnlyShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-l");
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsOnlyShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-w");
        assertFalse(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithNoArgsAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse(testFile1.toFile().getPath(), testFile2.toFile().getPath());
        // For WcApplication, it is okay to have no flag args declared. Shell will
        // presume that you want -clw.
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile1.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }
}
