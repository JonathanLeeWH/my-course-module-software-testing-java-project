package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.FindException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class FindArgumentsTest {
    private FindArguments findArguments;

    @BeforeEach
    public void setUp() {
        findArguments = new FindArguments();
    }

    // Positive test cases
    @Test
    public void testParseWithNoArgsShouldRunSuccessfully() throws Exception {
        assertThrows(FindException.class, () -> findArguments.parse(null));
    }

    @Test
    public void testParseWithEmptyArgShouldRunSuccessfully() throws Exception {
        assertThrows(FindException.class, () -> findArguments.parse(""));
    }

    @Test
    public void testParseWitNameFlagShouldRunSuccessfully() throws Exception {
        findArguments.parse("-name", "file1");
        String expectedOutput = "file1";
        assertEquals(expectedOutput,findArguments.getFilename());
    }

    @Test
    public void testParseWitNameFlagBeforeFolderShouldThrow() throws Exception {
        assertThrows(FindException.class, () -> findArguments.parse("-name", "file1","folder1"));

    }

    @Test
    public void testParseFileWithFileSeparatorThrowError() throws Exception {
        assertThrows(FindException.class, () -> findArguments.parse("-name","file"+ File.separator));

    }

    @Test
    public void testParseDirectoryShouldRunSuccessfully() throws Exception {
        findArguments.parse("folder1" ,"-name" , "file1");
        String fileName = "file1";
        Set<String> directories = new HashSet<>();
        directories.add("folder1");
        assertEquals(fileName,findArguments.getFilename());
        assertEquals(directories,findArguments.getDirectories());
    }

}
