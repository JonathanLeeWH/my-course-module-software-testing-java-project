package sg.edu.nus.comp.cs4218.impl.util;

@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.ClassNamingConventions", "PMD.LongVariable"})
public class ErrorConstants {

    // Streams related
    public static final String ERR_READ_STREAM = "Could not read from input stream";
    public static final String ERR_WRITE_STREAM = "Could not write to output stream";
    public static final String ERR_NULL_STREAMS = "Null Pointer Exception";
    public static final String ERR_CLOSING_STREAMS = "Unable to close streams";
    public static final String ERR_MULTIPLE_STREAMS = "Multiple streams provided";
    public static final String ERR_STREAM_CLOSED = "Stream is closed";
    public static final String ERR_NO_OSTREAM = "OutputStream not provided";
    public static final String ERR_NO_ISTREAM = "InputStream not provided";
    public static final String ERR_NO_INPUT = "No InputStream and no filenames";
    public static final String ERR_NO_FILE_ARGS = "No files provided";

    // Arguments related
    public static final String ERR_MISSING_ARG = "Missing Argument";
    public static final String ERR_NO_ARGS = "Insufficient arguments";
    public static final String ERR_NULL_ARGS = "Null arguments";
    public static final String ERR_TOO_MANY_ARGS = "Too many arguments";
    public static final String ERR_INVALID_FLAG = "Invalid flag option supplied";
    public static final String ERR_BAD_REGEX = "Invalid pattern";
    public static final String ERR_INVALID_ARGS = "Invalid arguments";

    // Files and folders related
    public static final String ERR_FILE_NOT_FOUND = "No such file or directory";
    public static final String ERR_READING_FILE = "Could not read file";
    public static final String ERR_IS_DIR = "This is a directory";
    public static final String ERR_IS_NOT_DIR = "Not a directory";
    public static final String ERR_NO_PERM = "Permission denied";
    public static final String ERR_NON_EMPTY_DIR = "Non empty directory";

    // `rm` related
    public static final String ERR_IS_CURR_DIR = "This is the current directory";
    public static final String ERR_IS_SUB_PATH = "This is the sub path of the current path";

    // `cp` related
    public static final String ERR_SRC_DEST_SAME = "The source file and the destination file are the same file.";

    // `diff` related
    public static final String ERR_MORE_THAN_TWO_FILES = "Only 2 files allowed for Diff";

    // `date` related
    public static final String ERR_INVALID_FORMAT_PREFIX = "Invalid format. Date format must start with '+'";
    public static final String ERR_INVALID_FORMAT_FIELD = "Invalid format. Missing or unknown character after '%'";
    public static final String ERR_MISSING_FIELD = "Invalid format";

    // `find` related
    public static final String ERR_INVALID_FILE = "Invalid Filename";
    public static final String ERR_NAME_FLAG = "Paths must precede -name";

    // `sed` related
    public static final String ERR_NO_REP_RULE = "No replacement rule supplied";
    public static final String ERR_INVALID_REP_RULE = "Invalid replacement rule";
    public static final String ERR_INVALID_REP_X = "X needs to be a number greater than 0";
    public static final String ERR_INVALID_REGEX = "Invalid regular expression supplied";
    public static final String ERR_EMPTY_REGEX = "Regular expression cannot be empty";

    // `grep` related
    public static final String ERR_NO_REGEX = "No regular expression supplied";

    // `mkdir` related
    public static final String ERR_NO_FOLDERS = "No folder names are supplied";
    public static final String ERR_FILE_EXISTS = "File or directory already exists";
    public static final String ERR_TOP_LEVEL_MISSING = "Top level folders do not exist";

    // `mv` related
    public static final String NO_ARG_EXCEPTION ="No input found, please specify file to be moved";
    public static final String MISSING_ARG_EXCEPTION = "Missing Arguments";
    public static final String NO_DESTINATION = "No destination file specified";
    public static final String NO_DESTINATION_FOLDER = "No destination folder specified";
    public static final String DESTINATION_FOLDER_NOT = "No destination folder specified does not exist";
    public static final String SRC_DEST_SAME = "Source File and Destination specified is the same";
    public static final String FAILED_TO_MOVE = "Failed to move file";
    public static final String NO_FILE = "No file specified found";
    public static final String NO_OVERWRITE = "No overwrite and destination file exist";
    public static final String IDENTICAL_LOCATION = "Moving folder to same location";
    public static final String MOVING_TO_CHILD = "Moving folder to child folder";

    // `Paste` related
    public static final String INVALID_DASH = "multiple dashes are not supported";
    public static final String FILE_NOT_FOUND = "No such file or directory";


    //`ls` related

    public static final String NO_FILE_OR_FOLDER = "No file or folder specified found";

    // General constants
    public static final String ERR_INVALID_APP = "Invalid app";
    public static final String ERR_NOT_SUPPORTED = "Not supported yet";
    public static final String ERR_SYNTAX = "Invalid syntax";
    public static final String ERR_GENERAL = "Exception Caught";
    public static final String ERR_IO_EXCEPTION = "IOException";

    //Integer constants
    public static final String ERR_LESS_THAN_ZERO = "Index less than 0";
}
