package util;

import java.io.PrintStream;

public class ErrorHandler {
    private int error_cnt;
//    private PrintStream printStream;

    public ErrorHandler() {
        error_cnt = 0;
    }
    public void error(Location location, String message) {
        error_cnt++;
        System.out.println(location.getLine() + "\t" + message);
    }

    public void printError() {

    }

    public int getError_cnt() {
        return error_cnt;
    }

    public void setError_cnt(int error_cnt) {
        this.error_cnt = error_cnt;
    }

}
