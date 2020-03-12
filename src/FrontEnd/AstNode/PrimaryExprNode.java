package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class PrimaryExprNode extends ExprStaNode{
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
