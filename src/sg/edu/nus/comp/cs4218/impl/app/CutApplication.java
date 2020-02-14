package sg.edu.nus.comp.cs4218.impl.app;

import javafx.util.Pair;
import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.CutArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CutApplication implements CutInterface {
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
               //throw new CutException(ERR_INVALID_FLAG);
            }
            if (files == null) {
                output.append(cutFromStdin(isCharPos, isBytePos, isRange, position.getKey(), position.getValue(), stdin));
            }
            else {
                output.append(cutFromFiles(isCharPos, isBytePos, isRange, position.getKey(), position.getValue(), files));
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

        List<String> lines = new ArrayList<>();
        List<String> results;
        for (String file : fileName) {
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

        if (isCharPo) {
            results = retrieveByCharPos(lines, isRange, startIdx, endIdx);
        }
        else if (isBytePo) {
            results = retrieveByBytePos(lines, isRange, startIdx, endIdx);
        }
        else {
            throw new CutException(ERR_MISSING_ARG);
        }

        return String.join(STRING_NEWLINE, results);
    }

    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new CutException(ERR_NULL_STREAMS);
        }
        List<String> lines = IOUtils.getLinesFromInputStream(stdin);
        List<String> results;
        if (isCharPo) {
            results = retrieveByCharPos(lines, isRange, startIdx, endIdx);
        }
        else if (isBytePo) {
            results = retrieveByBytePos(lines, isRange, startIdx, endIdx);
        }
        else {
            throw new CutException(ERR_MISSING_ARG);
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
            if (endIdx == 0) {
                char val = line.charAt(startIdx - 1);
                results.add(String.valueOf(val));
            }
            else if (isRange) {
                String val = line.substring(startIdx - 1, endIdx);
                results.add(val);
            }
            else {
                // This is assumed that size of list of comma separated numbers is 2.
                char startVal = line.charAt(startIdx - 1);
                char endVal = line.charAt(endIdx - 1);
                if (startIdx > endIdx) {
                    results.add(String.valueOf(endVal) + startVal);
                }
                else if (startIdx == endIdx) {
                    results.add(String.valueOf(startVal));
                }
                else {
                    results.add(String.valueOf(startVal) + endVal);
                }
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
        for (String line: lines) {
            int currBytePos = 1;
            if (endIdx == 0) {
                for (char val: line.toCharArray()) {
                    int byteLength = String.valueOf(val).getBytes().length;
                    if ((startIdx == currBytePos) || ((startIdx > currBytePos) && (startIdx < (currBytePos + byteLength)))) {
                        results.add(String.valueOf(val));
                        break;
                    }
                    currBytePos += byteLength;
                }
            }
            else {
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
        StringBuilder result = new StringBuilder();
        boolean hasStarted = false;
        boolean isWithinRange = false;
        for (char val : line.toCharArray()) {
            int byteLength = String.valueOf(val).getBytes().length;
            int nextBytePos = currBytePos + byteLength;
            if ((!hasStarted) && ((startIdx == currBytePos) || ((startIdx > currBytePos) && (startIdx < (nextBytePos))))) {
                if (isRange) {
                    isWithinRange = true;
                }
                hasStarted = true;
                result.append(val);
            } else if ((endIdx == currBytePos) || ((endIdx > currBytePos) && (endIdx < (nextBytePos)))) {
                result.append(val);
                break;
            } else if (isWithinRange) {
                result.append(val);
            }
            currBytePos = nextBytePos;
        }
        return result.toString();
    }
}
