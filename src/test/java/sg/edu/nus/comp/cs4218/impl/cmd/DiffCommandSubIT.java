package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

public class DiffCommandSubIT {
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
     * Diff Commands
     */
    @Test
    void testDiffCommandWithRmCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithEchoCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithPasteCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithSedCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithExitCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithDiffCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithGrepCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithWcCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithCdCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithCpCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithCutCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithLsCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithSortCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithFindCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testDiffCommandWithMvCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }
}
