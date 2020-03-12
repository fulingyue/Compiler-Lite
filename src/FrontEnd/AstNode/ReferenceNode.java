package FrontEnd.AstNode;
// this class save the info of those array,class and definition call

import FrontEnd.AstVisitor;

public class ReferenceNode extends PrimaryExprNode {
    public enum ReferenceType {
        METHOD, CLASS, VARIABLE
    }
    private String referenceName;
    private ReferenceType referenceType;
    private AstNode definitionNode;

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public AstNode getDefinitionNode() {
        return definitionNode;
    }

    public void setDefinitionNode(AstNode definitionNode) {
        this.definitionNode = definitionNode;
    }

    public ReferenceNode(String str) {
        referenceName = str;
    }
    @Override
    public void getInfo(int tab){
        super.getInfo(tab);
        util.print_tool.printSpaceAndStr(tab, "Reference:" + referenceName);
        if(referenceType != null){
            util.print_tool.printSpaceAndStr(tab , "ReferenceType:"+referenceType);
        }
        if(definitionNode != null){
            util.print_tool.printSpaceAndStr(tab , "ReferenceDef:"+ Integer.toString(definitionNode.location.getLine()));
        }

    }
    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
