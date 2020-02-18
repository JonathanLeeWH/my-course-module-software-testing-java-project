package sg.edu.nus.comp.cs4218.exception;

public class MvException extends AbstractApplicationException  {

    private static final long serialVersionUID = -4439395674558704575L;

    public MvException(String message) {
        super("mv: " + message);
    }
}
