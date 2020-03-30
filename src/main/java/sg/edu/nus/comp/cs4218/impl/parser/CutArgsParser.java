package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.util.MyPair;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CutArgsParser extends ArgsParser{

    public static final char FLAG_CHAR_POS = 'c';
    public static final char FLAG_BYTE_POS = 'b';
    public static final String LIST_COMMA_OPTION = ",";
    public static final String LIST_RANGE_OPTION = "-";
    private final static int INDEX_LIST = 0;
    private final static int INDEX_FILES = 1;

    public CutArgsParser() {
        super();
        legalFlags.add(FLAG_CHAR_POS);
        legalFlags.add(FLAG_BYTE_POS);
    }

    public Boolean isCharPos() {
        return flags.contains(FLAG_CHAR_POS);
    }

    public Boolean isBytePos() {
        return flags.contains(FLAG_BYTE_POS);
    }

    public Boolean isRange() throws Exception {
        if (nonFlagArgs.isEmpty()) {
            throw new Exception(ERR_MISSING_ARG);
        }
        return nonFlagArgs.get(INDEX_LIST).contains("-");
    }

    /**
     *
     * @return A pair of integers with the start and end position
     */
    public MyPair<Integer, Integer> getPositions() throws Exception {
        if (nonFlagArgs.isEmpty()) {
            throw new Exception(ERR_MISSING_ARG);
        }
        String list = nonFlagArgs.get(INDEX_LIST);
        int startPos = 0;
        int endPos;

        if (list.contains(LIST_COMMA_OPTION)) {
            int commaPos = list.indexOf(LIST_COMMA_OPTION);
            String startIndex = list.substring(startPos, commaPos);
            String endIndex = list.substring(commaPos + 1);
            if ((startIndex.contains(LIST_RANGE_OPTION)) || (endIndex.contains(LIST_RANGE_OPTION))) {
                throw new Exception(ERR_INVALID_ARGS);
            }

            startPos = Integer.parseInt(startIndex);
            endPos = Integer.parseInt(endIndex);
        }
        else if (list.contains(LIST_RANGE_OPTION)) {
            int dashPos = list.indexOf(LIST_RANGE_OPTION);
            String startIndex = list.substring(startPos, dashPos);
            String endIndex = list.substring(dashPos + 1);
            if ((startIndex.contains(LIST_COMMA_OPTION)) || (endIndex.contains(LIST_COMMA_OPTION))) {
                throw new Exception(ERR_INVALID_ARGS);
            }

            startPos = Integer.parseInt(list.substring(startPos, dashPos));
            endPos = Integer.parseInt(list.substring(dashPos + 1));
        }
        else {
            startPos = Integer.parseInt(list);
            endPos = startPos;
        }
        return new MyPair<>(startPos, endPos);
    }

    public String[] getFileNames() {
        return nonFlagArgs.size() <= 1 ? null : nonFlagArgs.subList(INDEX_FILES, nonFlagArgs.size())
                .toArray(new String[0]);
    }
}
