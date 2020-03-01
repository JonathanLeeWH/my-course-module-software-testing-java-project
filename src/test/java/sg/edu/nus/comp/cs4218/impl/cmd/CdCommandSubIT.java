package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

public class CdCommandSubIT {
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
     * Cd Commands
     */
    @Test
    void testCdCommandWithRmCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithEchoCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithPasteCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithSedCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithExitCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithDiffCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithGrepCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithWcCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithCdCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithCpCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithCutCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithLsCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithSortCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithFindCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testCdCommandWithMvCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }
}
