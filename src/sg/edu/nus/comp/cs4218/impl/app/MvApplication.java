package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.parser.MvArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class MvApplication implements MvInterface {

    private static final String NO_ARG_EXCEPTION ="No input found, please specify file to be moved";
    public static final String NO_DESTINATION = "No destination specified";
    public static final String FAILED_TO_MOVE = "Failed to move file";
    public static final String NO_FILE = "No file specified found";

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws MvException {

        if ( args == null || args.length == 0) {
            throw new MvException(NO_ARG_EXCEPTION);
        }

        if (stdout == null) {
            throw new MvException(ERR_NO_OSTREAM);
        }
        // Parse arguments.
        MvArgsParser parser = new MvArgsParser();
        StringBuilder output = new StringBuilder();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw (MvException) new MvException(e.getMessage()).initCause(e);
        }
        List<String> sourceDestination = parser.getNonFlagArgs();
        String source = sourceDestination.get(0);
        String dest = File.separator + sourceDestination.get(1);
        String currentDir = EnvironmentHelper.currentDirectory.trim();

        StringBuilder stringbuilder = new StringBuilder(currentDir);
        stringbuilder.append(dest);
        String checkDest = stringbuilder.toString();

        File file = new File(checkDest);
        if(file.isDirectory()){
            try{
                String complete = mvFilesToFolder(checkDest,source);
            } catch(Exception e){
                throw (MvException) new MvException(e.getMessage()).initCause(e);
            }

        }
        else{
            try{
                String complete = mvSrcFileToDestFile(source,checkDest);
            } catch(Exception e){
                throw (MvException) new MvException(e.getMessage()).initCause(e);
            }
    }


    }
    /**
     * renames the file named by the source operand to the destination path named by the target operand
     *
     * @param srcFile  of path to source file
     * @param destFile of path to destination file
     * @throws Exception
     */
    public String  mvSrcFileToDestFile(String srcFile, String destFile) throws Exception {
        boolean fileMoved = true;

        File file = IOUtils.resolveFilePath(srcFile).toFile();

        try{
            Files.move(Paths.get(srcFile), Paths.get(destFile), REPLACE_EXISTING);
        } catch (Exception e) {
            fileMoved = false;
            throw (MvException) new MvException(FAILED_TO_MOVE).initCause(e);
        }

        if(file.exists()) {
            // Abstract file path (does not exist)
            File destination = new File (destFile);

            // rename the source file
            file.renameTo(destination);
        }
        String returnString;

        if(fileMoved){

            returnString = "Files replaced";
        }
        else{
            returnString = "Files not replaced :";
        }

        return returnString;
    }

    /**
     * move files to destination folder
     *
     * @param destFolder of path to destination folder
     * @param fileName   Array of String of file names
     * @throws Exception
     */
    public String mvFilesToFolder(String destFolder, String... fileName) throws Exception{

        boolean fileMoved = true;
        for(String sourceFile : fileName) {
            File file = IOUtils.resolveFilePath(sourceFile).toFile();
            String currentSourcFileName = file.getName();
            String dest = destFolder + File.separator + currentSourcFileName;
            try {
                Files.move(Paths.get(sourceFile), Paths.get(dest), REPLACE_EXISTING);

            } catch (Exception e) {

                fileMoved = false;
                throw (MvException) new MvException(FAILED_TO_MOVE).initCause(e);
            }
        }
        String returnString;

        if(fileMoved){
            returnString = "Files moved to :" + destFolder;
        }
        else{
            returnString = "Files not moved to :"+ destFolder;
        }

        return returnString;
    }
}