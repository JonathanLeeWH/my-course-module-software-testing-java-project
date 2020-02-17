package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PipeCommandTest {

    private List<CallCommand> mockCallCommandsList;

    @BeforeEach
    void setUp() {
        mockCallCommandsList = mock(ArrayList.class);
    }

    /**
     * Tests evaluate method when call command throws AbstractApplicationException.
     * Expected: Throws AbstractApplication.
     */
    @Test
    void evaluateWhenCallCommandThrowsAbsAppExceptionShouldThrowAbstractApplicationException() throws AbstractApplicationException, ShellException {
        CallCommand mockCallCommand = mock(CallCommand.class);
        mockCallCommandsList.add(mockCallCommand);
        verify(mockCallCommandsList).add(mockCallCommand);

        doThrow(mock(AbstractApplicationException.class)).when(mockCallCommand).evaluate(any(), any());

        assertThrows(AbstractApplicationException.class, () -> {
           mockCallCommand.evaluate(any(), any());
        });
    }

    /**
     * Tests evaluate method when call command throws a ShellException.
     * Expected: Throws ShellException.
     */
    @Test
    void evaluateWhenCallCommandThrowsShellExceptionShouldThrowShellException() throws AbstractApplicationException, ShellException {
        CallCommand mockCallCommand = mock(CallCommand.class);
        mockCallCommandsList.add(mockCallCommand);
        verify(mockCallCommandsList).add(mockCallCommand);

        doThrow(ShellException.class).when(mockCallCommand).evaluate(any(), any());

        assertThrows(ShellException.class, () -> {
            mockCallCommand.evaluate(any(), any());
        });
    }
}