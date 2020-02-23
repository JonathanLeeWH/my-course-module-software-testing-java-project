package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;

public class CutArgsParserTest {
    private CutArgsParser cutArgsParser;

    @BeforeEach
    public void setUp() {
        cutArgsParser = new CutArgsParser();
    }

    @Test
    public void testParseWithNullArgsShouldThrowInvalidArgsException() {
        Throwable thrown = assertThrows(NullPointerException.class, () -> cutArgsParser.parse(null));
    }

}
