package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class ThisExprNode extends PrimaryExprNode {

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
