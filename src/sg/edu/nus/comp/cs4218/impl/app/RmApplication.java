package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser;
import sg.edu.nus.comp.cs4218.impl.parser.RmArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class RmApplication implements RmInterface {

    /**
     * Remove the file. (It does not remove folder by default)
     *
     * @param isEmptyFolder Boolean option to delete a folder only if it is empty
     * @param isRecursive   Boolean option to recursively delete the folder contents (traversing
     *                      through all folders inside the specified folder)
     * @param fileName    Array of String of file names
     * @throws Exception if the file does not exist (ERR_FILE_NOT_FOUND) or one of the cases as shown below:
     * 1) isEmptyFolder and isRecursive are false and the input file is a directory (ERR_IS_DIR)
     * 2) If exception is thrown from any of the methods called such as removeFileOnly, removeFileAndEmptyFolderOnly or
     * removeFilesAndFolderContent.
     */
    @Override
    public void remove(Boolean isEmptyFolder, Boolean isRecursive, String... fileName) throws Exception {
        RmException rmException = null;
        for (String current : fileName) {
            File node = IOUtils.resolveFilePath(current).toFile();
            if (!node.exists()) {
                rmException = new RmException(ERR_FILE_NOT_FOUND);
                continue;
            }
            if (node.isDirectory() && !isEmptyFolder && !isRecursive) {
                rmException = new RmException(ERR_IS_DIR);
                continue;
            }

            if (!isEmptyFolder && !isRecursive) { // no -r and no -d flag
                removeFileOnly(node);
            }

            if (isEmptyFolder && !isRecursive) { // no -r but have -d flag
                removeFileAndEmptyFolderOnly(node);
            }

            if (isRecursive) { // if -r flag is present for example -r or -rd will call the same method.
                removeFilesAndFolderContent(node);
            }
        }
        if (rmException != null) {
            throw rmException;
        }
    }

    public void removeFileOnly(File fileName) throws Exception {
        try {
            if (!fileName.isDirectory()) {
                Files.delete(fileName.toPath());
            }
        } catch (IOException e) {
            throw (RmException) new RmException(ERR_IO_EXCEPTION).initCause(e);
        }
    }

    public void removeFileAndEmptyFolderOnly(File fileName) throws Exception {
        try {
            Files.delete(fileName.toPath());
        } catch (DirectoryNotEmptyException e) {
            throw (RmException) new RmException(ERR_NON_EMPTY_DIR).initCause(e);
        } catch (IOException e) {
            throw (RmException) new RmException(ERR_IO_EXCEPTION).initCause(e);
        }
    }

    public void removeFilesAndFolderContent(File fileName) throws Exception {
        if (fileName.isDirectory()) {
            File[] contents = fileName.listFiles();
            if (contents != null) {
                for (File file : contents) {
                    removeFilesAndFolderContent(file); // Recursive call
                }
            }
        }

        try {
            Files.delete(fileName.toPath()); // delete the file.
        } catch (IOException e) {
            throw (RmException) new RmException(ERR_IO_EXCEPTION).initCause(e);
        }
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        if (args == null) {
            throw new RmException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new RmException(ERR_NO_OSTREAM);
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
            try {
                remove(emptyFolder, recursive, fileNames);
            } catch (RmException rmException) {
                throw rmException;
            } catch(Exception e) {
                throw (RmException) new RmException(e.getMessage()).initCause(e);
            }
        }


    }
}
