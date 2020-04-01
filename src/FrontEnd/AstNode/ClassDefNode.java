package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import static util.print_tool.*;

import java.util.*;

public class ClassDefNode extends AstNode {
    private String className;
    private List<VarDefListNode> memberList;
    private List<FunctionDefNode> functionDefList;
    private List<FunctionDefNode> constructionDefList;

    public void addMember(VarDefListNode node) {
//        node.setParent(this);
        memberList.add(node);
    }

    public void addFunction(FunctionDefNode node) {
//        node.setParent(this);
        functionDefList.add(node);
    }
    public void addConstructor(FunctionDefNode node) {
//        node.setParent(this);
        constructionDefList.add(node);
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<VarDefListNode> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<VarDefListNode> memberList) {
        this.memberList = memberList;
    }

    public List<FunctionDefNode> getFunctionDefList() {
        return functionDefList;
    }

    public void setFunctionDefList(List<FunctionDefNode> functionDefList) {
        this.functionDefList = functionDefList;
    }

    public List<FunctionDefNode> getConstructionDefList() {
        return constructionDefList;
    }

    public void setConstructionDefList(List<FunctionDefNode> constructionDefList) {
        this.constructionDefList = constructionDefList;
    }


    public ClassDefNode() {
        memberList = new LinkedList<VarDefListNode>();
        functionDefList = new LinkedList<FunctionDefNode>();
        constructionDefList = new LinkedList<FunctionDefNode>();
    }

    @Override
    public void getInfo(int tab) {
        super.getInfo(tab);
        printSpaceAndStr(tab, "name:" + className);
        for (VarDefListNode item: memberList) {
            item.getInfo(tab + 1);
        }
        for (FunctionDefNode item: functionDefList) {
            item.getInfo(tab + 1);
        }
        for (FunctionDefNode item: constructionDefList) {
            item.getInfo(tab + 1);
        }
    }

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
