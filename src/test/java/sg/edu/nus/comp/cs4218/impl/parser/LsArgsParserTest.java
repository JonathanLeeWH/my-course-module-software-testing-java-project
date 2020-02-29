package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LsArgsParserTest {

    private LsArgsParser lsArgsParser;
    private final static char FLAG_IS_RECURSIVE = 'R';
    private final static char FLAG_IS_FOLDERS = 'd';

    @BeforeEach
    public void setUp() {
        lsArgsParser = new LsArgsParser();
    }

    @Test
    void isNoArgsReturnTrue() throws InvalidArgsException {
        List<String> expected = new ArrayList<String>();
        lsArgsParser.parse("");
        assertEquals(expected.toString(), lsArgsParser.getDirectories().toString());
    }

    @Test
    void isOneArgsReturnTrue() throws InvalidArgsException {
        List<String> expected = new ArrayList<String>();
        expected.add("folder");
        lsArgsParser.parse("folder");
        assertEquals(expected.toString(), lsArgsParser.getDirectories().toString());
    }

    @Test
    void isMultipleArgsReturnTrue() throws InvalidArgsException {
        List<String> expected = new ArrayList<String>();
        expected.add("folder");
        expected.add("folder1");
        expected.add("folder2");
        lsArgsParser.parse("folder","folder1","folder2");
        assertEquals(expected.toString(), lsArgsParser.getDirectories().toString());
    }

    @Test
    void isRFlagReturnTrue() throws InvalidArgsException {
        lsArgsParser.parse("-R");
        assertTrue(lsArgsParser.isRecursive());
    }

    @Test
    void isNotRFlagReturnTrue() throws InvalidArgsException {
        lsArgsParser.parse("");
        assertTrue(!lsArgsParser.isRecursive());
    }

    @Test
    void isDFlagReturnTrue() throws InvalidArgsException {
        lsArgsParser.parse("-d");
        assertTrue(lsArgsParser.isFoldersOnly());
    }

    @Test
    void isNotDFlagReturnTrue() throws InvalidArgsException {
        lsArgsParser.parse("");
        assertTrue(!lsArgsParser.isFoldersOnly());
    }

}
