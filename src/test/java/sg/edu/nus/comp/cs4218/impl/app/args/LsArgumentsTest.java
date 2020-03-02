package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class LsArgumentsTest {
    
    private LsArguments lsArguments;

    @BeforeEach
    public void setUp() {
        lsArguments = new LsArguments();
    }

    // Positive test cases
    @Test
    public void testParseWithNoArgsShouldRunSuccessfully() throws Exception {
        lsArguments.parse();
        List<String> expectedOutput = new ArrayList<String>();
        assertFalse(lsArguments.isFoldersOnly());
        assertFalse(lsArguments.isRecursive());
        assertEquals(expectedOutput,lsArguments.getFolders());
    }

    @Test
    public void testParseWithEmptyArgShouldRunSuccessfully() throws Exception {
        lsArguments.parse("");
        List<String> expectedOutput = new ArrayList<String>();
        assertFalse(lsArguments.isFoldersOnly());
        assertFalse(lsArguments.isRecursive());
        assertEquals(expectedOutput,lsArguments.getFolders());
    }


    @Test
    public void testParseWitRecursiveShouldRunSuccessfully() throws Exception {
        lsArguments.parse("-R");
         List<String> expectedOutput = new ArrayList<String>();
        assertFalse(lsArguments.isFoldersOnly());
        assertTrue(lsArguments.isRecursive());
        assertEquals(expectedOutput,lsArguments.getFolders());
    }

    @Test
    public void testParseWithFoldersOnlyShouldRunSuccessfully() throws Exception {
        lsArguments.parse("-d");
        List<String> expectedOutput = new ArrayList<String>();
        assertTrue(lsArguments.isFoldersOnly());
        assertFalse(lsArguments.isRecursive());
        assertEquals(expectedOutput,lsArguments.getFolders());
    }

    @Test
    public void testParseWithFolderShouldRunSuccessfully() throws Exception {
        lsArguments.parse("folder1");
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("folder1");
        assertFalse(lsArguments.isFoldersOnly());
        assertFalse(lsArguments.isRecursive());
        assertEquals(expectedOutput,lsArguments.getFolders());
    }

    @Test
    public void testParseFoldersOnlyWithFolderShouldRunSuccessfully() throws Exception {
        lsArguments.parse("-d" , "folder1");
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("folder1");
        assertTrue(lsArguments.isFoldersOnly());
        assertFalse(lsArguments.isRecursive());
        assertEquals(expectedOutput,lsArguments.getFolders());
    }
    @Test
    public void testParseCharAtZeroFolderShouldRunSuccessfully() throws Exception {
        lsArguments.parse("-folder1");
        List<String> expectedOutput = new ArrayList<String>();
        expectedOutput.add("-folder1");
        assertFalse(lsArguments.isFoldersOnly());
        assertFalse(lsArguments.isRecursive());
        assertEquals(expectedOutput,lsArguments.getFolders());
    }
}


