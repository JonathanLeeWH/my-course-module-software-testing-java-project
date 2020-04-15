package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.DiffException;
import sg.edu.nus.comp.cs4218.impl.app.args.DiffArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

// Skeleton Code for DiffApplication since we are not allocated EF1
public class DiffApplication implements DiffInterface { //NOPMD
    private static final String FILES = "Files ";
    private static final String DIFFER = " differ";
    private static final int BUFFER_SIZE = 4096;

    public void run(String[] args, InputStream stdin, OutputStream stdout) throws DiffException {
        if (stdin == null || stdout == null) {
            throw new DiffException(ERR_NULL_STREAMS);
        }
        DiffArguments diffArguments = new DiffArguments();
        String output = "";
        try {
            diffArguments.parse(args);
            List<String> files = diffArguments.getFiles();
            File file = new File(IOUtils.resolveFilePath(files.get(0)).toString());
            if (!file.exists()) {
                throw new DiffException(ERR_FILE_NOT_FOUND);
            }
            String fileFormat = files.get(0).substring(files.get(0).length()-3);
            if ("bin".equals(fileFormat) || "bmp".equals(fileFormat)) {
                output = compareBinaryFiles(files.get(0), files.get(1), diffArguments.isShowIdenticalMessage());
            } else if (!file.isDirectory() && !diffArguments.isStdin()) { //NOPMD
                if (files.size() > 2) {
                    throw new DiffException(ERR_MORE_THAN_TWO_FILES);
                }
                output = diffTwoFiles(files.get(0), files.get(1), diffArguments.isShowIdenticalMessage(),
                        diffArguments.isIgnoreBlankLines(), diffArguments.isDiffMessage());
            } else if (!file.isDirectory() && diffArguments.isStdin()) { // Stdin and Files
                output = diffFileAndStdin(files.get(0), stdin, diffArguments.isShowIdenticalMessage(),
                            diffArguments.isIgnoreBlankLines(), diffArguments.isDiffMessage());
            } else {
                output = diffTwoDir(files.get(0), files.get(1), diffArguments.isShowIdenticalMessage(),
                        diffArguments.isIgnoreBlankLines(), diffArguments.isDiffMessage());
            }
            if (!output.isEmpty()) {
                output = output.concat(STRING_NEWLINE);
            }
            stdout.write(output.getBytes());
        } catch (Exception e) {
            throw (DiffException) new DiffException(ERR_INVALID_ARGS).initCause(e);
        }
    }

    public String diffTwoFiles(String fileNameA, String fileNameB, Boolean isShowSame,
                               Boolean isNoBlank, Boolean isSimple) throws DiffException {
        if (fileNameA == null || fileNameB == null) {
            throw new DiffException(ERR_NULL_STREAMS);
        }
        IOUtils.resolveFilePath(fileNameA);
        File fileA = IOUtils.resolveFilePath(fileNameA).toFile();
        File fileB = IOUtils.resolveFilePath(fileNameB).toFile();
        if (!fileA.exists() || !fileB.exists()) {
            throw new DiffException(ERR_FILE_NOT_FOUND);
        }
        if (fileA.isDirectory()) {
            throw new DiffException(ERR_IS_DIR);
        }
        try {

            String difference = generateDiffOutput(fileA.toPath().toString(),
                    fileB.toPath().toString(), isNoBlank).trim();
            if (difference.length() == 0 && isShowSame) {
                    return FILES + fileA.getParentFile().getName() + "/" +  fileA.getName()
                            + CHAR_SPACE + fileB.getParentFile().getName() + "/" + fileB.getName() + " are identical";
            } else if (difference.length() > 0 && isSimple) {
                return FILES + fileA.getName() + CHAR_SPACE + fileB.getName() + DIFFER;
            }
            return difference;
        } catch (IOException e) {
            throw (DiffException) new DiffException(ERR_FILE_NOT_FOUND).initCause(e);
        }
    }

    public String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank,
                             Boolean isSimple) throws DiffException {
        if (folderA == null || folderB == null) {
            throw new DiffException(ERR_NULL_ARGS);
        }
        String output = "";
        File tempA = new File(folderA);
        File tempB = new File(folderB);
        if (!tempA.exists() || !tempB.exists()) {
            throw new DiffException(ERR_IS_NOT_DIR);
        }
        String commonSubDir = findCommonSubDirectories(folderA, folderB);
        File[] folderAFiles = new File(convertToAbsolutePath(folderA)).listFiles();
        File[] folderBFiles = new File(convertToAbsolutePath(folderB)).listFiles();
        String firstSet = getDiffFilesAndFolders(folderAFiles, folderBFiles, folderA);
        output = output.concat(firstSet);
        if (!firstSet.isEmpty()) {
            output = output.concat(STRING_NEWLINE);
        }
        String secondSet = getDiffFilesAndFolders(folderBFiles, folderAFiles, folderB);
                output = output.concat(secondSet);
        if (!secondSet.isEmpty()) {
            output = output.concat(STRING_NEWLINE);
        }
        try {
            String thirdSet = findSameFilesDiff(folderA, folderB, folderAFiles, folderBFiles, isShowSame,
                    isNoBlank, isSimple);
            output = output.concat(thirdSet);
            if (!thirdSet.isEmpty()) {
                output = output.concat(STRING_NEWLINE);
            }
            return output.concat(commonSubDir);
        } catch (IOException e) {
            throw (DiffException) new DiffException(ERR_READING_FILE).initCause(e);
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
            String difference = compareLists(fileLines, stdinContents, isNoBlank).trim();
            if (difference.length() == 0 && isShowSame) {
                return FILES + file.getName() + CHAR_SPACE + "-" + " are identical";
            } else if (difference.length() > 0 && isSimple) {
                return FILES + file.getName() + CHAR_SPACE + "-" + DIFFER;
            }
            return difference;
        } catch (Exception e) {
            throw (DiffException) new DiffException(ERR_NULL_STREAMS).initCause(e);
        }
    }

    private String generateDiffOutput(String pathA, String pathB, boolean isNoBlank) throws DiffException, IOException {
        try {
            List<String> fileALines = new ArrayList<>();
            List<String> fileBLines = new ArrayList<>();
            FileReader frA = new FileReader(pathA); //NOPMD
            FileReader frB = new FileReader(pathB); //NOPMD
            BufferedReader readerA = new BufferedReader(frA); //NOPMD
            BufferedReader readerB = new BufferedReader(frB); //NOPMD
            String tempA = readerA.readLine(), tempB = readerB.readLine();
            while (tempA != null) {
                fileALines.add(tempA);
                tempA = readerA.readLine();
            }
            while (tempB != null) {
                fileBLines.add(tempB);
                tempB = readerB.readLine();
            }
            boolean isListAEmpty = checkIfListIsEmpty(fileALines);
            boolean isListBEmpty = checkIfListIsEmpty(fileBLines);
            if (isListAEmpty || isListBEmpty) {
                return "";
            }
            frA.close();
            frB.close();
            readerA.close();
            readerB.close();
            return compareLists(fileALines, fileBLines, isNoBlank);
        } catch (FileNotFoundException e) {
            throw (DiffException) new DiffException(ERR_FILE_NOT_FOUND).initCause(e);
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
                if (isNoBlank) {
                    if (!StringUtils.isBlank(firstList.get(i))) {
                        if (isInput) {
                            firstDiff = firstDiff.concat("< ");
                        } else {
                            firstDiff = firstDiff.concat("> ");
                        }
                        firstDiff = firstDiff.concat(firstList.get(i));
                    }
                } else {
                    if (isInput) {
                        firstDiff = firstDiff.concat("< ");
                    } else {
                        firstDiff = firstDiff.concat("> ");
                    }
                    firstDiff = firstDiff.concat(firstList.get(i));
                }
            }
        }
        return firstDiff;
    }

    private String findCommonSubDirectories(String folderA, String folderB) {
        String identicalDir = "";
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
                if (identicalDir.length() == 0) {
                    identicalDir = output.concat(identicalDir);
                } else {
                    identicalDir = identicalDir.concat(STRING_NEWLINE);
                    identicalDir = identicalDir.concat(output);
                }
                File folderAFile = new File(convertToAbsolutePath(folderA));
                File folderBFile = new File(convertToAbsolutePath(folderB));
                identicalDir = identicalDir.concat(folderAFile.getName() + "/");
                identicalDir = identicalDir.concat(subDirectoryA.getName());
                identicalDir = identicalDir.concat(" and ");
                identicalDir = identicalDir.concat(folderBFile.getName() + "/");
                identicalDir = identicalDir.concat(subDirectoryA.getName());
            }
        }
        return identicalDir;
    }

    private List<String> readFileContentsIntoList(File file) throws DiffException {
        try {
            List<String> fileContents = new ArrayList<>();
            FileReader fr = new FileReader(file); //NOPMD
            BufferedReader bufferedReader = new BufferedReader(fr); //NOPMD
            String currentLine = bufferedReader.readLine();
            while (currentLine != null) {
                fileContents.add(currentLine);
                currentLine = bufferedReader.readLine();
            }
            fr.close();
            bufferedReader.close();
            return fileContents;
        } catch (Exception e) {
            throw (DiffException) new DiffException(ERR_FILE_NOT_FOUND).initCause(e);
        }
    }

    private String getDiffFilesAndFolders(File[] firstFiles, File[] secondFiles, String folderName) {
        String diffFilesFolders = "";
        File folderAsFile = new File (folderName);
        String onlyInFolder = "Only in " + folderAsFile.getName() + ": ";
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
                if (diffFilesFolders.length()!=0) {
                    diffFilesFolders = diffFilesFolders.concat(STRING_NEWLINE);
                }
                diffFilesFolders = diffFilesFolders.concat(onlyInFolder).concat(fileA.getName());
            }
        }
        return diffFilesFolders;
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
                    String fileDifference = diffTwoFiles(fileA.toPath().toString(), fileB.toPath().toString(), isShowSame, isNoBlank, isSimple);
                    if (!isShowSame && !isSimple && !StringUtils.isBlank(fileDifference)) {
                        output = output.concat("diff ");
                        output = output.concat(folderAFile.getName() + "/");
                        output = output.concat(fileA.getName()).concat(String.valueOf(CHAR_SPACE));
                        output = output.concat(folderBFile.getName() + "/");
                        output = output.concat(fileB.getName());
                        output = output.concat(STRING_NEWLINE);
                    }
                    if (isShowSame && StringUtils.isBlank(fileDifference)) {
                        fileDifference = FILES + folderAFile.getName() + "/" + fileA.getName() + CHAR_SPACE + folderBFile.getName() + "/" + fileB.getName() + " are identical";
                    } else if (isSimple && StringUtils.isBlank(fileDifference)) {
                        fileDifference = FILES + folderAFile.getName() + "/" + fileA.getName() + CHAR_SPACE + folderBFile.getName() + "/" + fileB.getName() + DIFFER;
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
    private boolean checkIfListIsEmpty(List<String> list) {
        boolean isEmpty = true;
        for (String str : list) {
            if (!str.trim().equals("")) {
                isEmpty = false;
                break;
            }
        }
        return isEmpty;
    }

    private String compareBinaryFiles(String firstBin, String secondBin, boolean isShowSame) throws Exception {
        File firstFile = new File(convertToAbsolutePath(firstBin));
        File secondFile = new File(convertToAbsolutePath(secondBin));
        boolean binSame = Arrays.equals(Files.readAllBytes(Paths.get(String.valueOf(firstFile))), Files.readAllBytes(Paths.get(String.valueOf(secondFile))));
        if (binSame && isShowSame) {
            return "Binary files " + firstFile.getParentFile().getName() + "/" + firstFile.getName() + CHAR_SPACE
                    + secondFile.getParentFile().getName() + "/" + secondFile.getName() + " are identical";
        } else if (binSame) {
            return "";
        } else {
            return "Binary files " + firstFile.getParentFile().getName() + "/" + firstFile.getName() + CHAR_SPACE
                    + secondFile.getParentFile().getName() + "/" + secondFile.getName() + DIFFER;
        }
    }
}