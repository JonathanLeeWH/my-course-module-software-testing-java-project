package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

public class CpCommandSubIT {
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    public void setUp() {
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    /**
     * Cp Commands
     */
    @Test
    void testCpCommandWithRmCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithEchoCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithPasteCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithSedCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithExitCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithDiffCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithGrepCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithWcCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithCdCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithCpCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithCutCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithLsCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithSortCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithFindCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCpCommandWithMvCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }
}
