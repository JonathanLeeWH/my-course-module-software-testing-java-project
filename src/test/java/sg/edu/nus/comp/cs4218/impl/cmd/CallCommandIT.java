package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_APP;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;

class CallCommandIT {
    private ApplicationRunner applicationRunner;
    private List<String> argsList;
    private ArgumentResolver argumentResolver;
    private OutputStream outputStream;
    private CallCommand callCommand;
    private static final String SHELL_EXCEPTION = "shell: ";
    private static final String FILE_CONTENTS = "Line One";
    private static final String HELLO = "hello";
    private static final String ECHO = "echo";

    @BeforeEach
    void setUp() {
        applicationRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
        outputStream = new ByteArrayOutputStream();
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
    }

    /**
     * Test the evaluate method with a valid echo command that has double quotations in the echo's argument.
     * Expected: Print the argument without the quotations.
     */
    @Test
    void testRunValidEchoCommandWithDoubleQuotationsShouldPrintTheArgumentCorrectly() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList("echo", "\"hello world\""));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        assertEquals("hello world" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test the evaluate method with a valid echo command that has single quotations in the echo's argument.
     * Expected: Print the argument without the quotations.
     */
    @Test
    void testRunValidCommandWithSingleQuotationsInFrontCommandSubstitutionSuccessful() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList(ECHO, "\'hello world\'"));
        outputStream = new ByteArrayOutputStream();
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        assertEquals("hello world" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test call command with valid echo command
     * Expected: Print argument of echo.
     */
    @Test
    void testRunValidEchoCommandShouldPrintArgument() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList(ECHO, HELLO));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        assertEquals(HELLO + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test call command with valid echo command and a tab in front of the argument
     * Expected: Print argument of echo with the tab in front of the argument.
     */
    @Test
    void testRunValidEchoCommandWithTabInArgumentShouldPrintArgumentWithTab() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList(ECHO, "\thello"));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        assertEquals("\thello" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test call command with valid echo command and a space in front of the argument
     * Expected: Print argument of echo with the single space in front of the argument.
     */
    @Test
    void testRunValidEchoCommandWithSingleSpaceShouldPrintArgumentWithSingleSpace() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList(ECHO, " hello"));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        assertEquals(" hello" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test call command with valid echo command and multiple spaces in front of the argument
     * Expected: Print argument of echo with the multiple spaces in front of the argument.
     */
    @Test
    void testRunValidEchoCommandWithMultipleSpacesShouldPrintArgumentWithMultipleSpaces() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList(ECHO, "     hello"));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        assertEquals("     hello" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test call command with valid echo command and a tab in front of echo
     * Expected: Print argument of echo.
     */
    @Test
    void testRunEchoCommandWithSingleTabInFrontOfEchoShouldPrintArgument() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList("\techo", HELLO));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        String expected = HELLO + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    /**
     * Test call command with valid echo command and a tab at the back of echo
     * Expected: Print argument of echo.
     */
    @Test
    void testRunEchoCommandWithSingleTabAtTheBackOfEchoShouldPrintArgument() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList("echo\t", HELLO));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        String expected = HELLO + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    /**
     * Test call command with valid echo command and multiple tabs in front of echo
     * Expected: Print argument of echo.
     */
    @Test
    void testRunEchoCommandWithMultipleTabsInFrontOfEchoShouldPrintArgument() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList("\t\techo", HELLO));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        String expected = HELLO + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    /**
     * Test call command with valid echo command and multiple tabs at the back of echo
     * Expected: Print argument of echo.
     */
    @Test
    void testRunEchoCommandWithMultipleTabsAtTheBackOfEchoShouldPrintArgument() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList("echo\t\t", HELLO));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        String expected = HELLO + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    /**
     * Test call command with invalid echo command using a capital E.
     * Expected: throw Shell Exception with ERR_INVALID_APP message.
     */
    @Test
    void testRunEchoCommandWithCapitalEInFrontOfEchoShouldThrowShellException() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList("Echo", HELLO));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + "Echo: " + ERR_INVALID_APP;
        assertEquals(expected, thrown.getMessage());
    }

    /**
     * Test call command with invalid echo command using a capital O at the back.
     * Expected: throw Shell Exception with ERR_INVALID_APP message.
     */
    @Test
    void testRunEchoCommandWithCapitalOAtTheEndOfEchOShouldThrowShellException() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList("echO", HELLO));
        CallCommand callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + "echO: " + ERR_INVALID_APP;
        assertEquals(expected, thrown.getMessage());
    }

    /**
     * Test call command with different quotation marks
     * Expected: return string normally.
     */
    @Test
    void testRunDifferentQuotationsShouldReturnStringNormally() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList(ECHO, "\"hello\'"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        String expected = "hello\'" + System.lineSeparator();
        callCommand.evaluate(System.in, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    /**
     * Test call command with valid ls command.
     * Expected: Print out the names of the relevant files.
     */
    @Test
    void testRunLsCommandWithAsteriskArgumentShouldReturnTheRelevantFiles() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList("ls", "*.png"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        String expected = "CS4218_Architecture.png" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    /**
     * Test Ls Command with double asterisk argument.
     * Note that this follows Linux behavior.
     * Expected: Print out the names of the relevant files.
     */
    @Test
    void testRunLsCommandWithDoubleAsteriskArgumentShouldStillReturnTheRelevantFiles() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList("ls", "**.png"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        String expected = "CS4218_Architecture.png" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testRunOneArgumentInArgListHasInvalidSyntaxInParseArgumentShouldThrowShellException() {
        argsList = new ArrayList<>(Arrays.asList("`!@#$`", "line"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + "`!@#$`: " + ERR_INVALID_APP;
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunMultipleArgumentsInArgListHasInvalidSyntaxInParseArgumentShouldThrowShellException() {
        argsList = new ArrayList<>(Arrays.asList("!@#$", "`!@#$`"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + "!@#$: " + ERR_INVALID_APP;
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunArgumentWithDuplicateCommandInArgListShouldThrowShellException() {
        argsList = new ArrayList<>(Collections.singletonList("echo echo this"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + "echo echo this: " + ERR_INVALID_APP;
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunSingleQuotationsInArgListHasInvalidSyntaxInParseArgumentShouldThrowShellException() {
        argsList = new ArrayList<>(Collections.singletonList("\'echo this\'"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + "\'echo this\': " + ERR_INVALID_APP;
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunSingleQuotationsWithBackQuotesInArgListHasInvalidSyntaxInParseArgumentShouldThrowShellException() {
        argsList = new ArrayList<>();
        argsList.add("\'`echo this`\'");
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + ERR_SYNTAX;
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunDoubleQuotationsInArgListHasInvalidSyntaxInParseArgumentShouldThrowShellException() {
        argsList = new ArrayList<>(Collections.singletonList("\"echo this\""));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + "\"echo this\": " + ERR_INVALID_APP;
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunDoubleQuotationsWithBackQuotesInArgListHasInvalidSyntaxInParseArgumentShouldThrowShellException() {
        argsList = new ArrayList<>();
        argsList.add("\"`echo this`\"");
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + ERR_SYNTAX;
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunCallCommandWithValidCommandSubstitution() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList("paste", "`echo SingleLineFile.txt`"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        String expected = "Line One" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testRunCallCommandWithInvalidCommandSubstitution() {
        argsList = new ArrayList<>(Arrays.asList("echo", "`Echo SingleLineFile.txt`"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + "Echo: " + ERR_INVALID_APP;
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunCallCommandWithQuotedAsteriskAsArgumentShouldPrintAsterisk() throws AbstractApplicationException, ShellException {
        argsList = new ArrayList<>(Arrays.asList(ECHO, "\"*\""));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        callCommand.evaluate(System.in, outputStream);
        String expected = "*" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testRunCallCommandWithAsteriskAsFirstArgumentShouldPrintAsterisk() {
        argsList = new ArrayList<>(Arrays.asList("*", "*"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + "*: " + ERR_INVALID_APP;
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunCallCommandWithCommandSubstitutionThatHasExitAsArgumentShouldThrowExitException() {
        argsList = new ArrayList<>(Arrays.asList(ECHO, "`exit`"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ExitException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = "exit: 0";
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunCallCommandWithExit() {
        argsList = new ArrayList<>(Arrays.asList("paste", "`exit`"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        Exception thrown = assertThrows(ExitException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = "exit: 0";
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testRunCallCommandWithDiffWithInvalidArgShouldThrowException() {
        argsList = new ArrayList<>(Arrays.asList("diff", "invalidFile"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        assertThrows(DiffException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
    }

    @Test
    void testRunCallCommandWithSedWithInvalidArgShouldThrowException() {
        argsList = new ArrayList<>(Arrays.asList("sed", "/s/"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        assertThrows(SedException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
    }

    @Test
    void testRunCallCommandWithGrepWithInvalidArgShouldThrowException() {
        argsList = new ArrayList<>(Arrays.asList("grep", "-ic"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        assertThrows(GrepException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
    }

    @Test
    void testRunCallCommandWithRMWithInvalidArgShouldThrowException() {
        argsList = new ArrayList<>(Arrays.asList("rm", "invalid"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        assertThrows(RmException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
    }

    @Test
    void testRunCallCommandWithSortWithInvalidArgShouldThrowException() {
        argsList = new ArrayList<>(Arrays.asList("sort", "invalid"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        assertThrows(SortException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
    }

    @Test
    void testRunCallCommandWithFindWithInvalidArgShouldThrowException() {
        argsList = new ArrayList<>(Arrays.asList("find", "invalid"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        assertThrows(FindException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
    }

    @Test
    void testRunCallCommandWithCPWithInvalidArgShouldThrowException() {
        argsList = new ArrayList<>(Arrays.asList("cp", "invalid"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        assertThrows(CpException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
    }

    @Test
    void testRunCallCommandWithCutWithInvalidArgShouldThrowException() {
        argsList = new ArrayList<>(Arrays.asList("cut", "invalid"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        assertThrows(CutException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
    }

    @Test
    void testRunCallCommandWithLSWithInvalidArgShouldThrowException() {
        argsList = new ArrayList<>(Arrays.asList("ls", "invalid"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        assertThrows(LsException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
    }

    @Test
    void testRunCallCommandWithMVWithInvalidArgShouldThrowException() {
        argsList = new ArrayList<>(Arrays.asList("mv", "invalid"));
        callCommand = new CallCommand(argsList, applicationRunner, argumentResolver);
        assertThrows(MvException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
    }
}
