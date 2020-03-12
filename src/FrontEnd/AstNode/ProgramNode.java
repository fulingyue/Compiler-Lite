package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import java.util.*;

public class ProgramNode extends AstNode {
    private List<ClassDefNode> classDefList;
    private List<FunctionDefNode> functionDefList;
    private List<VarDefNode> varDefList;
    private List<AstNode> childenList;

    public void addClass(ClassDefNode node) {
        classDefList.add(node);
        childenList.add(node);
    }
    public void addFunc(FunctionDefNode node) {
        functionDefList.add(node);
        childenList.add(node);
    }

    public void addVar(VarDefNode node) {
        varDefList.add(node);
        childenList.add(node);
    }


    public List<ClassDefNode> getClassDefList() {
        return classDefList;
    }

    public void setClassDefList(List<ClassDefNode> classDefList) {
        this.classDefList = classDefList;
    }

    public List<FunctionDefNode> getFunctionDefList() {
        return functionDefList;
    }

    public void setFunctionDefList(List<FunctionDefNode> functionDefList) {
        this.functionDefList = functionDefList;
    }

    public List<VarDefNode> getVarDefList() {
        return varDefList;
    }

    public void setVarDefList(List<VarDefNode> varDefList) {
        this.varDefList = varDefList;
    }

    public List<AstNode> getChildenList() {
        return childenList;
    }

    public void setChildenList(List<AstNode> childenList) {
        this.childenList = childenList;
    }

    public ProgramNode() {
        classDefList = new LinkedList<ClassDefNode>();
        functionDefList = new LinkedList<FunctionDefNode>();
        varDefList = new LinkedList<VarDefNode>();
        childenList = new LinkedList<AstNode>();
    }

    @Override
    public void getInfo (int tab) {
        super.getInfo(tab);
        for (AstNode item: childenList) {
            item.getInfo(tab + 1);
        }
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }

}
