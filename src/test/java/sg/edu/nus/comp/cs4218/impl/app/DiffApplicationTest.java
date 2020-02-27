package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DiffApplicationTest {

    private static final String FILE_ONE_TEXT = "Same line" + System.lineSeparator() + "Different line";
    private static final String FILE_TWO_TEXT = "Same line" + System.lineSeparator() + "Same line";
    private static final String FILE_ONE_NAME = "fileOne";
    private static final String FILE_TWO_NAME = "fileTwo";
    private static final String FILE_FORMAT = ".txt";
    private static final String NO_DIFF_OUTPUT = FILE_ONE_NAME + " and " + FILE_TWO_NAME + " have no difference";
    private static DiffApplication diffApplication;
    private static File fileOne;
    private static File fileTwo;
    private static InputStream stdinOne, stdinTwo;
    private static OutputStream stdoutOne, stdoutTwo;
    private static boolean isShowSame, isNoBlank, isSimple;

    @BeforeAll
    public static void setUp() throws Exception {
        diffApplication = new DiffApplication();
        fileOne = File.createTempFile(FILE_ONE_NAME, FILE_FORMAT);
        fileTwo = File.createTempFile(FILE_TWO_NAME, FILE_FORMAT);
        stdinOne = new FileInputStream(fileOne);
        stdinTwo = new FileInputStream(fileTwo);
        stdoutOne = new FileOutputStream(fileOne);
        stdoutTwo = new FileOutputStream(fileTwo);
        stdoutOne.write(FILE_ONE_TEXT.getBytes());
        stdoutTwo.write(FILE_TWO_TEXT.getBytes());
    }

    @AfterAll
    public static void tearDown() throws Exception {
        fileOne.deleteOnExit();
        fileTwo.deleteOnExit();
    }
/*
    @Test
    public void execute_nullStdout_throwDiffException() throws Exception {
        String[] args = {FILE_ONE_NAME, FILE_TWO_NAME};
        diffApplication.run(args, null, null);
        //fail();
    }


    @Test
    public void execute_onlyOneArg_throwDiffException() throws Exception {
        String[] args = {FILE_ONE_NAME};
        diffApplication.run(args, stdinOne, stdoutOne);
        //fail();
    }

    @Test
    public void execute_nullStdin_throwDiffException() throws Exception {
        String[] args = {FILE_ONE_NAME, "-"};
        diffApplication.run(args, null, stdoutOne);
       // fail();
    }

    @Test
    public void execute_twoSameFiles_success() throws Exception {
        String[] args = {"-s", FILE_ONE_NAME, FILE_ONE_NAME};
        diffApplication.run(args, null, stdoutOne);
        //assertEquals(NO_DIFF_OUTPUT, stdoutOne.toString());
        //fail();
    }

 */
}