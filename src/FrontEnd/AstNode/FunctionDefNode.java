package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import static util.print_tool.*;
import java.util.*;

public class FunctionDefNode extends AstNode {
    private VariableTypeNode returnType;
    private String methodName;
    private List<VarDefNode> formalParameterList;
    private BlockNode block;

    public boolean hasReturnSta() {
        return ReturnSta;
    }

    public void setReturnSta(boolean returnSta) {
        ReturnSta = returnSta;
    }

    private boolean ReturnSta;

    public void addParameter(VarDefNode node) {
        node.setParent(this);
        formalParameterList.add(node);
    }
    public VariableTypeNode getReturnType() {
        return returnType;
    }

    public void setReturnType(VariableTypeNode returnType) {
        returnType.setParent(this);
        this.returnType = returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<VarDefNode> getFormalParameterList() {
        return formalParameterList;
    }

    public void setFormalParameterList(List<VarDefNode> formalParameterList) {
        this.formalParameterList = formalParameterList;
    }

    public BlockNode getBlock() {
        return block;
    }

    public void setBlock(BlockNode block) {
        block.setParent(this);
        this.block = block;
    }

    public FunctionDefNode() {
        formalParameterList = new LinkedList<VarDefNode>();
    }

    @Override
    public void getInfo(int tab) {
        super.getInfo(tab);
        printSpaceAndStr(tab, "name:" + methodName);
        if (returnType != null)
//            System.out.print("tttttype!");
            returnType.getInfo(tab + 1);

        for (VarDefNode item: formalParameterList) {
            item.getInfo(tab + 1);
        }
        block.getInfo(tab + 1);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
