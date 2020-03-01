package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

// Skeleton Code for DiffApplication since we are not allocated EF1
public class DiffApplication {
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws DiffException {
        if (stdin == null || stdout == null) {
            throw new DiffException(ERR_NULL_STREAMS);
        }
    }

    public String diffTwoFiles(String fileNameA, String fileNameB, Boolean isShowSame,
                               Boolean isNoBlank, Boolean isSimple) throws Exception {
        String noDiffOutput = "Files are identical";
        String diffOutput = "The two files are different";
        if (fileNameA.equals(fileNameB) || isFileContentsEqual(fileNameA, fileNameB)) {
            return  noDiffOutput;
        } else {
            return diffOutput;
        }
    }

    public String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank,
                             Boolean isSimple) throws Exception {
        return "";
    }
    public String diffFileAndStdin(String fileName, InputStream stdin, Boolean isShowSame,
                                   Boolean isNoBlank, Boolean isSimple) throws Exception {
        return "";
    }

    private boolean isFileContentsEqual (String fileNameA, String fileNameB) {
        return true;
    }

}

