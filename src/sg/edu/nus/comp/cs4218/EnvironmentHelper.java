package sg.edu.nus.comp.cs4218;

public final class EnvironmentHelper {

    /**
     * Java VM does not support changing the current working directory.
     * For this reason, we use Environment.currentDirectory instead.
     */
    public static volatile String currentDirectory = System.getProperty("user.dir");


    private EnvironmentHelper() {
    }

}
