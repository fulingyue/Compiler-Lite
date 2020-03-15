package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class WhileStaNode extends StatementNode {
    private ExprStaNode condition;
    private StatementNode block;

    public ExprStaNode getCondition() {
        return condition;
    }

    public void setCondition(ExprStaNode condition) {
//        condition.setParent(this);
        this.condition = condition;
    }

    public StatementNode getBlock() {
        return block;
    }

    public void setBlock(StatementNode block) {
//        block.setParent(this);
        this.block = block;
    }

    @Override
    public void  getInfo(int tab) {
        super.getInfo(tab);
        condition.getInfo(tab  + 1);
        block.getInfo(tab + 1);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}

