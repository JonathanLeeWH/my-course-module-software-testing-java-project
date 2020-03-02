package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.LsException;

import java.io.*;
import java.util.ArrayList;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class LsApplicationTest {

    private static LsApplication lsApplication;
    private static InputStream mockInputStream;
    private static OutputStream mockOutputStream;

    private static final String FOLDER_PATH = System.getProperty("user.dir");
    private static final String EMPTY_FOLDER_NAME = "ROOT";
    private static final String CURRENT_DIR = EnvironmentHelper.currentDirectory;

    private static final String FILE_NAME_1 = "file1.txt";
    private static final String FILE_NAME_2 = "file2.txt";
    private static final String FILE_NAME_3 = "file3.txt";
    
    private static final String FOLDER_NAME_1 = "folder1";
    private static final String FOLDER_NAME_2 = "folder2";
    private static final String SUBFOLDER_NAME_1 = "subfolder1";
    private static final String SUBFOLDER_NAME_2 = "subfolder2";
    

    private static final String L1_FOLDER_1 = FOLDER_NAME_1;
    private static final String L2_FOLDER_1 = L1_FOLDER_1 + File.separator + SUBFOLDER_NAME_1;
    private static final String L2_FILE_1 = L1_FOLDER_1 + File.separator + FILE_NAME_1;
    private static final String L3_FILE_1 = L2_FOLDER_1 + File.separator + FILE_NAME_1;


    private static final String L1_FOLDER_2 = FOLDER_NAME_2;
    private static final String L2_FOLDER_2 = L1_FOLDER_2 + File.separator + SUBFOLDER_NAME_2;
    private static final String L2_FILE_2 = L1_FOLDER_2 + File.separator + FILE_NAME_2;

    private static final String L1_FILE_1 = FILE_NAME_1;
    private static final String L1_FILE_2 = FILE_NAME_2;
    private static final String L1_FILE_3 = FILE_NAME_3;
    
    private static ArrayList<File> folders;

    private static final String FOLDER_OPTION = "-d";
    private static final String RECURSIVE_OPTION = "-R";

    @BeforeEach
    void setUp() throws IOException {
        lsApplication = spy(new LsApplication());
        mockInputStream = Mockito.mock(InputStream.class);
        mockOutputStream = Mockito.mock(OutputStream.class);

        folders = new ArrayList<>();
        File directory = new File(FOLDER_PATH + File.separator + EMPTY_FOLDER_NAME);
        folders.add(directory);
        directory.mkdirs();

        createFileFolder(L1_FOLDER_1, true);
        createFileFolder(L2_FOLDER_1, true);
        createFileFolder(L2_FILE_1, false);
        createFileFolder(L3_FILE_1, false);

        createFileFolder(L1_FOLDER_2, true);
        createFileFolder(L2_FOLDER_2, true);
        createFileFolder(L2_FILE_2, false);

        createFileFolder(L1_FILE_1, false);
        createFileFolder(L1_FILE_2, false);
        createFileFolder(L1_FILE_3, false);

        EnvironmentHelper.currentDirectory += File.separator + EMPTY_FOLDER_NAME;

    }


    private static void createFileFolder(String filePath, boolean isDirectory) throws IOException {
        File dir = new File(FOLDER_PATH + File.separator + EMPTY_FOLDER_NAME + File.separator + filePath);
        folders.add(dir);
        if (isDirectory) {
            dir.mkdirs();
        } else {
            dir.createNewFile();
        }
    }

    @AfterEach
    void tearDown() {
        //Delete all files
        for(int i = 0; i < folders.size(); i++) {
            File file = folders.get(folders.size() - i - 1);
            if (file.exists()) {
                file.delete();
            }
        }

        EnvironmentHelper.currentDirectory = CURRENT_DIR;
    }

    @Test
    void testListFolderContentNoArgumentSuccess() throws Exception {
        String expectedResult = FILE_NAME_1 + System.lineSeparator() + FILE_NAME_2 + System.lineSeparator() + FILE_NAME_3 + System.lineSeparator() + FOLDER_NAME_1 + System.lineSeparator() + FOLDER_NAME_2;
        String[] foldersName = {};
        assertEquals(expectedResult, lsApplication.listFolderContent(false,false,foldersName));
    }

    @Test
    public void runWhenMissingArgSpecifiedThrowsNoFileFolderFoundException() {

        String[] constructArgs = new String [] {"hello"};
        Exception exception = assertThrows(LsException.class, () -> {
            lsApplication.run(constructArgs, System.in, System.out);
        });

        assertEquals(new LsException(NO_FILE_OR_FOLDER).getMessage(), exception.getMessage());
    }

    @Test
    void testListFolderContentOneFolderSuccess() throws Exception {
        String expectedResult = FILE_NAME_1 + System.lineSeparator() + SUBFOLDER_NAME_1;
        assertEquals(expectedResult, lsApplication.listFolderContent(false,false, L1_FOLDER_1));
    }

    @Test
    void testListFolderContentSubFolderSuccess() throws Exception {
        String expectedResult = FILE_NAME_1;
        assertEquals(expectedResult, lsApplication.listFolderContent(false,false, L2_FOLDER_1));
    }

    @Test
    void testListFolderContentMultipleFolderSuccess() throws Exception {
        String expectedResult = FOLDER_NAME_1 +":" + System.lineSeparator() +
                FILE_NAME_1 + System.lineSeparator() + SUBFOLDER_NAME_1 + System.lineSeparator() + System.lineSeparator() +
                FOLDER_NAME_2 +":" + System.lineSeparator() + FILE_NAME_2 + System.lineSeparator() + SUBFOLDER_NAME_2;
        assertEquals(expectedResult, lsApplication.listFolderContent(false,false,
                L1_FOLDER_1, L1_FOLDER_2));
    }

    @Test
    void testListFolderContentSingleFileSuccess() throws Exception {
        String expectedResult = FILE_NAME_3;
        assertEquals(expectedResult, lsApplication.listFolderContent(false,false,
                L1_FILE_3));
    }

    @Test
    void testListFolderContentMultipleFolderAndFilesSuccess() throws Exception {
        String expectedResult = FOLDER_NAME_1 +":" + System.lineSeparator() + FILE_NAME_1 + System.lineSeparator() +
                SUBFOLDER_NAME_1 + System.lineSeparator() + System.lineSeparator() +
                FOLDER_NAME_2 +":" + System.lineSeparator() + FILE_NAME_2 + System.lineSeparator() + SUBFOLDER_NAME_2 +
                System.lineSeparator() + System.lineSeparator() + FILE_NAME_1;

        assertEquals(expectedResult, lsApplication.listFolderContent(false,false,
                L1_FOLDER_1, L1_FOLDER_2, L1_FILE_1));
    }



    @Test
    void testListFolderContentNoArgFolderOnlySuccess() throws Exception {
        String expectedResult = ".";
        String[] foldersName = {};
        assertEquals(expectedResult, lsApplication.listFolderContent(true,false,foldersName));
    }

    @Test
    void testListFolderContentFileFolderOnlySuccess() throws Exception {
        String expectedResult = L1_FILE_3;
        assertEquals(expectedResult, lsApplication.listFolderContent(true,false,
                L1_FILE_3));
    }

    @Test
    void testListFolderContentMultiFileFolderOnlySuccess() throws Exception {
        String expectedResult = L1_FILE_3 + System.lineSeparator() + L1_FILE_2;
        assertEquals(expectedResult, lsApplication.listFolderContent(true,false,
                L1_FILE_3, L1_FILE_2));
    }

    @Test
    void testListFolderContentOneFolderOnlySuccess() throws Exception {
        String expectedResult = L1_FOLDER_1;
        assertEquals(expectedResult, lsApplication.listFolderContent(true,false,
                L1_FOLDER_1));
    }

    @Test
    void testListFolderContentMultiFoldersOnlySuccess() throws Exception {
        String expectedResult = L1_FOLDER_1 + System.lineSeparator() + L1_FOLDER_2;
        assertEquals(expectedResult, lsApplication.listFolderContent(true,false,
                L1_FOLDER_1, L1_FOLDER_2));
    }

    @Test
    void testListFolderContentFileFolderFolderOnlySuccess() throws Exception {
        String expectedResult = L1_FOLDER_1 + System.lineSeparator() + L1_FILE_2;
        assertEquals(expectedResult, lsApplication.listFolderContent(true,false,
                L1_FOLDER_1, L1_FILE_2));
    }

    @Test
    void testListFolderContentFoldersOnlySuccess() throws Exception {
        String expectedResult = L1_FOLDER_1 + System.lineSeparator() + L1_FOLDER_2 + System.lineSeparator() + L1_FILE_3;
        assertEquals(expectedResult, lsApplication.listFolderContent(true,false,
                L1_FOLDER_1, L1_FOLDER_2, L1_FILE_3));
    }



    @Test
    void testListFolderContentFileRecursiveSuccess() throws Exception {
        String expectedResult = FILE_NAME_3;

        assertEquals(expectedResult, lsApplication.listFolderContent(false,true,
                L1_FILE_3));
    }

    @Test
    void testListFolderContentMultiFileRecursiveSuccess() throws Exception {
        String expectedResult = FILE_NAME_3 + System.lineSeparator() + FILE_NAME_2;

        assertEquals(expectedResult, lsApplication.listFolderContent(false,true,
                L1_FILE_3, L1_FILE_2));
    }

    @Test
    void testListFolderContentOneRecursiveSuccess() throws Exception {
        String expectedResult = FOLDER_NAME_1 + ":" + System.lineSeparator() + FILE_NAME_1 + System.lineSeparator()
                + SUBFOLDER_NAME_1 + System.lineSeparator() + System.lineSeparator() + FOLDER_NAME_1 + File.separator
                + SUBFOLDER_NAME_1 + ":" + System.lineSeparator() + FILE_NAME_1;

        assertEquals(expectedResult, lsApplication.listFolderContent(false,true,
                L1_FOLDER_1));
    }

    @Test
    void testListFolderContentMultiRecursiveSuccess() throws Exception {
        String expectedResult = FOLDER_NAME_1 + ":" + System.lineSeparator() + FILE_NAME_1 + System.lineSeparator()
                + SUBFOLDER_NAME_1 + System.lineSeparator() + System.lineSeparator() + FOLDER_NAME_1 + File.separator
                + SUBFOLDER_NAME_1 + ":" + System.lineSeparator() + FILE_NAME_1  + System.lineSeparator() + System.lineSeparator()
                + FOLDER_NAME_2 + ":" + System.lineSeparator() + FILE_NAME_2 + System.lineSeparator()
                + SUBFOLDER_NAME_2 + System.lineSeparator() + System.lineSeparator() + FOLDER_NAME_2 + File.separator
                + SUBFOLDER_NAME_2 + ":";

        assertEquals(expectedResult, lsApplication.listFolderContent(false,true,
                L1_FOLDER_1, L1_FOLDER_2));
    }

    @Test
    void testListFolderContentFileFolderRecursiveSuccess() throws Exception {
        String expectedResult = FILE_NAME_3  + System.lineSeparator() + System.lineSeparator() + FOLDER_NAME_1 + ":" + System.lineSeparator() + FILE_NAME_1 + System.lineSeparator()
                + SUBFOLDER_NAME_1 + System.lineSeparator() + System.lineSeparator() + FOLDER_NAME_1 + File.separator
                + SUBFOLDER_NAME_1 + ":" + System.lineSeparator() + FILE_NAME_1 + System.lineSeparator() + System.lineSeparator() + FOLDER_NAME_2 + ":" + System.lineSeparator() +FILE_NAME_2 + System.lineSeparator()
                + SUBFOLDER_NAME_2 + System.lineSeparator() + System.lineSeparator() + FOLDER_NAME_2 + File.separator
                + SUBFOLDER_NAME_2 + ":";

        assertEquals(expectedResult, lsApplication.listFolderContent(false,true,
                L1_FILE_3, L1_FOLDER_1, L1_FOLDER_2));
    }

    @Test
    void testListFolderContentNoArgRecursiveSuccess() throws Exception {
        String expectedResult = FILE_NAME_1 + System.lineSeparator() +FILE_NAME_2 + System.lineSeparator() + FILE_NAME_3 + System.lineSeparator() + FOLDER_NAME_1 + System.lineSeparator()
                + FOLDER_NAME_2 + System.lineSeparator() + System.lineSeparator()
                + "." + File.separator + FOLDER_NAME_1 + ":" + System.lineSeparator() + FILE_NAME_1 + System.lineSeparator() + SUBFOLDER_NAME_1 + System.lineSeparator() + System.lineSeparator()
                + "." + File.separator + FOLDER_NAME_1 + File.separator + SUBFOLDER_NAME_1 + ":" + System.lineSeparator() + FILE_NAME_1 + System.lineSeparator() + System.lineSeparator()
                + "." + File.separator + FOLDER_NAME_2 + ":" + System.lineSeparator() + FILE_NAME_2 + System.lineSeparator() + SUBFOLDER_NAME_2 + System.lineSeparator() + System.lineSeparator()
                + "." + File.separator + FOLDER_NAME_2 + File.separator + SUBFOLDER_NAME_2 + ":";

        String[] folderName = {};
        assertEquals(expectedResult, lsApplication.listFolderContent(false,true,
                folderName));
    }
    //ls invalid1 valid1 -R
    @Test
    void testListFolderContentRecursiveThrowInvalidDirectory() throws Exception {
        String[] folderName = {"invalid1",FILE_NAME_1};
        assertThrows(Exception.class, () -> lsApplication.listFolderContent(false,true,
                folderName));
    }

    //using mockito to check number of invocations to ensure listFolderContent is ran once
    @Test
    void testRunEmptyArg() throws Exception {
        String[] args = new String[] {};
        String[] foldername = {};
        lsApplication.run(args, mockInputStream, mockOutputStream);
        verify(lsApplication, Mockito.times(1))
                .listFolderContent(false, false, foldername);
    }

    @Test
    void testRunSingleFileArg() throws Exception {
        String[] args = new String[] {L1_FILE_3};

        lsApplication.run(args, mockInputStream, mockOutputStream);
        verify(lsApplication, Mockito.times(1))
                .listFolderContent(false, false, L1_FILE_3);
    }

    @Test
    void testRunSingleFolderArg() throws Exception {
        String[] args = new String[] {L1_FOLDER_1};

        lsApplication.run(args, mockInputStream, mockOutputStream);
        verify(lsApplication, Mockito.times(1))
                .listFolderContent(false, false, L1_FOLDER_1);
    }

    @Test
    void testRunMultiFileArg() throws Exception {
        String[] args = new String[] {L1_FILE_3, L1_FILE_2};

        lsApplication.run(args, mockInputStream, mockOutputStream);
        verify(lsApplication, Mockito.times(1))
                .listFolderContent(false, false, L1_FILE_3, L1_FILE_2);
    }

    @Test
    void testRunMultipleFolderArg() throws Exception {
        String[] args = new String[] {L1_FOLDER_1, L1_FOLDER_2};

        lsApplication.run(args, mockInputStream, mockOutputStream);
        verify(lsApplication, Mockito.times(1))
                .listFolderContent(false, false, L1_FOLDER_1, L1_FOLDER_2);
    }

    @Test
    void testRunFolderOnlyArg() throws Exception {
        String[] args = new String[] {FOLDER_OPTION, L1_FOLDER_1};

        lsApplication.run(args, mockInputStream, mockOutputStream);
        verify(lsApplication, Mockito.times(1))
                .listFolderContent(true, false, L1_FOLDER_1);
    }

    @Test
    void testRunRecursionArg() throws Exception {
        String[] args = new String[] {RECURSIVE_OPTION , L1_FOLDER_1};

        lsApplication.run(args, mockInputStream, mockOutputStream);
        verify(lsApplication, Mockito.times(1))
                .listFolderContent(false, true, L1_FOLDER_1);
    }

}
