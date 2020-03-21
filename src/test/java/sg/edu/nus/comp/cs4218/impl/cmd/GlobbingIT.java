package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.impl.StringsArgListHelper;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.ByteArrayOutputStream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobbingIT {

    private static final String FILE_NAME_1 = "CS4218A.txt";
    private static final String FILE_NAME_2 = "A4218A.txt";
    private static final String FILE_NAME_3 = "CS3203A.txt";
    private static final String FILE_NAME_4 = "1.txt";
    private static final String FILE_NAME_5 = "2.txt";
    private static final String FOLDER_NAME_1 = "folder1";
    private static final String FILE_CONTENT_1 = "Hello world";
    private static final String FILE_CONTENT_2 = "How are you";
    private static final String FILE_CONTENT_3 = "1";
    private static final String FILE_CONTENT_4 = "2";
    private static final String FILE_CONTENT_5 = "3";
    private static final String FILE_CONTENT_6 = "A";
    private static final String FILE_CONTENT_7 = "B";
    private static final String FILE_CONTENT_8 = "AA";
    private static final String WC_APP = "wc";
    private static final String ECHO_APP = "echo";
    private static final String GREP_APP = "grep";
    private static final String CUT_APP = "cut";
    private static final String SORT_APP = "sort";
    private static final String FIND_APP = "find";
    private static final String LS_APP = "ls";
    private static final String PASTE_APP = "paste";
    private static final String SED_APP = "sed";
    private static final String INVALID_APP = "lsa";
    private static final String B_FLAG = "-b";
    private static final String C_FLAG = "-c";
    private static final String NAME_FLAG = "-name";
    private static final String RELATIVE_CURR = ".";
    private static final String REGEX_EXPR_1 = "s/^/> /";
    private static final String REGEX_EXPR_2 = "s/^/1/";

    private OutputStream outputStream;
    private InputStream inputStream;
    private CallCommand callCommand;

    @BeforeEach
    void setUp() {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir"); // reset current directory
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir"); // reset current directory
    }

    /**
     * Tests evaluate method ls and glob interaction
     * For example: ls *
     * Expected: Outputs correctly in lexographical order
     */
    @Test
    void testEvaluateLsCommandWithGlobInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {
        EnvironmentHelper.currentDirectory = tempDir.toString();
        Files.createFile(tempDir.resolve(FILE_NAME_1));
        Files.createFile(tempDir.resolve(FILE_NAME_2));
        Files.createFile(tempDir.resolve(FILE_NAME_3));
        List<String> argList = StringsArgListHelper.concantenateStringsToList(LS_APP , "*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(System.in, outputStream);
        assertEquals(FILE_NAME_2 + System.lineSeparator() + FILE_NAME_3 + System.lineSeparator() + FILE_NAME_1 + System.lineSeparator(), outputStream.toString());
    }




}
