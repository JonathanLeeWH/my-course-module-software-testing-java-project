package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_GENERAL;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class SequenceCommandTest {

    private List<Command> spyCommandsList;
    private SequenceCommand sequenceCommand;
    private OutputStream outputStream;

    @BeforeEach
    void setUp() {
        spyCommandsList = spy(new LinkedList<>());
        outputStream = new ByteArrayOutputStream();
    }

    /**
     * Tests evaluate method when command.evaluate method execution throws ExitException.
     * Expected: Throws ExitException.
     */
    @Test
    void evaluateWhenCommandThrowsExitExceptionThrowsExitException() throws AbstractApplicationException, ShellException {
        Command mockCommand = mock(Command.class);
        spyCommandsList.add(mockCommand);
        verify(spyCommandsList).add(mockCommand);
        sequenceCommand = new SequenceCommand(spyCommandsList);

        doThrow(ExitException.class).when(mockCommand).evaluate(any(), any());

        assertThrows(ExitException.class, () -> {
            sequenceCommand.evaluate(mock(InputStream.class), mock(OutputStream.class));
        });
    }

    /**
     * Tests evaluate method when command.evaluate method execution throws ShellException.
     * Expected: stdout OutputStream at the end of evaluate method execution contains the ShellException in the format of the exception message with STRING_NEWLINE at the end.
     */
    @Test
    void evaluateWhenCommandThrowsShellExceptionShouldAddToOutputLines() throws AbstractApplicationException, ShellException {
        Command mockCommand = mock(Command.class);
        spyCommandsList.add(mockCommand);
        verify(spyCommandsList).add(mockCommand);
        sequenceCommand = new SequenceCommand(spyCommandsList);

        doThrow(new ShellException(ERR_GENERAL)).when(mockCommand).evaluate(any(), any());

        sequenceCommand.evaluate(mock(InputStream.class), outputStream);

        assertEquals(new ShellException(ERR_GENERAL).getMessage() + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests evaluate method when command.evaluate method execution throws a subclass of AbstractApplicationException.
     * For example, the command.evaluate method execution throws RmException which is a subclass of AbstractApplicationException.
     * Expected: stdout OutputStream at the end of evaluate method execution contains a RmException which is a subtype of AbstractApplicationException in the format of the exception message with STRING_NEWLINE at the end.
     */
    @Test
    void evaluateWhenCommandThrowsAbstractionApplicationExceptionShouldAddToOutputLines() throws AbstractApplicationException, ShellException {
        Command mockCommand = mock(Command.class);
        spyCommandsList.add(mockCommand);
        verify(spyCommandsList).add(mockCommand);
        sequenceCommand = new SequenceCommand(spyCommandsList);

        doThrow(new RmException(ERR_GENERAL)).when(mockCommand).evaluate(any(), any());

        sequenceCommand.evaluate(mock(InputStream.class), outputStream);

        assertEquals(new RmException(ERR_GENERAL).getMessage() + STRING_NEWLINE, outputStream.toString());
    }
}