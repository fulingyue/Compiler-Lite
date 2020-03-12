package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class ContinueStaNode extends StatementNode {
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}

