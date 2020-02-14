package test.sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.app.CutApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class CutApplicationTest {
    private CutApplication cutApplication;
    private String[] defaultCutArgs;
    private InputStream stdin;
    private OutputStream outputStream;

    @BeforeEach
    public void setUp() {
        cutApplication = new CutApplication();
        stdin = System.in;
        defaultCutArgs = Arrays.asList("-c","8").toArray(new String[1]);
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    void testRunNullArgs() {
       Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(null, stdin, outputStream));
       assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_NULL_ARGS);
    }

    @Test
    void testRunNullOutputStream() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(defaultCutArgs, stdin, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_NO_OSTREAM);
    }

    @Test
    void testCutFromStdinNullInputStream() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromStdin(false, false, false, 1, 2, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_NULL_STREAMS);
    }

    @Test
    void testCutFromFilesNullFile() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(false, false, false, 1, 2, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_GENERAL);
    }

    @Test
    void testRunSuccess() throws CutException {
        //Use a sample test file.
        cutApplication.run(Arrays.asList("-c", "6", "README.md").toArray(new String[3]), stdin, outputStream);
        assertEquals("2\na\nr\n", outputStream.toString());
    }
    
    
    @Test
    void testCutUsingCharPosAndCommaSeparatedNumWithASingleFile() { }

    @Test
    void testCutUsingBytePosAndCommaSeparatedNumWithMultipleFiles() { }

    @Test
    void testCutUsingCharPosAndNumRangeWithStdin() { }

    @Test
    void testCutUsingBytePosAndNumRangeWithASingleFile() { }

    @Test
    void testCutUsingCharPosAndSingleNumWithMultipleFiles() { }

    @Test
    void testCutUsingBytePosAndSingleNumWithStdin() { }

    @Test
    void testCutUsingCharPosAndCommaSeparatedNumWithStdin() { }

    @Test
    void testCutUsingBytePosAndNumRangeWithMultipleFiles() { }

    @Test
    void testCutUsingCharPosAndSingleNumWithASingleFile() { }
}