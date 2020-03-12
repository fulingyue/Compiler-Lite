package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

public class VariableTypeNode extends StatementNode{

    public String getType() {
        if (this instanceof ClassTypeNode)
            return ((ClassTypeNode) this).getType();
        else if (this instanceof ArrayTypeNode)
            return ((ArrayTypeNode)this).getType();
        else
            return ((PrimitiveTypeNode)this).getType();

    }


    public boolean isPrimitive() {
        if (this instanceof PrimitiveTypeNode) return true;
        else  return false;
    }

    public boolean equalPrimitive(PrimitiveTypeNode.PrimitiveTypeKeyword typeKeyword) {
        if (!(this instanceof PrimitiveTypeNode)) return false;
        String str = ((PrimitiveTypeNode)this).getType();
        String key = typeKeyword.toString();
        if (str.equals(key)) return true;
        else return false;
    }

    public boolean equalTo(VariableTypeNode node) {
        if(this instanceof PrimitiveTypeNode) return (((PrimitiveTypeNode)node).equalTo(node));
        else if(this instanceof ClassTypeNode) return (((ClassTypeNode)node).equalTo(node));
        else return ((ArrayTypeNode)node).equalTo(node);
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }

}
