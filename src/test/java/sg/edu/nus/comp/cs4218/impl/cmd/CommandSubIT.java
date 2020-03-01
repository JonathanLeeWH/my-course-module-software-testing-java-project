package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandSubIT {
    private ArgumentResolver argumentResolver;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    public void setUp() {
        argumentResolver = new ArgumentResolver();
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    @Test
    void testLsCommandAndEchoCommandWithBlankOutputShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`cut -c 200 "+testFile1.toFile().getPath()+"`");
        List<String> expectedResult = Arrays.asList("echo");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testLsCommandAndEchoCommandWithOneLineOutputShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", "-d", "`echo "+testFile1.toFile().getPath()+"`", "-R");
        List<String> expectedResult = Arrays.asList("ls", "-d", testFile1.toFile().getPath(), "-R");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testWcCommandAndCutCommandWithMultipleLineOutputShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "`echo " + testFile1.toFile().getPath()  + " " + testFile2.toFile().getPath() + "`");
        List<String> expectedResult = Arrays.asList("wc", testFile1.toFile().getPath(), testFile2.toFile().getPath());
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }
}
