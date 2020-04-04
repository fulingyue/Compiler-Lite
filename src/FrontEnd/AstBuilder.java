package FrontEnd;


import FrontEnd.AstNode.*;
import org.antlr.v4.runtime.tree.*;
import FrontEnd.Antlr.*;
import FrontEnd.Antlr.MxParser;
import FrontEnd.Antlr.MxLexer;

import java.lang.*;

import util.ErrorHandler;
import util.Location;


public class AstBuilder extends MxBaseVisitor<AstNode> {
    private ErrorHandler Error;
    private ProgramNode program;

    public AstBuilder() {
        this.program = new ProgramNode();
        this.Error = new ErrorHandler();
    }
    public ProgramNode getProgram(){
        return program;
    }

    public ErrorHandler getError() {
        return Error;
    }


    @Override
    public AstNode visitProgram(MxParser.ProgramContext ctx){
        program.setLocation( new Location(ctx));
        for (ParseTree item : ctx.children) {
            AstNode node = visit(item);
            node.setParent(program);
            if (item instanceof MxParser.ClassDefinitionContext)
                program.addClass((ClassDefNode) node);
            else if (item instanceof MxParser.FunctionDefinitionContext)
                program.addFunc((FunctionDefNode) node);
            else if (node instanceof VarDefListNode)
                for (VarDefNode var: ((VarDefListNode) node).getVarDefNodeList())
                    program.addVar(var);
        }
        return program;
    }

    @Override
    public AstNode visitClassDefinition(MxParser.ClassDefinitionContext ctx) {
        ClassDefNode ret = new ClassDefNode();
        ret.setLocation(new Location(ctx));
        if(ctx.Identifier() == null)
            Error.error(new Location(ctx),"error class definition");
        else
            ret.setClassName(ctx.Identifier().getText());

        for (MxParser.MemberVariableContext item: ctx.memberVariable()){
            VarDefListNode node = (VarDefListNode) visit(item);
            node.setParent(ret);
            ret.addMember(node);
        }
        for (MxParser.FunctionDefinitionContext item: ctx.functionDefinition()) {
            FunctionDefNode node = (FunctionDefNode) visit(item);
            node.setParent(ret);
            ret.addFunction(node);
        }
        for (MxParser.ConstructionFunctionDefinitionContext item: ctx.constructionFunctionDefinition()){
            FunctionDefNode node = (FunctionDefNode) visit(item);
            node.setParent(ret);
            ret.addConstructor(node);
        }

        return ret;
    }

    @Override public AstNode visitMemberVariable(MxParser.MemberVariableContext ctx) {
        return (VarDefListNode)visit(ctx.variableDefinition());
    }


    @Override
    public AstNode visitConstructionFunctionDefinition(MxParser.ConstructionFunctionDefinitionContext ctx) {
        FunctionDefNode ret = new FunctionDefNode();
        ret.setLocation(new Location(ctx));
        ret.setReturnType(new PrimitiveTypeNode("void"));
        ret.setMethodName(ctx.Identifier().getText());
        if (ctx.formalParameterList() != null) {
            for (MxParser.FormalParameterContext item : ctx.formalParameterList().formalParameter()) {
                VarDefNode node = (VarDefNode) visit(item);
                node.setParent(ret);
                ret.addParameter(node);
            }
        }
        BlockNode block = (BlockNode) visit(ctx.block());
        block.setParent(ret);
        ret.setBlock(block);
        return ret;
    }

    @Override
    public AstNode visitFunctionDefinition (MxParser.FunctionDefinitionContext ctx) {
        FunctionDefNode ret = new FunctionDefNode();
        ret.setLocation(new Location(ctx));
        ret.setReturnType((VariableTypeNode)visit(ctx.variableType()));
        ret.setMethodName(ctx.Identifier().getText());
        if (ctx.formalParameterList() != null) {
            for (MxParser.FormalParameterContext item: ctx.formalParameterList().formalParameter())
                ret.addParameter((VarDefNode)visit(item));
        }
        ret.setBlock((BlockNode) visit(ctx.block()));
        return ret;
    }

    @Override
    public AstNode visitFormalParameter(MxParser.FormalParameterContext ctx) {
        VarDefNode ret = new VarDefNode();
        ret.setLocation(new Location(ctx));
        ret.setVarType((VariableTypeNode) visit(ctx.variableType()));
        ret.setVarName(ctx.Identifier().getText());
        if(ctx.expression() != null) {
            ret.setInitVal((ExprStaNode) visit(ctx.expression()));//to be check
        }
        return ret;
    }

    @Override
    public AstNode visitBlock(MxParser.BlockContext ctx) {
        BlockNode ret = new BlockNode();
        ret.setLocation(new Location(ctx));
        for (MxParser.StatementContext item : ctx.statementList().statement()){
                ret.addChild(visit(item));
        }
        return ret;
    }


    @Override
    public AstNode visitVariableDefinition(MxParser.VariableDefinitionContext ctx) {
        VarDefListNode ret = new VarDefListNode();
        VariableTypeNode varType = (VariableTypeNode) visit(ctx.variableType());
        ret.setLocation(new Location(ctx));
        ret.setType(varType);
        for (MxParser.VariableDeclarExprContext item: ctx.variableDeclarExprs().variableDeclarExpr()){
            VarDefNode tmp = (VarDefNode)visit(item);
            tmp.setVarType(varType);
            ret.add(tmp);
        }
        return ret;
    }

    @Override
    public AstNode visitVariableDeclarExpr(MxParser.VariableDeclarExprContext ctx) {
        VarDefNode ret = new VarDefNode();
        ret.setLocation(new Location(ctx));
        ret.setVarName(ctx.Identifier().getText());
        if (ctx.expression() != null)
            ret.setInitVal((ExprStaNode)visit(ctx.expression()));
        else ret.setInitVal(null);

        return ret;
    }



    @Override
    public AstNode visitDefinitionStat(MxParser.DefinitionStatContext ctx) {
        return visit(ctx.variableDefinition());
    }

    @Override
    public AstNode visitExpressionStat(MxParser.ExpressionStatContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public AstNode visitIfStat(MxParser.IfStatContext ctx) {
        IfStaNode ret = new IfStaNode();
        ret.setLocation(new Location(ctx));
        ret.setCondition((ExprStaNode) visit(ctx.expression()));
        if(!(ctx.statement(0) instanceof MxParser.BlockStatContext)){
            AstNode statement = visit(ctx.statement(0));
            ret.setIfBlock(new BlockNode(statement));
        }
        else {
            ret.setIfBlock((BlockNode) visit(ctx.statement(0)));
        }

        if(ctx.ELSE() == null) ret.setElseBlock(null);
        else {
            AstNode statement = visit(ctx.statement(1));
            if(!(statement instanceof BlockNode)){
                ret.setElseBlock(new BlockNode(statement));
            }
            else
                ret.setElseBlock((BlockNode) statement);
        }

        return ret;
    }

    @Override
    public AstNode visitForStat(MxParser.ForStatContext ctx) {
        ForStaNode ret = new ForStaNode();
        ret.setLocation(new Location(ctx));
        if (ctx.init == null) ret.setInit(null);
        else ret.setInit((ExprStaNode) visit(ctx.init));
        if (ctx.condition == null) ret.setCondition(null);
        else ret.setCondition((ExprStaNode) visit(ctx.condition));
        if (ctx.after_block == null) ret.setRecursionCond(null);
        else ret.setRecursionCond((ExprStaNode) visit(ctx.after_block));
        ret.setBlock((StatementNode) visit(ctx.statement()));
        return ret;
    }

    @Override
    public AstNode visitWhileStat(MxParser.WhileStatContext ctx) {
        WhileStaNode ret = new WhileStaNode();
        ret.setLocation(new Location(ctx));
        ret.setCondition((ExprStaNode) visit(ctx.expression()));
        ret.setBlock((StatementNode)visit(ctx.statement()));
        return ret;
    }

    @Override
    public AstNode visitReturnStat(MxParser.ReturnStatContext ctx) {
        ReturnStaNode ret = new ReturnStaNode();
        ret.setLocation(new Location(ctx));
        if (ctx.expression() == null) ret.setReturnVal(null);
        else ret.setReturnVal((ExprStaNode) visit(ctx.expression()));
        return ret;
    }

    @Override
    public AstNode visitBreakStat(MxParser.BreakStatContext ctx) {
        BreakStaNode ret = new BreakStaNode();
        ret.setLocation(new Location(ctx));
        return ret;
    }

    @Override
    public AstNode visitContinueStat(MxParser.ContinueStatContext ctx) {
        ContinueStaNode ret = new ContinueStaNode();
        ret.setLocation(new Location(ctx));
        return ret;
    }

    @Override
    public AstNode visitEmptyStat(MxParser.EmptyStatContext ctx) {
        EmptyStaNode ret = new EmptyStaNode();
        ret.setLocation(new Location(ctx));
        return ret;
    }
    @Override
    public AstNode visitBlockStat(MxParser.BlockStatContext ctx) {
        return visit(ctx.block());
    }


    @Override
    public AstNode visitNewExpr(MxParser.NewExprContext ctx) {
        return visit(ctx.creator());
    }

    @Override public AstNode visitErrorCreator(MxParser.ErrorCreatorContext ctx){
        Error.error(new Location(ctx),"error array creatror");
        return new EmptyStaNode();
    }


    @Override
    public AstNode visitCorrectCreator(MxParser.CorrectCreatorContext ctx) {
        NewExprNode ret = new NewExprNode();
        VariableTypeNode varType, tmp, baseType;
        int dim = ctx.LBRACK().size();
        int dimOfExpr = ctx.expression().size();
        varType = (VariableTypeNode)visit(ctx.baseType());
        baseType = varType;
        for (int i = dim - 1;i >= 0; --i){
            tmp = new ArrayTypeNode(varType);
            ((ArrayTypeNode)tmp).setBaseType(baseType);
            ((ArrayTypeNode) tmp).setDim(i+1);
            if(i < dimOfExpr){
                ExprStaNode size = (ExprStaNode)visit(ctx.expression(i));
                ((ArrayTypeNode)tmp).setSize(size);
            } else{
                ((ArrayTypeNode) tmp).setSize(null);
            }
            varType.setParent(tmp);
            varType = tmp;
        }

        ret.setLocation(new Location(ctx));
        ret.setVariableType(varType);
        //ret.parameterList goes out of use
        return ret;
    }

    @Override
    public AstNode visitCommonCreator(MxParser.CommonCreatorContext ctx) {
        NewExprNode ret = new NewExprNode();
        ret.setLocation(new Location(ctx));
        VariableTypeNode node = (VariableTypeNode)visit(ctx.baseType());
        ret.setVariableType(node);
        return ret;
    }

    @Override
    public AstNode visitThisExpr(MxParser.ThisExprContext ctx) {//exactly it is same as primaryExpr
        ThisExprNode ret = new ThisExprNode();
        ret.setLocation(new Location(ctx));
        return ret;
    }

    @Override
    public AstNode visitUnaryExpr(MxParser.UnaryExprContext ctx) {
        UnaryOpNode ret = new UnaryOpNode();
        ret.setLocation(new Location(ctx));
        if (ctx.prefix != null) {
            switch (ctx.prefix.getType()) {
                case MxLexer.INC: ret.setOp(UnaryOpNode.UnaryOp.PREFIX_INC); break;
                case MxLexer.DEC: ret.setOp(UnaryOpNode.UnaryOp.PREFIX_DEX); break;
                case MxLexer.ADD: return visit(ctx.expression());
                case MxLexer.SUB: ret.setOp(UnaryOpNode.UnaryOp.NEGATE); break;
                case MxLexer.NOT: ret.setOp(UnaryOpNode.UnaryOp.NOT); break;
                case MxLexer.LNOT: ret.setOp(UnaryOpNode.UnaryOp.LNOT); break;
            }
        }
        else if (ctx.postfix != null) {
            switch (ctx.postfix.getType()) {
                case MxLexer.INC: ret.setOp(UnaryOpNode.UnaryOp.POSTFIX_INC); break;
                case MxLexer.DEC: ret.setOp(UnaryOpNode.UnaryOp.POSTFIX_DEC); break;
            }
        }
        ret.setInnerExpr((ExprStaNode)visit(ctx.expression()));
        return ret;
    }

    @Override
    public AstNode visitPrimaryExpr(MxParser.PrimaryExprContext ctx) {
//        PrimaryExprNode ret = new PrimaryExprNode();
        if (ctx.BoolConstant() != null) {
            BoolNode retBool = new BoolNode();
            retBool.setLocation(new Location(ctx));
            retBool.setExprType(new PrimitiveTypeNode("bool"));
            retBool.setVal(ctx.BoolConstant().toString());
            return retBool;
        }
        if (ctx.IntegerConstant() != null) {
            IntNode retInt =  new IntNode();
            retInt.setLocation(new Location(ctx));
            retInt.setExprType(new PrimitiveTypeNode("int"));
            retInt.setVal(ctx.IntegerConstant().toString());
            return retInt;
        }
        if (ctx.StringConstant() != null) {
            StringNode retString = new StringNode();
            retString.setLocation(new Location(ctx));
            retString.setExprType(new PrimitiveTypeNode("string"));
            retString.setVal(ctx.StringConstant().toString());
            return retString;
        }
        if (ctx.NullConstant() != null) {
            NullNode retNull = new NullNode();
            retNull.setLocation(new Location(ctx));
            retNull.setExprType(new PrimitiveTypeNode("null"));
            return retNull;
        }
        else {return null;}//tobe check
    }

    @Override
    public AstNode visitIndexAccessExpr(MxParser.IndexAccessExprContext ctx) {
        IndexAccessNode ret = new IndexAccessNode();
        ret.setLocation(new Location(ctx));
        if (ctx.caller instanceof MxParser.NewExprContext)
            Error.error(new Location(ctx),"invalid dim of array");
        ret.setCaller((ExprStaNode) visit(ctx.caller));
        ret.setIndex((ExprStaNode) visit(ctx.index));
        return ret;
    }

    @Override
    public AstNode visitFunctionCallExpr(MxParser.FunctionCallExprContext ctx) {
        FunctionCallNode ret = new FunctionCallNode();
        ret.setLocation(new Location(ctx));
        MxParser.ExpressionContext expression = ctx.expression();
        ret.setCaller((ReferenceNode) visit(ctx.expression()));
        if (ctx.actualParameterList() != null) {
            for (MxParser.ExpressionContext item: ctx.actualParameterList().expression())
                ret.addParameter((ExprStaNode)visit(item));
        }
        return ret;
    }

    @Override
    public AstNode visitMemberAccessExpr(MxParser.MemberAccessExprContext ctx) {
        MemberAccessNode ret = new MemberAccessNode();
        ret.setLocation(new Location(ctx));
        ret.setCaller((ExprStaNode) visit(ctx.caller));
        ret.setMember((ExprStaNode) visit(ctx.member));
        return ret;
    }

    @Override
    public AstNode visitBinaryExpr(MxParser.BinaryExprContext ctx) {
        BinaryOpNode ret = new BinaryOpNode();
        ret.setLocation(new Location(ctx));
        switch (ctx.op.getType()){
            case MxLexer.MUL: ret.setOp(BinaryOpNode.BinaryOp.MUL); break;
            case MxLexer.DIV: ret.setOp(BinaryOpNode.BinaryOp.DIV); break;
            case MxLexer.MOD: ret.setOp(BinaryOpNode.BinaryOp.MOD); break;
            case MxLexer.ADD: ret.setOp(BinaryOpNode.BinaryOp.ADD); break;
            case MxLexer.SUB: ret.setOp(BinaryOpNode.BinaryOp.SUB); break;
            case MxLexer.LSHIFT: ret.setOp(BinaryOpNode.BinaryOp.LSHIFT); break;
            case MxLexer.RSHIFT: ret.setOp(BinaryOpNode.BinaryOp.RSHIFT); break;
            case MxLexer.LE: ret.setOp(BinaryOpNode.BinaryOp.LE); break;
            case MxLexer.GE: ret.setOp(BinaryOpNode.BinaryOp.GE); break;
            case MxLexer.LT: ret.setOp(BinaryOpNode.BinaryOp.LT); break;
            case MxLexer.GT: ret.setOp(BinaryOpNode.BinaryOp.GT); break;
            case MxLexer.EQUAL: ret.setOp(BinaryOpNode.BinaryOp.EQUAL); break;
            case MxLexer.NOTEQUAL: ret.setOp(BinaryOpNode.BinaryOp.NOTEQUAL); break;
            case MxLexer.AND: ret.setOp(BinaryOpNode.BinaryOp.AND); break;
            case MxLexer.XOR: ret.setOp(BinaryOpNode.BinaryOp.XOR); break;
            case MxLexer.OR: ret.setOp(BinaryOpNode.BinaryOp.OR); break;
            case MxLexer.LAND: ret.setOp(BinaryOpNode.BinaryOp.LAND); break;
            case MxLexer.LOR: ret.setOp(BinaryOpNode.BinaryOp.LOR); break;
            case MxLexer.ASSIGN: ret.setOp(BinaryOpNode.BinaryOp.ASSIGN); break;
        }
        ret.setLhs((ExprStaNode) visit(ctx.lhs));
        ret.setRhs((ExprStaNode) visit(ctx.rhs));
        return ret;
    }

    @Override
    public AstNode visitParensExpr(MxParser.ParensExprContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public AstNode visitIdentifierExpr(MxParser.IdentifierExprContext ctx) {
        ReferenceNode ret = new ReferenceNode(ctx.getText());
        ret.setLocation(new Location(ctx));
        return ret;
    }



    @Override
    public AstNode visitVariableType(MxParser.VariableTypeContext ctx) {
        if(ctx.LBRACK().size() == 0) {
            return visitBaseType(ctx.baseType());
        }
        else{
            return new ArrayTypeNode((VariableTypeNode)visitBaseType(ctx.baseType()), ctx.LBRACK().size());
        }
    }
    @Override
    public AstNode visitBaseType(MxParser.BaseTypeContext ctx) {
        if(ctx.primitiveType() != null) {
            return visitPrimitiveType(ctx.primitiveType());
        }
        else {
            return visitClassType(ctx.classType());
        }
    }


    @Override
    public AstNode visitClassType(MxParser.ClassTypeContext ctx) {
        ClassTypeNode ret = new ClassTypeNode(ctx.Identifier().getText());
        ret.setLocation(new Location(ctx));
        return ret;
    }

    @Override
    public AstNode visitPrimitiveType(MxParser.PrimitiveTypeContext ctx) {
        PrimitiveTypeNode ret = new  PrimitiveTypeNode(ctx.getText());
        ret.setLocation(new Location(ctx));
        return ret;
    }


}
