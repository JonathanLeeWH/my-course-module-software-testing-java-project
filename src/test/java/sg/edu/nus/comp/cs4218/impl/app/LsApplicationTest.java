package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.LsException;

import java.io.IOException;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

import static org.junit.jupiter.api.Assertions.*;

public class LsApplicationTest {

    private static final String TEST_FILE = "testFile";
    private static final String TEST_SAME_NAME = "testFile";
    private static final String TEST_DIFFERENT = "testFile2";
    private static final String TEST_FOLDER = "testFolder";
    private static LsApplication lsApplication;


    @BeforeEach
    void setupBeforeTest() throws IOException {
        lsApplication = new LsApplication();
    }

    @Test
    public void executeNoArgSpecifiedThrowsArgException() {

        Exception exception = assertThrows(LsException.class, () -> {
            lsApplication.run(null, null, null);
        });

        assertEquals(new LsException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

}