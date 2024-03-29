package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.parser.RmArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class RmApplication implements RmInterface {

    /**
     * Remove the file. (It does not remove folder by default)
     *
     * @param isEmptyFolder Boolean option to delete a folder only if it is empty
     * @param isRecursive   Boolean option to recursively delete the folder contents (traversing
     *                      through all folders inside the specified folder)
     * @param fileName      Array of String of file names
     * @throws RmException If RmException is thrown by methods called in its body or the input file or directory does not exist or
     *                     if there are no -d flag and the input file is a directory or if the input is the current directory.
     */
    @Override
    public void remove(Boolean isEmptyFolder, Boolean isRecursive, String... fileName) throws RmException {
        if (isEmptyFolder == null || isRecursive == null || fileName == null) {
            throw new RmException(ERR_NULL_ARGS);
        }
        if (fileName.length == 0) {
            throw new RmException(ERR_MISSING_ARG);
        }
        RmException rmException = null;
        for (String current : fileName) {
            File node = IOUtils.resolveFilePath(current).normalize().toFile();
            if (!node.exists()) {
                rmException = new RmException(ERR_FILE_NOT_FOUND);
                continue;
            }
            try {
                // Check if both the current directory and the current input path is the same, if it is the same, throw an RmException with ERR_IS_CURR_DIR
                if (Files.isSameFile(Paths.get(EnvironmentHelper.currentDirectory), node.toPath())) {
                    rmException = new RmException(ERR_IS_CURR_DIR);
                    continue;
                }
                if (isSubPath(node.toPath())) {
                    rmException = new RmException(ERR_IS_SUB_PATH);
                    continue;
                }
                if (!isEmptyFolder && !isRecursive) { // no -r and no -d flag
                    removeFileOnly(node);
                }
                if (isEmptyFolder && !isRecursive) { // no -r but have -d flag
                    removeFileAndEmptyFolderOnly(node);
                }
                if (isRecursive) { // if -r flag is present for example -r or -r -d or -d -r or -rd or -dr will call the same method.
                    removeFilesAndFolderContent(node);
                }
            } catch (RmException e) {
                rmException = e;
            } catch (IOException e) {
                rmException = (RmException) new RmException(ERR_IO_EXCEPTION).initCause(e);
            }
        }
        if (rmException != null) {
            throw rmException;
        }
    }

    /**
     * Removes input file.
     * Precondition: Input file is not a directory.
     *
     * @param fileName input file to be deleted.
     * @throws RmException If an IOException occurred or if the input file is a directory.
     */
    public void removeFileOnly(File fileName) throws RmException {
        try {
            if (fileName.isDirectory()) {
                throw new RmException(ERR_IS_DIR);
            }
            validateReadOnly(fileName);
            Files.delete(fileName.toPath());
        } catch (IOException e) {
            throw (RmException) new RmException(ERR_IO_EXCEPTION).initCause(e);
        }
    }

    /**
     * Removes input file or input empty folder.
     *
     * @param fileName input file to be deleted.
     * @throws RmException If the input is a non empty directory or an IOException occurred.
     */
    public void removeFileAndEmptyFolderOnly(File fileName) throws RmException {
        try {
            if (Files.isDirectory(fileName.toPath())) {
                String[] directoryContents = fileName.list();
                if (directoryContents != null && directoryContents.length > 0) {
                    throw new RmException(ERR_NON_EMPTY_DIR);
                }
                validateWriteOnly(fileName);
            }
            validateReadOnly(fileName);
            Files.delete(fileName.toPath());
        } catch (IOException e) {
            throw (RmException) new RmException(ERR_IO_EXCEPTION).initCause(e);
        }
    }

    /**
     * Removes input file or input folder and its contents by traversing recursively to delete the input folder contents.
     *
     * @param fileName input file to be deleted.
     * @throws RmException if there is an IOException.
     */
    public void removeFilesAndFolderContent(File fileName) throws RmException {
        if (fileName.isDirectory()) {
            File[] contents = fileName.listFiles();
            if (contents != null) {
                for (File file : contents) {
                    removeFilesAndFolderContent(file); // Recursive call
                }
            }
        }

        try {
            validateReadOnly(fileName);
            Files.delete(fileName.toPath()); // delete the file.
        } catch (IOException e) {
            throw (RmException) new RmException(ERR_IO_EXCEPTION).initCause(e);
        }
    }

    /**
     * Runs RmApplication with specified input data and specified output stream.
     * Assumption:
     * 1) The rm command will allow throw one exception (the latest exception) when there are potentially one or more
     * exceptions due to the files and/or folders passed into the rm command arguments.
     * This behaviour differs from unix.
     * 2) The rm command -rd or -r -d or -r flags (including the permutation of the flags) would attempt to delete all files and/or folders input as arguments for the rm command
     * together with the contents of non empty folders. In other words, their behaviour is the same.
     *
     * @param args   Array of arguments for the RmApplication
     * @param stdin  An InputStream, not used.
     * @param stdout An OutputStream, not used.
     * @throws RmException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws RmException {
        if (args == null) {
            throw new RmException(ERR_NULL_ARGS);
        }

        RmArgsParser parser = new RmArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw (RmException) new RmException(e.getMessage()).initCause(e);
        }

        Boolean emptyFolder = parser.isEmptyFolder();
        Boolean recursive = parser.isRecursive();
        String[] fileNames = parser.getFileNames()
                .toArray(new String[parser.getFileNames().size()]);
        if (fileNames.length == 0) {
            throw new RmException(ERR_MISSING_ARG);
        } else {
            remove(emptyFolder, recursive, fileNames);
        }
    }

    /**
     * Returns true if the input path is a sub path of the current directory. Otherwise, returns false.
     *
     * @param inputPath The input path to be check whether it is the sub path of the current directory.
     * @return Returns true if the input path is sub path of the current directory. Otherwise, returns false.
     */
    private boolean isSubPath(Path inputPath) {
        Path currentDirectory = Paths.get(EnvironmentHelper.currentDirectory).normalize().toAbsolutePath();
        return currentDirectory.startsWith(inputPath.normalize().toAbsolutePath());
    }

    private boolean isReadOnly(File file) throws RmException {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD
            try {
                Object object = Files.getAttribute(file.toPath(), "dos:readonly");
                return Boolean.TRUE == Files.getAttribute(file.toPath(), "dos:readonly");
            } catch (IOException e) {
                throw (RmException) new RmException(ERR_IO_EXCEPTION).initCause(e);
            }
        } else {
            return !file.canWrite();
        }
    }

    private void validateReadOnly(File file) throws RmException {
        if (file.exists() && isReadOnly(file)) {
            throw new RmException(ERR_NO_PERM);
        }
    }

    private void validateWriteOnly(File file) throws RmException {
        if (file.exists() && !file.canRead() && !file.canExecute() && file.canWrite()) { // Might not work on Windows as stated in our Assumptions report. This is just for minimum check.
            throw new RmException(ERR_NO_PERM);
        }
    }
}
