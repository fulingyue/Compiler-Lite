package FrontEnd;

import FrontEnd.AstNode.*;
import FrontEnd.AstNode.IntNode;
import FrontEnd.AstNode.ProgramNode;
import FrontEnd.ErrorChecker.SemanticException;
import util.ErrorHandler;


import java.util.LinkedList;

public class ParentLinker extends AstVisitor {
    private LinkedList<AstNode> stack;

    public ParentLinker() {
        stack = new LinkedList<>();
    }
    public void linkParent(ProgramNode programNode)throws Exception  {

        visit(programNode);
    }


    @Override
    public void visit(ProgramNode node) throws Exception{
        stack.addLast(node);
        super.visit(node);

        stack.removeLast();
    }

    @Override
    public void visit(ClassDefNode node)  throws Exception{
        node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
        for (FunctionDefNode item : node.getConstructionDefList())
            if (!item.getMethodName().equals(node.getClassName()))
                throw new SemanticException(node.getLocation(),
                        "construction method name must be the same as class");
    }

    @Override 
    public void visit(FunctionDefNode node) throws Exception {
        node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(BlockNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(ReferenceNode node) throws Exception{
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(IntNode node)  throws Exception{
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }
    @Override
    public void visit(BoolNode node) throws Exception {
        node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }
    @Override
    public void visit(NullNode node)  throws Exception{
        node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }
    @Override
    public void visit(StringNode node)throws Exception  {
        node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(ThisExprNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(VarDefListNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }
    @Override
    public void visit(VarDefNode node)throws Exception  {
        node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }


    @Override
    public void visit(MemberAccessNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(IndexAccessNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(FunctionCallNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(NewExprNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(UnaryOpNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(BinaryOpNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(IfStaNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(ForStaNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(WhileStaNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(ReturnStaNode node) throws Exception {
         node.setParent(stack.getLast());
        AstNode ancestor = node.getParent();
        while (!(ancestor instanceof FunctionDefNode)) {
            if (ancestor instanceof ProgramNode)
                throw new SemanticException(node.getLocation(), "return must be in a method definition");
            ancestor = ancestor.getParent();
        }
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(BreakStaNode node) throws Exception {
         node.setParent(stack.getLast());
        AstNode ancestor = node.getParent();
        while (!(ancestor instanceof ForStaNode) &&
                !(ancestor instanceof WhileStaNode)) {
            if (ancestor instanceof ProgramNode)
                throw new SemanticException(node.getLocation(), "break must be in a loop");
            ancestor = ancestor.getParent();
        }
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(ContinueStaNode node) throws Exception {
         node.setParent(stack.getLast());
        AstNode ancestor = node.getParent();
        while (!(ancestor instanceof ForStaNode) &&
                !(ancestor instanceof WhileStaNode)) {
            if (ancestor instanceof ProgramNode)
                throw new SemanticException(node.getLocation(),"continue must be in a loop");
            ancestor = ancestor.getParent();
        }
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(EmptyStaNode node) throws Exception{
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }



    @Override
    public void visit(PrimitiveTypeNode node) throws Exception{
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(ClassTypeNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }

    @Override
    public void visit(ArrayTypeNode node) throws Exception {
         node.setParent(stack.getLast());
        stack.addLast(node);
        super.visit(node);
        stack.removeLast();
    }
}

