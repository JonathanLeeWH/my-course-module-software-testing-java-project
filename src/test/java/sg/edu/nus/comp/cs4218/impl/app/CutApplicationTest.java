package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class CutApplicationTest {
    private CutApplication cutApplication;
    private String[] defaultCutArgs;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    public void setUp() {
        cutApplication = new CutApplication();
        defaultCutArgs = Arrays.asList("-c","8").toArray(new String[1]);
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    /**
     * Test cut application with run().
     */

    // Error Test cases
    @Test
    void testRunNullArgs() {
       Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(null, ourTestStdin, ourTestStdout));
       assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_NULL_ARGS);
    }

    @Test
    void testRunNullOutputStream() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(defaultCutArgs, ourTestStdin, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_NO_OSTREAM);
    }

    @Test
    void testRunWithInvalidOption() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(
                Arrays.asList("-x", "6", testFile1.toFile().getPath()).toArray(new String[3]), ourTestStdin, ourTestStdout
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ILLEGAL_FLAG_MSG + "x");
    }

    @Test
    void testRunUsingWithoutByteAndCharPos() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(
                Arrays.asList("6", testFile1.toFile().getPath()).toArray(new String[2]), ourTestStdin, ourTestStdout
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_MISSING_ARG);
    }

    @Test
    void testRunUsingByteAndCharPos() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(
                Arrays.asList("-b","-c", "6", testFile1.toFile().getPath()).toArray(new String[3]), ourTestStdin, ourTestStdout
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_TOO_MANY_ARGS);
    }

    // Positive test cases
    @Test
    void testRunUsingStdin() throws CutException {
        List<String> args = Arrays.asList("-b", "1");
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = "d\n";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testRunUsingFiles() throws CutException {
        List<String> args = Arrays.asList("-c", "6", testFile1.toFile().getPath());
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = "8\n" + "m\n" + "e\n" + "c\n" + "r\n" + "a\n";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testRunUsingBytePosAndSingleNumAndFileIsDash() throws Exception {
        List<String> args = Arrays.asList("-b", "15", "-");
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = "n\n";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testRunUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() throws Exception {
        List<String> args = Arrays.asList("-b", "10-19", testFile3.toFile().getPath(),
                testFile1.toFile().getPath(), testFile3.toFile().getPath(), "-", "-");
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "oftware Te\n" + "ödülè cö\n" +
                "ssion test\n" + "e of error\n" + "ce predict\n" + "kills on t\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "ringen\n";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    /**
     * Test cut application using cutFromFiles()
     */
    // Error Test cases
    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndASingleFileWithStartNumLessThanZero() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                false, true, false, -2, 5, testFile2.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndASingleFileWithEndNumLessThanZero() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                true, false, false, 12, 0, testFile1.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndASingleFileWithNumLessThanZero() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                false, true, false, (int) -12.44, 15, testFile1.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndASingleFileWithFileNotFoundInDir() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                true, false, false, (int) 12.44, 15, "noFile.txt"
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_FILE_NOT_FOUND);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndMultipleFilesWithAtLeastOneFileNotFoundInDir() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                false, true, false, (int) 12.44, 15, testFile1.toFile().getPath(),
                "no-File.txt"
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_FILE_NOT_FOUND);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndNullFilename() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                false, true, false, 1, 2, (String[]) null)
        );
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_GENERAL);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndFileNameWhereFileHasNoReadAccess() {
        testFile1.toFile().setReadable(false);
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                true, false, false, 1, 1, testFile1.toFile().getPath()
        ));
        testFile1.toFile().setReadable(true);
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_NO_PERM);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndFileNameWhereFilenameIsADir() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                true, false, false, 1, 1, TestFileUtils.TESTDATA_DIR
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_IS_DIR);
    }

    // Positive test cases
    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 6, 13, testFile1.toFile().getPath()
        );
        String expectedResult = "8w\n" + "šü\n" + "eo\n" + "cf\n" + "rp\n" + "al";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 13, 6, testFile1.toFile().getPath()
        );
        String expectedResult = "8w\n" + "šü\n" + "eo\n" + "cf\n" + "rp\n" + "al";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanAnyNumLinesInFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, 2 , testFile1.toFile().getPath()
        );
        String expectedResult = "S\n" + "h\n" + "n\n" + "o\n" + "e\n" + "r";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithEndNumHigherThanAnyNumLinesInFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 2, Integer.MAX_VALUE, testFile1.toFile().getPath()
        );
        String expectedResult = "S\n" + "h\n" + "n\n" + "o\n" + "e\n" + "r";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 6, 12, testFile1.toFile().getPath()
        );
        String expectedResult = "8: Soft\n" + "š mödü\n" + "egressi\n" + "cause o\n" + "rmance \n" + "al skil";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 12, 5, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumHigherThanAnyNumLinesInFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, Integer.MAX_VALUE, 715, testFile2.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithEndNumHigherThanAnyNumLinesInFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 600, Integer.MAX_VALUE, testFile2.toFile().getPath()
        );
        String expectedResult = "ique. Feugiat pretium nibh ipsum consequat nisl.\n" +
                "\n" +
                "orta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet.\n" +
                "\n" +
                "rttitor eget dolor morbi non arcu.";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSingleFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 5, 5, testFile1.toFile().getPath()
        );
        String expectedResult = "1\n" + "š\n" + "r\n" + "-\n" + "o\n" + "i";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSingleFileWithIndexHigherThanAnyNumLinesInFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, Integer.MAX_VALUE, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 2, 29, testFile1.toFile().getPath()
        );
        String expectedResult = "S\n" + "hp\n" + "no\n" + "oi\n" + "eo\n" + "rd";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 56, 18, testFile1.toFile().getPath()
        );
        String expectedResult = "T\n" + "se\n" + "si\n" + "o \n" + "co\n" + " n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanAnyNumLinesInFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, 76, testFile2.toFile().getPath()
        );
        String expectedResult = "p\n" + "\n" + "e\n" + "\n" + "u";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithEndNumHigherThanAnyNumLinesInFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 81, Integer.MAX_VALUE, testFile2.toFile().getPath()
        );
        String expectedResult = "n\n" + "\n" + "t\n" + "\n" + " ";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 3, 63, testFile1.toFile().getPath()
        );
        String expectedResult = "4218: Software Testing\n" +
                "ìš mödülè cövèrs thè concepts and prãctīće of software testin\n" +
                "d regression testing. Various testing coverage criteria will \n" +
                "ot-cause of errors in failing test cases will also be investi\n" +
                "rformance prediction, performance clustering and performance \n" +
                "ucial skills on testing and debugging through hands-on assign";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 18, 17, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumHigherThanAnyNumLinesInFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, Integer.MAX_VALUE, 17, testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "\n";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithEndNumHigherThanAnyNumLinesInFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 18, Integer.MAX_VALUE, testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSingleFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 103, 103, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "e\n" + "d\n" + "f\n" + "l\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSingleFileWithIndexHigherThanAnyNumLinesInFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 10, 12,
                testFile1.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "ot\n" + "öü\n" + "si\n" + "eo\n" + "c \n" + "kl\n" + "u \n" +
                "\n" + "us\n" + "\n" + "sa";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 115, 1,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "C\n" + "Te\n" + "a\n" + "r\n" + "p\n" + "c\n" + "La\n" + "\n" + "Et\n" +
                "\n" + "Tt\n" + "1\n" + "2\n" + "5\n" + "2\n" + "2\n" + "5\n" + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, 8,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = " \n" + "m\n" + "r\n" + "u\n" + "a\n" + " \n" + "p\n" + "\n" + " \n" + "\n" +
                "m\n" + "0\n" + "\n" + "\n" + "\n" + "\n" + "0\n" + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithEndNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 8, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = " \n" + "m\n" + "r\n" + "u\n" + "a\n" + " \n" + "p\n" + "\n" + " \n" + "\n" +
                "m\n" + "0\n" + "\n" + "\n" + "\n" + "\n" + "0\n" + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 1, 11,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "CS4218: Sof\n" + "Thìš möd\n" + "and regress\n" + "root-cause \n" + "performance\n" + "crucial ski\n" +
                "Lorem ipsum\n" + "\n" + "Euismod qui\n" + "\n" + "Turpis mass\n" +
                "1.0, 5.0\n" + "2, 3\n" + "51, 15\n" + "21, 4\n" + "22, 41\n" + "551, 1200\n" + "001, 010";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 9, 1,
                testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, Integer.MAX_VALUE, 1,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithEndNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 9, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "Software Testing\n" +
                "ödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing,\n" +
                "ession testing. Various testing coverage criteria will be discussed. Debugging methods for finding the\n" +
                "se of errors in failing test cases will also be investigated. The use öf testing and analysis for\n" +
                "nce prediction, performance clustering and performance debugging will be studied. Students will acquire\n" +
                "skills on testing and debugging through hands-on assignments.\n" +
                "sum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl.\n" +
                "\n" +
                "quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet.\n" +
                "\n" +
                "assa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidDistinctFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 9, 9,
                testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "S\n" + "ö\n" + "e\n" +
                "s\n" + "n\n" + "s\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "0\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidDistinctFilesWithIndexHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 1, 274,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "Ls\n" + "\n" + "Ed\n" + "\n" + "Tn\n" +
                "1\n" + "2\n" + "5\n" + "2\n" + "2\n" + "5\n" + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 274, 1,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "Ls\n" + "\n" + "Ed\n" + "\n" + "Tn\n" +
                "1\n" + "2\n" + "5\n" + "2\n" + "2\n" + "5\n" + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, 1,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "L\n" + "\n" + "E\n" + "\n" + "T\n" +
                "1\n" + "2\n" + "5\n" + "2\n" + "2\n" + "5\n" + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithEndNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 274, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "s\n" + "\n" + "d\n" + "\n" + "n";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 1, 200,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cur\n" +
                "\n" +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverr\n" +
                "\n" +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehi\n" +
                "CS4218: Software Testing\n" +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing,\n" +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the\n" +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for\n" +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire\n" +
                "crucial skills on testing and debugging through hands-on assignments.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 200, 1,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, Integer.MAX_VALUE, 1,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithEndNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 200, Integer.MAX_VALUE,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "rsus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl.\n" +
                "\n" +
                "ra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet.\n" +
                "\n" +
                "icula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu.\n" +
                "\n" + "\n" + "\n" + "\n" + "\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidDistinctFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 250, 250,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "t\n" + "\n" + "a\n" + "\n" + "i\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidDistinctFilesWithIndexHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 32, 48,
                testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "si\n" + "\n" + "pn\n" + "\n" +
                "r.\n" + "si\n" + "\n" + "pn\n" + "\n" + "r.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 120, 8,
                testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "pq\n" + "\n" + " u\n" + "\n" + "mr\n" +
                "pq\n" + "\n" + " u\n" + "\n" + "mr";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, 144,
                testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "i\n" + "\n" + "g\n" + "\n" + "s\n" + "i\n" + "\n" + "g\n" + "\n" + "s";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithEndNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 120, Integer.MAX_VALUE,
                testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "q\n" + "\n" + "u\n" + "\n" + "r\n" + "q\n" +
                "\n" + "u\n" + "\n" + "r";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 2, 18,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "S4218: Software T\n" + "hìš mödülè c\n" + "nd regression tes\n" + "oot-cause of erro\n" + "erformance predic\n" + "rucial skills on \n" +
                "S4218: Software T\n" + "hìš mödülè c\n" + "nd regression tes\n" + "oot-cause of erro\n" + "erformance predic\n" + "rucial skills on \n" +
                "S4218: Software T\n" + "hìš mödülè c\n" + "nd regression tes\n" + "oot-cause of erro\n" + "erformance predic\n" + "rucial skills on ";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 10, 8,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, Integer.MAX_VALUE, 8,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithEndNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 90, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "ït testing, integration testing,\n" + "thods for finding the\n" +
                " and analysis for\n" + " Students will acquire\n" +
                "\n" + "\n" +
                "ït testing, integration testing,\n" + "thods for finding the\n" +
                " and analysis for\n" + " Students will acquire\n" +
                "\n" + "\n" +
                "ït testing, integration testing,\n" + "thods for finding the\n" +
                " and analysis for\n" + " Students will acquire\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSimilarFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 3, 3,
                testFile3.toFile().getPath(), testFile3.toFile().getPath(), testFile3.toFile().getPath(),
                testFile3.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "0\n" + " \n" + ",\n" + ",\n" + ",\n" + "1\n" + "1\n" +
                "0\n" + " \n" + ",\n" + ",\n" + ",\n" + "1\n" + "1\n" + "0\n" +
                " \n" + ",\n" + ",\n" + ",\n" + "1\n" + "1\n" + "0\n" + " \n" +
                ",\n" + ",\n" + ",\n" + "1\n" + "1\n" + "0\n" +
                " \n" + ",\n" + ",\n" + ",\n" + "1\n" + "1";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSimilarFilesWithIndexHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                testFile3.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 1, 4,
                testFile3.toFile().getPath(), testFile3.toFile().getPath(), testFile3.toFile().getPath(),
                testFile3.toFile().getPath()
        );
        String expectedResult = "1,\n" + "23\n" + "5 \n" + "2 \n" + "2 \n" + "5,\n" + "0,\n" +
                "1,\n" + "23\n" + "5 \n" + "2 \n" + "2 \n" + "5,\n" + "0,\n" +
                "1,\n" + "23\n" + "5 \n" + "2 \n" + "2 \n" + "5,\n" + "0,\n" +
                "1,\n" + "23\n" + "5 \n" + "2 \n" + "2 \n" + "5,\n" + "0,";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 6, 3,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua\n" +
                "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua\n" +
                "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua\n" +
                "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua\n" +
                "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua\n" +
                "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, 3,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "4\n" + "ì\n" + "d\n" + "o\n" + "r\n" + "u\n" +
                "4\n" + "ì\n" + "d\n" + "o\n" + "r\n" + "u\n" +
                "4\n" + "ì\n" + "d\n" + "o\n" + "r\n" + "u";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithEndNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 6, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "8\n" + "m\n" + "e\n" + "c\n" + "r\n" + "a\n" +
                "8\n" + "m\n" + "e\n" + "c\n" + "r\n" + "a\n" +
                "8\n" + "m\n" + "e\n" + "c\n" + "r\n" + "a";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 6, 17,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath()
        );
        String expectedResult = "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 16, 4,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, Integer.MAX_VALUE, 4,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithEndNumHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 55, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "re testing including unït testing, integration testing,\n" +
                "ria will be discussed. Debugging methods for finding the\n" + "e investigated. The use öf testing and analysis for\n" +
                "formance debugging will be studied. Students will acquire\n" + "on assignments.\n" + "\n" +
                "re testing including unït testing, integration testing,\n" + "ria will be discussed. Debugging methods for finding the\n" +
                "e investigated. The use öf testing and analysis for\n" + "formance debugging will be studied. Students will acquire\n" +
                "on assignments.\n" + "\n" + "re testing including unït testing, integration testing,\n" +
                "ria will be discussed. Debugging methods for finding the\n" + "e investigated. The use öf testing and analysis for\n" +
                "formance debugging will be studied. Students will acquire\n" + "on assignments.\n" + "\n" +
                "re testing including unït testing, integration testing,\n" + "ria will be discussed. Debugging methods for finding the\n" +
                "e investigated. The use öf testing and analysis for\n" + "formance debugging will be studied. Students will acquire\n" +
                "on assignments.\n" + "\n" + "re testing including unït testing, integration testing,\n" +
                "ria will be discussed. Debugging methods for finding the\n" + "e investigated. The use öf testing and analysis for\n" +
                "formance debugging will be studied. Students will acquire\n" + "on assignments.\n" + "\n" +
                "re testing including unït testing, integration testing,\n" + "ria will be discussed. Debugging methods for finding the\n" +
                "e investigated. The use öf testing and analysis for\n" + "formance debugging will be studied. Students will acquire\n" +
                "on assignments.\n" + "\n" + "re testing including unït testing, integration testing,\n" +
                "ria will be discussed. Debugging methods for finding the\n" + "e investigated. The use öf testing and analysis for\n" +
                "formance debugging will be studied. Students will acquire\n" + "on assignments.\n" + "\n" +
                "re testing including unït testing, integration testing,\n" + "ria will be discussed. Debugging methods for finding the\n" +
                "e investigated. The use öf testing and analysis for\n" + "formance debugging will be studied. Students will acquire\n" +
                "on assignments.\n" + "\n" + "re testing including unït testing, integration testing,\n" +
                "ria will be discussed. Debugging methods for finding the\n" + "e investigated. The use öf testing and analysis for\n" +
                "formance debugging will be studied. Students will acquire\n" + "on assignments.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSimilarFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 8, 8,
                testFile2.toFile().getPath(), testFile2.toFile().getPath(), testFile2.toFile().getPath(),
                testFile2.toFile().getPath()
        );
        String expectedResult = "p\n" + "\n" + " \n" + "\n" + "m\n" +
                "p\n" + "\n" + " \n" + "\n" + "m\n" +
                "p\n" + "\n" + " \n" + "\n" + "m\n" +
                "p\n" + "\n" + " \n" + "\n" + "m";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSimilarFilesWithIndexHigherThanAnyNumLinesInFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                testFile2.toFile().getPath(), testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test cut application using cutFromStdin()
     */
    // Error Test cases
    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLessThanZero() {
        Throwable thrown =
                assertThrows(CutException.class, () -> cutApplication.cutFromStdin(
                        false, true, false, -1, 15, ourTestStdin
                ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumLessThanZero() {
        Throwable thrown =
                assertThrows(CutException.class, () -> cutApplication.cutFromStdin(
                        true, false, false, 15, 0, ourTestStdin
                ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithNumLessThanZero() {
        Throwable thrown =
                assertThrows(CutException.class, () -> cutApplication.cutFromStdin(
                        false, true, false, (int) -12.65, (int) -5.5, ourTestStdin
                ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndNullInputStream() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromStdin(false, false, false, 1, 2, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_NULL_STREAMS);
    }

    // Single Test cases
    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithAnEmptyInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 4, 4,
                new ByteArrayInputStream(new byte[0])
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    // Positive Test cases
    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 1, 5,
                ourTestStdin
        );
        String expectedResult = "db";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 2, 1,
                ourTestStdin
        );
        String expectedResult = "dr";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanAnyNumLinesInInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, Integer.MAX_VALUE, 3,
                ourTestStdin
        );
        String expectedResult = "ü";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumHigherThanAnyNumLinesInInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 4, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "ü";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, true, 2, 13,
                ourTestStdin
        );
        String expectedResult = "rüberspring";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, true, 11, 7,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumHigherThanAnyNumLinesInInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, true, Integer.MAX_VALUE, 13,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithEndNumHigherThanAnyNumLinesInInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, true, 15, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 5, 5,
                ourTestStdin
        );
        String expectedResult = "b";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithIndexHigherThanAnyNumLinesInInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 14, 15,
                ourTestStdin
        );
        String expectedResult = "n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 14, 1,
                ourTestStdin
        );
        String expectedResult = "dn";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanAnyNumLinesInInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, Integer.MAX_VALUE, 1,
                ourTestStdin
        );
        String expectedResult = "d";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumHigherThanAnyNumLinesInInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 14, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, true, 4, 14,
                ourTestStdin
        );
        String expectedResult = "berspringen";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, true, 13, 9,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumHigherThanAnyNumLinesInInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, true, Integer.MAX_VALUE, 2,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithEndNumHigherThanAnyNumLinesInInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, true, 2, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "rüberspringen";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndSingleNumAndValidInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 4, 4,
                ourTestStdin
        );
        String expectedResult = "b";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndSingleNumAndValidInputStreamWithIndexHigherThanAnyNumLinesInInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }
}