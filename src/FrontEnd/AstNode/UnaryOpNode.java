package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class UnaryOpNode extends ExprStaNode {
    public enum UnaryOp{
        PREFIX_INC, PREFIX_DEX, NEGATE,NOT, LNOT,POSTFIX_INC,POSTFIX_DEC
    }
    private UnaryOp op;
    private ExprStaNode innerExpr;

    public UnaryOp getOp() {
        return op;
    }

    public void setOp(UnaryOp op) {
        this.op = op;
    }

    public ExprStaNode getInnerExpr() {
        return innerExpr;
    }

    public void setInnerExpr(ExprStaNode innerExpr) {
        innerExpr.setParent(this);
        this.innerExpr = innerExpr;
    }

    @Override public void getInfo(int tab) {
        super.getInfo(tab);
        innerExpr.getInfo(tab + 1);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
