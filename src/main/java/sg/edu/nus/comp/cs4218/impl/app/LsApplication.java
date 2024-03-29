package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;
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

@SuppressWarnings({"PMD.GodClass","PMD.ExcessiveMethodLength"})
public class LsApplication implements LsInterface {

    private final static String PATH_CURR_DIR = STRING_CURR_DIR + CHAR_FILE_SEP;

    @Override
    public String listFolderContent(Boolean isFoldersOnly, Boolean isRecursive,
                                    String... folderName) throws LsException {

        if(folderName.length == 0 && isFoldersOnly){
            return ".";
        }
        if (folderName.length == 0 && !isRecursive) {
            return listCwdContent(isFoldersOnly);
        }

        List<Path> paths;
        List<String> folderNames = new ArrayList<>();
        for(String folder: folderName) {
            folderNames.add(folder);
        }

        if (folderName.length == 0 && isRecursive) {
            String[] directories = new String[1];
            directories[0] = EnvironmentHelper.currentDirectory;
            paths = resolvePaths(directories);
            return buildResultForBaseDir(paths, isFoldersOnly, isRecursive);
        } else {
            paths = resolvePaths(folderName);
            if(paths.size() == 1 && !isRecursive) {
                Path path = paths.get(0);
                File currFile = new File(path.toString());

                if(currFile.isDirectory() && !isFoldersOnly){
                    EnvironmentHelper.currentDirectory = paths.get(0).toString();
                    return listCwdContent(isFoldersOnly);
                }
            }
        }

        return buildResult(paths,folderNames, isFoldersOnly, isRecursive);
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws LsException {
        if (args == null) {
            throw new LsException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new LsException(ERR_NO_OSTREAM);
        }

        LsArgsParser parser = new LsArgsParser();
        String currentDirectory = EnvironmentHelper.currentDirectory;
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw (LsException) new LsException(e.getMessage()).initCause(e);
        }

        Boolean foldersOnly = parser.isFoldersOnly();
        Boolean recursive = parser.isRecursive();
        String[] directories = parser.getDirectories()
                .toArray(new String[parser.getDirectories().size()]);
        String result = listFolderContent(foldersOnly, recursive, directories);

        try {
            stdout.write(result.getBytes());
            if(!result.isEmpty()) {
                stdout.write(StringUtils.STRING_NEWLINE.getBytes());
            }
        } catch (Exception e) {
            throw (LsException) new LsException(ERR_WRITE_STREAM).initCause(e);
        }
        EnvironmentHelper.currentDirectory = currentDirectory;
    }

    /**
     * Lists only the current directory's content and RETURNS. This does not account for recursive
     * mode in cwd.
     *
     * @param isFoldersOnly
     * @return
     */
    private String listCwdContent(Boolean isFoldersOnly) throws LsException {
        String cwd = EnvironmentHelper.currentDirectory;
        try {
            return formatContents(getContents(Paths.get(cwd), isFoldersOnly));
        } catch (InvalidDirectoryException e) {
            throw (LsException) new LsException("Unexpected error occurred!").initCause(e);
        }
    }

    /**
     * Builds the resulting string to be written into the output stream.
     * <p>
     * NOTE: This is recursively called if user wants recursive mode.
     *
     * @param paths         - list of java.nio.Path objects to list
     * @param isFoldersOnly - only list the folder contents
     * @param isRecursive   - recursive mode, repeatedly ls the child directories
     * @return String to be written to output stream.
     */
    private String buildResult(List<Path> paths, List<String> folderNames, Boolean isFoldersOnly, Boolean isRecursive) throws LsException {
        StringBuilder result = new StringBuilder();
        File previousFile = new File("");
        boolean lastFileInFolder = false;
        for (Path path : paths) {
            try {
                File file = new File(path.toString());
                if(file.isDirectory() && !isFoldersOnly) {
                    if(!previousFile.getPath().equals("") && !previousFile.isDirectory() && !lastFileInFolder && !isRecursive){
                        result.append(System.lineSeparator());
                        lastFileInFolder = true;
                    }
                    List<Path> contents = getContents(path, isFoldersOnly);
                    String formatted = formatContents(contents);
                    buildRelativePath(isRecursive, result, path);
                    result.append(':').append(System.lineSeparator());
                    result.append(formatted);

                    if (!formatted.isEmpty()) {
                        // Empty directories should not have an additional new line
                        result.append(StringUtils.STRING_NEWLINE);
                    }
                    result.append(StringUtils.STRING_NEWLINE);

                    // RECURSE!
                    if (isRecursive) {
                        List<Path> contentRe = new ArrayList<Path>();
                        buildRecurse(isFoldersOnly,  isRecursive, result, contents, contentRe ,folderNames);
                        result.append(System.lineSeparator());
                    }

                }
                else if (file.isDirectory() && isFoldersOnly) {
                    String toPrintName="";
                    boolean useFolderName = false;
                    for(String folder : folderNames) {
                        if(folder.contains(file.getName())) {
                            toPrintName = folder;
                            useFolderName = true;
                        }
                    }
                    if(useFolderName) {
                        result.append(toPrintName);
                        result.append(System.lineSeparator());
                    }
                    else{
                        result.append(file.getName());
                        result.append(System.lineSeparator());
                    }

                }
                else{
                    if(!file.exists()) {
                        throw (LsException) new LsException(NO_FILE_OR_FOLDER);
                    }
                    previousFile = file;
                    result.append(file.getName());
                    result.append(System.lineSeparator());
                }

            } catch (InvalidDirectoryException e) {
                // NOTE: This is pretty hackish IMO - we should find a way to change this
                // If the user is in recursive mode, and if we resolve a file that isn't a directory
                // we should not spew the error message.
                //
                // However the user might have written a command like `ls invalid1 valid1 -R`, what
                // do we do then?
                if (!isRecursive) {
                    result.append(e.getMessage());
                    result.append(System.lineSeparator());
                }
            }
        }

        return result.toString().trim();
    }

    private String buildResultForBaseDir(List<Path> paths, Boolean isFoldersOnly, Boolean isRecursive) {
        StringBuilder result = new StringBuilder();
        for (Path path : paths) {
            try {
                File file = new File(path.toString());
                if(file.isDirectory() && !isFoldersOnly) {
                    List<Path> contents = getContents(path, isFoldersOnly);
                    String formatted = formatContents(contents);
                    if(!path.toString().equals(EnvironmentHelper.currentDirectory)){
                        buildRelativePathForBaseDir(isRecursive, result, path);
                        result.append(':').append(System.lineSeparator());
                    }
                    result.append(formatted);

                    if (!formatted.isEmpty()) {
                        // Empty directories should not have an additional new line
                        result.append(StringUtils.STRING_NEWLINE);
                    }
                    result.append(StringUtils.STRING_NEWLINE);

                    // RECURSE!
                    if (isRecursive) {
                        List<Path> contentRe = new ArrayList<Path>();
                        buildRecurseForNoArgs(isFoldersOnly, isRecursive, result, contents, contentRe);
                        result.append(System.lineSeparator());
                    }

                }
                else if (file.isDirectory() && isFoldersOnly) {
                    result.append(file.getName());
                    result.append(System.lineSeparator());
                }
                else{
                    result.append(file.getName());
                    result.append(System.lineSeparator());
                }

            } catch (InvalidDirectoryException e) {
                // NOTE: This is pretty hackish IMO - we should find a way to change this
                // If the user is in recursive mode, and if we resolve a file that isn't a directory
                // we should not spew the error message.
                //
                // However the user might have written a command like `ls invalid1 valid1 -R`, what
                // do we do then?
                if (!isRecursive) {
                    result.append(e.getMessage());
                    result.append(System.lineSeparator() );
                }
            }
        }

        return result.toString().trim();
    }

    private void buildRelativePath(Boolean isRecursive, StringBuilder result, Path path) {
        String relativePath = getRelativeToCwd(path).toString();
        if(isRecursive) {
            relativePath = System.lineSeparator() +relativePath;
        }
        result.append(StringUtils.isBlank(relativePath) ? PATH_CURR_DIR :relativePath);
    }

    private void buildRelativePathForBaseDir(Boolean isRecursive, StringBuilder result, Path path) {
        String relativePath = getRelativeToCwd(path).toString();
        relativePath = System.lineSeparator() + PATH_CURR_DIR +relativePath;
        result.append(StringUtils.isBlank(relativePath) ? PATH_CURR_DIR : relativePath);
    }

    private void buildRecurse(Boolean isFoldersOnly, Boolean isRecursive, StringBuilder result, List<Path> contents, List<Path> contentRe , List<String> folderNames) throws LsException {
        for(Path content : contents) {
            File fileCheck = new File(content.toString());
            if(fileCheck.isDirectory()) {
                contentRe.add(content);
            }
        }
        result.append(buildResult(contentRe,folderNames, isFoldersOnly, isRecursive));
    }

    private void buildRecurseForNoArgs(Boolean isFoldersOnly, Boolean isRecursive, StringBuilder result, List<Path> contents, List<Path> contentRe) {
        for(Path content : contents) {
            File fileCheck = new File(content.toString());
            if(fileCheck.isDirectory()) {
                contentRe.add(content);
            }
        }
        result.append(buildResultForBaseDir(contentRe, isFoldersOnly, isRecursive));
    }

    /**
     * Formats the contents of a directory into a single string.
     *
     * @param contents - list of items in a directory
     * @return
     */
    private String formatContents(List<Path> contents) {
        List<String> fileNames = new ArrayList<>();
        for (Path path : contents) {
            fileNames.add(path.getFileName().toString());
        }

        StringBuilder result = new StringBuilder();
        for (String fileName : fileNames) {
            result.append(fileName);
            result.append(System.lineSeparator());
        }

        return result.toString().trim();
    }

    /**
     * Gets the contents in a single specified directory.
     *
     * @param directory
     * @return List of files + directories in the passed directory.
     */
    private List<Path> getContents(Path directory, Boolean isFoldersOnly)
            throws InvalidDirectoryException {
        if (!Files.exists(directory)) {
            throw new InvalidDirectoryException(getRelativeToCwd(directory).toString());
        }

        if (!Files.isDirectory(directory)) {
            throw new InvalidDirectoryException(getRelativeToCwd(directory).toString());
        }

        List<Path> result = new ArrayList<>();
        File pwd = directory.toFile();
        for (File f : pwd.listFiles()) {
            if (isFoldersOnly && !f.isDirectory()) {
                continue;
            }

            if (!f.isHidden()) {
                result.add(f.toPath());
            }
        }

        Collections.sort(result);

        return result;
    }

    /**
     * Resolve all paths given as arguments into a list of Path objects for easy path management.
     *
     * @param directories
     * @return List of java.nio.Path objects
     */
    private List<Path> resolvePaths(String... directories) {
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < directories.length; i++) {
            paths.add(resolvePath(directories[i]));
        }

        return paths;
    }

    /**
     * Converts a String into a java.nio.Path objects. Also resolves if the current path provided
     * is an absolute path.
     *
     * @param directory
     * @return
     */
    private Path resolvePath(String directory) {
        File file = new File(directory);
        if (file.isAbsolute()) {
            // This is an absolute path
            return Paths.get(directory).normalize();
        }

        return Paths.get(EnvironmentHelper.currentDirectory, directory).normalize();
    }

    /**
     * Converts a path to a relative path to the current directory.
     *
     * @param path
     * @return
     */
    private Path getRelativeToCwd(Path path) {
        return Paths.get(EnvironmentHelper.currentDirectory).relativize(path);
    }

    private class InvalidDirectoryException extends Exception {
        InvalidDirectoryException(String directory) {
            super(String.format("ls: cannot access '%s': No such file or directory", directory));
        }

        InvalidDirectoryException(String directory, Throwable cause) {
            super(String.format("ls: cannot access '%s': No such file or directory", directory),
                    cause);
        }
    }
}
