package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class SedArgumentsTest {
    private SedArguments sedArguments = new SedArguments();
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");

    @Test
    void testSedArgumentsValidateWithNullRegexp() {
        String thrown = assertThrows(Exception.class,
                () -> SedArguments.validate(null, "replace", 1)).getMessage();
        assertEquals(ERR_NULL_ARGS, thrown);
    }

    @Test
    void testSedArgumentsValidateWithNullReplacement() {
        String thrown = assertThrows(Exception.class,
                () -> SedArguments.validate("regexp", null, 1)).getMessage();
        assertEquals(ERR_NULL_ARGS, thrown);
    }

    @Test
    void testSedArgumentsValidateWithReplacementLessThanOne() {
        String thrown = assertThrows(Exception.class,
                () -> SedArguments.validate("regexp", "replacement", 0)).getMessage();
        assertEquals(ERR_INVALID_REP_X, thrown);
    }

    @Test
    void testSedArgumentsValidateWithEmptyRegex() {
        String thrown = assertThrows(Exception.class,
                () -> SedArguments.validate("", "replacement", 1)).getMessage();
        assertEquals(ERR_EMPTY_REGEX, thrown);
    }

    @Test
    void testSedArgumentsValidateWithInvalidRegex() {
        String thrown = assertThrows(Exception.class,
                () -> SedArguments.validate("[", "replacement", 1)).getMessage();
        assertEquals(ERR_INVALID_REGEX, thrown);
    }

    @Test
    void testParseWithNullArgs() {
        String[] args = null;
        String thrown = assertThrows(Exception.class,
                () -> sedArguments.parse(args)).getMessage();
        assertEquals(ERR_NULL_ARGS, thrown);
    }

    @Test
    void testParseWithArgsLengthLessThanOne() {
        String[] args = {};
        String thrown = assertThrows(Exception.class,
                () -> sedArguments.parse(args)).getMessage();
        assertEquals(ERR_NO_REP_RULE, thrown);
    }

    @Test
    void testParseWithInvalidReplacementRuleWithArgLengthLessThanFour() {
        String[] args = {"s/r"};
        String thrown = assertThrows(Exception.class,
                () -> sedArguments.parse(args)).getMessage();
        assertEquals(ERR_INVALID_REP_RULE, thrown);
    }

    @Test
    void testParseWithInvalidReplacementRuleWithFirstCharNotS() {
        String[] args = {"r/r/replacement"};
        String thrown = assertThrows(Exception.class,
                () -> sedArguments.parse(args)).getMessage();
        assertEquals(ERR_INVALID_REP_RULE, thrown);
    }

    @Test
    void testParseWithInvalidReplacementIndex() {
        String[] args = {"s/r/replacement/1.0"};
        String thrown = assertThrows(Exception.class,
                () -> sedArguments.parse(args)).getMessage();
        assertEquals(ERR_INVALID_REP_X, thrown);
    }

    @Test
    void testParseWithValidArgs() throws Exception {
        String[] args = {"s/r/replacement/1"};
        sedArguments.parse(args);
    }

    @Test
    void testParseWithFiles() throws Exception {
        String[] args = {"s/r/replacement/1",
                testFile1.toString()};
        sedArguments.parse(args);
    }

    @Test
    void testRegexGetter() {
        assertNull(sedArguments.getRegex());
    }

    @Test
    void testReplacementGetter() {
        assertNull(sedArguments.getReplacement());
    }

    @Test
    void testReplacementIndexGetter() {
        assertEquals(0, sedArguments.getReplacementIndex());
    }

    @Test
    void testGetterForFiles() {
        assertEquals(new ArrayList<>(), sedArguments.getFiles());
    }
}
