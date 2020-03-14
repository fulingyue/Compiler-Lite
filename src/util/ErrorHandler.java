package util;

import java.io.PrintStream;

public class ErrorHandler {
    private int error_cnt;
    private PrintStream printStream;

    public ErrorHandler() {
        error_cnt = 0;
        printStream=  System.err;
    }
    public void error(Location location, String message) {
        error_cnt++;
        printStream.println(location+ "\t" + message);
    }

    public int getError_cnt() {
        return error_cnt;
    }

    public void setError_cnt(int error_cnt) {
        this.error_cnt = error_cnt;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }
}
