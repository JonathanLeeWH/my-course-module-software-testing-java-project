package sg.edu.nus.comp.cs4218.impl.app;

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
     * @param fileName    Array of String of file names
     * @throws Exception  
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

            if (isRecursive) { // if -r flag is present for example -r or -r -d or -rd will call the same method.
                removeFilesAndFolderContent(node);
            }
        }
        if (rmException != null) {
            throw rmException;
        }
    }

    /**
     * Removes input file.
     * Precondition: Input file is not a directory.
     * @param fileName input file to be deleted.
     * @throws Exception
     */
    public void removeFileOnly(File fileName) throws Exception {
        try {
            if (!fileName.isDirectory()) {
                Files.delete(fileName.toPath());
            }
        } catch (IOException e) {
            throw (RmException) new RmException(ERR_IO_EXCEPTION).initCause(e);
        }
    }

    /**
     * Removes input file or input empty folder.
     * @param fileName  input file to be deleted.
     * @throws Exception
     */
    public void removeFileAndEmptyFolderOnly(File fileName) throws Exception {
        try {
            Files.delete(fileName.toPath());
        } catch (DirectoryNotEmptyException e) {
            throw (RmException) new RmException(ERR_NON_EMPTY_DIR).initCause(e);
        } catch (IOException e) {
            throw (RmException) new RmException(ERR_IO_EXCEPTION).initCause(e);
        }
    }

    /**
     * Removes input file or input folder and its contents by traversing recursively to delete the input folder contents.
     * @param fileName input file to be deleted.
     * @throws Exception
     */
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

    /**
     * Runs RmApplication with specified input data and specified output stream.
     * @param args Array of arguments for the RmApplication
     * @param stdin An InputStream, not used.
     * @param stdout An OutputStream, not used.
     * @throws AbstractApplicationException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
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