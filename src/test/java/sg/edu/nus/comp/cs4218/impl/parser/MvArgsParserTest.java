package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MvArgsParserTest {

    private  static  MvArgsParser mvArgsParser;
    private final static char FLAG_NOTOVERWRITE = 'n';
    private static final String FOLDER_NAME = "folder";

    @BeforeEach
    public void setUp() {
         mvArgsParser = new MvArgsParser();
    }
    @Test
    void testParseNoArgsReturn() throws InvalidArgsException {
        List<String> expected = new ArrayList<String>();
        mvArgsParser.parse("");
        assertEquals(expected.toString(), mvArgsParser.getNonFlagArgs().toString());
    }

    @Test
    void testParseOneArgsReturnTrue() throws InvalidArgsException {
        List<String> expected = new ArrayList<String>();
        expected.add(FOLDER_NAME);
        mvArgsParser.parse(FOLDER_NAME);
        assertEquals(expected.toString(), mvArgsParser.getNonFlagArgs().toString());
    }


    @Test
    void testParseMultipleArgsReturnTrue() throws InvalidArgsException {
        List<String> expected = new ArrayList<String>();
        expected.add(FOLDER_NAME);
        expected.add("folder1");
        mvArgsParser.parse(FOLDER_NAME,"folder1");
        assertEquals(expected.toString(), mvArgsParser.getNonFlagArgs().toString());
    }

    @Test
    void testParseNFlagReturnTrue() throws InvalidArgsException {
        mvArgsParser.parse("-n");
        assertTrue(mvArgsParser.isNotOverWrite());
    }

    @Test
    void testParseNFlagMultipleArgsReturnTrue() throws InvalidArgsException {
        mvArgsParser.parse("-n" , "file1","file2");
        assertTrue(mvArgsParser.isNotOverWrite());
        List<String> expected = new ArrayList<String>();
        expected.add("file1");
        expected.add("file2");
        assertEquals(expected.toString(), mvArgsParser.getNonFlagArgs().toString());


    }
}
