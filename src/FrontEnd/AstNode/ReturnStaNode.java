package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class ReturnStaNode extends StatementNode {
    private ExprStaNode returnVal;

    public ExprStaNode getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(ExprStaNode returnVal) {
        if(returnVal != null)
            returnVal.setParent(this);
        this.returnVal = returnVal;
    }

    @Override
    public void getInfo(int tab) {
        super.getInfo(tab);
        if(returnVal != null) {
            returnVal.getInfo(tab + 1);
        }
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
