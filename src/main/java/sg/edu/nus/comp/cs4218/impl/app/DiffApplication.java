package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.DiffException;
import sg.edu.nus.comp.cs4218.impl.app.args.DiffArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

// Skeleton Code for DiffApplication since we are not allocated EF1
public class DiffApplication implements DiffInterface {
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws DiffException {
        if (stdin == null || stdout == null) {
            throw new DiffException(ERR_NULL_STREAMS);
        }
        DiffArguments diffArguments = new DiffArguments();
        String output = "";
        try {
            diffArguments.parse(args);
            List<String> files = diffArguments.getFiles();
            File file = new File((files.get(0)));
            if (!file.isDirectory() && !diffArguments.isStdin()) { // Directories
                output = diffTwoFiles(files.get(0), files.get(1), diffArguments.isShowIdenticalMessage(),
                        diffArguments.isIgnoreBlankLines(), diffArguments.isDiffMessage()).concat(STRING_NEWLINE);
                stdout.write(output.getBytes());
            } else if (!file.isDirectory() && diffArguments.isStdin()) { // Stdin and Files
                output = diffFileAndStdin(files.get(0), stdin, diffArguments.isShowIdenticalMessage(),
                        diffArguments.isIgnoreBlankLines(), diffArguments.isDiffMessage()).concat(STRING_NEWLINE);
                stdout.write(output.getBytes());
            } else {
                output = diffTwoDir(files.get(0), files.get(1), diffArguments.isShowIdenticalMessage(),
                        diffArguments.isIgnoreBlankLines(), diffArguments.isDiffMessage()).concat(STRING_NEWLINE);
                stdout.write(output.getBytes());
            }
        } catch (Exception e) {
            throw (DiffException) new DiffException(ERR_NULL_ARGS).initCause(e);
        }
    }

    public String diffTwoFiles(String fileNameA, String fileNameB, Boolean isShowSame,
                               Boolean isNoBlank, Boolean isSimple) throws DiffException {
        String pathA = convertToAbsolutePath(fileNameA);
        String pathB = convertToAbsolutePath(fileNameB);
        File fileA = new File(pathA);
        File fileB = new File(pathB);
        if (!fileA.exists() || !fileB.exists()) {
            throw new DiffException(ERR_FILE_NOT_FOUND);
        }
        if (fileA.isDirectory()) {
            throw new DiffException(ERR_IS_DIR);
        }
        try {
            String difference = generateDiffOutput(fileNameA, fileNameB, isNoBlank).trim();
            if (difference.length() == 0 && isShowSame) {
                return "Files " + fileA.getName() + CHAR_SPACE + fileB.getName() + " are identical";
            } else if (difference.length() > 0 && isSimple) {
                return "Files " + fileA.getName() + CHAR_SPACE + fileB.getName() + " differ";
            }
            return difference;
        } catch (IOException e) {
            throw new DiffException(ERR_FILE_NOT_FOUND);
        }
    }

    public String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank,
                             Boolean isSimple) throws DiffException {
        String output = "";
        String commonSubDirectories = findCommonSubDirectories(folderA, folderB);
        File[] folderAFiles = new File(convertToAbsolutePath(folderA)).listFiles();
        File[] folderBFiles = new File(convertToAbsolutePath(folderB)).listFiles();
        assert folderAFiles != null;
        output = output.concat(getDiffFilesAndFolders(folderAFiles, folderBFiles, folderA));
        assert folderBFiles != null;
        output = output.concat(STRING_NEWLINE);
        output = output.concat(getDiffFilesAndFolders(folderBFiles, folderAFiles, folderB));
        output = output.concat(STRING_NEWLINE);
        try {
            output = output.concat(findSameFilesDiff(folderA, folderB, folderAFiles, folderBFiles, isShowSame,
                    isNoBlank, isSimple));
            return output.concat(STRING_NEWLINE).concat(commonSubDirectories);
        } catch (IOException e) {
            throw new DiffException(ERR_READING_FILE);
        }

    }

    public String diffFileAndStdin(String fileName, InputStream stdin, Boolean isShowSame,
                                   Boolean isNoBlank, Boolean isSimple) throws DiffException {
        try {
            List<String> stdinContents = IOUtils.getLinesFromInputStream(stdin);
            List<String> fileLines;
            String pathA = convertToAbsolutePath(fileName);
            File file = new File(pathA);
            fileLines = readFileContentsIntoList(file);
            String difference = compareLists(stdinContents, fileLines, isNoBlank).trim();
            if (difference.length() == 0 && isShowSame) {
                return "Files " + file.getName() + CHAR_SPACE + "-" + " are identical";
            } else if (difference.length() > 0 && isSimple) {
                return "Files " + file.getName() + CHAR_SPACE + "-" + " differ";
            }
            return difference;
        } catch (Exception e) {
            throw new DiffException(ERR_NULL_STREAMS);
        }
    }

    private String generateDiffOutput(String pathA, String pathB, boolean isNoBlank) throws DiffException, IOException {
        BufferedReader readerA;
        BufferedReader readerB;
        List<String> fileALines = new ArrayList<>();
        List<String> fileBLines = new ArrayList<>();
        try {
            readerA = new BufferedReader(new FileReader(pathA));
            readerB = new BufferedReader(new FileReader(pathB));
            String tempA = readerA.readLine(), tempB = readerB.readLine();
            while (tempA != null) {
                fileALines.add(tempA);
                tempA = readerA.readLine();
            }
            while (tempB != null) {
                fileBLines.add(tempB);
                tempB = readerB.readLine();
            }
            return compareLists(fileALines, fileBLines, isNoBlank);
        } catch (FileNotFoundException e) {
            throw new DiffException(ERR_FILE_NOT_FOUND);
        }
    }

    /**
     * Compare the file contents of two files.
     * Return the lines from fileA that are not in fileB, concatenated with the lines from fileB that are not in fileA..
     */
    private String compareLists(List<String> fileALines, List<String> fileBLines, boolean isNoBlank) {
        String fileADiff, fileBDiff;
        fileADiff = checkingForDiff(fileALines, fileBLines, isNoBlank, true);
        fileBDiff = checkingForDiff(fileBLines, fileALines, isNoBlank, false);
        return fileADiff.concat(STRING_NEWLINE).concat(fileBDiff);
    }

    /**
     * Checking the difference between the file contents of two files.
     * Return a String of lines in the firstList that do not appear in the secondList.
     */
    private String checkingForDiff(List<String> firstList, List<String> secondList, boolean isNoBlank, boolean isInput) {
        String firstDiff = "";
        boolean firstFound = false;
        for (int i = 0; i < firstList.size(); i++) {
            for (int j = 0; j < secondList.size(); j++) {
                if (firstList.get(i).equals(secondList.get(j))) {
                    firstFound = true;
                    break;
                }
            }
            if (firstFound) {
                firstFound = false;
            } else {
                if (firstDiff.length() != 0) {
                    firstDiff = firstDiff.concat(STRING_NEWLINE);
                }
                if (isInput) {
                    firstDiff = firstDiff.concat("< ");
                } else {
                    firstDiff = firstDiff.concat("> ");
                }
                if (isNoBlank) {
                    if (firstList.get(i).trim().isEmpty()) {
                        firstDiff = firstDiff.concat(firstList.get(i));
                    }
                } else {
                    firstDiff = firstDiff.concat(firstList.get(i));
                }

            }
        }
        return firstDiff;
    }

    private String findCommonSubDirectories(String folderA, String folderB) {
        String identicalDirectories = "";
        File[] directoriesA = new File(convertToAbsolutePath(folderA)).listFiles(File::isDirectory);
        File[] directoriesB = new File(convertToAbsolutePath(folderB)).listFiles(File::isDirectory);
        assert directoriesA != null;
        String output = "Common subdirectories: ";
        for (File subDirectoryA : directoriesA) {
            boolean isSame = false;
            assert directoriesB != null;
            for (File subDirectoryB : directoriesB) {
                if (subDirectoryA.getName().equals(subDirectoryB.getName())) {
                    isSame = true;
                    break;
                }
            }
            if (isSame) {
                if (identicalDirectories.length() != 0) {
                    identicalDirectories = identicalDirectories.concat(STRING_NEWLINE);
                    identicalDirectories = identicalDirectories.concat(output);
                } else {
                    identicalDirectories = output.concat(identicalDirectories);
                }
                identicalDirectories = identicalDirectories.concat(folderA + "/");
                identicalDirectories = identicalDirectories.concat(subDirectoryA.getName());
                identicalDirectories = identicalDirectories.concat(" and ");
                identicalDirectories = identicalDirectories.concat(folderB + "/");
                identicalDirectories = identicalDirectories.concat(subDirectoryA.getName());
            }
        }
        return identicalDirectories;
    }

    private List<String> readFileContentsIntoList(File file) throws DiffException {
        try {
            List<String> fileContents = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String currentLine = bufferedReader.readLine();
            while (currentLine != null) {
                fileContents.add(currentLine);
                currentLine = bufferedReader.readLine();
            }
            return fileContents;
        } catch (Exception e) {
            throw new DiffException(ERR_FILE_NOT_FOUND);
        }
    }

    private String getDiffFilesAndFolders(File[] firstFiles, File[] secondFiles, String folderName) {
        String diffFilesAndFolders = "";
        String onlyInFolder = "Only in " + folderName + ": ";
        assert firstFiles != null;
        for (File fileA : firstFiles) {
            boolean sameFilesFolders = false;
            assert secondFiles != null;
            for (File fileB : secondFiles) {
                if (fileA.getName().equals(fileB.getName())) {
                    sameFilesFolders = true;
                    break;
                }
            }
            if (!sameFilesFolders) {
                if (diffFilesAndFolders.length()!=0) {
                    diffFilesAndFolders = diffFilesAndFolders.concat(STRING_NEWLINE);
                }
                diffFilesAndFolders = diffFilesAndFolders.concat(onlyInFolder).concat(fileA.getName());
            }
        }
        return diffFilesAndFolders;
    }

    private String findSameFilesDiff(String folderA, String folderB, File[] folderAFiles, File[] folderBFiles, Boolean isShowSame,
                                 Boolean isNoBlank, Boolean isSimple) throws DiffException, IOException {
        String output = "";
        File folderAFile = new File(folderA);
        File folderBFile = new File(folderB);
        for (File fileA : folderAFiles) {
            for (File fileB : folderBFiles) {
                if (fileA.getName().equals(fileB.getName()) && !fileA.isDirectory()) {
                    if (output.length() != 0) {
                        output = output.concat(STRING_NEWLINE);
                    }
                    output = output.concat("diff ");
                    output = output.concat(folderAFile.getName() + "/");
                    output = output.concat(fileA.getName()).concat(String.valueOf(CHAR_SPACE));
                    output = output.concat(folderBFile.getName() + "/");
                    output = output.concat(fileB.getName());
                    output = output.concat(STRING_NEWLINE);
                    String fileDifference = diffTwoFiles(fileA.toPath().toString(), fileB.toPath().toString(), isShowSame, isNoBlank, isSimple);
                    if (isShowSame && fileDifference.trim().length() == 0) {
                        fileDifference = "Files " + folderAFile.getName() + "/" + fileA.getName() + CHAR_SPACE + "-" + " are identical";
                    } else if (isSimple && fileDifference.trim().length() > 0) {
                        fileDifference = "Files " + folderAFile.getName() + "/" + fileA.getName() + CHAR_SPACE + folderBFile.getName() + "/" + fileB.getName() + " differ";
                    }
                    output = output.concat(fileDifference);
                    break;
                }
            }
        }
        return output;
    }
    /**
     * Converts filename to absolute path, if initially was relative path
     *
     * @param fileName supplied by user
     * @return a String of the absolute path of the filename
     */
    private String convertToAbsolutePath(String fileName) {
        String home = System.getProperty("user.home").trim();
        String currentDir = EnvironmentHelper.currentDirectory.trim();
        String convertedPath = convertPathToSystemPath(fileName);

        String newPath;
        if (convertedPath.length() >= home.length() && convertedPath.substring(0, home.length()).trim().equals(home)) {
            newPath = convertedPath;
        } else {
            newPath = currentDir + CHAR_FILE_SEP + convertedPath;
        }
        return newPath;
    }

    /**
     * Converts path provided by user into path recognised by the system
     *
     * @param path supplied by user
     * @return a String of the converted path
     */
    private String convertPathToSystemPath(String path) {
        String convertedPath = path;
        String pathIdentifier = "\\" + Character.toString(CHAR_FILE_SEP);
        convertedPath = convertedPath.replaceAll("(\\\\)+", pathIdentifier);
        convertedPath = convertedPath.replaceAll("/+", pathIdentifier);

        if (convertedPath.length() != 0 && convertedPath.charAt(convertedPath.length() - 1) == CHAR_FILE_SEP) {
            convertedPath = convertedPath.substring(0, convertedPath.length() - 1);
        }

        return convertedPath;
    }
}