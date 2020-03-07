package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.app.CdInterface;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CdApplication implements CdInterface {


    /**
     * Change the environment context to a different directory.
     *
     * @param path String of the path to a directory
     * @throws CdException
     */
    @Override
    public void changeToDirectory(String path) throws CdException {
        EnvironmentHelper.currentDirectory = getNormalizedAbsolutePath(path);
    }

    /**
     * Runs the cd application with the specified arguments.
     * Assumption: The application must take in one arg. (cd without args is not supported)
     * The application also does not support taking in more than one argument.
     * @param args   Array of arguments for the application.
     * @param stdin  An InputStream, not used.
     * @param stdout An OutputStream, not used.
     * @throws CdException If the input arguments is null, missing arguments or too many arguments.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws CdException {
        if (args == null) {
            throw new CdException(ERR_NULL_ARGS);
        }

        if (stdin == null || stdout == null) {
            throw new CdException(ERR_NULL_STREAMS);
        }

        if (args.length == 0) {
            throw new CdException(ERR_MISSING_ARG);
        } else if (args.length == 1) {
            changeToDirectory(args[0]);
        } else {
            throw new CdException(ERR_TOO_MANY_ARGS);
        }

    }

    /**
     * Returns an absolute path in String data type with redundant elements removed from the input pathStr, in other words,
     * returns a normalised absolute path.
     * @param pathStr The input path in String data type.
     * @return Returns a normalised absolute path of the input path.
     * @throws CdException If there are no arguments, file not found in the path, or the input path is not a directory.
     */
    private String getNormalizedAbsolutePath(String pathStr) throws CdException {
        if (StringUtils.isBlank(pathStr)) {
            throw new CdException(ERR_NO_ARGS);
        }

        Path path = new File(pathStr).toPath();
        if (!path.isAbsolute()) {
            path = Paths.get(EnvironmentHelper.currentDirectory, pathStr);
        }

        if (!Files.exists(path)) {
            throw new CdException(String.format(ERR_FILE_NOT_FOUND, pathStr));
        }

        if (!Files.isDirectory(path)) {
            throw new CdException(String.format(ERR_IS_NOT_DIR, pathStr));
        }

        return path.normalize().toString();
    }
}
