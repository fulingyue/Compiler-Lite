package FrontEnd.ErrorChecker;

import util.Location;

public class SemanticException extends Error {
    public SemanticException(Location location, String str) {
        super("[" + Integer.toString(location.getLine()) + "," + Integer.toString(location.getColumn()) + "]"  + str);
    }

    public SemanticException(String str) {
        super(str);
    }
}
