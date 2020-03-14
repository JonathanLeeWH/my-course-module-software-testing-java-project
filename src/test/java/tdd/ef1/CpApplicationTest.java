package tdd.ef1;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.impl.app.CpApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

/**
 * Modify tdd's test method to include check for assert throw message. (Not initially provided in tdd's version)
 * The tdd's CpApplicationTest.java should be run with our CpApplicationTest.java for better coverage.
 */
class CpApplicationTest {

    static final InputStream INPUT_STREAM = mock(InputStream.class);
    static final OutputStream OUTPUT_STREAM = mock(OutputStream.class);
    static final String FILE_NAME_1 = "file1.txt";
    static final File FILE_1 = new File(FILE_NAME_1);
    static final String FILE_NAME_2 = "file2.txt";
    static final File FILE_2 = new File(FILE_NAME_2);
    static final String FILE_1_DATA = "hello world";
    static final String FILE_2_DATA = "lol more stuff here";
    CpApplication cpApplication;

    @BeforeEach
    void setUp() {
        cpApplication = new CpApplication();
        try {
            FILE_1.createNewFile();
            FILE_2.createNewFile();
            try (FileWriter fw1 = new FileWriter(FILE_NAME_1); FileWriter fw2 = new FileWriter(FILE_NAME_2)) {
                fw1.write(FILE_1_DATA);
                fw2.write(FILE_2_DATA);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        FILE_1.delete();
        FILE_2.delete();
    }

    void assertFileContentsEqual(String expected, Path path) {
        String actual = null;
        try {
            actual = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(expected, actual);
    }

    /**
     * Modify tdd's test method to include check for assert throw message. (Not initially provided in tdd's version)
     */
    @Test
    @DisplayName("should throw error if null args")
    void throwsExceptionNullArgs() {
        CpException exception = assertThrows(CpException.class, () -> cpApplication.run(null, INPUT_STREAM, OUTPUT_STREAM));
        assertEquals(new CpException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Modify tdd's test method to include check for assert throw message. (Not initially provided in tdd's version)
     */
    @Test
    @DisplayName("should throw error if not enough args")
    void throwsExceptionNotEnoughArgs() {
        CpException exception = assertThrows(CpException.class, () -> cpApplication.run(new String[]{"src.txt"}, INPUT_STREAM, OUTPUT_STREAM));
        assertEquals(new CpException(ERR_MISSING_ARG).getMessage(), exception.getMessage());
    }

    @Nested
    @DisplayName("cpSrcFileToDestFileTest")
    class CpSrcFileToDestFileTests {

        /**
         * Modify tdd's test method to include check for assert throw message. (Not initially provided in tdd's version)
         */
        @Test
        @DisplayName("should throw exception if invalid src file")
        void throwsExceptionSrcFileNotFound() {
            CpException exception = assertThrows(CpException.class, () -> cpApplication.run(new String[]{"src.txt", "dest.txt"},
                    INPUT_STREAM, OUTPUT_STREAM));
            assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("should copy contents of one file to another")
        void copySrcFileToDest() throws AbstractApplicationException {
            cpApplication.run(new String[]{FILE_NAME_1, FILE_NAME_2}, INPUT_STREAM, OUTPUT_STREAM);
            assertFileContentsEqual(FILE_1_DATA, FILE_1.toPath());
            assertFileContentsEqual(FILE_1_DATA, FILE_2.toPath());
        }

        @Test
        @DisplayName("should create dest file if nonexistent and copy contents of src")
        void copySrcFileToDestandCreateDest() throws AbstractApplicationException {
            String destFile = "dest.txt";
            cpApplication.run(new String[]{FILE_NAME_1, destFile}, INPUT_STREAM, OUTPUT_STREAM);

            assertFileContentsEqual(FILE_1_DATA, FILE_1.toPath());
            assertFileContentsEqual(FILE_1_DATA, Paths.get(destFile));
            new File(destFile).delete();
        }
    }

    @Nested
    @DisplayName("CpFilesToFolderTests")
    class CpFilesToFolderTests {
        private static final String DIR_NAME = "destDir";
        private final File destDirectory = new File(DIR_NAME);
        private final File fileInDest = new File(destDirectory, FILE_NAME_2);
        private static final String DEST_DATA = "different content";

        @BeforeEach
        void setUp() {
            try {
                destDirectory.mkdir();
                fileInDest.createNewFile();
                try (FileWriter fileWriter = new FileWriter(fileInDest)) {
                    fileWriter.write(DEST_DATA);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        @AfterEach
        void tearDown() {
            for (File f : destDirectory.listFiles()) {
                f.delete();
            }
            destDirectory.delete();
        }

        /**
         * Modify tdd's test method to include check for assert throw message. (Not initially provided in tdd's version)
         */
        @Test
        @DisplayName("should throw exception when invalid src file but copy the rest")
        void throwExceptionInvalidSrcFilesAndCopyValidOnes() {
            CpException exception = assertThrows(CpException.class, () -> cpApplication.run(new String[]{FILE_NAME_1, "invalid file name",
                    DIR_NAME}, INPUT_STREAM, OUTPUT_STREAM));

            assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());

            assertFileContentsEqual(FILE_1_DATA, FILE_1.toPath());
            assertFileContentsEqual(FILE_1_DATA, Paths.get(DIR_NAME, FILE_NAME_1));
            assertFileContentsEqual(DEST_DATA, fileInDest.toPath());
        }

        @Test
        @DisplayName("should copy one file to target folder")
        void copyFileToDestFolder() throws AbstractApplicationException {
            cpApplication.run(new String[]{FILE_NAME_1, DIR_NAME}, INPUT_STREAM, OUTPUT_STREAM);

            assertFileContentsEqual(FILE_1_DATA, FILE_1.toPath());
            assertFileContentsEqual(FILE_1_DATA, Paths.get(DIR_NAME, FILE_NAME_1));
            assertFileContentsEqual(DEST_DATA, fileInDest.toPath());
        }

        @Test
        @DisplayName("should copy multiple file to target folder")
        void copyMultiFilesToDestFolder() throws AbstractApplicationException {
            cpApplication.run(new String[]{FILE_NAME_1, FILE_NAME_2, DIR_NAME}, INPUT_STREAM, OUTPUT_STREAM);

            assertFileContentsEqual(FILE_1_DATA, FILE_1.toPath());
            assertFileContentsEqual(FILE_1_DATA, Paths.get(DIR_NAME, FILE_NAME_1));
            assertFileContentsEqual(FILE_2_DATA, fileInDest.toPath());
        }
    }
}