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
    void testEvaluateWhenCommandThrowsExitExceptionThrowsExitException() throws AbstractApplicationException, ShellException {
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
    void testEvaluateWhenCommandThrowsShellExceptionShouldAddToOutputLines() throws AbstractApplicationException, ShellException {
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
    void testEvaluateWhenCommandThrowsAbstractionApplicationExceptionShouldAddToOutputLines() throws AbstractApplicationException, ShellException {
        Command mockCommand = mock(Command.class);
        spyCommandsList.add(mockCommand);
        verify(spyCommandsList).add(mockCommand);
        sequenceCommand = new SequenceCommand(spyCommandsList);

        doThrow(new RmException(ERR_GENERAL)).when(mockCommand).evaluate(any(), any());

        sequenceCommand.evaluate(mock(InputStream.class), outputStream);

        assertEquals(new RmException(ERR_GENERAL).getMessage() + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests getCommands method when the list of commands passed to SequenceCommand constructor is nonempty only one element.
     * We do not test if the list commands is empty given that CommandBuilder.java which calls SequenceCommand,
     * the list passed to the SequenceCommand argument cannot be empty.
     * Expected: Returns commands list with that element.
     */
    @Test
    void testGetCommandsWhenListOfCommandsPassedToSequenceCommandConstructorIsNonEmptyOnlyOneElement() {
        Command mockCommand = mock(Command.class);
        spyCommandsList.add(mockCommand);
        verify(spyCommandsList).add(mockCommand);
        sequenceCommand = new SequenceCommand(spyCommandsList);

        List<Command> expected = new LinkedList<>();
        expected.add(mockCommand);

        assertEquals(expected, sequenceCommand.getCommands());
    }

    /**
     * Tests getCommands method when the list of commands passed to SequenceCommand constructor is nonempty more than one element.
     * We do not test if the list commands is empty given that CommandBuilder.java which calls SequenceCommand,
     * the list passed to the SequenceCommand argument cannot be empty.
     * Expected: Returns commands list with those elements.
     */
    @Test
    void testGetCommandsWhenListOfCommandsPassedToSequenceCommandConstructorIsNonEmptyMoreThanOneElements() {
        Command mockCommand1 = mock(Command.class);
        Command mockCommand2 = mock(Command.class);
        spyCommandsList.add(mockCommand1);
        verify(spyCommandsList).add(mockCommand1);
        spyCommandsList.add(mockCommand2);
        verify(spyCommandsList).add(mockCommand2);
        sequenceCommand = new SequenceCommand(spyCommandsList);

        List<Command> expected = new LinkedList<>();
        expected.add(mockCommand1);
        expected.add(mockCommand2);

        assertEquals(expected, sequenceCommand.getCommands());
    }
}