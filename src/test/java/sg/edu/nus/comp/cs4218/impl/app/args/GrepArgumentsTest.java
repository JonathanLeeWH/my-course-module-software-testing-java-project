package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class GrepArgumentsTest {
    private GrepArguments grepArguments = new GrepArguments();
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");

    @Test
    void testValidateMethodWithNullPatterns() {
        String thrown = assertThrows(Exception.class,
                () -> GrepArguments.validate(null)).getMessage();
        assertEquals(ERR_NULL_ARGS, thrown);
    }

    @Test
    void testValidateMethodWithEmptyPatterns() {
        String thrown = assertThrows(Exception.class,
                () -> GrepArguments.validate("")).getMessage();
        assertEquals(ERR_EMPTY_REGEX, thrown);
    }

    @Test
    void testValidateMethodWithInvalidPatterns() {
        String thrown = assertThrows(Exception.class,
                () -> GrepArguments.validate("[")).getMessage();
        assertEquals(ERR_INVALID_REGEX, thrown);
    }

    @Test
    void testParseWithNullArgs() {
        String[] args = null;
        String thrown = assertThrows(Exception.class,
                () -> grepArguments.parse(args)).getMessage();
        assertEquals(ERR_NULL_ARGS, thrown);
    }

    @Test
    void testParseMethodWithOneArgEmpty() {
        String[] args = {" ", ""};
        String thrown = assertThrows(Exception.class,
                () -> grepArguments.parse(args)).getMessage();
        assertEquals(ERR_EMPTY_REGEX, thrown);
    }

    @Test
    void testParseWithEmptyArgs() {
        String[] args = new String[0];
        String thrown = assertThrows(Exception.class,
                () -> grepArguments.parse(args)).getMessage();
        assertEquals(ERR_NO_REGEX, thrown);
    }

    @Test
    void testParseWithValidArgsWithFlags() throws Exception {
        grepArguments.parse("-i", "-c", "hunting the shark",
                testFile1.toString());
    }

    @Test
    void testParseWithValidArgsWithInvalidFlag() throws Exception {
        grepArguments.parse("-u", testFile1.toString());
    }

    @Test
    void testParseWithValidArgs() throws Exception {
        grepArguments.parse("“hunting the shark",
                testFile1.toString());
    }

    @Test
    void testFilesGetter() {
        assertEquals(new ArrayList<>(), grepArguments.getFiles());
    }

    @Test
    void testPatternGetter() {
        assertNull(grepArguments.getPattern());
    }

    @Test
    void testMethodIsCaseInsensitive() {
        assertFalse(grepArguments.isCaseInsensitive());
    }

    @Test
    void testMethodIsCountOfLinesOnly() {
        assertFalse(grepArguments.isCountOfLinesOnly());
    }
}
