package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.app.args.SortArguments.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

public class SortArgumentsTest {
    private SortArguments sortArguments;
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

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
        assertEquals(thrown.getMessage(), ERR_INVALID_FLAG);
    }

    // Single test cases
    @Test
    public void testParseWithNonFlagArgsFirstShouldRunSuccessfully() throws Exception {
        sortArguments.parse(testFile2.toFile().getPath(), "-nf");
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile2.toFile().getPath(), "-nf").toArray(new String[2]));
    }

    // Positive test cases
    @Test
    public void testParseWithNoFlagArgsAndNoFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse();
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithNoFlagArgsAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse(testFile2.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }


    @Test
    public void testParseWithNoFlagArgsAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse(testFile2.toFile().getPath(), testFile1.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile2.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithFirstWordNumberArgsOnlyAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n");
        assertTrue(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithFirstWordNumberArgsOnlyAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n", testFile3.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithFirstWordNumberArgsOnlyAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n", testFile3.toFile().getPath(), testFile1.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile3.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithReverseOrderArgsOnlyAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-r");
        assertFalse(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithReverseOrderArgsOnlyAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-r", testFile2.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithReverseOrderArgsOnlyAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-r", testFile2.toFile().getPath(), testFile3.toFile().getPath(), testFile1.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[3]),
                Arrays.asList(testFile2.toFile().getPath(), testFile3.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[3]));
    }

    @Test
    public void testParseWithCaseIndependentArgsOnlyAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-f");
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithCaseIndependentArgsOnlyAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-f", testFile1.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile1.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithCaseIndependentArgsOnlyAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-f", testFile1.toFile().getPath(), testFile3.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile1.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderArgsSeparatedAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n", "-r");
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderArgsSeparatedAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n", "-r", testFile2.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderArgsSeparatedAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n", "-r", testFile2.toFile().getPath(), testFile1.toFile().getPath(), testFile3.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[3]),
                Arrays.asList(testFile2.toFile().getPath(), testFile1.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[3]));
    }

    @Test
    public void testParseWithFirstWordNumberAndCaseIndependentArgsSeparatedAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n", "-f");
        assertTrue(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithFirstWordNumberAndCaseIndependentArgsSeparatedAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n", "-f", testFile3.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithFirstWordNumberAndCaseIndependentArgsSeparatedAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n", "-f", testFile3.toFile().getPath(), testFile2.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile3.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithReverseOrderAndCaseIndependentArgsSeparatedAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-r", "-f");
        assertFalse(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithReverseOrderAndCaseIndependentArgsSeparatedAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-r", "-f", testFile2.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithReverseOrderAndCaseIndependentArgsSeparatedAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-r", "-f", testFile2.toFile().getPath(), testFile3.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile2.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderArgsTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-nr");
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderArgsTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-nr", testFile3.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderArgsTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-nr", testFile3.toFile().getPath(), testFile2.toFile().getPath(), testFile1.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertFalse(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[3]),
                Arrays.asList(testFile3.toFile().getPath(), testFile2.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[3]));
    }

    @Test
    public void testParseWithFirstWordNumberAndCaseIndependentArgsTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-nf");
        assertTrue(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithFirstWordNumberAndCaseIndependentArgsTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-nf", testFile1.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile1.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithFirstWordNumberAndCaseIndependentArgsTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-nf", testFile1.toFile().getPath(), testFile3.toFile().getPath(), testFile2.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertFalse(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());assertArrayEquals(sortArguments.getFiles().toArray(new String[3]),
                Arrays.asList(testFile1.toFile().getPath(), testFile3.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[3]));
    }

    @Test
    public void testParseWithReverseOrderAndCaseIndependentArgsTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-rf");
        assertFalse(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithReverseOrderAndCaseIndependentArgsTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-rf", testFile2.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithReverseOrderAndCaseIndependentArgsTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-rf", testFile2.toFile().getPath(), testFile1.toFile().getPath());
        assertFalse(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile2.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-n", "-f", "-r");
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-f", "-n", "-r", testFile3.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-r", "-n", "-f", testFile3.toFile().getPath(), testFile1.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile3.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedWithFirstWordNumberAndReverserOrderTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-nr", "-f");
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedWithFirstWordNumberAndReverserOrderTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-rn", "-f", testFile2.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedWithFirstWordNumberAndReverserOrderTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-nr", "-f", testFile3.toFile().getPath(), testFile2.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile3.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedWithFirstWordNumberAndCaseIndependentTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-nf", "-r");
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedWithFirstWordNumberAndCaseIndependentTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-fn", "-r", testFile2.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedWithFirstWordNumberAndCaseIndependentTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-fn", "-r", testFile1.toFile().getPath(), testFile2.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile1.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedWithReverseOrderAndCaseIndependentTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-rf", "-n");
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedWithReverseOrderAndCaseIndependentTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-fr", "-n", testFile2.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsSeparatedWithReverseOrderAndCaseIndependentTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-fr", "-n", testFile1.toFile().getPath(), testFile2.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile1.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-nfr");
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertTrue(sortArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-fnr", testFile3.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithFirstWordNumberAndReverseOrderAndCaseIndependentArgsTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        sortArguments.parse("-rnf", testFile3.toFile().getPath(), testFile1.toFile().getPath());
        assertTrue(sortArguments.isFirstWordNumber());
        assertTrue(sortArguments.isReverseOrder());
        assertTrue(sortArguments.isCaseIndependent());
        assertArrayEquals(sortArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile3.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }
}
