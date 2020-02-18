package sg.edu.nus.comp.cs4218.exception;

public class CutException extends AbstractApplicationException {

    private static final long serialVersionUID = -4130922172179294678L;

    public CutException(String message) {
        super("cut: " + message);
    }
    public CutException(Exception exception) {
        super("cut: " + exception.getMessage());
    }
    public CutException(Exception exception, String message) {
        super("cut: " + exception.getClass() + message);
    }
}