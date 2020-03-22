package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.DiffException;
import sg.edu.nus.comp.cs4218.exception.PasteException;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

public class DiffArgumentsTest {
    private static final String DIFF = "diff: ";
    private DiffArguments diffArguments = new DiffArguments();

    @Test
    void testParseMethodWithNullArgs() {
        String[] args = null;
        Exception thrown = assertThrows(DiffException.class, () -> {
            diffArguments.parse(args);
        });
        String expected = DIFF + ERR_NULL_ARGS;
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testAllThreeFlags() throws Exception {
        String[] args = {"-sBq"};
        diffArguments.parse(args);
        assertTrue(diffArguments.isIgnoreBlankLines());
        assertTrue(diffArguments.isShowIdenticalMessage());
        assertTrue(diffArguments.isDiffMessage());
    }

    @Test
    void testTwoFlagsSAndB() throws Exception {
        String[] args = {"-sB"};
        diffArguments.parse(args);
        assertTrue(diffArguments.isIgnoreBlankLines());
        assertTrue(diffArguments.isShowIdenticalMessage());
    }

    @Test
    void testTwoFlagsSAndQ() throws Exception {
        String[] args = {"-sq"};
        diffArguments.parse(args);
        assertTrue(diffArguments.isDiffMessage());
        assertTrue(diffArguments.isShowIdenticalMessage());
    }

    @Test
    void testTwoFlagsBAndQ() throws Exception {
        String[] args = {"-Bq"};
        diffArguments.parse(args);
        assertTrue(diffArguments.isDiffMessage());
        assertTrue(diffArguments.isIgnoreBlankLines());
    }

    @Test
    void testSFlag() throws Exception {
        String[] args = {"-s"};
        diffArguments.parse(args);
        assertTrue(diffArguments.isShowIdenticalMessage());
    }

    @Test
    void testQFlag() throws Exception {
        String[] args = {"-q"};
        diffArguments.parse(args);
        assertTrue(diffArguments.isDiffMessage());
    }

    @Test
    void testBFlag() throws Exception {
        String[] args = {"-B"};
        diffArguments.parse(args);
        assertTrue(diffArguments.isIgnoreBlankLines());
    }

    @Test
    void testArgsWithEmptyArg() throws Exception {
        String[] args = {"-s", ""};
        diffArguments.parse(args);
    }

    @Test
    void testArgsWithFileName() throws Exception {
        String[] args = {"-s", "fileOne.txt"};
        diffArguments.parse(args);
    }

    @Test
    void testArgsWithStdinDash() throws Exception {
        String[] args = {"-s", "-"};
        diffArguments.parse(args);
        assertTrue(diffArguments.isStdin());
    }

    @Test
    void testGetFilesMethod() {
        assertEquals("[]", diffArguments.getFiles().toString());
    }

    @Test
    void testIsDiffMessageMethod() {
        assertFalse(diffArguments.isDiffMessage());
    }

    @Test
    void testIsShowIdenticalMessage() {
        assertFalse(diffArguments.isShowIdenticalMessage());
    }

    @Test
    void testIsIgnoreBlankLines() {
        assertFalse(diffArguments.isIgnoreBlankLines());
    }

    @Test
    void testIsStdin() {
        assertFalse(diffArguments.isStdin());
    }
}
