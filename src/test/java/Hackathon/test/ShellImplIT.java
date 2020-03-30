package Hackathon.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShellImplIT {
    private static Shell shell;
    private static InputStream inputStream;
    private static ByteArrayOutputStream outputStream;

    @AfterEach
    void tearDownAfterEach() throws IOException {
        inputStream.close();
        outputStream.close();
    }

    @BeforeEach
    void setUpBeforeEach() {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    void testParseAndEvaluateForBugReportNum3() throws Exception {
        String input = "cd main3/`ls main3`";
        shell.parseAndEvaluate(input, outputStream);
        String expectedResult = "";
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void testParseAndEvaluateForBugReportNum11() throws Exception {
        String input = "cut -b 1,3-5 file1.txt";
        Throwable thrown = assertThrows(Exception.class, () -> shell.parseAndEvaluate(input, outputStream));
        String expectedResult = "";
        assertEquals(thrown.getMessage(), expectedResult);
    }
}