package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.exception.FindException;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindApplicationTest {

    private static FindApplication application;
    private static InputStream mockInputStream;
    private static OutputStream mockOutputStream;
    private static ByteArrayOutputStream mockBos;
    private static ArrayList<String> testFilePath;

    private static final String FOLDER_PATH = System.getProperty("user.dir");
    private static final String TEST_FOLDER = "testFolder";
    private static final String ANOTHER_FOLDER = "anotherTestFolder";
    private static final String NESTED_FOLDER = "nestedFolder";
    private static final String NUMERIC_FOLDER = "123";
    private static final String INVALID_FOLDER = "InvalidFolder";
    private static final String EMPTY_FOLDER = "emptyFolder";

    private static final String TEST_FILE_NAME = "test.txt";
    private static final String ANOTHER_FILE_NAME = "test2.txt";
    private static final String NUMERIC_FILE_NAME = "123.txt";
    private static final String INVALID_FILE_NAME = "InvalidFile.txt";
    private static final String INVALID_PATTERN = "[";

    private static final String NAME_FLAG = "-name";
    private static final String OUTPUT_ERROR_MSG = "find: output stream is null";
    public static final String MULTIPLE_FILES = "Only one filename is allowed";
    private static final String NOT_FOUND_MSG = ": No such file or directory";

    @BeforeAll
    static void setUpAll() throws IOException {
        testFilePath = new ArrayList<>();
        File baseFolder = new File(FOLDER_PATH + File.separator + EMPTY_FOLDER);
        if (baseFolder.mkdirs()) {
        }
        baseFolder = new File(FOLDER_PATH + File.separator + TEST_FOLDER);
        if (baseFolder.mkdirs()) {
            addFileToFolder(baseFolder, TEST_FILE_NAME);
            addFileToFolder(baseFolder, NUMERIC_FILE_NAME);
        }
        File baseNestedFolder = new File(baseFolder.getPath() + File.separator + NESTED_FOLDER);
        if (baseNestedFolder.mkdirs()) {
            addFileToFolder(baseNestedFolder, ANOTHER_FILE_NAME);
        }
        File numericFolder = new File(baseFolder.getPath() + File.separator + NUMERIC_FOLDER);
        if (numericFolder.mkdirs()) {
            addFileToFolder(numericFolder, NUMERIC_FILE_NAME);
        }
        baseFolder = new File(FOLDER_PATH + File.separator + ANOTHER_FOLDER);
        if (baseFolder.mkdirs()) {
            addFileToFolder(baseFolder, ANOTHER_FILE_NAME);
        }
    }
    @AfterAll
    static void tearDownAll() {
        for (String path : testFilePath) {
            File file = new File(path);
            file.delete();
        }
        File baseFolder = new File(FOLDER_PATH + File.separator + TEST_FOLDER);
        File anotherBaseFolder = new File(FOLDER_PATH + File.separator + ANOTHER_FOLDER);
        File baseNestedFolder = new File(baseFolder.getPath() + File.separator + NESTED_FOLDER);
        File numericFolder = new File(baseFolder.getPath() + File.separator + NUMERIC_FOLDER);
        File emptyFolder = new File(FOLDER_PATH + File.separator + EMPTY_FOLDER);

        emptyFolder.delete();
        baseNestedFolder.delete();
        anotherBaseFolder.delete();
        numericFolder.delete();
        baseFolder.delete();
    }

    @BeforeEach
    void setUp() {
        application = spy(new FindApplication());
        mockInputStream = Mockito.mock(InputStream.class);
        mockOutputStream = Mockito.mock(OutputStream.class);
        mockBos = Mockito.mock(ByteArrayOutputStream.class);
    }

    private static void addFileToFolder(File folder, String fileName) throws IOException {
        File file = new File(folder.getPath(), fileName);
        if (file.createNewFile()) {
            testFilePath.add(file.getPath());
        }
    }

    @Test
    void testFindSingleFolderContentFileExists() throws Exception {
        String expectedResult = TEST_FOLDER + File.separator + TEST_FILE_NAME;

        assertEquals(expectedResult, application.findFolderContent(TEST_FILE_NAME, TEST_FOLDER));
    }

    @Test
    void testFindMultipleFoldersContentSameFileExists() throws Exception {
        String[] folders = new String[] {TEST_FOLDER, ANOTHER_FOLDER};
        String expectedResult = TEST_FOLDER + File.separator + NESTED_FOLDER + File.separator + ANOTHER_FILE_NAME + StringUtils.STRING_NEWLINE +
                ANOTHER_FOLDER + File.separator + ANOTHER_FILE_NAME ;

        assertEquals(expectedResult, application.findFolderContent(ANOTHER_FILE_NAME, folders));
    }

    @Test
    void testFindMultipleFoldersContentOnlyOneContainsFile() throws Exception {
        String[] folders = new String[] {TEST_FOLDER, ANOTHER_FOLDER, EMPTY_FOLDER};
        String expectedResult = TEST_FOLDER + File.separator + NUMERIC_FILE_NAME + StringUtils.STRING_NEWLINE +
                TEST_FOLDER + File.separator + NUMERIC_FOLDER + File.separator + NUMERIC_FILE_NAME ;

        assertEquals(expectedResult, application.findFolderContent(NUMERIC_FILE_NAME, folders));
    }

    @Test
    void testFindMultipleFoldersContentFileNotExists() throws Exception {
        String[] folders = new String[] {TEST_FOLDER, ANOTHER_FOLDER, EMPTY_FOLDER};
        String expectedResult = "";

        assertEquals(expectedResult, application.findFolderContent(INVALID_FILE_NAME, folders));
    }

    @Test
    void testFindSingleFolderContentMultipleFileExists() throws Exception {
        String expectedResult = TEST_FOLDER + File.separator + NUMERIC_FILE_NAME + StringUtils.STRING_NEWLINE +
                TEST_FOLDER + File.separator + NUMERIC_FOLDER + File.separator + NUMERIC_FILE_NAME;

        assertEquals(expectedResult, application.findFolderContent(NUMERIC_FILE_NAME, TEST_FOLDER));
    }

    @Test
    void testFindFolderContentFileNotExistsNoFileFound() throws Exception {
        String fileNotExist = ".+" + TEST_FILE_NAME;
        String expectedResult = "";

        assertEquals(expectedResult, application.findFolderContent(fileNotExist, TEST_FOLDER));
    }


    @Test
    void testFindFolderNotExistsContent() throws Exception {
        String expectedResult = "find: " + INVALID_FOLDER + ": " + ErrorConstants.ERR_FILE_NOT_FOUND;
        assertEquals(expectedResult, application.findFolderContent(TEST_FILE_NAME, INVALID_FOLDER));
    }

    @Test
    void testFindFolderInvalidPattern() {
        assertThrows(FindException.class, () -> application.findFolderContent(INVALID_PATTERN, TEST_FOLDER));
    }

    @Test
    void testRunOnlyWithoutFileIndentifierInvalid() {
        //no File name syntax after -name
        String[] args = new String[] { TEST_FOLDER, ANOTHER_FOLDER, TEST_FILE_NAME };

        Exception actualException = assertThrows(FindException.class, () -> application.run(args, mockInputStream, mockOutputStream));
        assertEquals(new FindException(FindApplication.NO_FILE).getMessage(), actualException.getMessage());
    }

    @Test
    void testRunOnlyWithoutFolderSpecifiedInvalid() {
        String[] args = new String[] { NAME_FLAG, TEST_FILE_NAME };

        Exception actualException = assertThrows(FindException.class, () -> application.run(args, mockInputStream, mockOutputStream));
        assertEquals(new FindException(FindApplication.NO_FOLDER).getMessage(), actualException.getMessage());
    }

    @Test
    void testRunOnlyWithoutFileSpecifiedInvalid() {
        String[] args = new String[] {TEST_FOLDER, ANOTHER_FOLDER, NAME_FLAG };

        Exception actualException = assertThrows(FindException.class, () -> application.run(args, mockInputStream, mockOutputStream));
        assertEquals(new FindException(FindApplication.NO_FILE).getMessage(), actualException.getMessage());
    }

    @Test
    void testRunWithoutFolderAndFileSpecifiedInvalid() {
        String[] args = new String[] { NAME_FLAG };

        Exception actualException = assertThrows(FindException.class, () -> application.run(args, mockInputStream, mockOutputStream));
        assertEquals(new FindException(FindApplication.NO_FOLDER).getMessage(), actualException.getMessage());
    }

    @Test
    void testRunWithoutFolderAndFileIdentifierSpecifiedInvalid() {
        //find 123.txt, where 123.txt is thought to be folder but no file specified and no file identifier
        String[] args = new String[] { NUMERIC_FILE_NAME };

        Exception actualException = assertThrows(FindException.class, () -> application.run(args, mockInputStream, mockOutputStream));
        assertEquals(new FindException(FindApplication.NO_FILE).getMessage(), actualException.getMessage());
    }

    @Test
    void testRunWithoutFileAndIdentifierSpecifiedInvalid() {
        String[] args = new String[] {EMPTY_FOLDER};

        Exception actualException = assertThrows(FindException.class, () -> application.run(args, mockInputStream, mockOutputStream));
        assertEquals(new FindException(FindApplication.NO_FILE).getMessage(), actualException.getMessage());
    }

    @Test
    void testRunWithoutAnyArgumentInvalid() {
        String[] args = new String[] {};

        Exception actualException = assertThrows(FindException.class, () -> application.run(args, mockInputStream, mockOutputStream));
        assertEquals(new FindException(FindApplication.NO_ARGS).getMessage(), actualException.getMessage());
    }

    @Test
    void testRunWithMultipleFoldersMultipleFilesInvalid() {
        String [] args = new String[] {TEST_FOLDER, ANOTHER_FOLDER, NAME_FLAG, TEST_FILE_NAME,
                ANOTHER_FILE_NAME };

        Exception actualException = assertThrows(FindException.class, () -> application.run(args, mockInputStream, mockOutputStream));
        assertEquals(new FindException(FindApplication.MULTIPLE_FILES).getMessage(), actualException.getMessage());
    }

    @Test
    void testRunWithSingleFolderMultipleFilesInvalid() {
        String [] args = new String[] {EMPTY_FOLDER, NAME_FLAG, TEST_FILE_NAME,
                ANOTHER_FILE_NAME };

        Exception actualException = assertThrows(FindException.class, () -> application.run(args, mockInputStream, mockOutputStream));
        assertEquals(new FindException(MULTIPLE_FILES).getMessage(), actualException.getMessage());
    }

    @Test
    void testRunWithNullStdoutInvalid() {
        String[] args = new String[] {ANOTHER_FOLDER, NAME_FLAG, ANOTHER_FILE_NAME };

        Exception actualException = assertThrows(FindException.class, () ->
                application.run(args, mockInputStream, null));
        assertEquals(actualException.getMessage(), OUTPUT_ERROR_MSG);
    }

    /*********************************************
     * Set of test cases to verify output result
     *********************************************/
    @Test
    void testRunOutputFileLocationResult() throws Exception {
        String[] args = new String[] {TEST_FOLDER, NAME_FLAG, NUMERIC_FILE_NAME };
        String expectedResult = TEST_FOLDER + File.separator + NUMERIC_FILE_NAME + StringUtils.STRING_NEWLINE +
                TEST_FOLDER + File.separator + NUMERIC_FOLDER + File.separator + NUMERIC_FILE_NAME +
                StringUtils.STRING_NEWLINE;
        mockBos = new ByteArrayOutputStream();

        application.run(args, mockInputStream, mockBos);
        assertEquals(expectedResult, new String(mockBos.toByteArray()));
    }

    @Test
    void testRunOutputEmptyResult() throws Exception {
        String[] args = new String[] {TEST_FOLDER, NAME_FLAG, INVALID_FILE_NAME };
        String expectedResult = "";
        mockBos = new ByteArrayOutputStream();

        application.run(args, mockInputStream, mockBos);
        assertEquals(expectedResult, new String(mockBos.toByteArray()));
    }

    @Test
    void testRunOutputFolderNotExistsResult() throws Exception {
        String[] args = new String[] {INVALID_FOLDER, NAME_FLAG, TEST_FILE_NAME };
        String expectedResult = "find: " + INVALID_FOLDER + NOT_FOUND_MSG + StringUtils.STRING_NEWLINE;
        mockBos = new ByteArrayOutputStream();

        application.run(args, mockInputStream, mockBos);
        assertEquals(expectedResult, new String(mockBos.toByteArray()));
    }

    @Test
    void testRunOutputMixedResults() throws Exception {
        String[] args = new String[] {INVALID_FOLDER, TEST_FOLDER, NAME_FLAG, TEST_FILE_NAME };
        String expectedResult = "find: " + INVALID_FOLDER + NOT_FOUND_MSG + StringUtils.STRING_NEWLINE +
                TEST_FOLDER + File.separator + TEST_FILE_NAME + StringUtils.STRING_NEWLINE;
        mockBos = new ByteArrayOutputStream();

        application.run(args, mockInputStream, mockBos);
        assertEquals(expectedResult, new String(mockBos.toByteArray()));
    }
}
