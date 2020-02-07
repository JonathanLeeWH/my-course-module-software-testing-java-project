package sg.edu.nus.comp.cs4218.impl.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class CutException extends AbstractApplicationException {

    private static final long serialVersionUID = -4130922172179294678L;

    public CutException(String message) {
        super("cut: " + message);
    }
}