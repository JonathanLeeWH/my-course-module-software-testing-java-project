package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.exception.RmException;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CpApplication implements CpInterface {
    /**
     * copy content of source file to destination file
     *
     * @param srcFile  of path to source file
     * @param destFile of path to destination file
     * @throws CpException
     */
    @Override
    public String cpSrcFileToDestFile(String srcFile, String destFile) throws CpException {
        return null;
    }

    /**
     * copy files to destination folder
     *
     * @param destFolder of path to destination folder
     * @param fileName   Array of String of file names
     * @throws CpException
     */
    @Override
    public String cpFilesToFolder(String destFolder, String... fileName) throws CpException {
        return null;
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
        // TODO: To be implemented
    }
}
