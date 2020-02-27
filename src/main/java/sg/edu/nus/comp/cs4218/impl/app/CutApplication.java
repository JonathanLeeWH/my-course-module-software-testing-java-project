package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.CutArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CutApplication implements CutInterface {
    public static final String COMMAND = "cut";

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws CutException {
        if (args == null) {
            throw new CutException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new CutException(ERR_NO_OSTREAM);
        }

        // Parse arguments.
        CutArgsParser parser = new CutArgsParser();
        StringBuilder output = new StringBuilder();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw (CutException) new CutException(e.getMessage()).initCause(e);
        }

        Boolean isCharPos = parser.isCharPos();
        Boolean isBytePos = parser.isBytePos();
        Boolean isRange = parser.isRange();
        Pair<Integer, Integer> position = parser.getPositions();
        String[] files = parser.getFileNames();

        try {
            if ((isCharPos) && (isBytePos)) {
                throw new Exception(ERR_TOO_MANY_ARGS);
            }
            if ((!isCharPos) && (!isBytePos)) {
                throw new Exception(ERR_MISSING_ARG);
            }
            if (files != null) {
                //Remove duplicate - and use the earliest -
                List<String> newFiles = new ArrayList<>();
                int dashIdx = -1;
                boolean hasDash = false;
                if (files.length > 1) {
                    for (int i = 0; i < files.length; i++) {
                        if (!files[i].equals("-") || !newFiles.contains("-")) {
                            if ((files[i].equals("-")) && (!newFiles.contains("-"))) {
                                dashIdx = i;
                                hasDash = true;
                            }
                            newFiles.add(files[i]);
                        }
                    }
                    if (hasDash) {
                        newFiles.set(dashIdx, cutFromStdin(isCharPos, isBytePos, isRange, position.getKey(), position.getValue(), stdin) + "_stdin");
                    }
                    files = newFiles.stream().toArray(file -> new String[newFiles.size()]);
                    output.append(cutFromFiles(isCharPos, isBytePos, isRange, position.getKey(), position.getValue(), files));
                }
                else if ((files.length == 1) && (files[0].equals("-"))) {
                    output.append(cutFromStdin(isCharPos, isBytePos, isRange, position.getKey(), position.getValue(), stdin));
                }
                else {
                    output.append(cutFromFiles(isCharPos, isBytePos, isRange, position.getKey(), position.getValue(), files));
                }

            }
            else {
                output.append(cutFromStdin(isCharPos, isBytePos, isRange, position.getKey(), position.getValue(), stdin));
            }
        } catch (Exception e) {
            throw new CutException(e);
        }

        try {
            if (!output.toString().isEmpty()) {
                stdout.write(output.toString().getBytes());
                stdout.write(STRING_NEWLINE.getBytes());
            }
        } catch (IOException e) {
            throw new CutException(e, ERR_WRITE_STREAM);
        }
    }

    @Override
    public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, String... fileName) throws Exception {
        if (fileName == null) {
            throw new CutException(ERR_GENERAL);
        }
        if ((startIdx <= 0) || (endIdx <= 0)) {
            throw new CutException(ERR_LESS_THAN_ZERO);
        }

        List<String> lines = new ArrayList<>();
        List<String> results = new ArrayList<>();
        for (String file : fileName) {
            if (!file.contains("_stdin")) {
                File node = IOUtils.resolveFilePath(file).toFile();
                if (!node.exists()) {
                    throw new CutException(ERR_FILE_NOT_FOUND);
                }
                if (node.isDirectory()) {
                    throw new CutException(ERR_IS_DIR);
                }
                if (!node.canRead()) {
                    throw new CutException(ERR_NO_PERM);
                }

                try (InputStream input = IOUtils.openInputStream(file)) {
                    lines.addAll(IOUtils.getLinesFromInputStream(input));
                    IOUtils.closeInputStream(input);
                }
            }
            else {
                lines.add(file);
            }
        }

        if (isCharPo) {
            results = retrieveByCharPos(lines, isRange, startIdx, endIdx);
        }
        else if (isBytePo) {
            results = retrieveByBytePos(lines, isRange, startIdx, endIdx);
        }

        return String.join(STRING_NEWLINE, results);
    }

    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new CutException(ERR_NULL_STREAMS);
        }
        if ((startIdx <= 0) || (endIdx <= 0)) {
            throw new CutException(ERR_LESS_THAN_ZERO);
        }

        List<String> lines = IOUtils.getLinesFromInputStream(stdin);
        List<String> results = new ArrayList<>();
        if (isCharPo) {
            results = retrieveByCharPos(lines, isRange, startIdx, endIdx);
        }
        else if (isBytePo) {
            results = retrieveByBytePos(lines, isRange, startIdx, endIdx);
        }
        return String.join(STRING_NEWLINE, results);
    }

    /**
     * Retrieve selected portions of each line based on the position of the character.
     *
     * @param lines All lines supplied by user.
     * @param isRange Boolean option to check if LIST is in the form of range.
     * @param startIdx Starting position supplied by user.
     * @param endIdx Ending position supplied by user.
     * @return A list of results from cutting selected portions of each line.
     */
    private List<String> retrieveByCharPos(List<String> lines, Boolean isRange, int startIdx, int endIdx) {
        List<String> results = new ArrayList<>();
        for (String line: lines) {
            if (line.contains("_stdin")) {
                results.add(line.substring(0, line.indexOf("_stdin")));
                continue;
            }
            int start = startIdx;
            int end = endIdx;
            if (start - 1 <= 0) {
                start = 1;
            }
            if (end == start) {
                if (start <= line.length()) {
                    char val = line.charAt(start - 1);
                    results.add(String.valueOf(val));
                }
                else {
                    results.add("");
                }
            }
            else if (isRange) {
                if ((start <= end) && (start < line.length())) {
                    if (end > line.length()) {
                        end = line.length();
                    }
                    String val = line.substring(start - 1, end);
                    results.add(val);
                }
                else {
                    results.add("");
                }
            }
            else {
                // This is assumed that size of list of comma separated numbers is 2.
                StringBuilder result = new StringBuilder();
                char startVal = 0;
                char endVal = 0;
                if (start > end) {
                    end = startIdx;
                    start = endIdx;
                }
                if (start - 1 < line.length()) {
                    startVal = line.charAt(start - 1);
                }
                if ((end - 1 >= 0) && (end - 1 < line.length())) {
                    endVal = line.charAt(end - 1);
                }
                if (startVal != 0) {
                    result.append(startVal);
                }
                if (endVal != 0) {
                    result.append(endVal);
                }
                results.add(result.toString());
            }
        }
        return results;
    }

    /**
     * Retrieve selected portions of each line based on the position of the byte.
     *
     * @param lines All lines supplied by user.
     * @param isRange Boolean option to check if LIST is in the form of range.
     * @param startIdx Starting position supplied by user.
     * @param endIdx Ending position supplied by user.
     * @return A list of results from cutting selected portions of each line.
     */
    private List<String> retrieveByBytePos(List<String> lines, Boolean isRange, int startIdx, int endIdx) {
        List<String> results = new ArrayList<>();
        for (String line : lines) {
            if (line.contains("_stdin")) {
                results.add(line.substring(0, line.indexOf("_stdin")));
                continue;
            }
            int currBytePos = 1;
            if (endIdx == startIdx) {
                boolean hasAdded = false;
                for (char val : line.toCharArray()) {
                    int byteLength = String.valueOf(val).getBytes().length;
                    if ((startIdx == currBytePos) || ((startIdx > currBytePos) && (startIdx < (currBytePos + byteLength)))) {
                        results.add(String.valueOf(val));
                        hasAdded = true;
                        break;
                    }
                    currBytePos += byteLength;
                }
                if (!hasAdded) {
                    results.add("");
                }
            } else {
                results.add(getBytePosFromString(isRange, line, startIdx, endIdx));
            }
        }
        return results;
    }

    /**
     * Get byte position(s) from a line of string.
     *
     * @param isRange Boolean option to check if LIST is in the form of range.
     * @param line The line supplied by user.
     * @param startIdx Starting position supplied by user.
     * @param endIdx Ending position supplied by user.
     * @return A string based on the byte position supplied by user.
     */
    private String getBytePosFromString(Boolean isRange, String line, int startIdx, int endIdx) {
        int currBytePos = 1;
        int start = startIdx;
        int end = endIdx;
        StringBuilder result = new StringBuilder();
        boolean hasStarted = false;
        boolean isWithinRange = false;
        if ((isRange) && (start > end)) {
            return result.toString();
        }
        else {
            if (start > end) {
                end = startIdx;
                start = endIdx;
            }
            for (char val : line.toCharArray()) {
                int byteLength = String.valueOf(val).getBytes().length;
                int nextBytePos = currBytePos + byteLength;
                //if (end )
                if ((!hasStarted) && ((start == currBytePos) || ((start > currBytePos) && (start < (nextBytePos))))) {
                    if (isRange) {
                        isWithinRange = true;
                    }
                    hasStarted = true;
                    result.append(val);
                } else if ((end == currBytePos) || ((end > currBytePos) && (end < (nextBytePos)))) {
                    result.append(val);
                    break;
                } else if (isWithinRange) {
                    result.append(val);
                }
                currBytePos = nextBytePos;
            }
        }
        return result.toString();
    }
}
