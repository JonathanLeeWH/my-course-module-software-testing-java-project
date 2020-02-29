package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.app.args.SortArguments.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

public class SortArgumentsTest {
    private SortArguments sortArguments;
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");

    @BeforeEach
    public void setUp() {
        sortArguments = new SortArguments();
    }

    // Error test cases
    @Test
    public void testParseWithNullArgsShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> sortArguments.parse(null));
        assertEquals(thrown.getMessage(), ERR_NULL_ARGS);
    }

    @Test
    public void testParseWithInvalidArgsShouldThrowSortException() {
        Throwable thrown = assertThrows(Exception.class, () -> sortArguments.parse("-x"));
        assertEquals(thrown.getMessage(), ILLEGAL_FLAG_MSG + "x");
    }

    // Positive test cases
    @Test
    public void testParseWithNoArgsShouldRunSuccessfully() throws Exception {
        sortArguments.parse();
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithEmptyArgShouldRunSuccessfully() throws Exception {
        sortArguments.parse("");
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }


    @Test
    public void testParseWithFirstWordNumberOnlyShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n");
        assertTrue(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
    }

    @Test
    public void testParseWithReverseOrderOnlyShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-r");
        assertFalse(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
    }

    @Test
    public void testParseWithCaseIndependentOnlyShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-f");
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
    }

    @Test
    public void testParseWithNoArgsAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse(testFile1.toFile().getPath(), testFile2.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile1.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }
}
