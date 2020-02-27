package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.app.CutApplication;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_LESS_THAN_ZERO;

public class CutArgsParserTest {
    private CutArgsParser cutArgsParser;
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");

    @BeforeEach
    public void setUp() {
        cutArgsParser = new CutArgsParser();
    }

    // Positive test cases
    @Test
    public void testParseUsingIsCharPosAnd2CommaSeparatedNumWithValidNums() throws InvalidArgsException {
        cutArgsParser.parse("-c", "1,2", testFile1.toFile().getPath());
        assertFalse(cutArgsParser.isBytePos());
        assertTrue(cutArgsParser.isCharPos());
    }

    @Test
    public void testParseUsingIsBytePosAnd2CommaSeparatedNumWithValidNums() throws InvalidArgsException {
        cutArgsParser.parse("-b", "1,2", testFile1.toFile().getPath());
        assertTrue(cutArgsParser.isBytePos());
        assertFalse(cutArgsParser.isCharPos());
    }

    @Test
    public void testParseUsingIsRangeAnd2CommaSeparatedNumWithValidNums() throws InvalidArgsException {
        cutArgsParser.parse("-b", "1,2", testFile1.toFile().getPath());
        assertFalse(cutArgsParser.isRange());
    }

    @Test
    public void testParseUsingIsRangeAndNumRangeWithValidNums() throws InvalidArgsException {
        cutArgsParser.parse("-b", "1-2", testFile1.toFile().getPath());
        assertTrue(cutArgsParser.isRange());
    }

    @Test
    public void testParseUsingIsRangeAndSingleNumWithValidNum() throws InvalidArgsException {
        cutArgsParser.parse("-b", "1", testFile1.toFile().getPath());
        assertFalse(cutArgsParser.isRange());
    }

    @Test
    public void testParseUsingGetPositionsAnd2CommaSeparatedNumWithValidNum() throws InvalidArgsException {
        cutArgsParser.parse("-b", "1,2", testFile1.toFile().getPath());
        assertEquals(1, cutArgsParser.getPositions().getKey());
        assertEquals(2, cutArgsParser.getPositions().getValue());
    }

    @Test
    public void testParseUsingGetPositionsAndNumRangeWithValidNum() throws InvalidArgsException {
        cutArgsParser.parse("-b", "2-12", testFile1.toFile().getPath());
        assertEquals(2, cutArgsParser.getPositions().getKey());
        assertEquals(12, cutArgsParser.getPositions().getValue());
    }

    @Test
    public void testParseUsingGetPositionsAndSingleNumWithValidNum() throws InvalidArgsException {
        cutArgsParser.parse("-b", "4", testFile1.toFile().getPath());
        assertEquals(4, cutArgsParser.getPositions().getKey());
        assertEquals(4, cutArgsParser.getPositions().getValue());
    }

    @Test
    public void testParseUsingGetFileNamesAndSingleNumWithValidNumAndNoFile() throws InvalidArgsException {
        cutArgsParser.parse("-b", "4");
        assertNull(cutArgsParser.getFileNames());
    }

    @Test
    public void testParseUsingGetFileNamesAndSingleNumWithValidNumAndSingleFile() throws InvalidArgsException {
        cutArgsParser.parse("-b", "4", testFile1.toFile().getPath());
        assertArrayEquals(new String[]{testFile1.toFile().getPath()}, cutArgsParser.getFileNames());
    }

    @Test
    public void testParseUsingGetFileNamesAndSingleNumWithValidNumAndMultipleFiles() throws InvalidArgsException {
        cutArgsParser.parse("-b", "4", testFile1.toFile().getPath(), testFile1.toFile().getPath());
        assertArrayEquals(new String[]{testFile1.toFile().getPath(), testFile1.toFile().getPath()}, cutArgsParser.getFileNames());
    }
}
