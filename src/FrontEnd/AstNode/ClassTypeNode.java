package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import static FrontEnd.AstNode.PrimitiveTypeNode.PrimitiveTypeKeyword.NULL;

public class ClassTypeNode extends VariableTypeNode {

    private String referenceClassName;
    private ClassDefNode referenceClass;

    public String getReferenceClassName() {
        return referenceClassName;
    }

    public void setReferenceClassName(String referenceClassName) {
        this.referenceClassName = referenceClassName;
    }

    public ClassDefNode getReferenceClass() {
        return referenceClass;
    }

    public void setReferenceClass(ClassDefNode referenceClass) {
        this.referenceClass = referenceClass;
    }



    public ClassTypeNode(String str) {
        referenceClassName = str;
    }
    @Override
    public String getType() {
        return referenceClassName;
    }

    @Override
    public boolean equalTo(VariableTypeNode node) {
        if(!referenceClassName.equals("string") && node.equalPrimitive(NULL)) return true;
        if (!(node instanceof ClassTypeNode)) return false;
        return referenceClassName.equals(((ClassTypeNode)node).referenceClassName);
    }

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
