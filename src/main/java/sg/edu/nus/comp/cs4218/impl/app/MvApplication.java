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
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class MvApplication implements MvInterface {

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

        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw (MvException) new MvException(e.getMessage()).initCause(e);
        }
        List<String> sourceDestination = parser.getNonFlagArgs();
        if(sourceDestination.isEmpty() || sourceDestination.size() ==1) {
            throw new MvException(MISSING_ARG_EXCEPTION);
        }

        String dest = sourceDestination.get(sourceDestination.size()-1);

        List<String> sources = new ArrayList<String>();
        for(int i = 0 ; i < sourceDestination.size()-1 ; i++){

            String sourceName = sourceDestination.get(i);
            if(!sourceName.equals(dest)) {
                sources.add(sourceName);
            }
            else{
                File file = new File(dest);
                File sourceFile = new File(sourceName);

                if(file.getPath().toString().equals(sourceFile.getPath().toString())) {
                    throw new MvException(SRC_DEST_SAME);
                }

            }
        }

        if(sources.size() > 1) {
            File destFile = new File(dest);
            if(destFile.exists()) {
                if(!destFile.isDirectory()) {
                    throw new MvException(DESTINATION_FOLDER_NOT);
                }
            }
            else{
                throw new MvException(DESTINATION_FOLDER_NOT);
            }
        }
        for(String source: sources) {

            String currentDir = EnvironmentHelper.currentDirectory.trim();

            StringBuilder stringbuilder = new StringBuilder(currentDir);
            stringbuilder.append(dest);
            String checkDest = dest;

            File file = new File(checkDest);
            File sourceFile = new File(source);

            if(parser.isNotOverWrite()) {
                noOverwriteProcess(sourceFile.getName(),checkDest, file);
            }
            else{
                overWriteProcess(source, checkDest, file);
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
        File destinationFile = IOUtils.resolveFilePath(destFile).toFile();

        if(!file.exists()) {
            fileMoved = false;
            throw (MvException) new MvException(NO_FILE);
        }

        if(destFile.isEmpty()) {
            fileMoved = false;
            throw (MvException) new MvException(NO_DESTINATION);
        }
        Files.move(Paths.get(srcFile), Paths.get(destFile), REPLACE_EXISTING);


        if(file.exists()) {
            // Abstract file path (does not exist)
            File destination = new File (destFile);

            // rename the source file
            file.renameTo(destination);
        }
        String returnString = "file not renamed";

        if(fileMoved){
            returnString = "Files replaced";
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
        File destinationFile = IOUtils.resolveFilePath(destFolder).toFile();
        if(!destinationFile.exists()) {
            throw (MvException) new MvException(NO_DESTINATION_FOLDER);
        }
        for(String sourceFile : fileName) {
            File file = IOUtils.resolveFilePath(sourceFile).toFile();
            String currentSrcName = file.getName();
            String dest = destinationFile.getPath().toString() + File.separator + currentSrcName;
            try {
                Files.move(Paths.get(sourceFile), Paths.get(dest), REPLACE_EXISTING);

            } catch (Exception e) {
                fileMoved = false;
                throw (MvException) new MvException(FAILED_TO_MOVE).initCause(e);
            }
        }
        String returnString = "File not moved";

        if(fileMoved){
            returnString = "Files moved to :" + destFolder;
        }
        return returnString;
    }

    private void mvFileToDestMethod(String source, String checkDest) throws MvException {
        try{
            String complete = mvSrcFileToDestFile(source,checkDest);
        } catch(Exception e){
            throw (MvException) new MvException(e.getMessage()).initCause(e);
        }
    }

    private void mvFilesForFolderMethod(String source, String checkDest) throws MvException {
        try{
            String complete = mvFilesToFolder(checkDest,source);
        } catch(Exception e){
            throw (MvException) new MvException(e.getMessage()).initCause(e);
        }
    }

    private void overWriteProcess(String source, String checkDest, File file) throws MvException {
        if(file.isDirectory()){
            mvFilesForFolderMethod(source, checkDest);
        }
        else{
            mvFileToDestMethod(source, checkDest);
        }
    }

    private void noOverwriteProcess(String source, String checkDest, File file) throws MvException {
        if(file.isDirectory()) {
            String fileInDir = checkDest + File.separator + source;
            File fileCheck = new File(fileInDir);
            if(fileCheck.exists()) {
                throw (MvException) new MvException(NO_OVERWRITE);
            }
            else{
                mvFilesForFolderMethod(source, checkDest);
            }
        }
        else{
            File fileCheck = new File(checkDest);
            if(fileCheck.exists()){
                throw (MvException) new MvException(NO_OVERWRITE);
            }
            else{
                mvFileToDestMethod(source, checkDest);
            }
        }
    }
}