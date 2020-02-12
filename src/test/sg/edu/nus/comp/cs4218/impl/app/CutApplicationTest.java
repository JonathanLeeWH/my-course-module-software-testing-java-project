package test.sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.app.CutApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CutApplicationTest {
    private CutApplication cutApplication;
    private String[] defaultCutArgs;
    private InputStream stdin;
    private OutputStream stdout;

    @BeforeEach
    public void setUp() {
        cutApplication = new CutApplication();
        stdin = System.in;
        stdout = System.out;
        defaultCutArgs = Arrays.asList("-c","8").toArray(new String[1]);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    void testRunNoArgs() {
       assertThrows(CutException.class, () -> cutApplication.run(null, stdin, stdout));
    }

    @Test
    void testRunNoOutputStream() {
        assertThrows(CutException.class, () -> cutApplication.run(defaultCutArgs, stdin, null));
    }
}