package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class MemberAccessNode extends ExprStaNode {
    private ExprStaNode caller;
    private ExprStaNode member;
    private ClassDefNode classDefNode;//ClassDef or array_

    public ExprStaNode getCaller() {
        return caller;
    }

    public void setCaller(ExprStaNode caller) {
//        caller.setParent(this);
        this.caller = caller;
    }

    public ExprStaNode getMember() {
        return member;
    }

    public void setMember(ExprStaNode member) {
//        member.setParent(this);
        this.member = member;
    }

    @Override public void getInfo(int tab) {
        super.getInfo(tab);
        caller.getInfo(tab + 1);
        member.getInfo(tab + 1);
    }

    public ClassDefNode getClassDefNode() {
        return classDefNode;
    }

    public void setClassDefNode(ClassDefNode classDefNode) {
        this.classDefNode = classDefNode;
    }

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }

}
