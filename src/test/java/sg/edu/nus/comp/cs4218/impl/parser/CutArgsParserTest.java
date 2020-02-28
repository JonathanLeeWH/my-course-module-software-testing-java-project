package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class CutArgsParserTest {
    private CutArgsParser cutArgsParser;
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");

    @BeforeEach
    public void setUp() {
        cutArgsParser = new CutArgsParser();
    }

    // Positive test cases
    @Test
    public void testParseUsingIsCharPosAnd2CommaSeparatedNumWithValidNumsShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-c", "1,2", testFile1.toFile().getPath());
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
    }

    @Test
    public void testParseUsingIsBytePosAnd2CommaSeparatedNumWithValidNumsShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-b", "5,8", testFile1.toFile().getPath());
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
    }

    @Test
    public void testParseUsingIsRangeAnd2CommaSeparatedNumWithValidNumsShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-b", "11,12", testFile1.toFile().getPath());
        assertFalse(cutArgsParser.isRange());
    }

    @Test
    public void testParseUsingIsRangeAndNumRangeWithValidNumsShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-b", "1-4", testFile1.toFile().getPath());
        assertTrue(cutArgsParser.isRange());
    }

    @Test
    public void testParseUsingIsRangeAndSingleNumWithValidNumShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-b", "1", testFile1.toFile().getPath());
        assertFalse(cutArgsParser.isRange());
    }

    @Test
    public void testParseUsingGetPositionsAnd2CommaSeparatedNumWithValidNumShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-b", "1,5", testFile1.toFile().getPath());
        assertEquals(1, cutArgsParser.getPositions().getKey());
        assertEquals(5, cutArgsParser.getPositions().getValue());
    }

    @Test
    public void testParseUsingGetPositionsAndNumRangeWithValidNumShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-b", "7-12", testFile1.toFile().getPath());
        assertEquals(7, cutArgsParser.getPositions().getKey());
        assertEquals(12, cutArgsParser.getPositions().getValue());
    }

    @Test
    public void testParseUsingGetPositionsAndSingleNumWithValidNumShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-b", "4", testFile1.toFile().getPath());
        assertEquals(4, cutArgsParser.getPositions().getKey());
        assertEquals(4, cutArgsParser.getPositions().getValue());
    }

    @Test
    public void testParseUsingGetFileNamesAndSingleNumWithValidNumAndNoFileShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-b", "4");
        assertNull(cutArgsParser.getFileNames());
    }

    @Test
    public void testParseUsingGetFileNamesAndSingleNumWithValidNumAndSingleFileShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-b", "4", testFile1.toFile().getPath());
        assertArrayEquals(new String[]{testFile1.toFile().getPath()}, cutArgsParser.getFileNames());
    }

    @Test
    public void testParseUsingGetFileNamesAndSingleNumWithValidNumAndMultipleFilesShouldRunSuccessfully() throws InvalidArgsException {
        cutArgsParser.parse("-b", "4", testFile1.toFile().getPath(), testFile1.toFile().getPath());
        assertArrayEquals(new String[]{testFile1.toFile().getPath(), testFile1.toFile().getPath()}, cutArgsParser.getFileNames());
    }
}
