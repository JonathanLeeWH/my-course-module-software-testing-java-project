package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_MISSING_ARG;

public class CutArgsParserTest {
    private CutArgsParser cutArgsParser;
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    public void setUp() {
        cutArgsParser = new CutArgsParser();
    }

    // Error test cases
    @Test
    public void testParseUsingIsBytePosWithNoFlagArgsShouldThrowException() throws InvalidArgsException {
        cutArgsParser.parse("-b");
        Throwable thrown1 = assertThrows(Exception.class, () -> cutArgsParser.isRange());
        Throwable thrown2 = assertThrows(Exception.class, () -> cutArgsParser.getPositions());
        assertEquals(thrown1.getMessage(), ERR_MISSING_ARG);
        assertEquals(thrown2.getMessage(), ERR_MISSING_ARG);
    }

    // Positive test cases
    @Test
    public void testParseUsingIsBytePosAndRangeNumWithValidNumAndNoFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-b", "5-8");
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
        assertTrue(cutArgsParser.isRange());
        assertEquals(5, cutArgsParser.getPositions().getKey());
        assertEquals(8, cutArgsParser.getPositions().getValue());
        assertNull(cutArgsParser.getFileNames());
    }

    @Test
    public void testParseUsingIsBytePosAndValidSingleNumAndNoFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-b", "6");
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(6, cutArgsParser.getPositions().getKey());
        assertEquals(6, cutArgsParser.getPositions().getValue());
        assertNull(cutArgsParser.getFileNames());
    }


    @Test
    public void testParseUsingIsBytePosAndCommaSeparatedNumWithValidNumAndNoFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-b", "11,15");
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(11, cutArgsParser.getPositions().getKey());
        assertEquals(15, cutArgsParser.getPositions().getValue());
        assertNull(cutArgsParser.getFileNames());
    }

    @Test
    public void testParseUsingIsCharPosAndRangeNumWithValidNumAndNoFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-c", "3-4");
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
        assertTrue(cutArgsParser.isRange());
        assertEquals(3, cutArgsParser.getPositions().getKey());
        assertEquals(4, cutArgsParser.getPositions().getValue());
        assertNull(cutArgsParser.getFileNames());
    }

    @Test
    public void testParseUsingIsCharPosAndValidSingleNumAndNoFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-c", "16");
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(16, cutArgsParser.getPositions().getKey());
        assertEquals(16, cutArgsParser.getPositions().getValue());
        assertNull(cutArgsParser.getFileNames());
    }


    @Test
    public void testParseUsingIsCharPosAndCommaSeparatedNumWithValidNumAndNoFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-c", "15,11");
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(15, cutArgsParser.getPositions().getKey());
        assertEquals(11, cutArgsParser.getPositions().getValue());
        assertNull(cutArgsParser.getFileNames());
    }

    @Test
    public void testParseUsingIsBytePosAndRangeNumWithValidNumAndSingleFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-b", "9-21", testFile3.toFile().getPath());
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
        assertTrue(cutArgsParser.isRange());
        assertEquals(9, cutArgsParser.getPositions().getKey());
        assertEquals(21, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseUsingIsBytePosAndValidSingleNumAndSingleFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-b", "9", testFile3.toFile().getPath());
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(9, cutArgsParser.getPositions().getKey());
        assertEquals(9, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseUsingIsBytePosAnd2CommaSeparatedNumWithValidNumsAndSingleFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-b", "35,58", testFile2.toFile().getPath());
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(35, cutArgsParser.getPositions().getKey());
        assertEquals(58, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Collections.singletonList(testFile2.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseUsingIsCharPosAndRangeNumWithValidNumsAndSingleFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-c", "102-47", testFile3.toFile().getPath());
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
        assertTrue(cutArgsParser.isRange());
        assertEquals(102, cutArgsParser.getPositions().getKey());
        assertEquals(47, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseUsingIsCharPosAndValidSingleNumAndSingleFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-c", "102", testFile3.toFile().getPath());
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(102, cutArgsParser.getPositions().getKey());
        assertEquals(102, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseUsingIsCharPosAnd2CommaSeparatedNumWithValidNumsAndSingleFileShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-c", "1,2", testFile1.toFile().getPath());
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(1, cutArgsParser.getPositions().getKey());
        assertEquals(2, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Collections.singletonList(testFile1.toFile().getPath()).toArray(new String[1]));
    }

    @Test
    public void testParseUsingIsBytePosAndRangeNumWithValidNumAndMultipleFilesShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-b", "19-31", testFile2.toFile().getPath(), testFile3.toFile().getPath());
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
        assertTrue(cutArgsParser.isRange());
        assertEquals(19, cutArgsParser.getPositions().getKey());
        assertEquals(31, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Arrays.asList(testFile2.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseUsingIsBytePosAndValidSingleNumAndMultipleFilesShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-b", "92", testFile3.toFile().getPath(), testFile1.toFile().getPath());
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(92, cutArgsParser.getPositions().getKey());
        assertEquals(92, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Arrays.asList(testFile3.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseUsingIsBytePosAnd2CommaSeparatedNumWithValidNumsAndMultipleFilesShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-b", "235,458", testFile2.toFile().getPath(), testFile1.toFile().getPath(), testFile3.toFile().getPath());
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(235, cutArgsParser.getPositions().getKey());
        assertEquals(458, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Arrays.asList(testFile2.toFile().getPath(), testFile1.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[3]));
    }


    @Test
    public void testParseUsingIsCharPosAndRangeNumWithValidNumAndMultipleFilesShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-c", "19-1", testFile2.toFile().getPath(), testFile2.toFile().getPath());
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
        assertTrue(cutArgsParser.isRange());
        assertEquals(19, cutArgsParser.getPositions().getKey());
        assertEquals(1, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Arrays.asList(testFile2.toFile().getPath(), testFile2.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseUsingIsCharPosAndValidSingleNumAndMultipleFilesShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-c", "92", testFile3.toFile().getPath(), testFile3.toFile().getPath());
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(92, cutArgsParser.getPositions().getKey());
        assertEquals(92, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Arrays.asList(testFile3.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[2]));
    }

    @Test
    public void testParseUsingIsCharPosAnd2CommaSeparatedNumWithValidNumsAndMultipleFilesShouldRunSuccessfully() throws Exception {
        cutArgsParser.parse("-c", "25,35", testFile1.toFile().getPath(), testFile1.toFile().getPath());
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
        assertFalse(cutArgsParser.isRange());
        assertEquals(25, cutArgsParser.getPositions().getKey());
        assertEquals(35, cutArgsParser.getPositions().getValue());
        assertArrayEquals(cutArgsParser.getFileNames(),
                Arrays.asList(testFile1.toFile().getPath(), testFile1.toFile().getPath()).toArray(new String[2]));
    }
}
