package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class BreakStaNode extends StatementNode{
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
