package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

public class GrepCommandSubIT {
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
     * Grep Commands
     */
    @Test
    void testGrepCommandWithRmCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithEchoCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithPasteCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithSedCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithExitCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithDiffCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithGrepCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithWcCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithCdCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithCpCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithCutCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithLsCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithSortCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithFindCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }

    @Test
    void testGrepCommandWithMvCommandAsSubCommandShouldRunSuccessfully() {
        fail();
    }
}
