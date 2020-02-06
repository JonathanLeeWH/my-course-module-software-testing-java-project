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
     * @throws RmException
     *
     */
    @Override
    public void remove(Boolean isEmptyFolder, Boolean isRecursive, String... fileName) throws RmException {
        for (String current : fileName) {
            File node = IOUtils.resolveFilePath(current).toFile();
            if (!node.exists()) {
                throw new RmException(ERR_FILE_NOT_FOUND);
            }
            if (node.isDirectory() && !isEmptyFolder && !isRecursive) {
                throw new RmException(ERR_IS_DIR);
            }

            if (!isEmptyFolder && !isRecursive) { // no -r and no -d flag
                removeFileOnly(node);
            }

            if (isEmptyFolder && !isRecursive) { // no -r but have -d flag
                removeFileAndEmptyFolderOnly(node);
            }

            if (!isEmptyFolder && isRecursive) { // -r but no -d flag
                removeFilesAndFolderContent(node, false); // Need to ask about -r but no -d flag.
            }

            if (isEmptyFolder && isRecursive) {
                removeFilesAndFolderContent(node, true);
            }

        }
    }

    public void removeFileOnly(File fileName) throws RmException {
        try {
            if (!fileName.isDirectory()) {
                Files.delete(fileName.toPath());
            }
        } catch (IOException e) {
            throw (RmException) new RmException(e.getMessage()).initCause(e);
        }
    }

    public void removeFileAndEmptyFolderOnly(File fileName) throws RmException {
        try {
            Files.delete(fileName.toPath());
        } catch (DirectoryNotEmptyException e) {
            throw (RmException) new RmException("Non empty directory").initCause(e);
        } catch (IOException e) {
            throw (RmException) new RmException(e.getMessage()).initCause(e);
        }
    }

    public void removeFilesAndFolderContent(File fileName, boolean removeFolder) throws RmException {
        if (fileName.isDirectory()) {
            File[] contents = fileName.listFiles();
            if (contents != null) {
                for (File file : contents) {
                    removeFilesAndFolderContent(file, removeFolder); // Recursive call
                }
            }
        }

        if (removeFolder) {
            fileName.delete(); // delete the file.
        } else {
            if (!fileName.isDirectory()) {
                fileName.delete(); // delete the file.
            }
        }
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws RmException {
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
            remove(emptyFolder, recursive, fileNames);
        }


    }
}
