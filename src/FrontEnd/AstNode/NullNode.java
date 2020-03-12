package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class NullNode extends PrimaryExprNode {
    @Override
    public void getInfo (int tab) {
        super.getInfo(tab);
        util.print_tool.printSpaceAndStr(tab,"null");
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }

}
