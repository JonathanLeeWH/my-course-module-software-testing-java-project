package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class GrepApplication implements GrepInterface {
    private static final String INVALID_PATTERN = "Invalid pattern syntax";
    private static final String EMPTY_PATTERN = "Pattern should not be empty.";
    private static final String IS_A_DIR = ": This is a directory" + StringUtils.STRING_NEWLINE;
    private static final String IS_DIRECTORY = "Is a directory";
    private static final String NULL_POINTER = "Null Pointer Exception";
    private static final String INVALID_FILES = "No such file or directory";
    private static final String INVALID_REGEX = "Invalid regular expression supplied";
    private static final String NO_READ_PERM = ": Permission denied" + StringUtils.STRING_NEWLINE;
    private static final String NO_EMPTY_REGEX = "Regular expression cannot be empty";

    private static final int NUM_ARGUMENTS = 2;
    private static final char CASE_INSEN_IDENT = 'i';
    private static final char COUNT_IDENT = 'c';
    private static final int CASE_INSEN_IDX = 0;
    private static final int COUNT_INDEX = 1;

    @Override
    public String grepFromFiles(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, String... fileNames) throws Exception {
        if (fileNames == null || pattern == null) {
            throw new GrepException(NULL_POINTER);
        }
        if (isCaseInsensitive == null || isCountLines == null) {
            throw new GrepException(NULL_POINTER);
        }
        try {
            Pattern.compile(pattern);
        } catch (PatternSyntaxException e) {
            throw (GrepException) new GrepException(INVALID_REGEX).initCause(e);
        }
        if (fileNames[0].equals("") && fileNames.length == 1) {
            return IS_A_DIR;
        }
        StringJoiner lineResults = new StringJoiner(STRING_NEWLINE);
        StringJoiner countResults = new StringJoiner(STRING_NEWLINE);

        grepResultsFromFiles(pattern, isCaseInsensitive, lineResults, countResults, fileNames);
        String results = "";
        if (isCountLines) {
            results = countResults.toString() + STRING_NEWLINE;
        } else {
            if (!lineResults.toString().isEmpty()) {
                results = lineResults.toString() + STRING_NEWLINE;
            }
        }
        return results;
    }

    /**
     * Extract the lines and count number of lines for grep from files and insert them into
     * lineResults and countResults respectively.
     *
     * @param pattern supplied by user
     * @param isCaseInsensitive supplied by user
     * @param lineResults a StringJoiner of the grep line results
     * @param countResults a StringJoiner of the grep line count results
     * @param fileNames a String Array of file names supplied by user
     */
    private void grepResultsFromFiles(String pattern, Boolean isCaseInsensitive, StringJoiner lineResults, StringJoiner countResults, String... fileNames) throws Exception {
        int count;
        boolean isSingleFile = (fileNames.length == 1);
        for (String f : fileNames) {
            BufferedReader reader = null;
            try {
                String path = convertToAbsolutePath(f);
                File file = new File(path);
                if (!file.exists()  || f.trim().equals("")) {
                    lineResults.add(f + ": " + ERR_FILE_NOT_FOUND);
                    countResults.add(f + ": " + ERR_FILE_NOT_FOUND);
                    continue;
                }

                if (file.isDirectory()) { // ignore if it's a directory
                    lineResults.add(f + ": " + IS_DIRECTORY);
                    countResults.add(f + ": " + IS_DIRECTORY);
                    continue;
                }
                reader = new BufferedReader(new FileReader(path));
                Pattern compiledPattern;
                if (isCaseInsensitive) {
                    compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                } else {
                    compiledPattern = Pattern.compile(pattern);
                }
                count = addMatchedPatternToLineResult(isSingleFile, reader, compiledPattern, f, lineResults);
                if (isSingleFile) {
                    countResults.add("" + count);
                } else {
                    countResults.add(f + ": " + count);
                }
                reader.close();
            } catch (PatternSyntaxException pse) {
                throw (GrepException) new GrepException(ERR_INVALID_REGEX).initCause(pse);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

    /**
     * Add lines that match pattern to an array of line result.
     *
     * @param isSingleFile a condition to check if there is only 1 file supplied by user.
     * @param reader a reader with the file supplied by user to read the entire file
     * @param compiledPattern which was compiled from pattern supplied by user
     * @param fileName supplied by user
     * @param lineResults a StringJoiner of the grep line results
     */
    private int addMatchedPatternToLineResult(boolean isSingleFile, BufferedReader reader, Pattern compiledPattern, String fileName, StringJoiner lineResults) throws IOException {
        int count = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = compiledPattern.matcher(line);
            if (matcher.find()) { // match
                if (isSingleFile) {
                    lineResults.add(line);
                } else {
                    lineResults.add(fileName + ": " + line);
                }
                count++;
            }
        }
        return count;
    }

    /**
     * Converts filename to absolute path, if initially was relative path
     * @param fileName supplied by user
     * @return a String of the absolute path of the filename
     */
    private String convertToAbsolutePath(String fileName) {
        String home = System.getProperty("user.home").trim();
        String currentDir = EnvironmentHelper.currentDirectory.trim();
        String convertedPath = convertPathToSystemPath(fileName);

        String newPath;
        if (convertedPath.length()>=home.length() && convertedPath.substring(0, home.length()).trim().equals(home)) {
            newPath = convertedPath;
        } else {
            newPath = currentDir + CHAR_FILE_SEP + convertedPath;
        }
        return newPath;
    }

    /**
     * Converts path provided by user into path recognised by the system
     * @param path supplied by user
     * @return a String of the converted path
     */
    private String convertPathToSystemPath(String path) {
        String convertedPath = path;
        String pathIdentifier = "\\" + Character.toString(CHAR_FILE_SEP);
        convertedPath = convertedPath.replaceAll("(\\\\)+", pathIdentifier);
        convertedPath = convertedPath.replaceAll("/+", pathIdentifier);

        if (convertedPath.length() != 0 && convertedPath.charAt(convertedPath.length() - 1) == CHAR_FILE_SEP) {
            convertedPath = convertedPath.substring(0, convertedPath.length() - 1);
        }

        return convertedPath;
    }

    @Override
    public String grepFromStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, InputStream stdin) throws Exception {
        int count = 0;
        StringJoiner stringJoiner = new StringJoiner(STRING_NEWLINE);
        if (isCaseInsensitive == null || isCountLines == null || pattern == null|| stdin == null) {
            throw new GrepException(NULL_POINTER);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stdin))) {
            String line;
            Pattern compiledPattern;
            if (isCaseInsensitive) {
                compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            } else {
                compiledPattern = Pattern.compile(pattern);
            }
            while ((line = reader.readLine()) != null) {
                Matcher matcher = compiledPattern.matcher(line);
                if (matcher.find()) { // match
                    stringJoiner.add(line);
                    count++;
                }
            }
        } catch (PatternSyntaxException pse) {
            throw (GrepException) new GrepException(ERR_INVALID_REGEX).initCause(pse);
        } catch (NullPointerException npe) {
            throw (GrepException) new GrepException(ERR_FILE_NOT_FOUND).initCause(npe);
        }

        String results = "";
        if (isCountLines) {
            results = count + STRING_NEWLINE;
        } else {
            if (!stringJoiner.toString().isEmpty()) {
                results = stringJoiner.toString() + STRING_NEWLINE;
            }
        }
        return results;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            boolean[] grepFlags = new boolean[NUM_ARGUMENTS];
            ArrayList<String> inputFiles = new ArrayList<>();
            String pattern = getGrepArguments(args, grepFlags, inputFiles);
            String result = "";

            if (stdin == null && inputFiles.isEmpty()) {
                throw new Exception(ERR_NO_INPUT);
            }
            if (args == null || args.length == 0 || args[0].equals("")) {
                throw new GrepException(NO_EMPTY_REGEX);
            }
            if (pattern == null) {
                throw new Exception(ERR_SYNTAX);
            }

            if (pattern.isEmpty()) {
                throw new Exception(EMPTY_PATTERN);
            } else {
                if (inputFiles.isEmpty()) {
                    result = grepFromStdin(pattern, grepFlags[CASE_INSEN_IDX], grepFlags[COUNT_INDEX], stdin);
                } else {
                    String[] inputFilesArray = new String[inputFiles.size()];
                    inputFilesArray = inputFiles.toArray(inputFilesArray);
                    result = grepFromFiles(pattern, grepFlags[CASE_INSEN_IDX], grepFlags[COUNT_INDEX], inputFilesArray);

                }
            }
            stdout.write(result.getBytes());
        } catch (GrepException grepException) {
            throw grepException;
        } catch (Exception e) {
            throw (GrepException) new GrepException(e.getMessage()).initCause(e);
        }
    }

    /**
     * Separates the arguments provided by user into the flags, pattern and input files.
     * @param args supplied by user
     * @param grepFlags a bool array of possible flags in grep
     * @param inputFiles a ArrayList<String> of file names supplied by user
     * @return regex pattern supplied by user. An empty String if not supplied.
     */
    private String getGrepArguments(String[] args, boolean[] grepFlags, ArrayList<String> inputFiles) throws Exception {
        String pattern = null;
        boolean isFile = false; // files can only appear after pattern

        for (String s : args) {
            char[] arg = s.toCharArray();
            if (isFile) {
                inputFiles.add(s);
            } else {
                if (!s.isEmpty() && arg[0] == CHAR_FLAG_PREFIX) {
                    arg = Arrays.copyOfRange(arg, 1, arg.length);
                    for (char c : arg) {
                        switch (c) {
                            case CASE_INSEN_IDENT:
                                grepFlags[CASE_INSEN_IDX] = true;
                                break;
                            case COUNT_IDENT:
                                grepFlags[COUNT_INDEX] = true;
                                break;
                            default:
                                throw new GrepException(ERR_SYNTAX);
                        }
                    }
                } else { // pattern must come before file names
                    pattern = s;
                    isFile = true; // next arg onwards will be file
                }
            }
        }
        return pattern;
    }
}
