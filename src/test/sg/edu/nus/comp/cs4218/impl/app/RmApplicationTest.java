package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.RmException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RmApplicationTest {

//    @BeforeEach
//    void setUp() {
//
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void remove() {
//    }

    @Test
    void removeFileOnlyWhenFileExistsShouldDeleteFile(@TempDir Path tempDir) throws IOException, RmException {
        RmApplication rmApplication = new RmApplication();
        Path fileToBeDeleted = tempDir.resolve("1.txt");
        List<String> lines = Arrays.asList("1", "2", "3");
        Files.write(fileToBeDeleted, lines);

        assertTrue(Files.exists(fileToBeDeleted));

        rmApplication.removeFileOnly(fileToBeDeleted.toFile());

        assertFalse(Files.exists(fileToBeDeleted));
    }

    @Test
    void removeFileOnlyWhenFileAbsentShouldThrowRmException(@TempDir Path tempDir) throws IOException, RmException {
        RmApplication rmApplication = new RmApplication();
        Path fileToBeDeleted = tempDir.resolve("1.txt");

        assertFalse(Files.exists(fileToBeDeleted)); // check to ensure file does not exist initially.

        Exception exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFileOnly(fileToBeDeleted.toFile());
        });
    }

//    @Test
//    void removeFileAndEmptyFolderOnly() {
//    }
//
//    @Test
//    void removeFilesAndFolderContent() {
//    }
//
//    @Test
//    void run() {
//    }
}