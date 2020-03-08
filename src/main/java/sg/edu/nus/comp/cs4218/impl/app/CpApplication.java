package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CpApplication implements CpInterface {
    /**
     * copy content of source file to destination file
     *
     * @param srcFile  of path to source file
     * @param destFile of path to destination file which may or may not exists. If it exists, it will be overwritten by the contents of srcFile.
     * @throws CpException Throws CpException if source file does not exists or if source file is a directory, or source file and destination file are the same file
     * or if an IOException occurs.
     */
    @Override
    public String cpSrcFileToDestFile(String srcFile, String destFile) throws CpException {
        Path srcPath = IOUtils.resolveFilePath(srcFile);
        Path destPath = IOUtils.resolveFilePath(destFile);
        if (!Files.exists(srcPath)) {
            throw new CpException(ERR_FILE_NOT_FOUND); // if source file does not exist.
        }
        if (Files.isDirectory(srcPath)) {
            throw new CpException(ERR_IS_DIR);
        }
        try {
            if (Files.exists(destPath) && Files.isSameFile(srcPath, destPath)) {
                throw new CpException(ERR_SRC_DEST_SAME);
            }
            Files.copy(srcPath, destPath, REPLACE_EXISTING);
        } catch (IOException e) {
            throw (CpException) new CpException(ERR_IO_EXCEPTION).initCause(e);
        }
        return destFile;
    }

    /**
     * copy files to destination folder
     *
     * @param destFolder of path to destination folder
     * @param fileName   Array of String of file names
     * @throws CpException Throws CpException if destination folder does not exists or input source file does not exist, input source file is a directory
     * or if an IOException occurs.
     */
    @Override
    public String cpFilesToFolder(String destFolder, String... fileName) throws CpException {
        Path destFolderPath = IOUtils.resolveFilePath(destFolder);
        if (!Files.isDirectory(destFolderPath)) {
            throw new CpException(ERR_FILE_NOT_FOUND); // if destination folder does not exist.
        }
        CpException cpException = null;
        for (String current : fileName) {
            File node = IOUtils.resolveFilePath(current).toFile();
            if (!node.exists()) {
                cpException = new CpException(ERR_FILE_NOT_FOUND); // if source file does not exist.
                continue;
            }
            if (Files.isDirectory(node.toPath())) {
                cpException = new CpException(ERR_IS_DIR);
                continue;
            }
            try {
                Files.copy(node.toPath(), destFolderPath.resolve(node.toPath().getFileName()), REPLACE_EXISTING);
            } catch (IOException e) {
                cpException = (CpException) new CpException(ERR_IO_EXCEPTION).initCause(e);
            }
        }
        if (cpException != null) {
            throw cpException;
        }
        return destFolder;
    }

    /**
     * Runs CpApplication with specified input data and specified output stream.
     * @param args Array of arguments for the CpApplication
     * @param stdin An InputStream, not used.
     * @param stdout An OutputStream, not used.
     * @throws CpException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws CpException {
        if (args == null) {
            throw new CpException(ERR_NULL_ARGS);
        }

        if (stdin == null || stdout == null) {
            throw new CpException(ERR_NULL_STREAMS); // for defensive programming despite streams not used as stated in skeleton comments.
        }

        if (args.length == 0) {
            throw new CpException(ERR_NO_ARGS);
        }

        if (args.length == 1) {
            throw new CpException(ERR_MISSING_ARG);
        }

        String lastArg = args[args.length - 1];
        Path path = IOUtils.resolveFilePath(lastArg);
        if (args.length == 2 && !Files.isDirectory(path)) { // if two input argument and the last argument is an existing destination folder, call cpSrcFileToDestFile method
            cpSrcFileToDestFile(args[0], args[args.length - 1]);
            return;
        }
        cpFilesToFolder(lastArg, Arrays.copyOfRange(args, 0, args.length - 1)); // the second argument exclude the last argument
        return;
    }
}
