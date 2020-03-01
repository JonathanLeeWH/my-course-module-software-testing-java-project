package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class IORedirectionHandlerTest {


    public static final String WRITTEN__TEXT = "test";

    private static IORedirectionHandler redirHandler;
    private List<String> argsList;
    private static ArgumentResolver argumemtResovler = new ArgumentResolver();

    private static InputStream inputStream;
    private static OutputStream outputStream;

    private static final String INPUT_FILE_NAME = "test1.txt";
    private static final String INPUT_FILE_NAME2 = "test2.txt";
    private static final String OUTPUT_FILE_NAME = "output.txt";

    private static final String INVALID_SYNTAX = "shell: " + ERR_SYNTAX;


    @BeforeAll
    static void setUp() throws IOException {
        // Create a few files in the PWD for wcApplication function to read

        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(INPUT_FILE_NAME));
        writer1.write(WRITTEN__TEXT);
        writer1.flush();
        writer1.close();

        File file1 = new File(INPUT_FILE_NAME2);
        file1.createNewFile();
    }

    @AfterAll
    static void tearDown() {
        // Delete the created files when testing is done.
        File file1 = new File(INPUT_FILE_NAME);
        File file2 = new File(OUTPUT_FILE_NAME);
        File file3 = new File(INPUT_FILE_NAME2);

        file1.delete();
        file2.delete();
        file3.delete();
    }


    @BeforeEach
    void setUpBeforeEach() {
        inputStream = null;
        outputStream = null;
    }

    @AfterEach
    void tearDownAfterEach() throws IOException {
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
        if (outputStream != null) {
            outputStream.close();
            outputStream = null;
        }

    }

    @Test
    void getInputStreamNotNullTest() throws FileNotFoundException {
        inputStream = new FileInputStream(new File(INPUT_FILE_NAME));
        argsList = Arrays.asList("<", INPUT_FILE_NAME);
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);

        assertNotNull(redirHandler.getInputStream());
    }

    @Test
    void getInputStreamNullTest() {
        argsList = Arrays.asList(INPUT_FILE_NAME);
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);

        assertNull(redirHandler.getInputStream());
    }

    @Test
    void getOutputStreamNullTest() throws FileNotFoundException {
        argsList = Arrays.asList(">", OUTPUT_FILE_NAME);
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);

        assertNull(redirHandler.getOutputStream());
    }

     @Test
    void getOutputStreamNotNullTest() throws FileNotFoundException {
        outputStream = new FileOutputStream(new File(OUTPUT_FILE_NAME));
        argsList = Arrays.asList(">", OUTPUT_FILE_NAME);
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);

        assertNotNull(redirHandler.getOutputStream());
    }

    @Test
    void extractRedirOptionsCharOutputTest() throws ShellException, IOException, AbstractApplicationException {
        outputStream = new FileOutputStream(new File(OUTPUT_FILE_NAME));
        argsList = Arrays.asList(">", OUTPUT_FILE_NAME);
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);
        redirHandler.extractRedirOptions();

        assertNotNull(redirHandler.getOutputStream());
    }

    @Test
    void extractRedirOptionsWithNullArgListTestThrowInvalidSyntaxException() throws ShellException, IOException {
        argsList = null;
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);
        Exception actualException = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
        assertEquals(INVALID_SYNTAX, actualException.getMessage());

    }

    @Test
    void extractRedirOptionsWithEmptyArgListTestThrowInvalidSyntaxException() throws ShellException, IOException {
        argsList = new ArrayList<>();
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);
        Exception actualException = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
        assertEquals(INVALID_SYNTAX, actualException.getMessage());

    }

    @Test
    void extractRedirOptionsWithTwoInputArgListTestThrowInvalidSyntaxException() throws ShellException, IOException {
        argsList = Arrays.asList("<", "<");
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);

        Exception actualException = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
        assertEquals(INVALID_SYNTAX, actualException.getMessage());
    }

    @Test
    void extractRedirOptionsWithTwoOutputArgListTestThrowInvalidSyntaxException() throws ShellException, IOException {
        argsList = Arrays.asList(">", ">");
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);

        Exception actualException = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
        assertEquals(INVALID_SYNTAX, actualException.getMessage());

    }

    @Test
    void extractRedirOptionsWithTwoFilesArgListTestThrowInvalidSyntaxException() throws ShellException, IOException {
        argsList = Arrays.asList("<", "test*");
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);

        Exception actualException = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
        assertEquals(INVALID_SYNTAX, actualException.getMessage());
    }

    @Test
    void extractRedirOptionsCharInputTestSuccess() throws ShellException, IOException, AbstractApplicationException {
        inputStream = new FileInputStream(new File(INPUT_FILE_NAME2));
        argsList = Arrays.asList("<", INPUT_FILE_NAME);
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);
        redirHandler.extractRedirOptions();

        StringBuilder stringWritten = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(redirHandler.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            stringWritten.append(line);
            stringWritten.append(System.lineSeparator());
        }
        reader.close();

        assertEquals(WRITTEN__TEXT, stringWritten.toString().trim());
    }

    @Test
    void extractRedirOptionsWithMultiFilesArgListTestSuccess() throws ShellException, IOException, AbstractApplicationException {
        inputStream = new FileInputStream(new File(INPUT_FILE_NAME2));
        argsList = Arrays.asList("<", INPUT_FILE_NAME, INPUT_FILE_NAME2);
        redirHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumemtResovler);
        redirHandler.extractRedirOptions();

        StringBuilder stringWritten = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(redirHandler.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            stringWritten.append(line);
            stringWritten.append(System.lineSeparator());
        }
        reader.close();

        assertEquals(WRITTEN__TEXT, stringWritten.toString().trim());
    }


}
