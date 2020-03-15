package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class IndexAccessNode extends ExprStaNode {
    private ExprStaNode caller;//it is the name of array
    private ExprStaNode index;// it is the index of array

    public ExprStaNode getCaller() {
        return caller;
    }

    public void setCaller(ExprStaNode caller) {
//        caller.setParent(this);
        this.caller = caller;
    }

    public ExprStaNode getIndex() {
        return index;
    }

    public void setIndex(ExprStaNode index) {
        if(index!= null)
            index.setParent(this);
        this.index = index;
    }

    @Override
    public void getInfo(int tab) {
        super.getInfo(tab);
        caller.getInfo(tab + 1);
        index.getInfo(tab + 1);
    }

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
