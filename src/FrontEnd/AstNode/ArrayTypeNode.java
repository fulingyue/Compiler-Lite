package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import static FrontEnd.AstNode.PrimitiveTypeNode.PrimitiveTypeKeyword.NULL;

public class ArrayTypeNode extends VariableTypeNode{ //an array definition
    private VariableTypeNode innerTypeNode;
    private ExprStaNode size;
    private VariableTypeNode baseType;
    private int dim;
    public VariableTypeNode getInnerTypeNode() {
        return innerTypeNode;
    }

    public void setInnerTypeNode(VariableTypeNode innerTypeNode) {
        this.innerTypeNode = innerTypeNode;
    }

    public ExprStaNode getSize() {
        return size;
    }

    public void setSize(ExprStaNode size) {
        this.size = size;
    }


    public ArrayTypeNode(VariableTypeNode inner) {
        innerTypeNode = inner;
        size = null;
    }

    public ArrayTypeNode(VariableTypeNode inner, int dim) {
        VariableTypeNode tmp;
        tmp = inner;
        for (int i = dim - 1; i >= 1; --i) {
            tmp = new ArrayTypeNode(tmp);
        }
        innerTypeNode = tmp;
        size = null;
    }


    @Override
    public String getType(){
        if(size == null) return innerTypeNode.getType() + "[]";
        else return innerTypeNode.getType() + "[" + size.toString() + "]";
    }

    public boolean equalType(ArrayTypeNode node) {
        if(size != null && node.size == null) return false;
        if(innerTypeNode instanceof ArrayTypeNode && node.innerTypeNode instanceof ArrayTypeNode)
            return ((ArrayTypeNode) innerTypeNode).equalType((ArrayTypeNode) node.innerTypeNode);
        else return innerTypeNode.getType().equals(node.innerTypeNode.getType());
    }

    @Override public boolean equalTo(VariableTypeNode node) {
        if (node.equalPrimitive((NULL))) return true;
        if(!(node instanceof ArrayTypeNode)) return false;
        return innerTypeNode.equalTo(((ArrayTypeNode) node).innerTypeNode);
    }

    public VariableTypeNode getBaseType() {
        return baseType;
    }

    public void setBaseType(VariableTypeNode baseType) {
        this.baseType = baseType;
    }

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }
}
