package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

public class PasteApplication implements PasteInterface {
    /**
     * Runs the paste application.
     *
     * @param args   Array of arguments for the application, not used.
     * @param stdin  An InputStream, not used.
     * @param stdout An OutputStream, not used.
     * @throws PasteException ERR_NULL_STREAMS, FILE_NOT_FOUND
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws PasteException {
        int hasFile = 0, hasStdin = 0, sum = 0; // Let hasStdin be 0 when there is no stdin, and 1 when there is stdin. Let hasFile be 0 when there is no file name, and 2 when there is at least one file name.
        if (stdout == null || stdin == null) { // if stdout is empty
            throw new PasteException(ERR_NULL_STREAMS);
        }
        if (args == null) {
            throw new PasteException(ERR_NULL_ARGS);
        }
        String output = "";
        if (args.length == 0) { // When there are no filenames provided (i.e. stdin provided)
            if (stdin == null) { // if stdin is empty
                throw new PasteException(ERR_NULL_STREAMS);
            }
            try { // if stdin is not empty.
                output = mergeStdin(stdin);
                if (output.isEmpty()) {
                    stdout.write(output.getBytes()); // print the output of the stdin
                } else {
                    stdout.write(output.concat(STRING_NEWLINE).getBytes());
                }
            } catch (Exception e) {
                throw (PasteException) new PasteException(FILE_NOT_FOUND).initCause(e);
            }
        } else {
            List<String> filesNamesList = new ArrayList<>(); // Since total number of files is unknown, use ArrayList.
            for (int i = 0; i < args.length; i++) {
                if (hasStdin == 1) {
                    if (args[i].equals("-")) {
                        throw new PasteException(INVALID_DASH);
                    }
                }
                if (args[i].equals("-")) { // check if argument is a stdin type of argument.
                    hasStdin = 1;
                } else { // else argument is a filename.
                    hasFile = 2;
                    filesNamesList.add(args[i]);
                }
            }
            String[] allFileNames = new String[filesNamesList.size()];
            for (int j = 0; j < filesNamesList.size(); j++) { //Convert List of Filenames to String Array to pass into the subsequent merging method.
                allFileNames[j] = filesNamesList.get(j);
            }
            sum = hasFile + hasStdin;
            try {
                if (sum == 1) { //1: means only standard inputs are present in the argument.
                    output = mergeStdin(stdin);
                    if (output.isEmpty()) {
                        stdout.write(output.getBytes());
                    } else {
                        stdout.write(output.concat(STRING_NEWLINE).getBytes());
                    }
                } else if (sum == 2) { //2: means only filenames are present in the argument.
                    output = mergeFile(allFileNames);
                    if (output.isEmpty()) {
                        stdout.write(output.getBytes());
                    } else {
                        stdout.write(output.concat(STRING_NEWLINE).getBytes());
                    }
                } else { //  3: means both standard inputs and filenames are present in the argument.
                    output = mergeFileAndStdin(stdin, allFileNames);
                    if (output.isEmpty()) {
                        stdout.write(output.getBytes());
                    } else {
                        stdout.write(output.concat(STRING_NEWLINE).getBytes());
                    }
                }
            } catch (Exception e) {
                throw (PasteException) new PasteException(ERR_FILE_NOT_FOUND).initCause(e);
            }
        }
    }
    /**
     * Returns string of line-wise concatenated (tab-separated) Stdin arguments. If only one Stdin
     * arg is specified, echo back the Stdin.
     *
     * @param stdin InputStream containing arguments from Stdin
     * @throws PasteException ERR_NULL_STREAMS
     */
    public String mergeStdin(InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new PasteException(ERR_NULL_STREAMS);
        }
        List<String> stdinContent = IOUtils.getLinesFromInputStream(stdin);
        String delimiter = STRING_NEWLINE;
        return String.join(delimiter, stdinContent);
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files. If only one file is
     * specified, echo back the file content.
     *
     * @param fileName Array of file names to be read and merged
     * @throws PasteException FILE_NOT_FOUND
     */
    public String mergeFile(String... fileName) throws PasteException {
        if (fileName == null) {
            throw new PasteException(ERR_NULL_ARGS);
        }
        for (String s : fileName) {
            try {
                Path path = IOUtils.resolveFilePath(s);
                File file = new File(path.toString());
                if (file.isDirectory()) {
                    throw new PasteException(ERR_IS_DIR);
                }
            } catch (InvalidPathException e) {
                throw new PasteException(ERR_INVALID_FILE);
            }
        }
        try {
            BufferedReader[] bufferedReaders = new BufferedReader[fileName.length];
            FileReader fileReader = null; //NOPMD
            for (int i = 0; i < fileName.length; i++) {
                fileReader = new FileReader(IOUtils.resolveFilePath(fileName[i]).toString());
                bufferedReaders[i] = new BufferedReader(fileReader);
            }
            String output = paste(bufferedReaders);
            assert fileReader != null;
            fileReader.close();
            for (BufferedReader bufferedReader : bufferedReaders) { //NOPMD
                bufferedReader.close();
            }
            return output;
        } catch (Exception e) {
            throw (PasteException) new PasteException(FILE_NOT_FOUND).initCause(e);
        }
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files and Stdin arguments.
     *
     * @param stdin    InputStream containing arguments from Stdin
     * @param fileName Array of file names to be read and merged
     * @throws PasteException ERR_NULL_STREAMS
     */
    public String mergeFileAndStdin(InputStream stdin, String... fileName) throws Exception {
        if (stdin == null) {
            throw new PasteException(ERR_NULL_STREAMS);
        }
        if (fileName == null) {
            throw new PasteException(ERR_NULL_ARGS);
        }
        if (fileName.length == 1 && fileName[0].equals("-")) {
            return mergeStdin(stdin);
        }
        try {
            BufferedReader[] bufferedReaders = new BufferedReader[fileName.length + 1];
            bufferedReaders[0] = new BufferedReader(new InputStreamReader(stdin));
            FileReader fileReader = null; //NOPMD
            for (int i = 1; i <= fileName.length; i++) {
                fileReader = new FileReader(IOUtils.resolveFilePath(fileName[i-1]).toString());
                bufferedReaders[i] = new BufferedReader(fileReader);
            }
            String output = paste(bufferedReaders);
            assert fileReader != null;
            fileReader.close();
            for (BufferedReader br : bufferedReaders) { //NOPMD
                br.close();
            }
            return output;
        } catch (Exception e) {
            throw (PasteException) new PasteException(FILE_NOT_FOUND).initCause(e);
        }
    }

    /**
     * paste all contents from all buffered readers
     *
     * @param bufferedReaders
     *            buffered readers to merge content
     * @return the merged string
     */
    private String paste(BufferedReader... bufferedReaders) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        boolean hasMoreLines = true;
        String previousLine = "";
        while (hasMoreLines) {
            boolean allLinesNull = true;
                for (int i = 0; i < bufferedReaders.length; i++) {
                    String currentLine = bufferedReaders[i].readLine();
                    if (currentLine != null && allLinesNull) {
                        allLinesNull = false;
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(STRING_NEWLINE);
                            stringBuilder.append(currentLine);
                        } else if (i == 0) {
                            stringBuilder.append(currentLine);
                        }
                    } else if (currentLine != null && !StringUtils.isBlank(previousLine)) {
                        stringBuilder.append(CHAR_TAB).append(currentLine);
                    } else if (currentLine != null && StringUtils.isBlank(previousLine)) {
                        stringBuilder.append(currentLine);
                    }
                    previousLine = currentLine;
                }
            if (allLinesNull) {
                hasMoreLines = false;
            }
        }
        for (BufferedReader br : bufferedReaders) { //NOPMD
            br.close();
        }
        return stringBuilder.toString();
    }
}