package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public abstract class StatementNode extends AstNode{
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
