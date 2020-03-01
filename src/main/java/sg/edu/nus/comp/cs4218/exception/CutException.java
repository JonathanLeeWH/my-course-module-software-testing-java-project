package sg.edu.nus.comp.cs4218.exception;

import sg.edu.nus.comp.cs4218.impl.app.CutApplication;

public class CutException extends AbstractApplicationException {

    private static final long serialVersionUID = -4130922172179294678L;

    public CutException(String message) {
        super(CutApplication.COMMAND + ": " + message);
    }
}