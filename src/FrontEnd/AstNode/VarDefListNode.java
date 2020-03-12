package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import java.util.LinkedList;
import java.util.List;

import static util.print_tool.printSpaceAndStr;

public class VarDefListNode extends StatementNode{
    private List<VarDefNode> varDefNodeList;
    private VariableTypeNode type;

    public List<VarDefNode> getVarDefNodeList() {
        return varDefNodeList;
    }

    public void setVarDefNodeList(List<VarDefNode> varDefNodeList) {
        this.varDefNodeList = varDefNodeList;
    }

    public VariableTypeNode getType() {
        return type;
    }

    public void setType(VariableTypeNode type) {
        type.setParent(this);
        this.type = type;
    }

    public VarDefListNode() {
        varDefNodeList = new LinkedList<>();
        type = null;
    }
    public void add(VarDefNode node){
        node.setParent(this);
        varDefNodeList.add(node);
    }

    @Override public void getInfo(int tab) {
        super.getInfo(tab);
        printSpaceAndStr(tab,"variableList" + type.getType());
        if (type != null) type.getInfo(tab + 1);
        for(VarDefNode item: varDefNodeList)
            item.getInfo(tab + 1);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
