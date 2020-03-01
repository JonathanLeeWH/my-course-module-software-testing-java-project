package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

public class SedCommandSubIT {
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
     * Sed Commands
     */
    @Test
    void testSedCommandWithRmCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithEchoCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithPasteCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithSedCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithExitCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithDiffCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithGrepCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithWcCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithCdCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithCpCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithCutCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithLsCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithSortCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithFindCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testSedCommandWithMvCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }
}
