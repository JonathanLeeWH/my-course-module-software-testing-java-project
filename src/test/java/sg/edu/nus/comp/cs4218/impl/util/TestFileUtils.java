package sg.edu.nus.comp.cs4218.impl.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestFileUtils {
    public static final String TESTDATA_DIR = "src/test/sg/edu/nus/comp/cs4218/testdata/";

    public static void createEmptyFile(Path file) throws IOException {
        Files.createFile(file);
    }

    public static void createAndWriteToFile(Path file, String content) throws IOException {
        Files.createFile(file);
        Files.write(file, content.getBytes());
    }

    public static void deleteFile(Path file) throws IOException {
        Files.delete(file);
    }
}
