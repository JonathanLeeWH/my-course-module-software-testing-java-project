package Hackathon.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.impl.app.DiffApplication;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_SPACE;

public class DiffApplicationIT {

    private static DiffApplication diffApp;
    private static final String ORIGINAL_DIR = EnvironmentHelper.currentDirectory;
    private static final Path DIRECTORY = Paths.get("src", "test", "java", "Hackathon", "test");
    private static final String ABSOLUTE_PATH = DIRECTORY.toFile().getAbsolutePath();
    private static OutputStream stdout;

    private static final String IDENTICAL = " are identical";
    private static final String FILES = "Files ";
    private static final String DIFF1_FILE = ABSOLUTE_PATH + "/diff1.txt";
    private static final String DIFF1_FILENAME = "diff1.txt";
    private static final String DIFF1_IDENTICAL_FILE = ABSOLUTE_PATH + "/diff1-identical.txt"; // NOPMD
    private static final String DIFF1_IDENTICAL_FILENAME = "diff1-identical.txt"; // NOPMD
    private static final String DIFF1_BLANK_LINES_FILE = ABSOLUTE_PATH + "/diff1-blank-lines.txt"; // NOPMD
    private static final String DIFF1_BLANK_LINES_FILENAME = "diff1-blank-lines.txt"; // NOPMD
    private static final String DIFF2_FILE = ABSOLUTE_PATH + "/diff2.txt";
    private static final String DIFF2_FILENAME = "diff2.txt";

    private static final String DIFFDIR1 = ABSOLUTE_PATH + "/diffDir1";
    private static final String DIFFDIR1NAME = "diffDir1";
    private static final String DIFFDIR1_IDENTICAL = ABSOLUTE_PATH + "/diffDir1-identical"; // NOPMD
    private static final String DIFFDIR1NAME_IDENTICAL = "diffDir1-identical"; // NOPMD
    private static final String DIFFDIR2 = ABSOLUTE_PATH + "/diffDir2";
    private static final String DIFFDIR2NAME = "diffDir2";

    @BeforeEach
    void setUp() {
        diffApp = new DiffApplication();
        stdout = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() throws IOException {
        stdout.flush();
        EnvironmentHelper.currentDirectory = ORIGINAL_DIR;
    }



}
