package tdd.bf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.EchoException;
import sg.edu.nus.comp.cs4218.impl.app.EchoApplication;
import tdd.util.StdOutStubIOExceptionOnWrite;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

/**
 * The tdd's EchoApplicationTest.java should be run with our EchoApplicationTest.java for better coverage.
 */
@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.AvoidDuplicateLiterals"})
class EchoApplicationTest {
    private static EchoApplication echoApp;
    private static ByteArrayOutputStream outContent;

    @BeforeAll
    public static void setUpBeforeAll() {
        echoApp = new EchoApplication();
    }

    @BeforeEach
    public void setUpBeforeEach() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    // echo
    public void testRun_noArgs() throws Exception {
        String expected = STRING_NEWLINE;
        echoApp.run(new String[]{}, null, System.out);
        assertEquals(expected, outContent.toString());
    }

    @Test
    // echo
    public void testRun_nullStringArg() throws Exception {
        String expected = STRING_NEWLINE;
        echoApp.run(new String[]{""}, null, System.out);
        assertEquals(expected, outContent.toString());
    }

    @Test
    // echo Hello
    public void testRun_singleArg() throws Exception {
        String expected = "Hello" + STRING_NEWLINE;
        echoApp.run(new String[]{"Hello"}, null, System.out);
        assertEquals(expected, outContent.toString());
    }

    @Test
    // echo echo
    public void testRun_singleArgKeyword() throws Exception {
        String expected = "echo" + STRING_NEWLINE;
        echoApp.run(new String[]{"echo"}, null, System.out);
        assertEquals(expected, outContent.toString());
    }

    @Test
    // echo Hello world
    public void testRun_twoArgs() throws Exception {
        String expected = "Hello world" + STRING_NEWLINE;
        echoApp.run(new String[]{"Hello world"}, null, System.out);
        assertEquals(expected, outContent.toString());
    }

    @Test
    // echo Hello world I am a program look at me doing stuff wow
    public void testRun_manyArgs() throws Exception {
        String expected = "Hello world I am a program look at me doing stuff wow" + STRING_NEWLINE;
        echoApp.run(new String[]{"Hello world I am a program look at me doing stuff wow"}, null, System.out);
        assertEquals(expected, outContent.toString());
    }

    @Test
    public void testRun_nullStdOut_throwEchoException() throws Exception {
        String expected = "echo: " + ERR_NO_OSTREAM;
        String thrown = assertThrows(EchoException.class, () -> echoApp.run(new String[]{}, null,null)).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testRunApp_stdoutError_throwPasteException() throws Exception {
        String expected = "echo: " + ERR_IO_EXCEPTION;
        String thrown = assertThrows(EchoException.class, () -> echoApp.run(new String[]{}, null,
                new StdOutStubIOExceptionOnWrite())).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testConstructResult_noArgs() {
        String expected = "echo: " + ERR_NULL_ARGS;
        String thrown = assertThrows(EchoException.class, () -> {
            echoApp.constructResult(null);
        }).getMessage();
        assertEquals(expected, thrown);
    }

    /**
     * Modify original tdd's version due to difference in implementation of EchoApplication.java.
     * Add additional new line in expected.
     */
    @Test
    public void testConstructResult_singleBlankArg() throws Exception {
        String expected = "";
        assertEquals(expected + STRING_NEWLINE, echoApp.constructResult(new String[]{""}));
    }

    /**
     * Modify original tdd's version due to difference in implementation of EchoApplication.java.
     * Add additional new line in expected.
     */
    @Test
    public void testConstructResult_singleArg() throws Exception {
        String expected = "Hello";
        assertEquals(expected + STRING_NEWLINE, echoApp.constructResult(new String[]{"Hello"}));
    }

    /**
     * Modify original tdd's version due to difference in implementation of EchoApplication.java.
     * Add additional new line in expected.
     */
    @Test
    public void testConstructResult_manyArgs() throws Exception {
        String expected = "Hello world I am a program look at me doing stuff wow";
        assertEquals(expected + STRING_NEWLINE, echoApp.constructResult(new String[]{
                "Hello", "world", "I", "am", "a", "program", "look", "at", "me", "doing", "stuff", "wow"
        }));
    }


}
