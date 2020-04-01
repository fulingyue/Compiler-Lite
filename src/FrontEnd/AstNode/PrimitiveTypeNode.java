package FrontEnd.AstNode;
import FrontEnd.AstVisitor;
import util.print_tool;

public class PrimitiveTypeNode extends VariableTypeNode { //it is the lowest level structure of lexer
    public enum PrimitiveTypeKeyword {
        BOOL,INT,VOID,NULL //,STRING
    }

    private PrimitiveTypeKeyword type;

    public void setType(PrimitiveTypeKeyword type) {
        this.type = type;
    }
    public PrimitiveTypeKeyword getKeyword() {
        return this.type;
    }
    @Override
    public void getInfo(int tab) {
        super.getInfo(tab);
        print_tool.printSpaceAndStr(tab,"type: " + type.toString()) ;
    }

    public PrimitiveTypeNode(String str) {
        if(str.equals("bool")) this.type = PrimitiveTypeKeyword.BOOL;
        if(str.equals("int")) this.type = PrimitiveTypeKeyword.INT;
        if(str.equals("void")) this.type = PrimitiveTypeKeyword.VOID;
        if(str.equals("null")) this.type = PrimitiveTypeKeyword.NULL;
//        if(str.equals("string")) this.type = PrimitiveTypeKeyword.STRING;
    }

    @Override public String getType() {
        switch (type) {
            case INT: return "INT";
            case BOOL: return "BOOL";
            case NULL: return "NULL";
            case VOID: return "VOID";
            //case STRING: return "string";//define string as a class
            default: return "";
        }
    }


    @Override public boolean equalTo(VariableTypeNode node) {
        switch (type) {
            case BOOL:return node.equalPrimitive(PrimitiveTypeKeyword.BOOL);
            case INT:return node.equalPrimitive(PrimitiveTypeKeyword.INT);
            case VOID:return node.equalPrimitive(PrimitiveTypeKeyword.VOID);
            //case STRING:return node.equalPrimitive(PrimitiveTypeKeyword.STRING);
            case NULL:
                if(node instanceof ArrayTypeNode) return true;
                if (node instanceof ClassTypeNode) {
                    if(!((ClassTypeNode)node).getReferenceClassName().equals("string"))//string node cannot be null
                        return true;
                }
                return false;
            default: return false;
        }
    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
