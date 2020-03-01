package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.SortInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public class SortApplicationTest {
    private SortApplication sortApplication;
    private String[] defaultSortArgs;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "11"+  System.lineSeparator() +
            "1 test 1 2" +  System.lineSeparator() + "5" + System.lineSeparator() + "+";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    void setUp() {
        sortApplication = new SortApplication();
        defaultSortArgs = Collections.singletonList("-n").toArray(new String[1]);
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    /**
     * Test cases with run().
     */
    // Error test cases
    @Test
    void testRunWithNullOutputStreamShouldThrowSortException() {
        Throwable thrown = assertThrows(SortException.class, () -> sortApplication.run(defaultSortArgs, ourTestStdin, null));
        assertEquals(thrown.getMessage(), SortApplication.COMMAND + ": " + ERR_NULL_STREAMS);
    }

    // Positive test cases
    @Test
    void testRunWithMultipleFilesShouldRunSuccessfully() throws SortException {
        sortApplication.run(Arrays.asList("-n", testFile3.toFile().getPath()).toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "001, 010" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() +
                "2, 3" + System.lineSeparator() + "21, 4" + System.lineSeparator() + "22, 41" + System.lineSeparator() +
                "51, 15" + System.lineSeparator() + "551, 1200" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testRunWithNoFilesShouldRunSuccessfully() throws SortException {
        sortApplication.run(Collections.singletonList("-nr").toArray(new String[1]), ourTestStdin, ourTestStdout);
        String expectedResult = "11" + System.lineSeparator() + "5" + System.lineSeparator() + "1 test 1 2" +
                System.lineSeparator() + "+" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    /**
     * Test cases with sortFromFiles().
     */
    // Error test cases
    @Test
    void testSortFromFilesUsingASingleFileWithFileNotFoundInDirShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> sortApplication.sortFromFiles(
                false, false, false,
                "no-file.txt"
        ));
        assertEquals(thrown.getMessage(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testSortFromFilesUsingMultipleFilesWithAtLeastOneFileNotFoundInDirShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> sortApplication.sortFromFiles(
                false, false, false,
                testFile3.toFile().toString(), "no-file.txt"
        ));
        assertEquals(thrown.getMessage(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testSortFromFilesWithNullFileNameShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> sortApplication.sortFromFiles(
                false, false, false,
                ((String[]) null)
        ));
        assertEquals(thrown.getMessage(), ERR_NULL_ARGS);
    }

    @Test
    void testSortFromFilesUsingASingleFileWithFileHasNoReadAccessShouldThrowException() {
        SortApplicationStubWithFileHasNoReadAccess sortApplicationStub = new SortApplicationStubWithFileHasNoReadAccess();
        Throwable thrown = assertThrows(Exception.class, () -> sortApplicationStub.sortFromFiles(
                true, false, false,
                testFile1.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), ERR_NO_PERM);
    }

    @Test
    void testSortFromFilesUsingASingleFileWithFilenameIsADirShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> sortApplication.sortFromFiles(
                true, false, false,
                TestFileUtils.TESTDATA_DIR
        ));
        assertEquals(thrown.getMessage(), ERR_IS_DIR);
    }
    // Positive test cases
    @Test
    void testSortFromFilesWithNoFlagArgsAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, false,
                testFile1.toFile().toString()
        );
        String expectedResult = "CS4218: Software Testing" + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithNoFlagArgsAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, false,
                testFile3.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "001, 010" + System.lineSeparator() + "001, 010" + System.lineSeparator() + "1.0, 5.0" +
                System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() + "2, 3" + System.lineSeparator() + "2, 3" +
                System.lineSeparator() + "21, 4" + System.lineSeparator() + "21, 4" + System.lineSeparator() + "22, 41" +
                System.lineSeparator() + "22, 41" + System.lineSeparator() + "51, 15" + System.lineSeparator() + "51, 15" +
                System.lineSeparator() + "551, 1200" + System.lineSeparator() + "551, 1200";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithNoFlagArgsAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, false,
                testFile2.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + "CS4218: Software Testing" + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumArgOnlyAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, false,
                testFile3.toFile().toString()
        );
        String expectedResult = "001, 010" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() +
                "2, 3" + System.lineSeparator() + "21, 4" + System.lineSeparator() + "22, 41" + System.lineSeparator() +
                "51, 15" + System.lineSeparator() + "551, 1200";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumArgOnlyAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, false,
                testFile1.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "CS4218: Software Testing" + System.lineSeparator() + "CS4218: Software Testing" + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumArgOnlyAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, false,
                testFile3.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "001, 010" + System.lineSeparator() +
                "1.0, 5.0" + System.lineSeparator() +
                "2, 3" + System.lineSeparator() +
                "21, 4" + System.lineSeparator() +
                "22, 41" + System.lineSeparator() +
                "51, 15" + System.lineSeparator() +
                "551, 1200" + System.lineSeparator() +
                "CS4218: Software Testing" + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderArgOnlyAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, false,
                testFile3.toFile().toString()
        );
        String expectedResult = "551, 1200" + System.lineSeparator() + "51, 15" + System.lineSeparator() + "22, 41" + System.lineSeparator() +
                "21, 4" + System.lineSeparator() + "2, 3" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() + "001, 010";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderArgOnlyAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, false,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderArgOnlyAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, false,
                testFile3.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "CS4218: Software Testing" + System.lineSeparator() +
                "551, 1200" + System.lineSeparator() + "51, 15" + System.lineSeparator() + "22, 41" + System.lineSeparator() +
                "21, 4" + System.lineSeparator() + "2, 3" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() + "001, 010";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithCaseIndependentArgOnlyAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, true,
                testFile2.toFile().toString()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithCaseIndependentArgOnlyAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, true,
                testFile3.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "001, 010" + System.lineSeparator() + "001, 010" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() +
                "1.0, 5.0" + System.lineSeparator() + "2, 3" + System.lineSeparator() + "2, 3" + System.lineSeparator() +
                "21, 4" + System.lineSeparator() + "21, 4" + System.lineSeparator() + "22, 41" + System.lineSeparator() +
                "22, 41" + System.lineSeparator() + "51, 15" + System.lineSeparator() + "51, 15" + System.lineSeparator() +
                "551, 1200" + System.lineSeparator() + "551, 1200";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithCaseIndependentArgOnlyAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, true,
                testFile1.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() + "CS4218: Software Testing" + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, false,
                testFile2.toFile().toString()
        );
        String expectedResult = "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet."
                + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, false,
                testFile1.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "CS4218: Software Testing" + System.lineSeparator() + "CS4218: Software Testing";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, false,
                testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "551, 1200" + System.lineSeparator() +
                "51, 15" + System.lineSeparator() +
                "22, 41" + System.lineSeparator() +
                "21, 4" + System.lineSeparator() +
                "2, 3" + System.lineSeparator() +
                "1.0, 5.0" + System.lineSeparator() +
                "001, 010" + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndCaseIndependentArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, true,
                testFile3.toFile().toString()
        );
        String expectedResult = "001, 010" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() + "2, 3" + System.lineSeparator() +
                "21, 4" + System.lineSeparator() + "22, 41" + System.lineSeparator() + "51, 15" + System.lineSeparator() + "551, 1200";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndCaseIndependentArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult =  System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndCaseIndependentArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, true,
                testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult =  System.lineSeparator() + System.lineSeparator() +
                "001, 010" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() + "2, 3" + System.lineSeparator() + "21, 4" + System.lineSeparator() +
                "22, 41" + System.lineSeparator() + "51, 15" + System.lineSeparator() + "551, 1200" + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderAndCaseIndependentArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, true,
                testFile2.toFile().toString()
        );
        String expectedResult = "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderAndCaseIndependentArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderAndCaseIndependentArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, true,
                testFile1.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "CS4218: Software Testing" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "551, 1200" + System.lineSeparator() + "51, 15" + System.lineSeparator() + "22, 41" + System.lineSeparator() + "21, 4" + System.lineSeparator()
                + "2, 3" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() + "001, 010";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, true,
                testFile1.toFile().toString()
        );
        String expectedResult = "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "CS4218: Software Testing" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the";
                assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." +
                System.lineSeparator() + System.lineSeparator() +  System.lineSeparator() +  System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, true,
                testFile1.toFile().toString(), testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." +
                System.lineSeparator() + "CS4218: Software Testing" + System.lineSeparator() + "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "551, 1200" + System.lineSeparator() + "51, 15" + System.lineSeparator() + "22, 41" + System.lineSeparator() + "21, 4" + System.lineSeparator() +
                "2, 3" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() + "001, 010" + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test cases with sortFromStdin().
     */
    // Error test cases
    @Test
    void testSortFromStdinWithNullInputStreamShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> sortApplication.sortFromStdin(
                false, false, false, null
        ));
        assertEquals(thrown.getMessage(), ERR_NULL_STREAMS);
    }

    // Single Test cases
    @Test
    void testSortFromStdinWithEmptyInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                false, false, false,
                new ByteArrayInputStream(new byte[0])
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    // Positive test cases
    @Test
    void testSortFromStdinWithNoFlagArgsAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                false, false, false,
                ourTestStdin
        );
        String expectedResult = "+" + System.lineSeparator() + "1 test 1 2" + System.lineSeparator() +
                "11" + System.lineSeparator() + "5";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithFirstWordNumArgOnlyAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                true, false, false,
                ourTestStdin
        );
        String expectedResult = "+" + System.lineSeparator() + "1 test 1 2" + System.lineSeparator() +
                "5" + System.lineSeparator() + "11";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithReverseOrderArgOnlyAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                false, true, false,
                ourTestStdin
        );
        String expectedResult = "5" + System.lineSeparator() + "11" + System.lineSeparator() +
                "1 test 1 2" + System.lineSeparator() + "+";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithCaseIndependentArgOnlyAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                false, false, true,
                ourTestStdin
        );
        String expectedResult = "+" + System.lineSeparator() + "1 test 1 2" + System.lineSeparator() +
                "11" + System.lineSeparator() + "5";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithFirstWordNumAndReverseOrderArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                true, true, false,
                ourTestStdin
        );
        String expectedResult = "11" + System.lineSeparator() + "5" + System.lineSeparator() +
                "1 test 1 2" + System.lineSeparator() + "+";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithFirstWordNumAndCaseIndependentArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                true, false, true,
                ourTestStdin
        );
        String expectedResult = "+" + System.lineSeparator() + "1 test 1 2" + System.lineSeparator() +
                "5" + System.lineSeparator() + "11";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithReverseOrderAndCaseIndependentArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                false, true, true,
                ourTestStdin
        );
        String expectedResult = "5" + System.lineSeparator() + "11" + System.lineSeparator() +
                "1 test 1 2" + System.lineSeparator() + "+";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                true, true, true,
                ourTestStdin
        );
        String expectedResult = "11" + System.lineSeparator() + "5" + System.lineSeparator() +
                "1 test 1 2" + System.lineSeparator() + "+";
        assertEquals(expectedResult, actualResult);
    }

    private class SortApplicationStub implements SortInterface {
        @Override
        public String sortFromFiles(Boolean isFirstWordNumber, Boolean isReverseOrder, Boolean isCaseIndependent, String... fileName) throws Exception {
            throw new AssertionError("This method should not be called");
        }

        @Override
        public String sortFromStdin(Boolean isFirstWordNumber, Boolean isReverseOrder, Boolean isCaseIndependent, InputStream stdin) throws Exception {
            throw new AssertionError("This method should not be called");
        }

        @Override
        public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
            throw new AssertionError("This method should not be called");
        }
    }

    private class SortApplicationStubWithFileHasNoReadAccess extends SortApplicationStub {
        @Override
        public String sortFromFiles(Boolean isFirstWordNumber, Boolean isReverseOrder, Boolean isCaseIndependent, String... fileName) throws Exception {
            throw new Exception(ERR_NO_PERM);
        }
    }
}
