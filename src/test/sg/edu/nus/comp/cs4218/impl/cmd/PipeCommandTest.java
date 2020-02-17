package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class PipeCommandTest {

    @Mock
    private List<CallCommand> callCommands;

    /**
     * Tests evaluate method when call command throws a subclass of AbstractApplicationException.
     */
    @Test
    void evaluateWhenCallCommandThrowsAbsAppExceptionShouldThrowAbstractApplicationException() throws AbstractApplicationException, ShellException {
        CallCommand mockCallCommand = mock(CallCommand.class);

        doThrow(mock(AbstractApplicationException.class)).when(mockCallCommand).evaluate(any(), any());

        assertThrows(AbstractApplicationException.class, () -> {
           mockCallCommand.evaluate(any(), any());
        });
    }

    /**
     * Tests evaluate method when call command throws a ShellException.
     */
    @Test
    void evaluateWhenCallCommandThrowsShellExceptionShouldThrowShellException() throws AbstractApplicationException, ShellException {
        CallCommand mockCallCommand = mock(CallCommand.class);

        doThrow(ShellException.class).when(mockCallCommand).evaluate(any(), any());

        assertThrows(ShellException.class, () -> {
            mockCallCommand.evaluate(any(), any());
        });
    }

    @Test
    void getCallCommands() {
    }
}