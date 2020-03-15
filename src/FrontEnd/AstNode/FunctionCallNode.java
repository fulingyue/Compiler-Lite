package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import java.util.*;

public class FunctionCallNode extends ExprStaNode {
    private ReferenceNode caller;
    private List<ExprStaNode> actualParameterList;

    public ReferenceNode getCaller() {
        return caller;
    }

    public void setCaller(ReferenceNode caller) {
//        if( caller != null)
//            caller.setParent(this);
        this.caller = caller;
    }

    public List<ExprStaNode> getActualParameterList() {
        return actualParameterList;
    }

    public void addParameter(ExprStaNode node) {
//        node.setParent(this);
        actualParameterList.add(node);
    }

    public void setActualParameterList(List<ExprStaNode> actualParameterList) {
        this.actualParameterList = actualParameterList;
    }

    public FunctionCallNode(){
        actualParameterList = new LinkedList<ExprStaNode>();
    }
    @Override
    public void getInfo(int tab){
        super.getInfo(tab);
        caller.getInfo(tab + 1);
        for (ExprStaNode item: actualParameterList){
            item.getInfo(tab + 1);
        }

    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
