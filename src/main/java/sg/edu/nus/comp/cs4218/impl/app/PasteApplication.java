package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.PasteException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

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
        if (stdout == null) { // if stdout is empty
            throw new PasteException(ERR_NULL_STREAMS);
        }
        if (args.length == 0) { // When there are no filenames provided (i.e. stdin provided)
            if (stdin == null) { // if stdin is empty
                throw new PasteException(ERR_NULL_STREAMS);
            }
            try { // if stdin is not empty.
                stdout.write(mergeStdin(stdin).getBytes()); // print the output of the stdin
            } catch (Exception e) {
                throw (PasteException) new PasteException(FILE_NOT_FOUND).initCause(e);
            }
        } else {
            List<String> filesNamesList = new ArrayList<>(); // Since total number of files is unknown, use ArrayList.
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-")) { // check if argument is a stdin type of argument.
                    hasStdin = 1;
                    if (i != 0) {
                        throw new PasteException(INVALID_DASH);
                    }
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
                    stdout.write(mergeStdin(stdin).getBytes());
                } else if (sum == 2) { //2: means only filenames are present in the argument.
                    stdout.write(mergeFile(allFileNames).getBytes());
                } else { //  3: means both standard inputs and filenames are present in the argument.
                    stdout.write(mergeFileAndStdin(stdin, allFileNames).getBytes());
                }
            } catch (Exception e) {
                throw (PasteException) new PasteException(FILE_NOT_FOUND).initCause(e);
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
        return paste(stdinToBRArray(stdin));
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files. If only one file is
     * specified, echo back the file content.
     *
     * @param fileName Array of file names to be read and merged
     * @throws PasteException FILE_NOT_FOUND
     */
    public String mergeFile(String... fileName) throws PasteException, IOException {
        try {
            BufferedReader[] bufferedReaders = new BufferedReader[fileName.length];
            FileReader fileReader = null; //NOPMD
            for (int i = 0; i < fileName.length; i++) {
                fileReader = new FileReader(fileName[i]);
                bufferedReaders[i] = new BufferedReader(fileReader);
            }
            String output = paste(bufferedReaders);
            assert fileReader != null;
            fileReader.close();
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
        try {
            BufferedReader[] stdinBR = stdinToBRArray(stdin);
            int totalSize = fileName.length + stdinBR.length;
            BufferedReader[] bufferedReaders = new BufferedReader[totalSize];
            System.arraycopy(stdinBR, 0, bufferedReaders, 0, stdinBR.length);
            FileReader fileReader = null; //NOPMD
            for (int i = stdinBR.length, k = 0; i < totalSize; i++) {
                fileReader = new FileReader(fileName[k]);
                bufferedReaders[i] = new BufferedReader(fileReader);
            }
            String output = paste(bufferedReaders);
            assert fileReader != null;
            fileReader.close();
            for (BufferedReader sBR : stdinBR) { //NOPMD
                sBR.close();
            }
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
        String tab = "\t", newLine = System.lineSeparator();
        StringBuilder stringBuilder = new StringBuilder();
        boolean hasMoreLines = true;
        while (hasMoreLines) {
            boolean allLinesNull = true;
                for (int i = 0; i < bufferedReaders.length; i++) {
                    String currentLine = bufferedReaders[i].readLine();
                    if (currentLine != null && allLinesNull) {
                        allLinesNull = false;
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(newLine);
                            stringBuilder.append(currentLine);
                        } else if (i == 0) {
                            stringBuilder.append(currentLine);
                        }
                    }
                    else if (currentLine != null) {
                        stringBuilder.append(tab).append(currentLine);
                    }
                }
            if (allLinesNull) {
                hasMoreLines = false;
            }
        }
        for (BufferedReader br : bufferedReaders) { //NOPMD
            br.close();
        }
        return stringBuilder.toString().concat(newLine);
    }

    private BufferedReader[] stdinToBRArray(InputStream stdin) throws PasteException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdin));
            StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());
            List<String> tokens = new ArrayList<>();
            while (stringTokenizer.hasMoreElements()) {
                tokens.add(stringTokenizer.nextToken());
            }
            String[] fileNames = new String[tokens.size()];
            // ArrayList to Array Conversion
            for (int j = 0; j < tokens.size(); j++) {
                // Assign each value to String array
                fileNames[j] = tokens.get(j);
            }
            BufferedReader[] bufferedReaders = new BufferedReader[fileNames.length];
            FileReader fileReader = null; //NOPMD
            for (int j = 0; j < fileNames.length; j++) {
                fileReader = new FileReader(fileNames[j]);
                bufferedReaders[j] = new BufferedReader(new FileReader(fileNames[j]));
            }
            assert fileReader != null;
            fileReader.close();
            return bufferedReaders;
        } catch (IOException e) {
            throw (PasteException) new PasteException(FILE_NOT_FOUND).initCause(e);
        }
    }
}