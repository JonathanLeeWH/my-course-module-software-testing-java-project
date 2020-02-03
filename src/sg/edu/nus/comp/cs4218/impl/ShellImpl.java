package sg.edu.nus.comp.cs4218.impl;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ShellImpl implements Shell {

    /**
     * Main method for the Shell Interpreter program.
     *
     * @param args List of strings arguments, unused.
     */
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            Shell shell = new ShellImpl();

            String currentDirectory = EnvironmentHelper.currentDirectory;
            System.out.print(currentDirectory + ">");
            String commandString;

            commandString = reader.readLine();

            if (!StringUtils.isBlank(commandString)) {
                shell.parseAndEvaluate(commandString, System.out);
            }

        } catch (IOException e) {
            // Streams are auto closed using try with resources.
            // Terminate process with non zero exit code.
            /**
             * TODO: Need to check if the system exit code is correct.
             */
            System.err.println(e.getMessage());
            System.exit(1); // Streams are closed, terminate process
        } catch (Exception e) {
            /**
             * TODO: Might or might not need to change this to return non zero exit code and more specific exceptions.
             */
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void parseAndEvaluate(String commandString, OutputStream stdout)
            throws AbstractApplicationException, ShellException {
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
    }
}
