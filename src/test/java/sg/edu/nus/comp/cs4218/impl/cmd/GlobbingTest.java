package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobbingTest {
    private static final Path DIRECTORY = Paths.get("src","test","java" , "tdd","util", "dummyTestFolder", "GlobbingTestFolder");
    private static final String RESOURCE_PATH = DIRECTORY.toString() + File.separator;
    private static final String FOLDER_WITH_FILE = RESOURCE_PATH + "B";
    private static final String FOLDER_FILE_PATH = FOLDER_WITH_FILE + File.separator;
    private static final String FILE_1 = RESOURCE_PATH + "globbing_bar.txt";
    private static final String FILE_2 = RESOURCE_PATH + "globbing_foo.txt";
    private static final String FILE_3 = RESOURCE_PATH + "image.bmp";
    private static final String FILE_4 = RESOURCE_PATH + "file4.aat";
    private static final String FILE_5 = RESOURCE_PATH + "file5.txt";
    private static final String FWF_FILE_1 = FOLDER_FILE_PATH + "text.txt";
    private static String emptyFolder;
    private static Path tempDir;

    private ArgumentResolver argumentResolver;

    @BeforeAll
    static void init() throws IOException {
        tempDir = Files.createTempDirectory(DIRECTORY.toAbsolutePath(), "emptyFolder");
        emptyFolder = RESOURCE_PATH + tempDir.getFileName();
    }

    @AfterAll
    static void tearDown() {
        tempDir.toFile().deleteOnExit();
    }

    @BeforeEach
    void setUp() {
        argumentResolver = new ArgumentResolver();
    }

    @Test
    void resolveOneArgumentSingleAsteriskOnlySuccess() throws AbstractApplicationException, ShellException {
        String input = RESOURCE_PATH + "*";
        List<String> expected = Arrays.asList(emptyFolder, FOLDER_WITH_FILE, FILE_1, FILE_2, FILE_3);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void resolveOneArgumentSingleAsteriskFileExtensionExistSuccess() throws AbstractApplicationException, ShellException {
        String input = RESOURCE_PATH + "*.txt";
        List<String> expected = Arrays.asList(FILE_1, FILE_2);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void resolveOneArgumentSingleAsteriskFileExtensionNonExistentSuccess() throws AbstractApplicationException, ShellException {
        String input = RESOURCE_PATH + "*.t";
        List<String> expected = Arrays.asList(input);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void resolveOneArgumentMultipleAsterisksInARowWorkTheSameAsSingleAsteriskSuccess() throws AbstractApplicationException, ShellException {
        // Double asterisks
        String input = RESOURCE_PATH + "**";
        List<String> expected = Arrays.asList(emptyFolder, FOLDER_WITH_FILE, FILE_1, FILE_2, FILE_3);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        // Multiple asterisks
        input = RESOURCE_PATH + "*******";
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void resolveOneArgumentMultipleAsteriskFileExtensionSuccess() throws AbstractApplicationException, ShellException {
        String input = RESOURCE_PATH + "*.t*";
        List<String> expected = Arrays.asList(FILE_1, FILE_2);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        input = RESOURCE_PATH + "*.t*t";
        expected = Arrays.asList(FILE_1, FILE_2);
        Collections.sort(expected);
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        input = RESOURCE_PATH + "*.*m*";
        expected = Arrays.asList(FILE_3);
        Collections.sort(expected);
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }


    @Test
    void resolveOneArgumentSingleAsteriskEmptyFolderSuccess() throws AbstractApplicationException, ShellException {
        String input = emptyFolder + File.separator + "*";
        // Not sure about the expected output
        List<String> expected = Arrays.asList(input);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void resolveOneArgumentSingleAsteriskNonExistentFolderSuccess() throws AbstractApplicationException, ShellException {
        String input = RESOURCE_PATH + File.separator + "nonExistent" + File.separator + "*";
        List<String> expected = Arrays.asList(input);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void resolveOneArgumentSingleAsteriskFolderWithFileSuccess() throws AbstractApplicationException, ShellException {
        String input = FOLDER_FILE_PATH + "*";
        List<String> expected = Arrays.asList(FWF_FILE_1);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void resolveOneArgumentSingleAsteriskFolderWithFileFileExtensionExistSuccess() throws AbstractApplicationException, ShellException {
        String input = FOLDER_FILE_PATH + "*.txt";
        List<String> expected = Arrays.asList(FWF_FILE_1);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void resolveOneArgumentSingleAsteriskFolderWithFileFileExtensionNonExistentSuccess() throws AbstractApplicationException, ShellException {
        String input = FOLDER_FILE_PATH + "*.t";
        List<String> expected = Arrays.asList(input);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void resolveOneArgumentMultipleAsterisksInARowFolderWithFileWorkTheSameAsSingleAsteriskSuccess() throws AbstractApplicationException, ShellException {
        // Double asterisks
        String input = FOLDER_FILE_PATH + "**";
        List<String> expected = Arrays.asList(FWF_FILE_1);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        // Multiple asterisks
        input = FOLDER_FILE_PATH + "*******";
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void resolveOneArgumentMultipleAsteriskFolderWithFileFileExtensionSuccess() throws AbstractApplicationException, ShellException {
        String input = FOLDER_FILE_PATH + "*.t*";
        List<String> expected = Arrays.asList(FWF_FILE_1);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        input = FOLDER_FILE_PATH + "*.t*t";
        expected = Arrays.asList(FWF_FILE_1);
        Collections.sort(expected);
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

    }
}
