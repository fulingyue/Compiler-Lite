package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class BinaryOpNode extends ExprStaNode {
    public enum BinaryOp {
        MUL, DIV, MOD, ADD, SUB,
        LSHIFT, RSHIFT,
        LE, GE, LT, GT,
        EQUAL, NOTEQUAL,
        AND, XOR, OR, LAND, LOR,
        ASSIGN
    }

    private BinaryOp op;
    private ExprStaNode lhs;
    private ExprStaNode rhs;

    public BinaryOp getOp() {
        return op;
    }

    public void setOp(BinaryOp op) {
        this.op = op;
    }

    public ExprStaNode getLhs() {
        return lhs;
    }

    public void setLhs(ExprStaNode lhs) {
        lhs.setParent(this);
        this.lhs = lhs;
    }

    public ExprStaNode getRhs() {
        return rhs;
    }

    public void setRhs(ExprStaNode rhs) {
        rhs.setParent(this);
        this.rhs = rhs;
    }



    @Override public void getInfo(int tab) {
        super.getInfo(tab);
        lhs.getInfo(tab + 1);
        rhs.getInfo(tab + 1);
    }

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
