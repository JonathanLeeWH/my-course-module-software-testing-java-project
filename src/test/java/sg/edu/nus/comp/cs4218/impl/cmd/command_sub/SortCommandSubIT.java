package sg.edu.nus.comp.cs4218.impl.cmd.command_sub;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SortCommandSubIT {
    private ApplicationRunner applicationRunner;
    private ArgumentResolver argumentResolver;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");
    private final Path testFile4 = Paths.get(TestFileUtils.TESTDATA_DIR + "testFileWithTestDataAbsoluteFilePath.txt");
    private final Path testFile5 = Paths.get(TestFileUtils.TESTDATA_DIR + "testFileWithTestDataFileName.txt");

    @BeforeEach
    public void setUp() {
        applicationRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    // Positive test cases
    @Test
    void testSortCommandAndEchoAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("sort", "-rnf", "`echo " + testFile1.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "CS4218: Software Testing" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortCommandAndPasteAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("sort", "-rnf", "`paste " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    @Disabled("Incompatible with Windows.")
    void testSortCommandAndSedAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("sort", "-r", "`sed \"s|" + testFile2.getFileName() +
                "|" + testFile2.toFile().getPath() + "|\" " + testFile5.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet."+ System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortCommandAndGrepAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("sort", "-f", "`grep \"test\" " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = System.lineSeparator() + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortCommandAndCutAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("sort", "-r", "`cut -c 1-100 " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet."+ System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortCommandAndLsAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("sort", "`ls -d " + TestFileUtils.TESTDATA_DIR + "`" + testFile3.toFile().getName());
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "001, 010" + System.lineSeparator() + "1.0, 5.0" + System.lineSeparator() +
                "2, 3" + System.lineSeparator() + "21, 4" + System.lineSeparator() +
                "22, 41" + System.lineSeparator() + "51, 15" + System.lineSeparator() + "551, 1200" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortCommandAndSortAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("sort", "`sort " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = System.lineSeparator() + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testSortCommandAndFindAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("sort", "`find " + TestFileUtils.TESTDATA_DIR +
                " -name \"" + testFile1.toFile().getName() + "\"`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "CS4218: Software Testing" + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }
}
