package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class EmptyStaNode extends ExprStaNode {

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
