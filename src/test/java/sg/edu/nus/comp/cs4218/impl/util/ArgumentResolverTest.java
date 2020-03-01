package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgumentResolverTest {

    private static final String TEST_STRING = "testString1";
    private static final String INPUT1 = "TEST2*";
    private static final ArgumentResolver ARGUMENT_RESOLVER = new ArgumentResolver();

    @Test
    void resolveOneArgumentSingleStringNoSpecialTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument(TEST_STRING);
        List<String> expectedOutput = Arrays.asList(TEST_STRING);
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentOneSingleQuoteOneTestThrowException() throws Exception {
        assertThrows(Exception.class, () -> {
            ARGUMENT_RESOLVER.resolveOneArgument("'");
        });
    }

    @Test
    void resolveOneArgumentOneBackQuoteTestThrowException() throws Exception {
        assertThrows(Exception.class, () -> {
            ARGUMENT_RESOLVER.resolveOneArgument("`");
        });
    }

    @Test
    void resolveOneArgumentOneDoubleQuoteOneTestThrowShellException() throws Exception {
        String expected = "shell: Unmatched quotes in input"; // can be other messages
        assertThrows(Exception.class, () -> {
            ARGUMENT_RESOLVER.resolveOneArgument("\"");
        });
    }

    @Test
    void resolveOneArgumentStartEndWithSingleQuoteTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("'echo'");
        List<String> expectedOutput = Arrays.asList("echo");
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentStartEndWithBackQuoteTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("`echo`");
        List<String> expectedOutput = new ArrayList<>();
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentStartEndWithDoubleQuoteTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("\"echo\"");
        List<String> expectedOutput = Arrays.asList("echo");
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentBackQuoteStartSingleQuoteTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("'`echo`'");
        List<String> expectedOutput = Arrays.asList("`echo`");
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }


    @Test
    void resolveOneArgumentBackQuoteStartSingleQuoteWithOtherCharTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("'A `echo`'");
        List<String> expectedOutput = Arrays.asList("A `echo`");
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentBackQuoteStartDoubleQuoteTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("\"`echo`\"");
        List<String> expectedOutput = Arrays.asList("");
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentDoubleQuoteInBackQuoteTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("`echo \"testString1\"`");
        List<String> expectedOutput = Arrays.asList(TEST_STRING);
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentDoubleQuoteInSingleQuoteTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("'\"testString1\"'");
        List<String> expectedOutput = Arrays.asList("\"testString1\"");
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentSingleQuoteInDoubleQuoteTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("\"'testString1'\"");
        List<String> expectedOutput = Arrays.asList("'testString1'");
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentSingleQuoteInBackQuoteTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("`echo 'testString1'`");
        List<String> expectedOutput = Arrays.asList(TEST_STRING);
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    //globbing
    @Test
    void resolveOneArgumentOneAsteriskTestSuccess() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument(INPUT1);
        List<String> expectedOutput = Arrays.asList(INPUT1);
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentBackQuoteWithAsteriskTest() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("`echo TEST2*`");
        List<String> expectedOutput = Arrays.asList(INPUT1);
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentSingleQuoteWithAsteriskTest() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("'TEST2*'");
        List<String> expectedOutput = Arrays.asList(INPUT1);
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

    @Test
    void resolveOneArgumentDoubleQuoteWithAsteriskTest() throws Exception {
        List<String> actualOutput = ARGUMENT_RESOLVER.resolveOneArgument("\"TEST2*\"");
        List<String> expectedOutput = Arrays.asList(INPUT1);
        assertArrayEquals(expectedOutput.toArray(),actualOutput.toArray());
    }

}