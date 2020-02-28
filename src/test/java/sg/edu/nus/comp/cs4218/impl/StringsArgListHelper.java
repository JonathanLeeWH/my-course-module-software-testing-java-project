package sg.edu.nus.comp.cs4218.impl;

import java.util.ArrayList;
import java.util.List;

public class StringsArgListHelper {

    private StringsArgListHelper() {}

    public static List<String> concantenateStringsToList(String...toAppend) {
        List<String> result = new ArrayList<>();
        for(String args : toAppend) {
            result.add(args);
        }
        return result;
    }
}
