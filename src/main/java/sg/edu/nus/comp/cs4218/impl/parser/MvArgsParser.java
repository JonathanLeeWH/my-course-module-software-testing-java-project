package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class MvArgsParser extends ArgsParser {
        private final static char FLAG_NOTOVERWRITE = 'n';

        public MvArgsParser() {
            super();
            legalFlags.add(FLAG_NOTOVERWRITE);
        }

        public Boolean isNotOverWrite() {
            return flags.contains(FLAG_NOTOVERWRITE);
        }

    public List<String> getNonFlagArgs() {
        return nonFlagArgs;
    }

}
