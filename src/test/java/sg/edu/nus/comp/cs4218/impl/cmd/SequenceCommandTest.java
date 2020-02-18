package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SequenceCommandTest {

    private List<Command> spyCommandsList;
    private SequenceCommand sequenceCommand;

    @BeforeEach
    void setUp() {
        spyCommandsList = spy(ArrayList.class);
    }

    /**
     * Tests evaluate method when command throws ExitException.
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
}