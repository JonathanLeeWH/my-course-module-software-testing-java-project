package sg.edu.nus.comp.cs4218.impl.app;

import javafx.util.Pair;
import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.CutArgsParser;

import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CutApplication implements CutInterface {
    @Override
    public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, String... fileName) throws Exception {
        return null;
    }

    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, InputStream stdin) throws Exception {
        return null;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        if (args == null) {
            throw new CutException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new CutException(ERR_NO_OSTREAM);
        }

        // Parse arguments.
        CutArgsParser parser = new CutArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw (CutException) new CutException(e.getMessage()).initCause(e);
        }

        Boolean isCharPos = parser.isCharPos();
        Boolean isBytePos = parser.isBytePos();
        Boolean isRange = parser.isRange();
        Pair<Integer, Integer> position = parser.getPositions();
        String[] files = parser.getFileNames();

        try {
            //cutFromFiles(isCharPos, isBytePos, isRange, position.getKey(), position.getValue(), files);
            //cutFromStdin(isCharPos, isBytePos, isRange, position.getKey(), position.getValue(), stdin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
