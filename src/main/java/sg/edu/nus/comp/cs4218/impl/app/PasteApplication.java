package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.PasteException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class PasteApplication implements PasteInterface {
    /**
     * Runs the paste application.
     *
     * @param args   Array of arguments for the application, not used.
     * @param stdin  An InputStream, not used.
     * @param stdout An OutputStream, not used.
     * @throws PasteException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws PasteException {
        int hasFile = 0, hasStdin = 0, sum = 0;
        if (args.length == 0) { // When there are no filenames provided (i.e. stdin provided)
            if (stdin == null) { // if stdin is empty
                throw new PasteException(ERR_NULL_STREAMS);
            }
            try { // if stdin is not empty
                stdout.write(mergeStdin(stdin).getBytes()); // print the output of the stdin
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            List<String> filesNamesList = new ArrayList<>(); // Since total number of files is unknown, use ArrayList.
            /*
              Let hasStdin be 0 when there is no stdin, and 1 when there is at least one stdin.
              Let hasFile be 0 when there is no file name, and 2 when there is at least one file name.
              Let sum = hasStdin + hasFile.
              A sum of 1: means only standard inputs are present in the argument.
                       2: means only filenames are present in the argument.
                       3: means both standard inputs and filenames are present in the argument.
            */
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-")) { // check if argument is a stdin type of argument.
                    hasStdin = 1;
                } else { // else argument is a filename.
                    hasFile = 2;
                    filesNamesList.add(args[i]);
                }
            }
            String[] allFileNames = new String[filesNamesList.size()];
            //Convert List of Filenames to String Array to pass into the subsequent merging method.
            for (int j = 0; j < filesNamesList.size(); j++) {
                allFileNames[j] = filesNamesList.get(j);
            }
            sum = hasFile + hasStdin;
            try {
                if (sum == 1) {
                    stdout.write(mergeStdin(stdin).getBytes());
                }
                else if (sum == 2) { // only file present
                    stdout.write(mergeFile(allFileNames).getBytes());
                } else if (sum == 3) { // one argument is stdin, while the other is fileName
                    stdout.write(mergeFileAndStdin(stdin, allFileNames).getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Returns string of line-wise concatenated (tab-separated) Stdin arguments. If only one Stdin
     * arg is specified, echo back the Stdin.
     *
     * @param stdin InputStream containing arguments from Stdin
     * @throws Exception
     */
    public String mergeStdin(InputStream stdin) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdin));
            return paste(bufferedReader);
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files. If only one file is
     * specified, echo back the file content.
     *
     * @param fileName Array of file names to be read and merged
     * @throws Exception
     */
    public String mergeFile(String... fileName) throws Exception {
        BufferedReader[] bufferedReaders = new BufferedReader[fileName.length];
        for (int i = 0; i < fileName.length; i++) {
            bufferedReaders[i] = new BufferedReader(new FileReader(fileName[i]));
        }
        return paste(bufferedReaders);
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files and Stdin arguments.
     *
     * @param stdin    InputStream containing arguments from Stdin
     * @param fileName Array of file names to be read and merged
     * @throws Exception
     */
    public String mergeFileAndStdin(InputStream stdin, String... fileName) throws Exception {
        BufferedReader[] bufferedReaders = new BufferedReader[fileName.length + 1];
        for (int i = 0; i < fileName.length; i++) {
            bufferedReaders[i] = new BufferedReader(new FileReader(fileName[i]));
        }
        bufferedReaders[bufferedReaders.length - 1] = new BufferedReader(new InputStreamReader(stdin));
        return paste(bufferedReaders);
    }

    /**
     * paste all contents from all buffered readers
     *
     * @param bufferedReaders
     *            buffered readers to merge content
     * @return the merged string
     * @throws PasteException
     */
    private String paste(BufferedReader... bufferedReaders) throws PasteException {
        String tab = "\t", newLine = "\n";
        StringBuilder stringBuilder = new StringBuilder();
        boolean hasMoreLines = true, startOfLine = false;
        while (hasMoreLines) {
            boolean allLinesNull = true;
            for (int i = 0; i < bufferedReaders.length; i++) {
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (allLinesNull) {
                hasMoreLines = false;
            }

        }
        return stringBuilder.toString();
    }
}