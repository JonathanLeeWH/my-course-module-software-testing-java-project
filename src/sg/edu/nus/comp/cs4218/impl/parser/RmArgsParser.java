package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class RmArgsParser extends ArgsParser {
    private final static char FLAG_IS_RECURSIVE = 'r';
    private final static char FLAG_INCLUDE_FOLDERS = 'd';

    public RmArgsParser() {
        super();
        legalFlags.add(FLAG_IS_RECURSIVE);
        legalFlags.add(FLAG_INCLUDE_FOLDERS);
    }

    public Boolean isEmptyFolder() {
        return !flags.contains(FLAG_INCLUDE_FOLDERS);
    }

    public Boolean isRecursive() {
        return flags.contains(FLAG_IS_RECURSIVE);
    }

    public List<String> getFilesOrDirectories() {
        return nonFlagArgs;
    }
}
