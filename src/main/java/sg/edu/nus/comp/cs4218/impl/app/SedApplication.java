package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.SedInterface;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.app.args.SedArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class SedApplication implements SedInterface {

    /**
     * Runs the sed application with the specified arguments.
     *
     * @param args   Array of arguments for the application.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws SedException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws SedException {
        // Format: sed REPLACEMENT [FILE]
        if (args == null) {
            throw new SedException(ERR_NULL_ARGS);
        }
        if (args.length == 0) {
            throw new SedException(ERR_NO_ARGS);
        }
        if (stdout == null) {
            throw new SedException(ERR_NULL_STREAMS);
        }
        SedArguments sedArgs = new SedArguments();
        try {
            sedArgs.parse(args);
            SedArguments.validate(sedArgs.getRegex(), sedArgs.getReplacement(), sedArgs.getReplacementIndex());
        } catch (Exception e) {
            throw new SedException(e.getMessage());//NOPMD
        }
        StringBuilder output = new StringBuilder();
        try {
            if (sedArgs.getFiles().isEmpty()) {
                output.append(replaceSubstringInStdin(sedArgs.getRegex(), sedArgs.getReplacement(), sedArgs.getReplacementIndex(), stdin));
            } else {
                for (String file : sedArgs.getFiles()) {
                    output.append(replaceSubstringInFile(sedArgs.getRegex(), sedArgs.getReplacement(), sedArgs.getReplacementIndex(), file));
                }
            }
        } catch (Exception e) {
            throw new SedException(e.getMessage());//NOPMD
        }
        try {
            stdout.write(output.toString().getBytes());
        } catch (IOException e) {
            throw new SedException(ERR_WRITE_STREAM);//NOPMD
        }
    }

    /**
     * Returns string of the file content with the matched substring on each line replaced. For each
     * line, find the substring that matched the pattern and replace the substring in the specified
     * index of the matched substring list.
     *
     * @param regexp           String specifying a regular expression in JAVA format
     * @param replacement      String to replace the matched pattern
     * @param replacementIndex Integer specifying the index of the matched substring to be replaced
     *                         (default is 1)
     * @param fileName         String specifying name of the file
     * @throws Exception
     */
    @Override
    public String replaceSubstringInFile(String regexp, String replacement, int replacementIndex,
                                         String fileName) throws Exception {
        if (fileName == null) {
            throw new Exception(ERR_NULL_ARGS);
        }
        File node = IOUtils.resolveFilePath(fileName).toFile();
        if (!node.exists()) {
            throw new Exception(ERR_FILE_NOT_FOUND);
        }
        if (node.isDirectory()) {
            throw new Exception(ERR_IS_DIR);
        }
        if (!node.canRead()) {
            throw new Exception(ERR_NO_PERM);
        }
        String[] fileContents = getFileContents(node);

        return replacementHandler(fileContents, regexp, replacement, replacementIndex);
    }

    /**
     * Returns string of the Stdin arg content with the matched substring on each line replaced. For
     * each line, find the substring that matched the pattern and replace the substring in the
     * specified index of the matched substring list.
     *
     * @param regexp           String specifying a regular expression in JAVA format
     * @param replacement      String to replace the matched pattern
     * @param replacementIndex Integer specifying the index of the matched substring to be replaced
     *                         (default is 1)
     * @param stdin            InputStream containing arguments from Stdin
     * @throws Exception
     */
    @Override
    public String replaceSubstringInStdin(String regexp, String replacement, int replacementIndex,
                                          InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new Exception(ERR_NULL_STREAMS);
        }
        List<String> stdinContents = IOUtils.getLinesFromInputStream(stdin);
        String[] contentArray = new String[stdinContents.size()];
        for (int i = 0; i < stdinContents.size(); i++) {
            contentArray[i] = stdinContents.get(i);
        }

        return replacementHandler(contentArray, regexp, replacement, replacementIndex);
    }

    private String replacementHandler(String[] input, String regexp, String replacement, int replacementIndex) {
        StringBuilder output = new StringBuilder();
        for (String line : input) {
            StringBuilder builder = new StringBuilder();
            if (replacementIndex == 1) {
                builder.append(line.replaceFirst(regexp, replacement));
                output.append(builder.toString()).append(STRING_NEWLINE);
            } else if (replacementIndex <= input.length){
                builder.append(replaceString(line, regexp, replacement, replacementIndex));
                output.append(builder.toString()).append(STRING_NEWLINE);
            }
            if (regexp.isEmpty() || replacementIndex > input.length) {
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < input.length; i++) {
                    if (i != 0) {
                        stringBuilder.append(STRING_NEWLINE);
                    }
                    stringBuilder.append(input[i]);
                }
                String returnString = stringBuilder.toString();
                return returnString + STRING_NEWLINE;
            }
        }
        return output.toString();
    }
    private String replaceString(String line, String regexp, String replacement, int replacementIndex) {
        int index = 0;
        String space = " ";
        String[] words = line.split("\\s+", 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains(regexp)) {
                index++;
                if(index == replacementIndex) {
                    words[i] = words[i].replace(regexp, replacement);
                }
            }
            stringBuilder.append(words[i]);
            if (i!= words.length-1) {
                stringBuilder.append(space);
            }
        }
        return stringBuilder.toString();
    }

    private String[] getFileContents(File file) throws SedException {
        try(FileReader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            List<String> fileContentList = new ArrayList<>();
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                fileContentList.add(currentLine);
            }
            String[] result = new String[fileContentList.size()];
            for (int j = 0; j < fileContentList.size(); j++) {
                result[j] = fileContentList.get(j);
            }
            return result;
        } catch (IOException e) {
            throw (SedException) new SedException(ERR_READING_FILE).initCause(e);
        }
    }
}
