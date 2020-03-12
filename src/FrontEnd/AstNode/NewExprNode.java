package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import java.util.*;

public class NewExprNode extends ExprStaNode{
    private VariableTypeNode variableType;
    private LinkedList<ExprStaNode> actualParameterList;

    public VariableTypeNode getVariableType() {
        return variableType;
    }

    public void setVariableType(VariableTypeNode variableType) {
        variableType.setParent(this);
        this.variableType = variableType;
    }

    public LinkedList<ExprStaNode> getActualParameterList() {
        return actualParameterList;
    }

    public void setActualParameterList(LinkedList<ExprStaNode> actualParameterList) {
        this.actualParameterList = actualParameterList;
    }

    public NewExprNode() {
        actualParameterList = new LinkedList<ExprStaNode>();
    }

    @Override public void getInfo(int tab) {
        super.getInfo(tab);
        if (variableType != null)
            variableType.getInfo(tab + 1);
        for (ExprStaNode item : actualParameterList)
            item.getInfo(tab + 1);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
