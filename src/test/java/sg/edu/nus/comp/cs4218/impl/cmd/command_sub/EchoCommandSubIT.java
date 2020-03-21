package sg.edu.nus.comp.cs4218.impl.cmd.command_sub;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class EchoCommandSubIT {
    private ApplicationRunner applicationRunner;
    private ArgumentResolver argumentResolver;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");
    private final Path testFile4 = Paths.get(TestFileUtils.TESTDATA_DIR + "testFileWithTestDataAbsoluteFilePath.txt");
    private final Path testFile5 = Paths.get(TestFileUtils.TESTDATA_DIR + "testFileWithTestDataFileName.txt");

    @BeforeEach
    public void setUp() {
        applicationRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    // Positive test cases
    @Test
    void testEchoCommandAndEchoAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`echo \"Welcome to CS4218: Software Testing\"`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "Welcome to CS4218: Software Testing" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testEchoCommandAndPasteAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`paste " + testFile4.toFile().getPath()
                + " " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = testFile2.toFile().getPath() + " " + testFile2.toFile().getPath() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testEchoCommandAndSedAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`sed \"s/^/> /\" " + testFile3.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "> 1.0, 5.0 > 2, 3 > 51, 15 > 21, 4 > 22, 41 > 551, 1200 > 001, 010" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testEchoCommandAndDiffAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`diff " + testFile1.toFile().getPath() + " " + testFile3.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testEchoCommandAndGrepAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`grep \"1\" " + testFile3.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "1.0, 5.0 51, 15 21, 4 22, 41 551, 1200 001, 010" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testEchoCommandAndWcAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`wc -cw " + testFile2.toFile().getPath() +  " `");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "320 2081 " + testFile2.toFile().getPath() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testEchoCommandAndCutAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`cut -c 1-4 " + testFile1.toFile().getPath() +  " `");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "CS42 Thìš and root perf cruc" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testEchoCommandAndLsAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`ls " + TestFileUtils.TESTDATA_DIR + "*`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = String.format("%s %s %s %s %s", testFile1.getFileName(), testFile2.getFileName(),
                testFile3.getFileName(), testFile4.getFileName(), testFile5.getFileName()) + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testEchoCommandAndSortAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`sort -n " + testFile2.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet " +
                "cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. " +
                "Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. " +
                "Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. " +
                "Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. " +
                "Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. " +
                "Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. " +
                "Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet. Lorem ipsum dolor sit amet, " +
                "consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. " +
                "Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. " +
                "Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. " +
                "Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. " +
                "Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. " +
                "Feugiat pretium nibh ipsum consequat nisl. Turpis massa tincidunt dui ut ornare lectus sit. " +
                "Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare " +
                "arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. " +
                "Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis." +
                " Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testEchoCommandAndFindAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`find " + TestFileUtils.TESTDATA_DIR + " -name \"test*.txt\"`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = String.format("%s %s %s %s", testFile1.toFile().getPath(),
                testFile2.toFile().getPath(), testFile4.toFile().getPath(), testFile5.toFile().getPath()) + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }
}
