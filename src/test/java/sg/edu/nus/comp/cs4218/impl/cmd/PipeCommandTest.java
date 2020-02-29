package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PipeCommandTest {

    private List<CallCommand> spyCallCommands;
    private PipeCommand pipeCommand;

    @BeforeEach
    void setUp() {
        spyCallCommands = spy(new LinkedList<>());
    }

    /**
     * Tests evaluate method when call command throws AbstractApplicationException.
     * Expected: Throws AbstractApplication.
     */
    @Test
    void testEvaluateWhenCallCommandThrowsAbsAppExceptionShouldThrowAbstractApplicationException() throws AbstractApplicationException, ShellException {
        CallCommand mockCallCommand = mock(CallCommand.class);
        spyCallCommands.add(mockCallCommand);
        verify(spyCallCommands).add(mockCallCommand);
        pipeCommand = new PipeCommand(spyCallCommands);

        doThrow(mock(AbstractApplicationException.class)).when(mockCallCommand).evaluate(any(), any());

        assertThrows(AbstractApplicationException.class, () -> {
           pipeCommand.evaluate(mock(InputStream.class), mock(OutputStream.class));
        });
    }

    /**
     * Tests evaluate method when call command throws a ShellException.
     * Expected: Throws ShellException.
     */
    @Test
    void testEvaluateWhenCallCommandThrowsShellExceptionShouldThrowShellException() throws AbstractApplicationException, ShellException {
        CallCommand mockCallCommand = mock(CallCommand.class);
        spyCallCommands.add(mockCallCommand);
        verify(spyCallCommands).add(mockCallCommand);
        pipeCommand = new PipeCommand(spyCallCommands);

        doThrow(ShellException.class).when(mockCallCommand).evaluate(any(), any());

        assertThrows(ShellException.class, () -> {
            pipeCommand.evaluate(mock(InputStream.class), mock(OutputStream.class));
        });
    }

    /**
     * Tests getCallCommands method when the list of callCommands passed to PipeCommand constructor is nonempty only one element.
     * We do not test if the list callCommands is empty given that CommandBuilder.java which calls PipeCommand,
     * the list passed to the PipeCommand argument cannot be empty.
     * Expected: Returns callCommands list with that element.
     */
    @Test
    void testGetCallCommandsWhenListOfCallCommandsPassedToPipeCommandConstructorIsNonEmptyOnlyOneElement() {
        CallCommand mockCallCommand = mock(CallCommand.class);
        spyCallCommands.add(mockCallCommand);
        verify(spyCallCommands).add(mockCallCommand);
        pipeCommand = new PipeCommand(spyCallCommands);

        List<CallCommand> expected = new LinkedList<>();
        expected.add(mockCallCommand);

        assertEquals(expected, pipeCommand.getCallCommands());
    }

    /**
     * Tests getCallCommands method when the list of callCommands passed to PipeCommand constructor is nonempty more than one element.
     * We do not test if the list callCommands is empty given that CommandBuilder.java which calls PipeCommand,
     * the list passed to the PipeCommand argument cannot be empty.
     * Expected: Returns callCommands list with those elements.
     */
    @Test
    void testGetCallCommandsWhenListOfCallCommandsPassedToPipeCommandConstructorIsNonEmptyMoreThanOneElements() {
        CallCommand mockCallCommand1 = mock(CallCommand.class);
        CallCommand mockCallCommand2 = mock(CallCommand.class);
        spyCallCommands.add(mockCallCommand1);
        verify(spyCallCommands).add(mockCallCommand1);
        spyCallCommands.add(mockCallCommand2);
        verify(spyCallCommands).add(mockCallCommand2);
        pipeCommand = new PipeCommand(spyCallCommands);

        List<CallCommand> expected = new LinkedList<>();
        expected.add(mockCallCommand1);
        expected.add(mockCallCommand2);

        assertEquals(expected, pipeCommand.getCallCommands());
    }
}