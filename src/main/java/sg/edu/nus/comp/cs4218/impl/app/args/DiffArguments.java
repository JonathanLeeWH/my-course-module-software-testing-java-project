package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

public class DiffArguments {
    private static final char IDENT_FILES = 's';
    private static final char IGNORE_BLANKS = 'B';
    private static final char DIFF_FILES = 'q';
    private final List<String> files;
    private boolean showIdentMessage, ignoreBlankLines, diffMessageOnly, stdin;

    public DiffArguments() {
        this.files = new ArrayList<>();
        this.showIdentMessage = false;
        this.ignoreBlankLines = false;
        this.diffMessageOnly = false;
        this.stdin = false;
    }

    /**
     * Handles argument list parsing for the `diff` application.
     *
     * @param args Array of arguments to parse
     * @throws Exception
     */
    public void parse(String... args) throws Exception {
        if (args == null) {
            throw new DiffException(ERR_NULL_ARGS);
        }
        for (String arg : args) {
            if ("-".equals(arg)) {
                stdin = true;
                break;
            }
        }
        for (String arg : args) {
            if (arg.isEmpty()) {
                continue;
            }
            // `parsingFlag` is to ensure all flags come first, followed by files.
            if (arg.charAt(0) == CHAR_FLAG_PREFIX && arg.length() == 4) {
                if (arg.contains(String.valueOf(IDENT_FILES)) && arg.contains(String.valueOf(IGNORE_BLANKS))
                        && arg.contains(String.valueOf(DIFF_FILES))) {
                    this.showIdentMessage = true;
                    this.ignoreBlankLines = true;
                    this.diffMessageOnly = true;
                }
            } else if (arg.charAt(0) == CHAR_FLAG_PREFIX && arg.length() == 3) {
                if (arg.contains(String.valueOf(IDENT_FILES)) && arg.contains(String.valueOf(IGNORE_BLANKS))) {
                    this.showIdentMessage = true;
                    this.ignoreBlankLines = true;
                } else if (arg.contains(String.valueOf(IDENT_FILES)) && arg.contains(String.valueOf(DIFF_FILES))) {
                    this.showIdentMessage = true;
                    this.diffMessageOnly = true;
                } else if (arg.contains(String.valueOf(IGNORE_BLANKS)) && arg.contains(String.valueOf(DIFF_FILES))) {
                    this.ignoreBlankLines = true;
                    this.diffMessageOnly = true;
                }
            } else if (arg.charAt(0) == CHAR_FLAG_PREFIX && arg.length() == 2) {
                if (arg.contains(String.valueOf(IGNORE_BLANKS))) {
                    this.ignoreBlankLines = true;
                } else if (arg.contains(String.valueOf(DIFF_FILES))) {
                    this.diffMessageOnly = true;
                } else if (arg.contains(String.valueOf(IDENT_FILES))){
                    this.showIdentMessage = true;
                }
            } else {
                if (!"-".equals(arg)) {
                    this.files.add(arg.trim());
                }
            }
        }
    }

    public List<String> getFiles() {
        return files;
    }

    public boolean isStdin() { return stdin; }

    public boolean isIgnoreBlankLines() { return ignoreBlankLines; }

    public boolean isShowIdenticalMessage() {
        return showIdentMessage;
    }

    public boolean isDiffMessage() {
        return diffMessageOnly;
    }
}
