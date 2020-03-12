package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class IfStaNode extends StatementNode {

    private ExprStaNode condition;
    private StatementNode ifBlock;
    private StatementNode elseBlock;

    public ExprStaNode getCondition() {
        return condition;
    }

    public void setCondition(ExprStaNode condition) {
        condition.setParent(this);
        this.condition = condition;
    }

    public StatementNode getIfBlock() {
        return ifBlock;
    }

    public void setIfBlock(StatementNode ifBlock) {
        ifBlock.setParent(this);
        this.ifBlock = ifBlock;
    }

    public StatementNode getElseBlock() {
        return elseBlock;
    }

    public void setElseBlock(StatementNode elseBlock) {
        if(elseBlock != null)
            elseBlock.setParent(this);
        this.elseBlock = elseBlock;
    }

    @Override
    public void getInfo(int tab){
        super.getInfo(tab);
        condition.getInfo(tab + 1);
        ifBlock.getInfo(tab + 1);
        if (elseBlock != null) elseBlock.getInfo(tab + 1);
    }

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
