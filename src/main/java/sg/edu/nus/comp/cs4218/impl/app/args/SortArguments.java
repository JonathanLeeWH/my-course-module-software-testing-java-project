package sg.edu.nus.comp.cs4218.impl.app.args;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

public class SortArguments {
    public static final String ILLEGAL_FLAG_MSG = "illegal option -- ";

    public static final char CHAR_FIRST_W_NUM = 'n';
    public static final char CHAR_REV_ORDER = 'r';
    public static final char CHAR_CASE_IGNORE = 'f';
    private final List<String> files;
    private boolean firstWordNumber, reverseOrder, caseIndependent;

    public SortArguments() {
        this.firstWordNumber = false;
        this.reverseOrder = false;
        this.caseIndependent = false;
        this.files = new ArrayList<>();
    }

    /**
     * Handles argument list parsing for the `sort` application.
     *
     * @param args Array of arguments to parse
     * @throws Exception
     */
    public void parse(String... args) throws Exception {
        if (args == null) {
            throw new Exception(ERR_NULL_ARGS);
        }

        boolean parsingFlag = true, skip = false;
        // Parse arguments
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.isEmpty()) {
                    continue;
                }
                // `parsingFlag` is to ensure all flags come first, followed by files.
                if (parsingFlag && arg.charAt(0) == CHAR_FLAG_PREFIX) {
                    // Loop through to see if we have any invalid flags
                    for (char c : arg.toCharArray()) {
                        if (c == CHAR_FLAG_PREFIX || c == CHAR_FIRST_W_NUM || c == CHAR_REV_ORDER || c == CHAR_CASE_IGNORE) {
                            continue;
                        }
                        throw new Exception(ERR_INVALID_FLAG);
                    }

                    for (char c : arg.toCharArray()) {
                        if (c == CHAR_FLAG_PREFIX) {
                            continue;
                        }
                        if (c == CHAR_FIRST_W_NUM) {
                            this.firstWordNumber = true;
                        }
                        if (c == CHAR_REV_ORDER) {
                            this.reverseOrder = true;
                        }
                        if (c == CHAR_CASE_IGNORE) {
                            this.caseIndependent = true;
                        }
                    }
                } else {
                    parsingFlag = false;
                    this.files.add(arg.trim());
                }
            }
        }
    }

    public List<String> getFiles() {
        return files;
    }

    public boolean isFirstWordNumber() {
        return firstWordNumber;
    }

    public boolean isReverseOrder() {
        return reverseOrder;
    }

    public boolean isCaseIndependent() {
        return caseIndependent;
    }
}
