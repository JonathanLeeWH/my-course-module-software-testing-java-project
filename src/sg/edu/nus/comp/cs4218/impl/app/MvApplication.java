package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_CURR_DIR;

public class MvApplication implements MvInterface {

    private static final String NO_ARG_EXCEPTION ="No input found, please specify file to be moved";
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws MvException {

        if ( args == null || args.length == 0) {
            throw new MvException(NO_ARG_EXCEPTION);
        } else {
            mvFilesToFolder(args);
        }

    }
    /**
     * renames the file named by the source operand to the destination path named by the target operand
     *
     * @param srcFile  of path to source file
     * @param destFile of path to destination file
     * @throws Exception
     */
    public String  mvSrcFileToDestFile(String srcFile, String destFile) throws Exception{

    }

    /**
     * move files to destination folder
     *
     * @param destFolder of path to destination folder
     * @param fileName   Array of String of file names
     * @throws Exception
     */
    public String mvFilesToFolder(String destFolder, String... fileName) throws Exception{

    }
}