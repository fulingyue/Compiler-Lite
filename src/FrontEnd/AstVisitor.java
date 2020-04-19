package FrontEnd;

import FrontEnd.AstNode.*;
import FrontEnd.ErrorChecker.*;

import java.lang.reflect.AnnotatedArrayType;

//3.2 check finished

public abstract class AstVisitor {

    public void visit(AstNode node) throws Exception {
//        System.out.print("visitAstnode");
        if (node instanceof ProgramNode) visit((ProgramNode) node);
        else if (node instanceof ClassDefNode) visit((ClassDefNode) node);
        else if (node instanceof BlockNode) visit((BlockNode) node);
        else if (node instanceof FunctionDefNode) visit((FunctionDefNode) node);
        else if (node instanceof StatementNode) visit((StatementNode) node);
    }

    public void  visit(StatementNode node) throws Exception {
        if (node instanceof VariableTypeNode) visit((VariableTypeNode) node);
        else if (node instanceof VarDefNode) visit((VarDefNode) node);
        else if (node instanceof ExprStaNode) visit((ExprStaNode) node);
        else if (node instanceof WhileStaNode) visit((WhileStaNode) node);
        else if (node instanceof ForStaNode) visit((ForStaNode) node);
        else if (node instanceof IfStaNode) visit((IfStaNode) node);
        else if (node instanceof BreakStaNode) visit((BreakStaNode) node);
        else if (node instanceof ContinueStaNode) visit((ContinueStaNode) node);
        else if (node instanceof EmptyStaNode) visit((EmptyStaNode) node);
        else if (node instanceof ReturnStaNode) visit((ReturnStaNode) node);
        else if(node instanceof  VarDefListNode) visit((VarDefListNode)node);
        else if (node instanceof BlockNode) visit((BlockNode) node);
        else visit(node);
    }

    public void  visit(BlockNode node) throws Exception {
        for (AstNode item: node.getChildList()) {
            visit(item);
        }
    }
    public void  visit(ClassDefNode node) throws Exception {

        for (VarDefNode item: node.getMemberList()) visit(item);
        for (FunctionDefNode item: node.getConstructionDefList()) visit(item);
        for (FunctionDefNode item: node.getFunctionDefList()) visit(item);
    }

    public void  visit(FunctionDefNode node) throws Exception {
        visit(node.getReturnType());
        for (VarDefNode item: node.getFormalParameterList()) {
            visit(item);
        }
        visit(node.getBlock());
    }

    public void  visit(ProgramNode node) throws Exception {
        for (AstNode item : node.getChildenList()) {
            if (item instanceof ClassDefNode) visit(((ClassDefNode)item));
            else if (item instanceof FunctionDefNode) visit(((FunctionDefNode)item));
            else visit(((VarDefNode)item));
        }
    }

    public void  visit(ExprStaNode node) throws Exception {
        if (node instanceof UnaryOpNode) visit(((UnaryOpNode) node));
        else if (node instanceof BinaryOpNode) visit(((BinaryOpNode) node));
        else if (node instanceof IndexAccessNode) visit(((IndexAccessNode) node));
        else if (node instanceof MemberAccessNode) visit(((MemberAccessNode) node));
        else if (node instanceof PrimaryExprNode) visit(((PrimaryExprNode) node));
        else if (node instanceof FunctionCallNode) visit(((FunctionCallNode) node));
        else if (node instanceof NewExprNode) visit(((NewExprNode) node));
//        node.accept(this);
        else if(node instanceof EmptyStaNode) visit((EmptyStaNode)node);
        else throw new  Exception("no expr");
    }
    public void  visit(IfStaNode node) throws Exception {
        visit(node.getCondition());
        visit(node.getIfBlock());
        if (node.getElseBlock() != null) visit(node.getElseBlock());

    }

    public void  visit(ForStaNode node) throws Exception {
        if (node.getInit() != null) visit(node.getInit());
        if (node.getCondition() != null) visit(node.getCondition());
        if (node.getRecursionCond() != null) visit(node.getRecursionCond());
        visit(node.getBlock());
    }

    public void  visit(WhileStaNode node) throws Exception {
        if (node.getCondition() != null) visit(node.getCondition());
        visit(node.getBlock());
    }

    public void  visit(BreakStaNode node) throws Exception {}
    public void  visit(ContinueStaNode node) throws Exception{}
    public void  visit(EmptyStaNode node) throws Exception {}
    public void  visit(ReturnStaNode node) throws Exception{
        if (node.getReturnVal() != null) visit(node.getReturnVal());
    }


    public void  visit(UnaryOpNode node) throws Exception {
        visit(node.getInnerExpr());
    }

    public void  visit(BinaryOpNode node) throws Exception {
        visit(node.getLhs());
        visit(node.getRhs());
    }

    public void  visit(VarDefListNode node) throws Exception{
        visit(node.getType());
        for (VarDefNode item: node.getVarDefNodeList()){
            visit(item);
        }
    }
    public void  visit(VarDefNode node) throws Exception {
        visit(node.getVarType());
        if (node.getInitVal() != null) visit(node.getInitVal());
    }


    public void  visit(IndexAccessNode node) throws Exception {
        visit(node.getCaller());
        visit(node.getIndex());
    }
    public void  visit(MemberAccessNode node) throws Exception {
        visit(node.getCaller());
        visit(node.getMember());
    }
    public void  visit(PrimaryExprNode node) throws Exception {
        if (node instanceof ReferenceNode) visit((ReferenceNode) node);
        else if (node instanceof IntNode) visit((IntNode) node);
        else if( node instanceof BoolNode) visit((BoolNode)node);
        else if (node instanceof StringNode) visit((StringNode)node);
        else if(node instanceof NullNode) visit((NullNode) node);
        else if (node instanceof ThisExprNode) visit((ThisExprNode) node);
//        node.accept(this);
    }

    public void  visit(FunctionCallNode node) throws Exception {
        visit(node.getCaller());
        for(ExprStaNode item: node.getActualParameterList()) {
           visit(item);
        }

    }
    public void  visit(NewExprNode node) throws Exception {
        visit(node.getVariableType());
        for(ExprStaNode item:node.getActualParameterList()) {
            visit(item);
        }
    }

    public void  visit(VariableTypeNode node) throws Exception {
        if (node instanceof PrimitiveTypeNode) visit(((PrimitiveTypeNode) node));
        else if (node instanceof ClassTypeNode) visit(((ClassTypeNode) node));
        else if (node instanceof ArrayTypeNode) visit(((ArrayTypeNode)node));
    }

    public void  visit(PrimitiveTypeNode node) throws Exception{

    }

    public void  visit(ClassTypeNode node) throws Exception {

    }
    public void  visit(ArrayTypeNode node) throws Exception {
        visit(node.getInnerTypeNode());
        if (node.getSize()!= null) visit(node.getSize());
    }
    public void  visit(ReferenceNode node) throws Exception {

    }
    public void  visit(NullNode node) throws Exception {

    }

    public void  visit(BoolNode node) throws Exception {

    }
    public void  visit(IntNode node) throws Exception {

    }
    public void  visit(StringNode node) throws Exception {

    }

    public void  visit(ThisExprNode node) throws Exception{

    }

















}
