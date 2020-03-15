package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import static util.print_tool.*;
public class VarDefNode extends StatementNode {
    private VariableTypeNode varType;
    private String varName;
    private ExprStaNode initVal;

    public VariableTypeNode getVarType() {
        return varType;
    }

    public void setVarType(VariableTypeNode varType) {
//        varType.setParent(this);
        this.varType = varType;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public ExprStaNode getInitVal() {
        return initVal;
    }

    public void setInitVal(ExprStaNode initVal) {
//        if(initVal != null)
//            initVal.setParent(this);
        this.initVal = initVal;
    }

    public VarDefNode() {
        this.varType = null;
        this.varName = null;
        initVal = null;
    }
    public VarDefNode(VariableTypeNode type, String varName) {
        this.varName = varName;
        varType = type;
    }

    @Override public void getInfo(int tab) {
        super.getInfo(tab);
        printSpaceAndStr(tab,"variableName:" + varName);
        if (varType != null) varType.getInfo(tab + 1);
        if(initVal != null) initVal.getInfo(tab + 1);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }

}
