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
            throw new Exception(ERR_GENERAL);
        }

        List<String> lines = new ArrayList<>();
        List<String> results = new ArrayList<>();
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
            results = retrieveByCharPos(lines, isRange, startIdx, endIdx);
        }
        else {
            throw new CutException(ERR_MISSING_ARG);
        }

        return String.join(STRING_NEWLINE, results);
    }

    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, InputStream stdin) throws Exception {
        return null;
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
        return results;
    }
}
