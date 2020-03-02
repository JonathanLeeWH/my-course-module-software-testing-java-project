package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.EnvironmentHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import static org.junit.jupiter.api.Assertions.*;

public class RegexArgumentTest {

    RegexArgument regexArgument;

    static String defaultDir = EnvironmentHelper.currentDirectory;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup(){
        regexArgument = new RegexArgument();
    }

    @AfterAll
    static void tearDownAfterAll(){
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
    }

    @Test
    void appendTest() {
        regexArgument.append('a');
        String actual = regexArgument.toString();
        String actualRegex = regexArgument.getRegex();
        boolean actualIsRegex = regexArgument.hasRegex();
        assertEquals("a", actual);
        assertEquals("\\Qa\\E", actualRegex);
        assertEquals(false, actualIsRegex);
    }

    @Test
    void appendAsteriskTest() {
        regexArgument.appendAsterisk();
        String actualPlainText = regexArgument.toString();
        String actualRegex = regexArgument.getRegex();
        assertEquals("*", actualPlainText);
        assertEquals("[^" + StringUtils.fileSeparator() + "]*", actualRegex);

    }

    @Test
    void mergeRegexArgumentWithoutRegexTest() {
        RegexArgument regexArgument2 = new RegexArgument("a");
        regexArgument.merge(regexArgument2);
        String actualPlainText = regexArgument.toString();
        String actualRegex = regexArgument.getRegex();
        boolean actualIsRegex = regexArgument.hasRegex();
        assertEquals("a", actualPlainText);
        assertEquals("\\Qa\\E", actualRegex);
        assertEquals(false, actualIsRegex);
    }

    @Test
    void mergeRegexArgumentWithRegexTest() {
        RegexArgument regexArgument2 = new RegexArgument();
        regexArgument2.appendAsterisk();
        regexArgument.merge(regexArgument2);
        String actualPlainText = regexArgument.toString();
        String actualRegex = regexArgument.getRegex();
        boolean actualIsRegex = regexArgument.hasRegex();
        assertEquals("*", actualPlainText);
        assertEquals("[^" + StringUtils.fileSeparator() + "]*", actualRegex);
        assertEquals(true, actualIsRegex);
    }

    @Test
    void mergeStringTest() {
        regexArgument.merge("a");
        String actual = regexArgument.toString();
        String actualRegex = regexArgument.getRegex();
        boolean actualIsRegex = regexArgument.hasRegex();
        assertEquals("a", actual);
        assertEquals("\\Qa\\E", actualRegex);
        assertEquals(false, actualIsRegex);
    }

    @Test
    void isEmptyTest() {
        assertTrue(regexArgument.isEmpty());
    }

    @Test
    void isNotEmptyTest(){
        regexArgument.appendAsterisk();
        assertFalse(regexArgument.isEmpty());
    }

    @Test
    void globFilesTest() throws IOException {
        String currentDirectory = EnvironmentHelper.currentDirectory;
        EnvironmentHelper.currentDirectory = tempDir.toString();

        File nonGlobFile = new File(tempDir.resolve("File1.txt").toString());
        File globFile1 = new File(tempDir.resolve("globFile1.txt").toString());
        File globFile2 = new File(tempDir.resolve("globFile2.txt").toString());

        nonGlobFile.createNewFile();
        globFile1.createNewFile();
        globFile2.createNewFile();

        regexArgument.merge("glob");
        regexArgument.appendAsterisk();
        List<String> actualOutput= new ArrayList<String>();
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("globFile1.txt");
        expectedOutput.add("globFile2.txt");
        actualOutput = regexArgument.globFiles();
        assertEquals(expectedOutput,actualOutput);
        assertEquals(2,regexArgument.globFiles().size());
        //to restore directory
        EnvironmentHelper.currentDirectory =currentDirectory;
    }
}