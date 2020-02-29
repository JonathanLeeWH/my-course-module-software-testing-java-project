package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MvArgsParserTest {

    private  static  MvArgsParser mvArgsParser;
    private final static char FLAG_NOTOVERWRITE = 'n';

    @BeforeEach
    public void setUp() {
         mvArgsParser = new MvArgsParser();
    }
    @Test
    void isNoArgsReturn() throws InvalidArgsException {
        List<String> expected = new ArrayList<String>();
        mvArgsParser.parse("");
        assertEquals(expected.toString(), mvArgsParser.getNonFlagArgs().toString());
    }

    @Test
    void isOneArgsReturnTrue() throws InvalidArgsException {
        List<String> expected = new ArrayList<String>();
        expected.add("folder");
        mvArgsParser.parse("folder");
        assertEquals(expected.toString(), mvArgsParser.getNonFlagArgs().toString());
    }


    @Test
    void isMultipleArgsReturnTrue() throws InvalidArgsException {
        List<String> expected = new ArrayList<String>();
        expected.add("folder");
        expected.add("folder1");
        mvArgsParser.parse("folder","folder1");
        assertEquals(expected.toString(), mvArgsParser.getNonFlagArgs().toString());
    }
}
