package tdd.ef1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.impl.app.CdApplication;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

/**
 * Modify tdd's CdApplicationTest.java expected exception message as the format of the CdException message thrown differs in implementation.
 * The tdd's CdApplicationTest.java should be run with our CdApplicationTest.java for better coverage.
 * Permissions related test cases are disabled especially since even with workaround Windows ATTRIB only support readOnly and does not support executeOnly etc.
 * Read more: https://web.csulb.edu/~murdock/attrib.html
 */
@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.LongVariable"})
class CdApplicationTest {

    private CdApplication app;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String results;//NOPMD

    private static final String ABSOLUTE_PATH_PATH_EXISTS = System.getProperty("user.dir") +  StringUtils.CHAR_FILE_SEP + "src";//NOPMD
    private static final String ABSOLUTE_PATH_PATH_DONT_EXISTS = System.getProperty("user.dir") + StringUtils.CHAR_FILE_SEP + "abcdef";

    private static final String RELATIVE_PATH_PATH_EXISTS = "src";
    private static final String RELATIVE_PATH_PATH_DONT_EXISTS = "abcdef";
    private static final String RELATIVE_PATH_NOT_DIR = "README.md";

    private static final String FILE_NOT_FOUND = ": No such file or directory";
    private static final String IS_NOT_DIR = ": Not a directory";
    private static final String NO_READ_PERM = ": Permission denied";
    private static final String NO_ARGS = ": Insufficient arguments";
    private static final String NULL_POINTER_EXCEPTION = ": Null Pointer Exception";
    private static final String NULL_ARGS = ": Null arguments";
    private static final String TOO_MANY_ARGS = ": Too many arguments";

    private static final String CD_PATH = System.getProperty("user.dir") + StringUtils.CHAR_FILE_SEP + "cd_test" + StringUtils.CHAR_FILE_SEP;
    private File testDir;

    @BeforeEach
    public void setUp() throws IOException {
        app = new CdApplication();
        outputStream = new ByteArrayOutputStream();
        testDir = new File(CD_PATH);
        testDir.mkdir();
        testDir.setExecutable(false);
    }

    @AfterEach
    public void tearDown() throws CdException {
        testDir.delete();
        results = "";
        app.changeToDirectory(System.getProperty("user.dir"));
    }

    @Test
    public void testChangeToDirectory_absolutePath_pathExists_shouldCd() throws CdException {
        app.changeToDirectory(ABSOLUTE_PATH_PATH_EXISTS);
        assertEquals(ABSOLUTE_PATH_PATH_EXISTS, EnvironmentHelper.currentDirectory);
    }

    /**
     * Modify original tdd's version to throw CdException with ERR_FILE_NOT_FOUND
     */
    @Test
    public void testChangeToDirectory_absolutePath_pathDontExists_shouldThrowCdException() throws CdException {
        Exception exception = assertThrows(Exception.class, () -> {
            app.changeToDirectory(ABSOLUTE_PATH_PATH_DONT_EXISTS);
        });
        assertEquals(new CdException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());//NOPMD
    }

    @Test
    public void testChangeToDirectory_relativePath_pathExists_shouldCd() throws CdException {
        app.changeToDirectory(RELATIVE_PATH_PATH_EXISTS);
        assertEquals(System.getProperty("user.dir") + StringUtils.CHAR_FILE_SEP + RELATIVE_PATH_PATH_EXISTS, EnvironmentHelper.currentDirectory);
    }

    /**
     * Modify original tdd's version to throw CdException with ERR_FILE_NOT_FOUND
     */
    @Test
    public void testChangeToDirectory_relativePath_pathDontExists_shouldThrowCdException() {
        Exception exception = assertThrows(Exception.class, () -> {
            app.changeToDirectory(RELATIVE_PATH_PATH_DONT_EXISTS);
        });
        assertEquals(new CdException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    /**
     * Modify original tdd's version to throw CdException with ERR_IS_NOT_DIR
     */
    @Test
    public void testChangeToDirectory_isNotADirectory() {
        Exception exception = assertThrows(Exception.class, () -> {
            app.changeToDirectory(RELATIVE_PATH_NOT_DIR);
        });
        assertEquals(new CdException(ERR_IS_NOT_DIR).getMessage(), exception.getMessage());
    }

    /**
     * Ignore tdd's test cases involving permissions as stated in our assumptions we assume all files and folders have correct permissions for commands to execute properly due to difference in behaviour in setting file permissions using Java API between filesystems as well as operating system
     * You can read more about it on our Assumptions report.
     */
    @Disabled("This test case is disabled as it does not match our assumption and Windows Attribute ATTRIB only support read only permission")
    @Test
    public void testChangeToDirectory_noReadPermission() {
        Exception exception = assertThrows(Exception.class, () -> {
            app.changeToDirectory(CD_PATH);
        });
        assertEquals("cd: " + CD_PATH + NO_READ_PERM, exception.getMessage());
    }

    /**
     * Modify original tdd's version to throw CdException with ERR_NO_ARGS
     */
    @Test
    public void testChangeToDirectory_emptyPathString() {
        Exception exception = assertThrows(Exception.class, () -> {
            app.changeToDirectory("");
        });
        assertEquals(new CdException(ERR_NO_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Modify original tdd's version to throw CdException with ERR_NULL_ARGS
     */
    @Test
    public void testRun_nullArgs_shouldThrowCdException() {
        String[] args = null;
        inputStream = new ByteArrayInputStream("abc".getBytes());
        Exception exception = assertThrows(Exception.class, () -> {
            app.run(args, inputStream, outputStream);
        });
        assertEquals(new CdException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Modify original tdd's version to throw CdException with ERR_NULL_STREAMS
     */
    @Test
    public void testRun_nullInputStream() {
        String[] args = {RELATIVE_PATH_PATH_EXISTS};
        inputStream = null;
        Exception exception = assertThrows(Exception.class, () -> {
            app.run(args, inputStream, outputStream);
        });
        assertEquals(new CdException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    /**
     * Modify original tdd's version to throw CdException with ERR_NULL_STREAMS
     */
    @Test
    public void testRun_nullOutputStream() {
        String[] args = {RELATIVE_PATH_PATH_EXISTS};
        inputStream = new ByteArrayInputStream("abc".getBytes());
        Exception exception = assertThrows(Exception.class, () -> {
            app.run(args, inputStream, null);
        });
        assertEquals(new CdException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    /**
     * Modify original tdd's version to throw CdException with ERR_TOO_MANY_ARGS
     */
    @Test
    public void testRun_tooManyArgs() {
        String[] args = {RELATIVE_PATH_PATH_EXISTS, ABSOLUTE_PATH_PATH_EXISTS};
        inputStream = new ByteArrayInputStream("abc".getBytes());
        Exception exception = assertThrows(Exception.class, () -> {
            app.run(args, inputStream, outputStream);
        });
        assertEquals(new CdException(ERR_TOO_MANY_ARGS).getMessage(), exception.getMessage());
    }
}
