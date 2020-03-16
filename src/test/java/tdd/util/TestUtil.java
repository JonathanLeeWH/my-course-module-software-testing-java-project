package tdd.util;

import sg.edu.nus.comp.cs4218.EnvironmentHelper;

import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("PMD.LongVariable")
public final class TestUtil {

    private TestUtil () {
        // empty constructor
    }

    public static Path resolveFilePath(String fileName) {
        Path currentDirectory = Paths.get(EnvironmentHelper.currentDirectory);
        return currentDirectory.resolve(fileName);
    }
}
