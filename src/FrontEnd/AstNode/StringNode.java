package FrontEnd.AstNode;


import FrontEnd.AstVisitor;

public class StringNode extends PrimaryExprNode {
    private String value;

    public StringNode() {
        value = null;
    }
    public StringNode(String str) {
        value = str;
    }
    public void setVal(String str) {
        value = util.print_tool.whiteSpace(str);
    }
    public String getVal() {
        return value;
    }
    @Override
    public void getInfo (int tab) {
//        super.getInfo(tab);
        util.print_tool.printSpaceAndStr(tab,"STRING" + value);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
