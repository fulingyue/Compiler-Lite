package FrontEnd.AstNode;

import FrontEnd.AstVisitor;

import java.util.LinkedList;
import java.util.List;

public class BlockNode extends StatementNode{
    public List<AstNode> getChildList() {
        return childList;
    }

    public void setChildList(List<AstNode> childList) {
        this.childList = childList;
    }

    private List<AstNode> childList;

    public void addChild(AstNode node) {
        node.setParent(this);
        childList.add(node);
    }
    public BlockNode() {
        childList = new LinkedList<AstNode>();
    }
    @Override
    public void getInfo(int tab) {
        super.getInfo(tab);
        for (AstNode item : childList) {
            ((StatementNode)item).getInfo(tab + 1);
        }
    }

    @Override
    public void accept(AstVisitor vistor) throws Exception{
        vistor.visit(this);
    }
}
