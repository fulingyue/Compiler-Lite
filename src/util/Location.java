package util;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Location {
    private int line;
    private int column;

    public Location() {
        line = 0;
        column = 0;
    }

    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public Location (Token token) {
        this.line = token.getLine();
        this.column = token.getCharPositionInLine();
    }

    public Location (ParserRuleContext ctx) {
        this(ctx.start);
    }

    public void setLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }
    public void setLocation(Token token) {
        this.line = token.getLine();
        this.column = token.getCharPositionInLine();
    }
    public void setLocation(ParserRuleContext ctx) {
        setLocation(ctx.start);
    }
    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
