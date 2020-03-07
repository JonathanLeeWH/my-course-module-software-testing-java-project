package tdd.bf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.app.RmApplication;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import tdd.util.FilePermissionTestUtil;
import tdd.util.RmTestUtil;

import java.io.*;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static tdd.util.RmTestUtil.ABSOLUTE_RM_TEST_PATH;
import static tdd.util.RmTestUtil.RM_TEST_DIR;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.LongVariable"})
/**
 * Note: All tdd's test cases involving permissions are commented out or disabled as stated in our assumptions we assume all files and folders have correct permissions for commands to execute properly due to difference in behaviour in setting file permissions using Java API between filesystems as well as operating system
 * You can read more about it on our Assumptions report.
 * Added additional test case for sub path.
 * The tdd's RmApplicationTest.java should be run with our RmApplicationTest.java and RmApplicationIT.java for both unit testing and integration testing and better coverage.
 */
public class RmApplicationTest {
    private static final String ERR_CURR_DIR = "current directory error";
    private RmApplication rmApplication;
    private RmTestUtil rmTestUtil;
    private FilePermissionTestUtil filePermissionTestUtil;

    private static final String INVALID_OPTION = "-e";
    private static final String EMPTY_FOLDER_OPTION = "-d";
    private static final String RECURSIVE_OPTION = "-r";
    private static final String EMPTY_FOLDER_RECURSIVE_OPTION = "-rd";
    public static final String CURRENT_DIR = ".";
    private static final boolean IS_EMPTY_DIR = true;
    private static final boolean IS_RECURSIVE = true;
    private static final String EXCEPTION_MESSAGE_HEADER = "rm: ";
    private InputStream inputStream;
    private OutputStream outputStream;
    private OutputStream checkingOutputStream;
    private PrintStream checkingPrintStream;//NOPMD
    private String[] expected;
    private String expectedMsg;
    private String[] actual;

    @BeforeEach
    public void setUp() {
        rmApplication = new RmApplication();
        inputStream = null;
        outputStream = null;
        checkingOutputStream = new ByteArrayOutputStream();
        checkingPrintStream = new PrintStream(checkingOutputStream);
        System.setOut(checkingPrintStream);
        rmTestUtil = new RmTestUtil();
        rmTestUtil.createTestEnv();
        filePermissionTestUtil = new FilePermissionTestUtil();
        filePermissionTestUtil.createTestEnv();
        /**
         * Modify tdd's test suite to set Environment.currentDirectory to the RM_TEST_DIR at the start of execution of each test case.
         */
        EnvironmentHelper.currentDirectory = ABSOLUTE_RM_TEST_PATH;
    }

    @AfterEach
    public void tearDown() {
        System.setOut(System.out);
        rmTestUtil.removeTestEnv();
        filePermissionTestUtil.removeTestEnv();
        /**
         * Modify tdd's test suite to set Environment.currentDirectory to the default.
         */
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
    }

    /**
     * Modify original tdd's version to throw RmException with ERR_NULL_ARGS
     */
    @Test
    public void testRemove_nullIsEmptyFolder_shouldThrowRmException() {
        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(null, !IS_RECURSIVE));
        assertEquals(new RmException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Modify original tdd's version to throw RmException with ERR_NULL_ARGS
     */
    @Test
    public void testRemove_nullIsRecursive_shouldThrowRmException() {
        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(!IS_EMPTY_DIR, null));
        assertEquals(new RmException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Modify original tdd's version to throw RmException with ERR_NULL_ARGS
     */
    @Test
    public void testRemove_nullFileName_shouldThrowRmException() {
        String[] nullFileName = null;
        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(!IS_EMPTY_DIR, !IS_RECURSIVE, nullFileName));
        assertEquals(new RmException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Modify original tdd's version to throw RmException with ERR_MISSING_ARG
     */
    @Test
    public void testRemove_emptyArgument_shouldThrowRmException() {
        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(!IS_EMPTY_DIR, !IS_RECURSIVE));
        assertEquals(new RmException(ERR_MISSING_ARG).getMessage(), exception.getMessage());
    }

    @Test
    public void testRemove_noOption_singleFile_shouldRemove() {
        expected = new String[] { RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR };

        assertDoesNotThrow(() -> rmApplication.remove(!IS_EMPTY_DIR, !IS_RECURSIVE, RmTestUtil.RELATIVE_FILE_ONE_PATH));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_noOption_twoFiles_shouldRemoveBothFiles() {
        expected = new String[] { RmTestUtil.EMPTY_DIR, RmTestUtil.NONEMPTY_DIR };

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_FILE_ONE_PATH,
                RmTestUtil.RELATIVE_FILE_TWO_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    /**
     * Modify from original tdd test case to throw the latest RmException
     * as our implementation throws the latest RmException and at the same time remove the file.
     * In this case, the latest RmException thrown is ERR_IS_DIR
     */
    @Test
    public void testRemove_noOption_singleFile_singleEmptyDir_shouldRemoveFileAndThrowRmException() {
        expected = new String[] { RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR };

        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(
                !IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_FILE_ONE_PATH,
                RmTestUtil.RELATIVE_EMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
        assertEquals(new RmException(ERR_IS_DIR).getMessage(), exception.getMessage());
    }

    /**
     * Modify original tdd's version to throw RmException with ERR_FILE_NOT_FOUND instead of tdd's version of printing error message.
     */
    @Test
    public void testRemove_invalidDir_shouldThrowRmException() {
        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_INVALID_DIR_PATH
        ));

        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    /**
     * Modify from original tdd test case to throw the latest RmException
     * as our implementation throws the latest RmException and at the same time remove the empty directory.
     * In this case, the latest RmException thrown is ERR_FILE_NOT_FOUND
     */
    @Test
    public void testRemove_invalidDir_emptyDir_shouldRemoveEmptyDirAndThrowRmException() {
        expected = new String[] { RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR };

        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_INVALID_DIR_PATH,
                RmTestUtil.RELATIVE_EMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    /**
     * Modify from original tdd test case to throw the latest RmException
     * as our implementation throws the latest RmException.
     * In this case, the latest RmException thrown is ERR_FILE_NOT_FOUND
     */
    @Test
    public void testRemove_invalidFile_shouldThrowRmException() {
        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(
                !IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_INVALID_FILE_PATH
        ));
        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    /**
     * Modify from original tdd test case to throw the latest RmException
     * as our implementation throws the latest RmException and at the same time remove the valid file.
     * In this case, the latest RmException thrown is ERR_FILE_NOT_FOUND
     */
    @Test
    public void testRemove_invalidFile_validFile_shouldRemoveValidFileAndThrowRmException() {
        expected = new String[] { RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR };

        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(
                !IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_INVALID_FILE_PATH,
                RmTestUtil.RELATIVE_FILE_ONE_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    @Test
    public void testRemove_recursiveOption_validFile_shouldRemoveFile() {
        expected = new String[] { RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR };

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_FILE_ONE_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_recursiveOption_emptyDir_shouldRemoveDir() {
        expected = new String[] { RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR };

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_EMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_recursiveOption_nonEmptyDir_shouldRemoveDirAndAllFilesInDir() {
        expected = new String[] { RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO };

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_emptyFolderOption_validFile_shouldRemoveFile() {
        expected = new String[] { RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR };

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_FILE_ONE_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_emptyFolderOption_emptyDir_shouldRemoveDir() {
        expected = new String[] { RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR };

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_EMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    /**
     * Modify from original tdd test case to throw the latest RmException
     * as our implementation throws the latest RmException.
     * In this case, the latest RmException thrown is ERR_NON_EMPTY_DIR
     */
    @Test
    public void testRemove_emptyFolderOption_nonEmptyDir_shouldThrowRmException() {
        expected = new String[] {
                RmTestUtil.EMPTY_DIR,
                RmTestUtil.FILE_ONE,
                RmTestUtil.FILE_TWO,
                RmTestUtil.NONEMPTY_DIR
        };

        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
        assertEquals(new RmException(ERR_NON_EMPTY_DIR).getMessage(), exception.getMessage());
    }

    /**
     * Modify from original tdd test case to throw the latest RmException
     * as our implementation throws the latest RmException.
     * In this case, the latest RmException thrown is ERR_IS_CURR_DIR
     */
    @Test
    public void testRemove_emptyFolderOption_currDir_shouldThrowRmException() {
        expected = new String[] {
                RmTestUtil.EMPTY_DIR,
                RmTestUtil.FILE_ONE,
                RmTestUtil.FILE_TWO,
                RmTestUtil.NONEMPTY_DIR
        };

        RmException exception = assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                CURRENT_DIR
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
        assertEquals(new RmException(ERR_IS_CURR_DIR).getMessage(), exception.getMessage());
    }

    @Test
    public void testRemove_recursiveAndEmptyFolderOption_validFile_shouldRemoveFile() {
        expected = new String[] { RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR };

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_FILE_ONE_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_recursiveAndEmptyFolderOption_emptyDir_shouldRemoveDir() {
        expected = new String[] { RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR };

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_EMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_recursiveAndEmptyFolderOption_nonEmptyDir_shouldRemoveDirAndAllFilesInDir() {
        expected = new String[] { RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO };

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    /**
     * Modify from original tdd test case to throw the latest RmException
     * as our implementation throws the latest RmException.
     * In this case, the latest RmException thrown is ERR_IS_CURR_DIR
     */
    @Test
    public void testRemove_recursiveAndEmptyFolderOption_currDir_shouldThrowRmException() {
        expected = new String[] {
                RmTestUtil.EMPTY_DIR,
                RmTestUtil.FILE_ONE,
                RmTestUtil.FILE_TWO,
                RmTestUtil.NONEMPTY_DIR
        };

        Exception exception = assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                CURRENT_DIR
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
        assertEquals(new RmException(ERR_IS_CURR_DIR).getMessage(), exception.getMessage());
    }

    @Test
    public void testRemove_absoluteFilePath_shouldRemove() {
        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.ABSOLUTE_NESTED_DIR_FILE_PATH
        ));
        actual = rmTestUtil.nestedNonEmptyDir.list();
        Arrays.sort(actual);

        assertEquals(0, actual.length);
    }

    /**
     * Ignore tdd's test cases involving permissions as stated in our assumptions we assume all files and folders have correct permissions for commands to execute properly due to difference in behaviour in setting file permissions using Java API between filesystems as well as operating system
     * You can read more about it on our Assumptions report.
     */
    @Disabled("This test case is disabled as it does not match our assumption")
    @Test
    public void testRemove_onReadOnlyDir_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + FilePermissionTestUtil.READ_ONLY_DIR_PATH
                + ": "
                + ERR_NO_PERM
                + StringUtils.STRING_NEWLINE;

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.READ_ONLY_DIR_PATH
        ));
        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    /**
     * Comment out or disabled tdd's test cases involving permissions as stated in our assumptions we assume all files and folders have correct permissions for commands to execute properly due to difference in behaviour in setting file permissions using Java API between filesystems as well as operating system
     * You can read more about it on our Assumptions report.
     */
    @Disabled("This test case is disabled as it does not match our assumption")
    @Test
    public void testRemove_onExecuteOnlyDir_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + FilePermissionTestUtil.EXECUTE_ONLY_DIR_PATH
                + ": "
                + ERR_NO_PERM
                + StringUtils.STRING_NEWLINE;

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.EXECUTE_ONLY_DIR_PATH
        ));
        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    /**
     * Comment out or disabled tdd's test cases involving permissions as stated in our assumptions we assume all files and folders have correct permissions for commands to execute properly due to difference in behaviour in setting file permissions using Java API between filesystems as well as operating system
     * You can read more about it on our Assumptions report.
     */
    @Disabled("This test case is disabled as it does not match our assumption")
    @Test
    public void testRemove_onWriteOnlyDir_shouldRemove() {
        expected = new String[] {
                FilePermissionTestUtil.EXECUTE_ONLY_FILE,
                FilePermissionTestUtil.EXECUTE_ONLY_DIR,
                FilePermissionTestUtil.NO_PERMISSION_FILE,
                FilePermissionTestUtil.NO_PERMISSION_DIR,
                FilePermissionTestUtil.NO_WRITE_FILE,
                FilePermissionTestUtil.NO_WRITE_DIR,
                FilePermissionTestUtil.READ_ONLY_FILE,
                FilePermissionTestUtil.READ_ONLY_DIR,
                FilePermissionTestUtil.WRITE_ONLY_FILE
        };

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.WRITE_ONLY_DIR_PATH
        ));
        actual = filePermissionTestUtil.resourceDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    /**
     * Comment out or disabled tdd's test cases involving permissions as stated in our assumptions we assume all files and folders have correct permissions for commands to execute properly due to difference in behaviour in setting file permissions using Java API between filesystems as well as operating system
     * You can read more about it on our Assumptions report.
     */
    @Disabled("This test case is disabled as it does not match our assumption")
    @Test
    public void testRemove_onWriteOnlyDir_noRecursiveOption_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + FilePermissionTestUtil.WRITE_ONLY_DIR_PATH
                + ": "
                + ERR_NO_PERM
                + StringUtils.STRING_NEWLINE;

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                FilePermissionTestUtil.WRITE_ONLY_DIR_PATH
        ));
        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    /**
     * Comment out or disabled tdd's test cases involving permissions as stated in our assumptions we assume all files and folders have correct permissions for commands to execute properly due to difference in behaviour in setting file permissions using Java API between filesystems as well as operating system
     * You can read more about it on our Assumptions report.
     */
    @Disabled("This test case is disabled as it does not match our assumption")
    @Test
    public void testRemove_onReadOnlyFile_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + FilePermissionTestUtil.READ_ONLY_FILE_PATH
                + ": "
                + ERR_NO_PERM
                + StringUtils.STRING_NEWLINE;

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.READ_ONLY_FILE_PATH
        ));
        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    /**
     * Comment out or disabled tdd's test cases involving permissions as stated in our assumptions we assume all files and folders have correct permissions for commands to execute properly due to difference in behaviour in setting file permissions using Java API between filesystems as well as operating system
     * You can read more about it on our Assumptions report.
     */
    @Disabled("This test case is disabled as it does not match our assumption")
    @Test
    public void testRemove_onExecuteOnlyFile_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + FilePermissionTestUtil.EXECUTE_ONLY_FILE_PATH
                + ": "
                + ERR_NO_PERM
                + StringUtils.STRING_NEWLINE;

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.EXECUTE_ONLY_FILE_PATH
        ));
        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    /**
     * Comment out or disabled tdd's test cases involving permissions as stated in our assumptions we assume all files and folders have correct permissions for commands to execute properly due to difference in behaviour in setting file permissions using Java API between filesystems as well as operating system
     * You can read more about it on our Assumptions report.
     */
    @Disabled("This test case is disabled as it does not match our assumption")
    @Test
    public void testRemove_onWriteOnlyFile_shouldRemove() {
        expected = new String[] {
                FilePermissionTestUtil.EXECUTE_ONLY_FILE,
                FilePermissionTestUtil.EXECUTE_ONLY_DIR,
                FilePermissionTestUtil.NO_PERMISSION_FILE,
                FilePermissionTestUtil.NO_PERMISSION_DIR,
                FilePermissionTestUtil.NO_WRITE_FILE,
                FilePermissionTestUtil.NO_WRITE_DIR,
                FilePermissionTestUtil.READ_ONLY_FILE,
                FilePermissionTestUtil.READ_ONLY_DIR,
                FilePermissionTestUtil.WRITE_ONLY_DIR,
        };

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.WRITE_ONLY_FILE_PATH
        ));
        actual = filePermissionTestUtil.resourceDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    /**
     * Modify the tdd's way expected RmException is used in test cases for consistency.
     * Instead of tdd's provided way of EXCEPTION_MESSAGE_HEADER + <error message>,
     * we use new RmException(message).getMessage().
     */
    @Test
    public void testRun_nullArgs_shouldThrowRmException() {
        Exception exception = assertThrows(RmException.class, () -> rmApplication.run(null, inputStream, outputStream));
        assertEquals(new RmException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Modify the tdd's expected RmException thrown as we use ERR_MISSING_ARG in our implementation rather than ERR_NO_ARGS for the test case scenario below.
     * Modify the tdd's way expected RmException is used in test cases for consistency.
     * Instead of tdd's provided way of EXCEPTION_MESSAGE_HEADER + <error message>,
     * we use new RmException(message).getMessage().
     */
    @Test
    public void testRun_noOptionAndArgument_shouldThrowRmException() {
        Exception exception = assertThrows(RmException.class, () -> rmApplication.run(new String[0], inputStream, outputStream));
        assertEquals(new RmException(ERR_MISSING_ARG).getMessage(), exception.getMessage());
    }

    /**
     * Modify the tdd's way expected RmException is used in test cases for consistency.
     * Instead of tdd's provided way of EXCEPTION_MESSAGE_HEADER + <error message>,
     * we use new RmException(message).getMessage().
     */
    @Test
    public void testRun_invalidOptions_shouldThrowRmException() {
        String[] args = { INVALID_OPTION };

        Exception exception = assertThrows(RmException.class, () -> rmApplication.run(args, inputStream, outputStream));
        assertEquals(new RmException(ILLEGAL_FLAG_MSG + "e").getMessage(), exception.getMessage());
    }

    @Test
    public void testRun_separatedEmptyFolderAndRecursiveOption_emptyDir_shouldRemove() {
        expected = new String[] {
                RmTestUtil.EMPTY_DIR,
                RmTestUtil.FILE_ONE,
                RmTestUtil.FILE_TWO,
        };
        String[] args = { EMPTY_FOLDER_OPTION, RECURSIVE_OPTION, RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH };

        assertDoesNotThrow(() -> rmApplication.run(args, inputStream, outputStream));

        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRun_joinedEmptyFolderAndRecursiveOption_emptyDir_shouldRemove() {
        expected = new String[] {
                RmTestUtil.EMPTY_DIR,
                RmTestUtil.FILE_ONE,
                RmTestUtil.FILE_TWO,
        };
        String[] args = { EMPTY_FOLDER_RECURSIVE_OPTION, RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH };

        assertDoesNotThrow(() -> rmApplication.run(args, inputStream, outputStream));

        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    /**
     * Modify the tdd's way expected RmException is used in test cases for consistency.
     * Instead of tdd's provided way of EXCEPTION_MESSAGE_HEADER + <error message>,
     * we use new RmException(message).getMessage().
     */
    @Test
    public void testRun_invalidRemoveArguments_shouldThrowRmException() {
        String[] args = { EMPTY_FOLDER_RECURSIVE_OPTION };

        Exception exception = assertThrows(RmException.class, () -> rmApplication.run(args, inputStream, outputStream));
        assertEquals(new RmException(ERR_MISSING_ARG).getMessage(), exception.getMessage());
    }

    /**
     * Additional test cases added to tdd's RmApplicationTest.java following the way the tdd's test case is being created.
     */

    /**
     * Attempt to remove sub path of the current path.
     * For example: rm ..
     * Expected: Throws RmException ERR_IS_SUB_PATH
     */
    @Test
    public void testRun_inputSubPathOfCurrentPath_shouldThrowRmException() {
        String[] args = {".."};
        Exception exception = assertThrows(RmException.class, () -> rmApplication.run(args, inputStream, outputStream));
        assertEquals(new RmException(ERR_IS_SUB_PATH).getMessage(), exception.getMessage());
    }
}