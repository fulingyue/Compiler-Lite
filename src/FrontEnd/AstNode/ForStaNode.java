package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class ForStaNode extends StatementNode {
    private ExprStaNode init;
    private ExprStaNode condition;
    private ExprStaNode recursionCond;
    private StatementNode block;

    public ExprStaNode getInit() {
        return init;
    }

    public void setInit(ExprStaNode init) {
//        if(init != null)
//            init.setParent(this);
        this.init = init;
    }

    public ExprStaNode getCondition() {
        return condition;
    }

    public void setCondition(ExprStaNode condition) {
//        if(condition!=null)
//            condition.setParent(this);
        this.condition = condition;
    }

    public ExprStaNode getRecursionCond() {
        return recursionCond;
    }

    public void setRecursionCond(ExprStaNode recursionCond) {
//        if (recursionCond!= null)
//            recursionCond.setParent(this);
        this.recursionCond = recursionCond;
    }

    public StatementNode getBlock() {
        return block;
    }

    public void setBlock(StatementNode block) {
//        if(block!= null)
//            block.setParent(this);
        this.block = block;
    }


    @Override
    public void getInfo(int tab) {
        super.getInfo(tab);
        if (init != null) init.getInfo(tab + 1);
        if (condition != null) condition.getInfo(tab + 1);
        if (recursionCond != null) recursionCond.getInfo(tab + 1);
        block.getInfo(tab + 1);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
