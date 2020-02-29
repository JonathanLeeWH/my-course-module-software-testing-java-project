package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class RmArgsParser extends ArgsParser {
    private final static char FLAG_IS_RECURSIVE = 'r';
    private final static char FLAG_WITH_FOLDER = 'd';

    public RmArgsParser() {
        super();
        legalFlags.add(FLAG_IS_RECURSIVE);
        legalFlags.add(FLAG_WITH_FOLDER);
    }

    /**
     * Returns true if there is d flag. Otherwise, returns false.
     * @return Returns true if there is d flag. Otherwise, returns false.
     */
    public Boolean isEmptyFolder() {
        return flags.contains(FLAG_WITH_FOLDER);
    }

    /**
     * Returns true if there is r flag. Otherwise, returns false.
     * @return Returns true if there is r flag. Otherwise, returns false.
     */
    public Boolean isRecursive() {
        return flags.contains(FLAG_IS_RECURSIVE);
    }

    /**
     * Returns the list of strings of file names from the input.
     * @return Returns the list of strings of file names from the input.
     */
    public List<String> getFileNames() {
        return nonFlagArgs;
    }
}
