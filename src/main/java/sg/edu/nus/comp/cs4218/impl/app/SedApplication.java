package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.SedInterface;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.app.args.SedArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (fileName == null || regexp == null || regexp.isEmpty() || replacement == null || replacement.isEmpty()) {
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
        if (regexp == null || regexp.isEmpty() || replacement == null || replacement.isEmpty()) {
            throw new Exception(ERR_NULL_ARGS);
        }
        List<String> stdinContents = IOUtils.getLinesFromInputStream(stdin);
        String[] contentArray = new String[stdinContents.size()];
        for (int i = 0; i < stdinContents.size(); i++) {
            contentArray[i] = stdinContents.get(i);
        }
        return replacementHandler(contentArray, regexp, replacement, replacementIndex);
    }

    private String replacementHandler(String[] input, String regexp, String replacement, int replacementIndex) {
        Pattern pattern = Pattern.compile(regexp);
        StringBuilder output = new StringBuilder();
        for (String currentLine : input) { // Loop through the lines in the input, since sed replaces with the replacement index for every line
            boolean matched = false; // use this boolean flag to indicate that a match has been found at the replacement index
            Matcher matcher = pattern.matcher(currentLine);
            StringBuilder builder = new StringBuilder();
            int index = 1;
            while (matcher.find()) {
                if (index == replacementIndex) {
                    matched = true;
                    break;
                }
                index++;
            }
            if (matched) {
                builder.append(currentLine, 0, matcher.start()); //append everything from start of the line to the starting point that the match is found.
                builder.append(replacement); // then add in the replacement
                if (matcher.end() < currentLine.length()) {
                    builder.append(currentLine, matcher.end(), currentLine.length()); // add in those characters that are after the end of match.
                }
            } else {
                builder.append(currentLine);
            }
            output.append(builder.toString()).append(STRING_NEWLINE); // add a newline to separate every lines.
        }
        return output.toString().trim() + STRING_NEWLINE; //Trim away excess newlines at the end of file.
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
