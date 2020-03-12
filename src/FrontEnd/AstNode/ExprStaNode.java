package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import static util.print_tool.*;

public abstract class ExprStaNode extends StatementNode {
    protected VariableTypeNode exprType;
    private boolean left_val;

    public VariableTypeNode getExprType() {
        return exprType;
    }

    public void setExprType(VariableTypeNode exprType) {
        exprType.setParent(this);
        this.exprType = exprType;
    }

    public boolean isLeft_val() {
        return left_val;
    }

    public void setLeft_val(boolean left_val) {
        this.left_val = left_val;
    }

    public ExprStaNode(){
        exprType = null;
        left_val = false;
    }

    @Override public void getInfo(int tab){
        super.getInfo(tab);
        if(exprType != null) exprType.getInfo(tab+1);
        if(left_val == true) printSpaceAndStr(tab,"left_val:" + left_val);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }

}
