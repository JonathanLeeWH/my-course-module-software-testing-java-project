package sg.edu.nus.comp.cs4218.impl;

import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD.CloseResource")
public final class FileIOHelper {

    private  FileIOHelper() {}

    public static void createFileFolder(String filePath, boolean isDirectory) throws IOException {
        File dir = new File(filePath);
        if (isDirectory) {
            dir.mkdirs();
        } else {
            dir.createNewFile();
        }
    }

    public static void deleteTestFiles(String...filesToClose) {
        for (String fileName : filesToClose) {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
                file.deleteOnExit();
            }
        }
    }

    public static void deleteFiles(String...filesToClose) {
        for (String fileName : filesToClose) {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
                file.deleteOnExit();
            }
        }
    }

    public static String extractAndConcatenate(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        StringBuilder result = new StringBuilder();
        String currentLine = reader.readLine();
        while(currentLine != null) {
            result.append(currentLine);
            result.append(StringUtils.STRING_NEWLINE);
            currentLine = reader.readLine();
        }
        reader.close();

        if (result.length() == 0) {
            return "";
        } else {
            return result.toString().trim();
        }

    }

    public static String[] extractAsStringArray(String... files) throws IOException {
        List<String> result = new ArrayList<>();
        for (String fileName : files) {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String currentLine = reader.readLine();
            while (currentLine != null) {
                result.add(currentLine);
                currentLine = reader.readLine();
            }
            reader.close();
        }
        return result.toArray(new String[0]);
    }
}
