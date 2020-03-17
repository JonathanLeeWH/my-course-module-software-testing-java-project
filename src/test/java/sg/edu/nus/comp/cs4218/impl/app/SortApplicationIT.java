package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class SortApplicationIT {
    private SortApplication sortApplication;
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
    void testSortApplicationAndSortArgumentWithNullOutputStreamShouldThrowSortException() {
        Throwable thrown = assertThrows(SortException.class, () -> sortApplication.run(Collections.singletonList("-nr").toArray(new String[1]), ourTestStdin, null));
        assertEquals(thrown.getMessage(), SortApplication.COMMAND + ": " + ERR_NULL_STREAMS);
    }

    // Positive test cases
    @Test
    void testSortApplicationAndSortArgumentUsingNoArgsWithSingleFileShouldRunSuccessfully() throws SortException {
        sortApplication.run(Collections.singletonList(testFile3.toFile().getPath()).toArray(new String[1]), ourTestStdin, ourTestStdout);
        String expectedResult = "001, 010" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() +
                "2, 3" + System.lineSeparator() + "21, 4" + System.lineSeparator() + "22, 41" + System.lineSeparator() +
                "51, 15" + System.lineSeparator() + "551, 1200" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingNoArgsAndInputStreamWithValidValuesShouldRunSuccessfully() throws SortException {
        sortApplication.run(new String[0], ourTestStdin, ourTestStdout);
        String expectedResult = "+" + System.lineSeparator() + "1 test 1 2" + System.lineSeparator() +
                "11" + System.lineSeparator() + "5" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingNoArgsAndInputStreamWithNoValuesShouldRunSuccessfully() throws SortException {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        sortApplication.run(new String[0], emptyInputStream, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingFirstWordNumberArgsOnlyWithSingleFileShouldRunSuccessfully() throws SortException {
        sortApplication.run(Arrays.asList("-n", testFile3.toFile().getPath()).toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "001, 010" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() +
                "2, 3" + System.lineSeparator() + "21, 4" + System.lineSeparator() + "22, 41" + System.lineSeparator() +
                "51, 15" + System.lineSeparator() + "551, 1200" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingReverseOrderArgsOnlyAndInputStreamWithValidValuesShouldRunSuccessfully() throws SortException {
        sortApplication.run(Collections.singletonList("-r").toArray(new String[1]), ourTestStdin, ourTestStdout);
        String expectedResult = "5" + System.lineSeparator() + "11" + System.lineSeparator() +
                "1 test 1 2" + System.lineSeparator() + "+" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingCaseIndependentArgsOnlyAndInputStreamWithNoValuesShouldRunSuccessfully() throws SortException {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        sortApplication.run(Collections.singletonList("-f").toArray(new String[1]), emptyInputStream, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingFirstWordNumberAndCaseIndependentArgsOnlyAndMultipleFilesShouldRunSuccessfully() throws SortException {
        sortApplication.run(Arrays.asList("-nf", testFile2.toFile().getPath(), testFile3.toFile().getPath()).toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult =  System.lineSeparator() + System.lineSeparator() +
                "001, 010" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() + "2, 3" + System.lineSeparator() + "21, 4" + System.lineSeparator() +
                "22, 41" + System.lineSeparator() + "51, 15" + System.lineSeparator() + "551, 1200" + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingFirstWordNumberAndReverseOrderArgsOnlyAndInputStreamWithValidValuesShouldRunSuccessfully() throws SortException {
        sortApplication.run(Collections.singletonList("-nr").toArray(new String[1]), ourTestStdin, ourTestStdout);
        String expectedResult = "11" + System.lineSeparator() + "5" + System.lineSeparator() + "1 test 1 2" +
                System.lineSeparator() + "+" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingFirstWordNumberAndReverseOrderArgsOnlyAndInputStreamWithNoValuesShouldRunSuccessfully() throws SortException {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        sortApplication.run(Collections.singletonList("-nr").toArray(new String[1]), emptyInputStream, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingAllFlagArgsAndMultipleFilesShouldRunSuccessfully() throws SortException {
        sortApplication.run(Arrays.asList("-nfr", testFile1.toFile().getPath()).toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "CS4218: Software Testing" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingAllFlagArgsAndInputStreamWithValidValuesShouldRunSuccessfully() throws SortException {
        sortApplication.run(Collections.singletonList("-fnr").toArray(new String[1]), ourTestStdin, ourTestStdout);
        String expectedResult = "11" + System.lineSeparator() + "5" + System.lineSeparator() +
                "1 test 1 2" + System.lineSeparator() + "+" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortApplicationAndSortArgumentUsingAllFlagArgsAndInputStreamWithNoValuesShouldRunSuccessfully() throws SortException {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        sortApplication.run(Collections.singletonList("-frn").toArray(new String[1]), emptyInputStream, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }
}
