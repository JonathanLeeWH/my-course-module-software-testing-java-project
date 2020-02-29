package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.AdditionalMatchers;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.app.FindApplication;
import sg.edu.nus.comp.cs4218.impl.app.LsApplication;
import sg.edu.nus.comp.cs4218.impl.app.RmApplication;
import sg.edu.nus.comp.cs4218.impl.app.WcApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class ApplicationRunnerIT {

    private ApplicationRunner appRunner;

    @BeforeEach
    void setUp() {
        appRunner = new ApplicationRunner();
    }


    /**
     * Tests runApp method when input app is rm, execute RmApplication.
     * For example: rm 1.txt
     * Where 1.txt exists.
     * Expected: 1.txt is removed.
     */
    @Test
    void runAppWhenInputRmAppShouldExecuteRmApplication(@TempDir Path tempDir) throws AbstractApplicationException, ShellException, IOException {
        Path file1 = tempDir.resolve("1.txt");
        String[] argsList = {file1.toString()};
        Files.createFile(file1);
        assertTrue(Files.exists(file1)); // check 1.txt exists.
        appRunner.runApp("rm", argsList, mock(InputStream.class), mock(OutputStream.class));
        assertFalse(Files.exists(file1));
    }

    /**
     * Tests runApp method when input app is exit, execute ExitApplication.
     * For example: exit
     * Expected: Throws ExitException with exit code 0.
     */
    @Test
    void runAppWhenInputExitAppShouldExecuteExitApplication() {
        ExitException exception = assertThrows(ExitException.class, () -> {
            appRunner.runApp("exit", null, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new ExitException("0").getMessage(), exception.getMessage());
    }

    /**
     * Tests runApp method when input app is echo, execute EchoApplication.
     * For example: echo hello world
     * Expected: Outputstream should contain hello world.
     */
    @Test
    void runAppWhenInputEchoAppShouldExecuteEchoApplication() throws AbstractApplicationException, ShellException {
        String[] argsList = {"hello", "world"};
        OutputStream outputStream = new ByteArrayOutputStream();
        appRunner.runApp("echo", argsList, mock(InputStream.class), outputStream);
        assertEquals("hello world" + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests runApp method when input app is cd, execute CdApplication.
     * For example: cd ..
     * Expected: Sets EnvironmentHelper.currentDirectory to the full absolute path (converted from the non absolute/relative path.
     * In this case, the current directory is changed to the parent folder path of the present working directory.
     */
    @Test
    void runAppWhenInputCdAppShouldExecuteCdApplication() throws AbstractApplicationException, ShellException {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        String[] argsList = {".."};
        String parentAbsPath = Paths.get(EnvironmentHelper.currentDirectory).getParent().toString();

        assertFalse(new File(argsList[0]).toPath().isAbsolute());

        appRunner.runApp("cd", argsList, mock(InputStream.class), mock(OutputStream.class));
        String newPath = EnvironmentHelper.currentDirectory;

        assertEquals(parentAbsPath, newPath);
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir"); // reset environment directory to default
    }
}
