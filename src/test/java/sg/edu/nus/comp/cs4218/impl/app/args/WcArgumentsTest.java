package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.parser.ArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

public class WcArgumentsTest {
    private WcArguments wcArguments;
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

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
    public void testParseWithInvalidArgsShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcArguments.parse("-x"));
        assertEquals(thrown.getMessage(), ERR_INVALID_FLAG);
    }

    // Single test cases
    @Test
    public void testParseWithNonFlagArgsFirstShouldRunSuccessfully() throws Exception {
        wcArguments.parse(testFile2.toFile().getPath(), "-cl");
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile2.toFile().getPath(), "-cl").toArray(new String[2]));
    }

    // Positive test cases
    @Test
    public void testParseWithNoFlagArgsAndNoFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse();
        // For WcApplication, it is okay to have no flag args declared. Shell will
        // presume that you want -clw.
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithNoFlagArgsAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse(testFile1.toFile().getPath());
        // For WcApplication, it is okay to have no flag args declared. Shell will
        // presume that you want -clw.
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile1.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithNoFlagArgsAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse(testFile1.toFile().getPath(), testFile2.toFile().getPath());
        // For WcApplication, it is okay to have no flag args declared. Shell will
        // presume that you want -clw.
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile1.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsBytesOnlyAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c");
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsBytesOnlyAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", testFile2.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsBytesOnlyAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", testFile2.toFile().getPath(), testFile3.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile2.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsLinesOnlyAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-l");
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsLinesOnlyAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-l", testFile3.toFile().getPath());
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsLinesOnlyAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-l", testFile3.toFile().getPath(), testFile1.toFile().getPath());
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile3.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsWordsOnlyAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-w");
        assertFalse(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsOnlyAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-w", testFile1.toFile().getPath());
        assertFalse(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile1.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsWordsOnlyAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-w", testFile1.toFile().getPath(), testFile2.toFile().getPath());
        assertFalse(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile1.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsBytesAndIsLinesSeparatedAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-l");
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsBytesAndIsLinesSeparatedAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-l", testFile1.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile1.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsBytesAndIsLinesSeparatedAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-l", testFile1.toFile().getPath(), testFile2.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile1.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsWordsAndIsLinesSeparatedAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-l", "-w");
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsAndIsLinesSeparatedAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-l", "-w", testFile2.toFile().getPath());
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsWordsAndIsLinesSeparatedAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-l", "-w", testFile2.toFile().getPath(), testFile1.toFile().getPath());
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile2.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesSeparatedAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-w");
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsAndIsBytesSeparatedAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-w", testFile3.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesSeparatedAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-w", testFile1.toFile().getPath(), testFile3.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile1.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsBytesAndIsLinesTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cl");
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsBytesAndIsLinesTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cl", testFile2.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsBytesAndIsLinesTogetherAndValidDistinctFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cl", testFile3.toFile().getPath(), testFile2.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile3.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsWordsAndIsLinesTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-lw");
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsAndIsLinesTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-lw", testFile1.toFile().getPath());
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile1.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsWordsAndIsLinesTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-lw", testFile2.toFile().getPath(), testFile1.toFile().getPath());
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile2.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cw");
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsAndIsBytesTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cw", testFile2.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cw", testFile2.toFile().getPath(), testFile3.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile2.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-l", "-w");
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-l", "-w", testFile3.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-l", "-w", testFile3.toFile().getPath(), testFile2.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile3.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedWithIsBytesAndIsLinesTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cl", "-w");
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedWithIsBytesAndIsLinesTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cl", "-w", testFile2.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedWithIsBytesAndIsLinesTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cl", "-w", testFile2.toFile().getPath(), testFile3.toFile().getPath(), testFile1.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[3]),
                Arrays.asList(testFile2.toFile().getPath(), testFile3.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[3]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedWithIsWordsAndIsLinesTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-lw");
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedWithIsWordsAndIsLinesTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-lw", testFile1.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile1.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedWithIsWordsAndIsLinesTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-c", "-lw", testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[3]),
                Arrays.asList(testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[3]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedWithIsWordsAndIsBytesTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cw", "-l");
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedWithIsWordsAndIsBytesTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cw", "-l", testFile3.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }


    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesSeparatedWithIsWordsAndIsBytesTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-cw", "-l", testFile3.toFile().getPath(), testFile2.toFile().getPath(), testFile1.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[3]),
                Arrays.asList(testFile3.toFile().getPath(), testFile2.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[3]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesTogetherAndNoFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-clw");
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.getFiles().isEmpty());
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesTogetherAndValidSingleFileShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-clw", testFile2.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[1]),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseWithIsWordsAndIsBytesAndIsLinesTogetherAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        wcArguments.parse("-clw", testFile3.toFile().getPath(), testFile1.toFile().getPath());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertTrue(wcArguments.isWords());
        assertArrayEquals(wcArguments.getFiles().toArray(new String[2]),
                Arrays.asList(testFile3.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }
}
