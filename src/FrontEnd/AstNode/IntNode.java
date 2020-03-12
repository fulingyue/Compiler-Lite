package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class IntNode extends PrimaryExprNode {
    private int value;

    public IntNode() {
        value = 0;
    }
    public void setVal(String str) {
        this.value = Integer.parseInt(str);
    }
    public int getValue() {
        return value;
    }
    @Override
    public void getInfo (int tab) {
//        super.getInfo(tab);
        util.print_tool.printSpaceAndStr(tab,"INT" + value);
    }

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
