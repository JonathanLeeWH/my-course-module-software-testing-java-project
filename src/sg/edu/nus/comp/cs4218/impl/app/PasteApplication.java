package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.PasteException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
        if (args == null) {
            throw new PasteException(ERR_NULL_ARGS);
        }
        if (stdout == null) {
            throw new PasteException(ERR_NULL_STREAMS);
        }

        File outputFile = new File(args[args.length-1]);
        for (int i = 1; i < args.length-1; i++) {
            try {
                File currentFile = new File(args[i]);
                if (i == 1) {
                    Files.copy(currentFile.toPath(), outputFile.toPath());
                } else {
                    pasteFile(currentFile, outputFile);
                }
            } catch (Exception e) {
                throw new PasteException(ERR_FILE_NOT_FOUND);
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
        return "";
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files. If only one file is
     * specified, echo back the file content.
     *
     * @param fileName Array of file names to be read and merged
     * @throws Exception
     */
    @Override
    public String mergeFile(String... fileName) throws Exception {
        return "";
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files and Stdin arguments.
     *
     * @param stdin    InputStream containing arguments from Stdin
     * @param fileName Array of file names to be read and merged
     * @throws Exception
     */
    @Override
    public String mergeFileAndStdin(InputStream stdin, String... fileName) throws Exception {
        return "";
    }

    private void pasteFile (File currentFile, File outputFile) throws IOException {
        List<String> linesInCurrentFile = new ArrayList<>(Files.readAllLines(currentFile.toPath(), StandardCharsets.UTF_8));
        List<String> linesInOutputFile = new ArrayList<>(Files.readAllLines(outputFile.toPath(), StandardCharsets.UTF_8));

        for (int i = 0; i < linesInOutputFile.size(); i++) {
            String outputLine = linesInOutputFile.get(i);
            outputLine = outputLine.concat("\t");
            outputLine = outputLine.concat(linesInCurrentFile.get(i));
            linesInOutputFile.set(i, outputLine);
        }
        Files.write(outputFile.toPath(), linesInOutputFile);
    }
}
