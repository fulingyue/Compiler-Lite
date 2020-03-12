package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class BoolNode extends PrimaryExprNode {
    private boolean value;

    public BoolNode() {
        value = false;
    }

    public void setVal(String str) {
        switch (str) {
            case "true" :
                this.value = true;
                break;
            case "false":
                this.value = false;
                break;
        }
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void getInfo (int tab) {
//        super.getInfo(tab);
        util.print_tool.printSpaceAndStr(tab,"bool:" + value);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
