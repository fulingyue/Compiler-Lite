package FrontEnd;

import FrontEnd.AstNode.*;
import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.*;
import FrontEnd.IR.Type.*;
import FrontEnd.Scope.GlobalScope;

import util.Pair;

import java.util.*;
import java.util.List;

import static FrontEnd.AstNode.BinaryOpNode.BinaryOp.*;

public class IRBuilder extends AstVisitor{
    private Module program;
    private GlobalScope topLevelScope;

    private BasicBlock currentBB;
    private ClassDefNode currentClass;
    private IRFunction currentFunction;

    private IRFunction initialize;

    private boolean isleft = false;
    private Stack<BasicBlock>  loopNextStepBB,  loopBreakBB;
    private HashMap<AstNode,Operand> identifierTable = new HashMap<>();
    private Operand curArrayHead;


    public IRBuilder() {
        topLevelScope = null;
        currentBB = null;
        currentClass = null;
        currentFunction = null;
        loopBreakBB = new Stack<>();
        loopNextStepBB = new Stack<>();

        initialize = new IRFunction("__init__", program,
                new ArrayList<Parameter>(), new VoidType(),false);
        initialize.setSideEffect(true);
        initialize.init();

    }

    public Module build(ProgramNode node) throws Exception{
        program = new Module(node);//have generate IRTypeTable
        program.addFunction(initialize);
        topLevelScope = (GlobalScope)node.getScope();

        program.initBuildin();

        visit(node);
        IRFunction main = program.getFunction("main");
        main.getEntranceBB().addFirstInst(
                new CallFunction("call_init",main.getEntranceBB(),initialize,new ArrayList<>(),null)
        );

        return this.program;
    }


    /////////private util functions//////////


    private void registerFunction(FunctionDefNode node)  {
        String  name;
        IRType returnType = program.getTypeTable().transport(node.getReturnType());
        ArrayList<Parameter> parameters = new ArrayList<>();

        if(currentClass != null) {
            ClassIRType classIRType = (ClassIRType)program.getTypeTable().get(currentClass.getClassName());
            parameters.add(new Parameter("this", classIRType.getPtrType()));
            //need to set parent

            name = currentClass.getClassName() + "_" + node.getMethodName();
        } else {
            name = node.getMethodName();
        }

        IRFunction function = new IRFunction(name ,program,parameters,returnType,false);
        if(parameters.size() >0)
            parameters.get(0).setFuncParent(function);

        for(VarDefNode item: node.getFormalParameterList()) {
            parameters.add(new Parameter(item.getVarName(),program.getTypeTable().transport(item.getVarType()),function));
        }

        program.addFunction(function);
        function.init();

        currentBB = function.getEntranceBB();
        int offset;
        if(currentClass != null) {
            Parameter parameter = parameters.get(0);
            Register reg = new VirtualReg("this$addr",new PtrType(parameter.getType()));
            function.getSymbolTable().put(reg.getName(),reg);
            AllocateInst alloc = new AllocateInst(currentBB,"alloca",reg, parameter.getType());
            currentBB.addInst(alloc);

            currentBB.addInst(new Store("storeThisAddr",currentBB,parameter,reg));//still need add value
            offset = 1;
        }
        else
            offset = 0;

        for (int  i = 0;i < parameters.size() - offset; ++i) {
            Parameter parameter = parameters.get(i + offset);
            Register dest = new VirtualReg(parameter.getName()  + "$addr",new PtrType(parameter.getType()));
            function.getSymbolTable().put(dest.getName(),dest);
            AllocateInst alloc  = new AllocateInst(currentBB, "alloca",  dest, parameter.getType());
            currentBB.addInst(alloc);

            currentBB.addInst(new  Store("storeAddr2", currentBB,parameter, dest));
        }


    }




////////////visit functions////////////////
    @Override
    public void visit(ProgramNode node) throws Exception{
        ////init class functions/////
        for (ClassDefNode item: node.getClassDefList()) {
            currentClass = item;

            List<FunctionDefNode> constructors = item.getConstructionDefList();
            if(constructors.size() > 0)  {
                for(FunctionDefNode def: constructors) {
                    registerFunction(def);
                }
            }
            for (FunctionDefNode function: item.getFunctionDefList()) {
                registerFunction(function);
            }
        }
        currentClass = null;
        for(FunctionDefNode item: node.getFunctionDefList()) {
            registerFunction(item);
        }

        currentFunction = initialize;
        currentBB = initialize.getEntranceBB();

        for(VarDefNode item: node.getVarDefList()) {
            item.accept(this);
        }

        currentBB.addInst(new BranchJump(currentBB.getName() + "_branch",
                currentBB,null,currentFunction.getReturnBB(),null));

        currentFunction.addBB(currentFunction.getReturnBB());
        currentFunction = null;
        currentBB = null;

        //just  simply visit their children
        for (ClassDefNode item :node.getClassDefList()) {
            visit(item);
        }
        for(FunctionDefNode item: node.getFunctionDefList()) {
            visit(item);
        }

    }



    @Override
    public void visit(ClassDefNode node)  throws Exception {
        currentClass  =  node;
        if(node.getConstructionDefList().size() > 0) {
            for (FunctionDefNode constructor:  node.getConstructionDefList())
                constructor.accept(this);
        }

        for (FunctionDefNode function: node.getFunctionDefList())
            function.accept(this);
        currentClass = null;
    }


    @Override
    public void  visit(FunctionDefNode node) throws Exception {
        String name;
        if(currentClass == null) {// not in class
            name = node.getMethodName();
        } else {
            name = currentClass.getClassName() + "_" + node.getMethodName();
        }

        IRFunction irFunction = (IRFunction)program.getFunction(name);
        currentFunction = irFunction;
        currentBB = irFunction.getEntranceBB();

        node.getBlock().accept(this);

        currentBB.addInst(new BranchJump(node.getMethodName()+ "_branch",
                currentBB,null,currentFunction.getReturnBB(),  null));

        irFunction.addBB(irFunction.getReturnBB());
        currentBB = null;
        currentFunction = null;
    }

    @Override
    public void  visit(BlockNode node) throws Exception {
        for (AstNode item: node.getChildList()) {
            item.accept(this);
        }
    }

    @Override
    public void  visit(IfStaNode node) throws Exception {
        BasicBlock thenBB = new BasicBlock("if.then", currentFunction);
        BasicBlock elseBB  = null;
        BasicBlock endBB = new BasicBlock("if.end", currentFunction);

        node.getCondition().accept(this);
        Operand condition = node.getCondition().getResult();
        if (node.getElseBlock() != null) {
            elseBB = new BasicBlock("if.else",  currentFunction);
            currentBB.addInst(new BranchJump("jump_if",currentBB,
                    condition,thenBB, elseBB));
        } else {
            currentBB.addInst(new BranchJump("jump_if",currentBB,
                    condition, thenBB, endBB));
        }

        currentBB = thenBB;
        node.getIfBlock().accept(this);
        currentBB.addInst(new BranchJump("jump_end",currentBB,
                null, endBB,null));
        currentFunction.addBB(thenBB);

        currentBB  = elseBB;
        if(node.getElseBlock()!= null) {
            node.getElseBlock().accept(this);
            currentBB.addInst(new BranchJump("jump_end",currentBB,
                    null, endBB, null));
            currentFunction.addBB(elseBB);
        }
        currentBB = endBB;
        currentFunction.addBB(endBB);
        if(elseBB != null)
            currentFunction.getSymbolTable().put(elseBB.getName(),elseBB);
        currentFunction.getSymbolTable().put(thenBB.getName(),thenBB);
        currentFunction.getSymbolTable().put(endBB.getName(),endBB);


    }
    @Override
    public void  visit(ForStaNode node) throws Exception {
        BasicBlock conditionBB = null;
        if(node.getCondition() !=null)
            conditionBB = new BasicBlock("for.cond", currentFunction);
        BasicBlock recursionBB = null;
        if(node.getRecursionCond() != null)
            recursionBB = new BasicBlock("for.inc",currentFunction);
        BasicBlock bodyBB = new BasicBlock("for.body",currentFunction);
        BasicBlock endBB = new BasicBlock("for.end",currentFunction);


        if(node.getInit() != null)//add init expr to current block
            node.getInit().accept(this);

        if(node.getCondition() != null) {
            currentBB.addInst(new BranchJump("jump_for.cond",currentBB,
                    null,conditionBB,null));//terminal instruction of previous block
            currentBB = conditionBB;
            node.getCondition().accept(this);//add condition to condition block
            Operand condition = node.getCondition().getResult();
            currentBB.addInst(new BranchJump("br_for.body||end",currentBB,condition,bodyBB,endBB));
            currentFunction.addBB(conditionBB);//condtionBB end

            currentBB = bodyBB;
            if(node.getRecursionCond() != null) {
                loopNextStepBB.push(recursionBB);
            }
            else
                loopNextStepBB.push(conditionBB);
            loopBreakBB.push(endBB);
            node.getBlock().accept(this);
            if(node.getRecursionCond() != null)
                currentBB.addInst(
                        new BranchJump("jump_for.inc",currentBB,null,recursionBB,null)
                );
            else
                currentBB.addInst(
                        new BranchJump("jump_for.cond",currentBB,null,conditionBB,null)
                );
            loopNextStepBB.pop();
            loopBreakBB.pop();
            currentFunction.addBB(bodyBB);//body block end

            if(recursionBB != null){
                currentBB = recursionBB;
                node.getRecursionCond().accept(this);
                currentBB.addInst(
                        new BranchJump("jump_for.cond",currentBB,null,conditionBB,null)
                );
                currentFunction.addBB(recursionBB);
            }

        }
        else {
            currentBB.addInst(
                    new BranchJump("jump_for.body",currentBB,null,bodyBB,null)
            );

            loopBreakBB.push(endBB);
            if(recursionBB != null)
                loopNextStepBB.push(recursionBB);
            else
                loopNextStepBB.push(bodyBB);//this situation cannot visit!

            currentBB = bodyBB;
            node.getBlock().accept(this);
            if(node.getRecursionCond() != null)
                currentBB.addInst(
                        new BranchJump("jump_for.inc",currentBB,null,recursionBB,null)
                );
            else
                currentBB.addInst(
                        new BranchJump("jump_for.body",currentBB,null,bodyBB,null)
                );
            loopNextStepBB.pop();
            loopBreakBB.pop();
            currentFunction.addBB(bodyBB);//body ended

            if(recursionBB != null){
                currentBB = recursionBB;
                node.getRecursionCond().accept(this);
                currentBB.addInst(
                        new BranchJump("jump_for.body",currentBB,null,bodyBB,null)
                );
                currentFunction.addBB(recursionBB);
            }

        }

        currentBB = endBB;
        currentFunction.addBB(endBB);

    }

    @Override
    public void  visit(WhileStaNode node) throws Exception {
        BasicBlock conditionBB = null;
        if(node.getCondition() != null)
            conditionBB = new BasicBlock("while.cond",currentFunction);
        BasicBlock bodyBB = new BasicBlock("while.body",currentFunction);
        BasicBlock endBB = new BasicBlock("while.end",currentFunction);
        if(conditionBB != null){
            currentBB.addInst(new BranchJump("jump_while.body",currentBB,null,conditionBB,null));
            currentBB = conditionBB;
            node.getCondition().accept(this);
            Operand condition = node.getCondition().getResult();
            currentBB.addInst(
                    new BranchJump("br_while.body||end",currentBB,condition,bodyBB,endBB)
            );
            currentFunction.addBB(conditionBB);

            currentBB = bodyBB;
            loopBreakBB.push(endBB);
            loopNextStepBB.push(conditionBB);
            node.getBlock().accept(this);
            currentBB.addInst(
                    new BranchJump("jump_while.cond",currentBB,null,conditionBB,null)
            );
            loopNextStepBB.pop();
            loopBreakBB.pop();
            currentFunction.addBB(bodyBB);//bodyBB end

            currentBB = endBB;
            currentFunction.addBB(endBB);

        } else {
            currentBB.addInst(new BranchJump("jump_while.body",currentBB,null,bodyBB,null));

            currentBB = bodyBB;
            loopBreakBB.push(endBB);
            loopNextStepBB.push(bodyBB);
            node.getBlock().accept(this);
            currentBB.addInst(
                    new BranchJump("jump_while.body",currentBB,null,bodyBB,null)
            );
            loopNextStepBB.pop();
            loopBreakBB.pop();
            currentFunction.addBB(bodyBB);// bodyBB end

            currentBB = endBB;
            currentFunction.addBB(endBB);
        }

    }
    @Override
    public void  visit(BreakStaNode node) throws Exception {
        currentBB.addInst(new BranchJump(
                "jump_loop.end",currentBB,null,loopBreakBB.peek(),null));
    }

    @Override
    public void  visit(ContinueStaNode node) throws Exception{
        currentBB.addInst(new BranchJump(
                "jump_loop.continue",currentBB,null,loopNextStepBB.peek(),null
        ));
    }
    @Override
    public void  visit(ReturnStaNode node) throws Exception{
        if(node.getReturnVal() != null) {
            node.getReturnVal().accept(this);
            Operand res = node.getReturnVal().getResult();
            currentBB.addInst(new Store(
                    "store_retval",currentBB,res,currentFunction.getReturnVal()
            ));
        }
        currentBB.addInst(new BranchJump(
                "jump_ret",currentBB,null,currentFunction.getReturnBB(),null
        ));
    }


    @Override
    public void  visit(UnaryOpNode node) throws Exception {
        Operand inner;

            node.getInnerExpr().accept(this);// include  those  load insts
            inner  = node.getInnerExpr().getResult();



        switch (node.getOp()) {
            case LNOT: //!
                Register lnot_result = new VirtualReg("not",new IntIRType(IntIRType.intType.i1));
                BinaryOp inst = new BinaryOp(currentBB,lnot_result, BinaryOp.BinOp.XOR,new ConstBool(true),inner);

                currentBB.addInst(inst);
                node.setResult(lnot_result);
                node.setAddr(null);
                currentFunction.getSymbolTable().put(lnot_result.getName(),lnot_result);
                break;
            case NOT: //~
                Register not_res = new VirtualReg("bitwise",new IntIRType(IntIRType.intType.i32));
                BinaryOp not_inst = new BinaryOp(
                        currentBB,not_res, BinaryOp.BinOp.XOR,new ConstInt(-1, IntIRType.intType.i32),inner
                );

                currentBB.addInst(not_inst);
                node.setResult(not_res);
                node.setAddr(null);
                currentFunction.getSymbolTable().put(not_res.getName(),not_res);
                break;
            case NEGATE: //-
                if(inner instanceof Constant) {
                    assert inner instanceof ConstInt;
                    node.setResult(
                            new ConstInt(-((ConstInt) inner).getValue(), IntIRType.intType.i32)
                    );
                    currentFunction.getSymbolTable().put(node.getResult().getName(),node.getResult());
                }
                else {
                    Register neg_res = new VirtualReg("neg",new IntIRType(IntIRType.intType.i32));
                    BinaryOp neg_inst = new BinaryOp(
                            currentBB,neg_res, BinaryOp.BinOp.SUB,new ConstInt(0, IntIRType.intType.i32),inner
                    );

                    currentBB.addInst(neg_inst);
                    node.setResult(neg_res);
                    node.setAddr(null);
                    currentFunction.getSymbolTable().put(neg_res.getName(),neg_res);

                }
                break;
            case PREFIX_INC://++a
                if(inner instanceof Constant) {
                    assert inner instanceof ConstInt;
                    node.setResult(
                            new ConstInt(((ConstInt) inner).getValue()+1, IntIRType.intType.i32)
                    );
                    node.setAddr(null);
                    currentFunction.getSymbolTable().put(node.getResult().getName(),node.getResult());
                }
                else {
                    Register pinc_res = new VirtualReg("pre_inc",new IntIRType(IntIRType.intType.i32));
                    BinaryOp pinc_inst = new BinaryOp(
                            currentBB,pinc_res, BinaryOp.BinOp.ADD,new ConstInt(1, IntIRType.intType.i32),inner
                    );

                    currentBB.addInst(pinc_inst);
                    Operand addr = node.getInnerExpr().getAddr();
                    currentBB.addInst(
                            new Store("++store",currentBB,pinc_res,addr)
                    );
                    node.setResult(pinc_res);
                    node.setAddr(addr);
                    currentFunction.getSymbolTable().put(pinc_res.getName(),pinc_res);
                }
                break;

            case PREFIX_DEX:
                if(inner instanceof Constant) {
                    assert inner instanceof ConstInt;
                    node.setResult(
                            new ConstInt(((ConstInt) inner).getValue()-1, IntIRType.intType.i32)
                    );
                    node.setAddr(null);
                    currentFunction.getSymbolTable().put(node.getResult().getName(),node.getResult());
                }
                else {
                    Register pdec_res = new VirtualReg("pre_dec",new IntIRType(IntIRType.intType.i32));
                    BinaryOp pdec_inst = new BinaryOp(
                            currentBB,pdec_res, BinaryOp.BinOp.SUB,inner,new ConstInt(1, IntIRType.intType.i32)
                    );

                    currentBB.addInst(pdec_inst);

                    Operand addr = node.getInnerExpr().getAddr();
                    currentBB.addInst(
                            new Store("store++",currentBB,pdec_res,addr)
                    );
                    node.setResult(pdec_res);
                    node.setAddr(addr);
                    currentFunction.getSymbolTable().put(pdec_res.getName(),pdec_res);
                }
                break;
            case POSTFIX_INC:
                Register poinc_res = new VirtualReg("poinc_pos",new IntIRType(IntIRType.intType.i32));
                BinaryOp poinc_inst = new BinaryOp(currentBB, poinc_res, BinaryOp.BinOp.ADD,inner,new ConstInt(1, IntIRType.intType.i32));
                currentBB.addInst(poinc_inst);

                Instruction store = new Store(
                        "store_posinc",currentBB,poinc_res, node.getInnerExpr().getAddr()
                );

                currentBB.addInst(store);

                node.setResult(inner);
                node.setAddr(null);
                currentFunction.getSymbolTable().put(poinc_res.getName(),poinc_res);
                break;
            case POSTFIX_DEC:

                Register podec_res = new VirtualReg("podec_pos",new IntIRType(IntIRType.intType.i32));
                BinaryOp podec_inst = new BinaryOp(currentBB, podec_res, BinaryOp.BinOp.SUB,inner,new ConstInt(1, IntIRType.intType.i32));
                currentBB.addInst(podec_inst);

                Instruction storee = new Store(
                        "store_posdec",currentBB,podec_res, node.getInnerExpr().getAddr()
                );

                currentBB.addInst(storee);

                node.setResult(inner);
                node.setAddr(null);
                currentFunction.getSymbolTable().put(podec_res.getName(),podec_res);
                break;
        }
    }


    @Override
    public void  visit(BinaryOpNode node) throws Exception {
        if(node.getOp() == BinaryOpNode.BinaryOp.ASSIGN) {

            isleft = true;
            node.getLhs().accept(this);
            isleft = false;
            node.getRhs().accept(this);
            Operand lhs = node.getLhs().getAddr();
            Operand rhs = node.getRhs().getResult();
            currentBB.addInst(new Store("storeVal",currentBB,rhs,lhs));

            node.setResult(rhs);
            node.setAddr(null);
        }
        else if(node.getOp()!= LOR && node.getOp() != BinaryOpNode.BinaryOp.LAND) {
            //not short-circuit evaluation
            node.getLhs().accept(this);
            node.getRhs().accept(this);
            Operand lhs = node.getLhs().getResult();
            Operand rhs = node.getRhs().getResult();
            Register res;
            Instruction inst;
            String name;
            boolean cmp = false;
            BinaryOp.BinOp op1  = BinaryOp.BinOp.ADD;
            Icmp.CompareOp op2 = Icmp.CompareOp.LEQ;

            VariableTypeNode  lhsType = node.getLhs().getExprType();
            VariableTypeNode rhsType = node.getRhs().getExprType();
            boolean lhsIsString =false, rhsIsString = false;

            if(lhsType instanceof ClassTypeNode &&
            ((ClassTypeNode) lhsType).getReferenceClassName().equals("string"))
                lhsIsString = true;

            if(rhsType instanceof ClassTypeNode &&
                    (((ClassTypeNode) rhsType).getReferenceClassName().equals("string")))
                rhsIsString = true;

            if(node.getLhs() instanceof StringNode)
                lhsIsString = true;
            if (node.getRhs() instanceof StringNode)
                rhsIsString = true;

            if (lhs.getType() instanceof IntIRType && rhs.getType() instanceof IntIRType) {
                switch (node.getOp()) {
                    case ADD:
                        name = "add";
                        op1 = BinaryOp.BinOp.ADD;
                        break;
                    case SUB:
                        name = "sub";
                        op1 = BinaryOp.BinOp.SUB;
                        break;
                    case MOD:
                        name = "mod";
                        op1 = BinaryOp.BinOp.MOD;
                        break;
                    case MUL:
                        name = "mul";
                        op1 = BinaryOp.BinOp.MUL;
                        break;
                    case DIV:
                        name = "div";
                        op1 = BinaryOp.BinOp.DIV;
                        break;
                    case LSHIFT:
                        name = "lshift";
                        op1 = BinaryOp.BinOp.SAL;
                        break;
                    case RSHIFT:
                        name = "rshift";
                        op1 = BinaryOp.BinOp.SAR;
                        break;
                    case AND:
                        name = "and";
                        op1 = BinaryOp.BinOp.AND;
                        break;
                    case OR:
                        name = "or";
                        op1 = BinaryOp.BinOp.OR;
                        break;
                    case XOR:
                        name = "xor";
                        op1 = BinaryOp.BinOp.XOR;
                        break;

                    case NOTEQUAL:
                        cmp = true;
                        name = "noteq";
                        op2 = Icmp.CompareOp.NOTEQUAL;
                        break;
                    case EQUAL:
                        cmp = true;
                        name = "equal";
                        op2 = Icmp.CompareOp.EQUAL;
                        break;
                    case LT:
                        cmp = true;
                        name = "lt";
                        op2 = Icmp.CompareOp.LESS;
                        break;
                    case LE:
                        cmp = true;
                        name = "le";
                        op2 = Icmp.CompareOp.LEQ;
                        break;
                    case GT:
                        cmp = true;
                        name = "gt";
                        op2 = Icmp.CompareOp.GREATER;
                        break;
                    case GE:
                        cmp = true;
                        name = "ge";
                        op2 = Icmp.CompareOp.GEQ;
                        break;
                    default:
                        throw new RuntimeException("binaryOp fault");
                }
                if (cmp) {
                    res = new VirtualReg(name, new IntIRType(IntIRType.intType.i1));
                    inst = new Icmp(currentBB, res, op2, lhs, rhs);
                } else {
                    res = new VirtualReg(name, new IntIRType(IntIRType.intType.i32));
                    inst = new BinaryOp(currentBB, res, op1, lhs, rhs);
                }

                node.setResult(res);
                node.setAddr(null);
                currentBB.addInst(inst);
                currentFunction.getSymbolTable().put(res.getName(), res);


            } else if (lhsIsString && rhsIsString){
                IRFunction function;
                String funcName;
                switch (node.getOp()) {
                    case GE:
                        funcName = "ge";
                        break;
                    case LE:
                        funcName = "le";
                        break;
                    case LT:
                        funcName = "lt";
                        break;
                    case GT:
                        funcName = "gt";
                        break;
                    case EQUAL:
                        funcName = "equal";
                        break;
                    case NOTEQUAL:
                        funcName = "notequal";
                        break;
                    case ADD:
                        funcName = "link";
                        break;
                    default:
                        throw new RuntimeException("string_binaryOp error");

                }
                function = program.getFunction("__string_" + funcName);
                ArrayList<Operand> parameters = new ArrayList<>();
                parameters.add(lhs);
                parameters.add(rhs);

                res = new VirtualReg(
                        funcName, new PtrType(new IntIRType(IntIRType.intType.i8))
                );
                inst = new CallFunction("call_string_"+funcName,currentBB,function,parameters,res);
                node.setResult(res);
                node.setAddr(null);
                currentBB.addInst(inst);
                currentFunction.getSymbolTable().put(res.getName(), res);
            }
            else if((lhs instanceof ConstNull) && (rhs instanceof ConstNull)){
               node.setResult(new ConstBool(true));
               node.setAddr(null);

            }  else{
                switch (node.getOp()){
                    case EQUAL:
                        op2 = Icmp.CompareOp.EQUAL;
                        name = "eq";
                        break;
                    case NOTEQUAL:
                        op2 = Icmp.CompareOp.NOTEQUAL;
                        name  = "neq";
                        break;
                    default:
                        throw new RuntimeException();
                }
                res = new VirtualReg(name, new IntIRType(IntIRType.intType.i1));
                inst = new Icmp(currentBB, res, op2, lhs, rhs);


                node.setResult(res);
                node.setAddr(null);
                currentBB.addInst(inst);
                currentFunction.getSymbolTable().put(res.getName(), res);
            }

        }

        else {
            //short-circuit evaluation
            if (node.getOp() == LOR) {

                node.getLhs().accept(this);
                Operand lhs = node.getLhs().getResult();

                BasicBlock branchBB = new BasicBlock("lorBranchBB", currentFunction);
                BasicBlock mergeBB = new BasicBlock("lorMergeBB", currentFunction);
                BasicBlock phi1;
                BasicBlock phi2;

                currentBB.addInst(new BranchJump("br_br_merge", currentBB, lhs, mergeBB, branchBB));
                phi1 = currentBB;

                currentBB = branchBB;
                node.getRhs().accept(this);
                Operand rhs = node.getRhs().getResult();
                currentBB.addInst(
                        new BranchJump("jump_merge", currentBB, null, mergeBB, null)
                );
                currentFunction.addBB(currentBB);
                phi2 = currentBB;

                currentBB = mergeBB;
                Register res = new VirtualReg("lor", new IntIRType(IntIRType.intType.i1));
                Set<Pair<Operand, BasicBlock>> branches = new HashSet<Pair<Operand, BasicBlock>>();
                branches.add(new Pair<>(new ConstBool(true), phi1));
                branches.add(new Pair<>(rhs, phi2));
                currentBB.addInst(new Phi("lor_phi", currentBB, branches, res));
                currentFunction.addBB(currentBB);

                node.setResult(res);
                node.setAddr(null);
                currentFunction.getSymbolTable().put(res.getName(), res);

            }

            else if(node.getOp() == LAND) {
                    node.getLhs().accept(this);
                    Operand lhs = node.getLhs().getResult();

                    BasicBlock branchBB = new BasicBlock("landBranchBB", currentFunction);
                    BasicBlock mergeBB = new BasicBlock("landMergeBB", currentFunction);
                    BasicBlock phi1;
                    BasicBlock phi2;

                    currentBB.addInst(new BranchJump("br_br_merge", currentBB, lhs, branchBB, mergeBB));
                    phi1 = currentBB;

                    currentBB = branchBB;
                    node.getRhs().accept(this);
                    Operand rhs = node.getRhs().getResult();
                    currentBB.addInst(
                            new BranchJump("jump_merge", currentBB, null, mergeBB, null)
                    );
                    currentFunction.addBB(currentBB);
                    phi2 = currentBB;

                    currentBB = mergeBB;
                    Register res = new VirtualReg("land", new IntIRType(IntIRType.intType.i1));
                    Set<Pair<Operand, BasicBlock>> branches = new HashSet<Pair<Operand, BasicBlock>>();
                    branches.add(new Pair<>(new ConstBool(false), phi1));
                    branches.add(new Pair<>(rhs, phi2));
                    currentBB.addInst(new Phi("land_phi", currentBB, branches, res));
                    currentFunction.addBB(currentBB);

                    node.setResult(res);
                    node.setAddr(null);
                    currentFunction.getSymbolTable().put(res.getName(), res);


            }
                else
                    throw new RuntimeException("short-circuit error");

        }
    }

    @Override
    public void  visit(VarDefListNode node) throws Exception{
        for (VarDefNode item: node.getVarDefNodeList()){
            visit(item);
        }
    }

    @Override
    public void  visit(VarDefNode node) throws Exception {
        //alloca + store initial value
        IRType type = program.getTypeTable().transport(node.getVarType());
        String name  =  node.getVarName();

        if(currentClass == null && currentFunction == initialize) { // global
            StaticVar globalVar = new StaticVar(name, type,null);
            Operand init;
            if(node.getInitVal() != null) {
                isleft = true;
                curArrayHead = globalVar;
                node.getInitVal().accept(this);
                curArrayHead = null;
                isleft = false;
                init = node.getInitVal().getResult();
                if(type instanceof IntIRType) {
                    if (init instanceof Constant) {
                        globalVar.setInit(init);
                    } else {
                        globalVar.setInit(new ConstInt(0, ((IntIRType) type).getIntType()));
                        currentBB.addInst(new Store("storeInit1" + name, currentBB, init, globalVar));
                    }
                }
                else {
                    globalVar.setInit(new ConstNull());
                    currentBB.addInst(new Store("storeInit2" + name, currentBB, init, globalVar));
                }


            }
            else {//no initial
                globalVar.setInit(type.getDefaultValue());
            }

            program.addStaticVar(globalVar);

            currentFunction.getSymbolTable().put(globalVar.getName(),globalVar);
            identifierTable.put(node, globalVar);
        }

//        else if((node.getVarType() instanceof ClassTypeNode)){
//            //local  class def node
//            Register addr  = new VirtualReg(name,new PtrType(type));
//            BasicBlock entranceBlock = currentFunction.getEntranceBB();
//            entranceBlock.addFirstInst(new AllocateInst(entranceBlock,"alloca"+name,addr,type));
//            currentFunction.getSymbolTable().put(addr.getName(),addr);
//            identifierTable.put(node,addr);
//        }
        else{
                Register addr  = new VirtualReg(name,new PtrType(type));
                BasicBlock entranceBlock = currentFunction.getEntranceBB();
                entranceBlock.addFirstInst(new AllocateInst(entranceBlock,"alloca"+name,addr,type));

                if(node.getInitVal() != null) {
                    node.getInitVal().accept(this);
                    Operand init = node.getInitVal().getResult();
                    currentBB.addInst(new Store("storeInit3" + name,currentBB,init,addr));

                }

                currentFunction.getSymbolTable().put(addr.getName(),addr);
                identifierTable.put(node,addr);

        }
    }


    @Override
    public void  visit(IndexAccessNode node) throws Exception {
        if(isleft) {
            isleft = false;
            node.getCaller().accept(this);
            node.getIndex().accept(this);
            isleft = true;
        } else {
            node.getCaller().accept(this);
            node.getIndex().accept(this);
        }

        Operand arrayPtr = node.getCaller().getResult();
        ArrayList<Operand> index = new ArrayList<>();

        index.add(node.getIndex().getResult());
        Register result = new VirtualReg("Array$",arrayPtr.getType());
        currentFunction.getSymbolTable().put(result.getName(),result);
        currentBB.addInst(new GetPtr("getptr",currentBB,arrayPtr,index,result));


        Register load = new VirtualReg("arrayLoad",((PtrType)arrayPtr.getType()).getPointerType());
        currentBB.addInst(new Load("load",currentBB,((PtrType)arrayPtr.getType()).getPointerType(),result,load));


        currentFunction.getSymbolTable().put(load.getName(),load);

        node.setResult(load);
        node.setAddr(result);

        //leftValue

    }

    @Override
    public void  visit(MemberAccessNode node) throws Exception {
        VariableTypeNode  type = node.getCaller().getExprType();
        node.getCaller().accept(this);

        if(type instanceof ArrayTypeNode) {
            Register ptr;

            Operand caller = node.getCaller().getResult();
            if(caller instanceof Register &&//TODO caller instance of global?
                    caller.getType().equals(new PtrType(new IntIRType(IntIRType.intType.i32)))) {
                ptr = (Register)caller;
            }
            else {
                ptr = new VirtualReg("cast",new PtrType( new IntIRType(IntIRType.intType.i32)));
                currentBB.addInst(new BitCast("castPtr",currentBB,caller,ptr.getType(),ptr));
                currentFunction.getSymbolTable().put(ptr.getName(),ptr);
            }

            ArrayList<Operand> index = new ArrayList<>();
            index.add(new ConstInt(-1, IntIRType.intType.i32));
            Register res = new VirtualReg("size_ptr",ptr.getType());
            Register size =  new VirtualReg("size", new IntIRType(IntIRType.intType.i32));
            currentFunction.getSymbolTable().put(res.getName(),res);
            currentFunction.getSymbolTable().put(size.getName(),size);
            currentBB.addInst(new GetPtr("getArrayPtr",currentBB,ptr,index,res));
            currentBB.addInst(new Load("loadSize",currentBB,new IntIRType(IntIRType.intType.i32),res,size));

            node.setResult(size);
            node.setAddr(null);

            return;
        }
        else if((type instanceof ClassTypeNode) && ((ClassTypeNode) type).getReferenceClassName().equals("string")){
            assert node.getMember() instanceof FunctionCallNode;
            IRFunction function = program.getExternalFuncMap().get("__string_" + ((FunctionCallNode) node.getMember()).getCaller().getReferenceName());
            ArrayList<Operand> paras = new ArrayList<>();
            IRType retType = function.getFunctionType().getReturnType();
            paras.add(node.getCaller().getResult());
            for (ExprStaNode item: ((FunctionCallNode) node.getMember()).getActualParameterList()) {
                item.accept(this);
                paras.add(item.getResult());
            }
            Register res  = retType instanceof VoidType? null: new VirtualReg("call",retType);
            currentBB.addInst(new CallFunction(
                    "call"+function.getName(),currentBB,function,paras,res
            ));
            if(res != null)
                currentFunction.getSymbolTable().put(res.getName(),res);
            node.setResult(res);
            node.setAddr(null);
            return;
        }

        //pure class member access
        assert type instanceof ClassTypeNode ;
        IRType irType  =  program.getTypeTable().get(((ClassTypeNode) type).getReferenceClassName());
        assert irType instanceof ClassIRType;
        Operand pointer = node.getCaller().getResult();

        if(node.getMember() instanceof ReferenceNode) {

            assert ((ReferenceNode) node.getCaller()).getReferenceType() == ReferenceNode.ReferenceType.CLASS;


            int byteIndex = ((ClassIRType) irType).getIndex(((ReferenceNode) node.getMember()).getReferenceName());

            ArrayList<Operand> index = new ArrayList<>();
            index.add(new ConstInt(0, IntIRType.intType.i32));
            index.add(new ConstInt(byteIndex, IntIRType.intType.i32));
            IRType memberType = ((ClassIRType) irType).getMemberList().get(byteIndex);
            Register res = new VirtualReg(
                    ((ClassIRType) irType).getName()+ "." + ((ReferenceNode) node.getMember()).getReferenceName() + "$addr",
                    new PtrType(memberType));
            Register load = new VirtualReg(
                    ((ClassIRType) irType).getName()+ "." + ((ReferenceNode) node.getMember()).getReferenceName(),
                    memberType);
            currentFunction.getSymbolTable().put(load.getName(),load);
            currentFunction.getSymbolTable().put(res.getName(),res);

            Instruction getPtr = new GetPtr("getptr",currentBB,pointer,index,res);

            currentBB.addInst(getPtr);

            Instruction loadI = new Load("load_member",currentBB,memberType,res,load);
            currentBB.addInst(loadI);
            node.setResult(load);
            node.setAddr(res);


        }
        else {//member  is functionnode
            assert node.getMember() instanceof FunctionCallNode;

            String funcName =
                    ((ClassTypeNode) type).getReferenceClassName() + '_' +
                            ((FunctionCallNode) node.getMember()).getCaller().getReferenceName();

            IRFunction function = program.getFunction(funcName);
            assert funcName != null;
            ArrayList<Operand> parameters = new ArrayList<>();
            IRType retType = function.getFunctionType().getReturnType();
            Register retReg;
            if(retType instanceof VoidType) {
                retReg =  null;
            } else {
                retReg = new VirtualReg("call",retType);
            }
            parameters.add(pointer);
            for (ExprStaNode parameter: ((FunctionCallNode) node.getMember()).getActualParameterList()) {
                parameter.accept(this);
                parameters.add(parameter.getResult());
            }
            Instruction call = new CallFunction("call" + funcName,currentBB,function
                    ,parameters,retReg);

            currentBB.addInst(call);

            if(retReg != null)
                currentFunction.getSymbolTable().put(retReg.getName(),retReg);

            node.setResult(retReg);
            node.setAddr(null);

        }

    }


    public void  visit(FunctionCallNode node) throws Exception {
        IRFunction function = program.getFunction(node.getCaller().getReferenceName());


        ArrayList<Operand> parameter = new ArrayList<>();
        if(function == null) {//this.method()
            assert currentClass != null;
            function = program.getFunction(currentClass.getClassName()+ "_" + node.getCaller().getReferenceName());

            Operand allocaAddr = (Operand)currentFunction.getSymbolTable().get("this$addr");
            IRType base = ((PtrType)allocaAddr.getType()).getPointerType();
            Register thisPtr = new VirtualReg("thisPtr",base);
            currentFunction.getSymbolTable().put(thisPtr.getName(),thisPtr);
            currentBB.addInst(new Load("loadThis",currentBB,allocaAddr,thisPtr));
            parameter.add(thisPtr);
        }
        assert function != null;

        for(ExprStaNode item: node.getActualParameterList()) {
            item.accept(this);
            parameter.add(item.getResult());
        }

        Register res;
        if (function.getFunctionType().getReturnType() instanceof VoidType)
            res = null;
        else {
            res = new VirtualReg("ret", function.getFunctionType().getReturnType());
            currentFunction.getSymbolTable().put(res.getName(), res);
        }

        currentBB.addInst(new CallFunction("call" + function.getName(), currentBB, function
                , parameter, res));
        node.setAddr(null);
        node.setResult(res);


    }

    @Override
    public void  visit(NewExprNode node) throws Exception {
        VariableTypeNode astType = node.getVariableType();

        if(astType instanceof ArrayTypeNode) {
            newArrayMalloc(node);

        } else if (astType instanceof ClassTypeNode) {

           IRType type = new PtrType(program.getTypeTable().get(((ClassTypeNode) astType).getReferenceClassName()));
           IRFunction  function  = program.getFunction("malloc");
           int size = ((PtrType)type).getPointerType().getByteWidth();
           ArrayList<Operand> parameters = new ArrayList<>();
           parameters.add(new ConstInt(size, IntIRType.intType.i32));

           Register res = new VirtualReg("malloc", new PtrType(new IntIRType(IntIRType.intType.i8)));
           Register bitcast = new VirtualReg("bitcast", type);
           currentBB.addInst(new CallFunction(
                   "call_malloc",currentBB,function,parameters,res
           ));

           currentBB.addInst(new BitCast("caseClass",currentBB,res,type,bitcast));

           if(((ClassTypeNode) astType).getReferenceClass().getConstructionDefList().size() > 0) {
               function = program.getFunction(((ClassTypeNode) astType).getReferenceClassName() + "_" + ((ClassTypeNode) astType).getReferenceClassName());
               parameters =  new ArrayList<>();
               parameters.add(bitcast);
               //costructor donot have parameters
               currentBB.addInst(
                       new CallFunction("call_constructor",currentBB,function,parameters,null)
               );
           }

           node.setResult(bitcast);
           node.setAddr(null);
           currentFunction.getSymbolTable().put(res.getName(),res);
           currentFunction.getSymbolTable().put(bitcast.getName(),bitcast);
           identifierTable.put(node,res);//Class type save the addr
//end

        }
    }


    @Override
    public void  visit(VariableTypeNode node) throws Exception {
        //won't be called
    }

    @Override
    public void  visit(PrimitiveTypeNode node) throws Exception{
//won't be called
    }
    @Override
    public void  visit(ClassTypeNode node) throws Exception {
//won't be called
    }

    @Override
    public void  visit(ArrayTypeNode node) throws Exception {
        //won't be called
    }


    @Override
    public void  visit(ReferenceNode node) throws Exception {
        Operand allocaAddr = identifierTable.get(node.getDefinitionNode());
        if(allocaAddr == null) {
            allocaAddr = (Operand)currentFunction.getSymbolTable().get(node.getReferenceName() + "$addr");
        }
        if(allocaAddr != null) {//variables in scope
            IRType type;

//            if(allocaAddr instanceof StaticVar) {
//                type = allocaAddr.getType();
//            }
//            else {
                type =  ((PtrType)allocaAddr.getType()).getPointerType();
//            }

            Register res = new VirtualReg("",type);

                currentBB.addInst(
                        new Load("loadVar", currentBB, type, allocaAddr, res)
                );//may be res is useful also
                node.setResult(res);
                node.setAddr(allocaAddr);


            currentFunction.getSymbolTable().put(res.getName(),res);
        }
        else {//variables in class
            Register thisAddr = (Register)currentFunction.getSymbolTable().get("this$addr");
            assert thisAddr != null;

            IRType classType = ((PtrType)thisAddr.getType()).getPointerType();
            Register classVal = new VirtualReg("this",classType);
            currentFunction.getSymbolTable().put(classVal.getName(),classVal);
            currentBB.addInst(
                    new Load("loadThis",currentBB, thisAddr, classVal)
            );

            assert currentClass != null;
            int index = 0;

            while(!currentClass.getMemberList().get(index).getVarName().equals(node.getReferenceName())){
                index++;
                if(index == currentClass.getMemberList().size()){
                    throw new RuntimeException("such member does not exist");
                }
            }
            assert currentClass.getMemberList().get(index).getVarType().equalTo(node.getExprType());
            VariableTypeNode astMemberType = node.getExprType();
            ArrayList<Operand> iindex = new ArrayList<>();
            iindex.add(new ConstInt(0, IntIRType.intType.i32));
            iindex.add(new ConstInt(index, IntIRType.intType.i32));
            IRType memberType = program.getTypeTable().transport(astMemberType);

            Register dest = new VirtualReg("member_" + node.getReferenceName() + "$addr",new PtrType(memberType));
            currentFunction.getSymbolTable().put(dest.getName(),dest);

            currentBB.addInst(new GetPtr("get_"+node.getReferenceName()+"$addr",
                    currentBB,classVal,iindex,dest));

            Register result = new VirtualReg("load",memberType);
            currentFunction.getSymbolTable().put(result.getName(),result);
            currentBB.addInst(new Load(
                    "load_member",currentBB,dest,result
            ));

            node.setResult(result);
            node.setAddr(dest);
        }
    }





    @Override
    public void  visit(NullNode node) throws Exception {
        node.setAddr(null);
        node.setResult(new ConstNull());
    }
    @Override
    public void  visit(BoolNode node) throws Exception {
        node.setAddr(null);
        node.setResult(new ConstBool(node.getValue()));
    }
    @Override
    public void  visit(IntNode node) throws Exception {
        node.setResult(new ConstInt(node.getValue(), IntIRType.intType.i32));
        node.setAddr(null);
    }


    public void  visit(StringNode node) throws Exception {
        StaticVar var = program.addString(node.getVal());
        ArrayList<Operand> index = new ArrayList<>();
        index.add(new ConstInt(0, IntIRType.intType.i32));
        index.add(new ConstInt(0, IntIRType.intType.i32));
        Register res = new VirtualReg("stringPtr",new PtrType(new IntIRType(IntIRType.intType.i8)));
        currentBB.addInst(new GetPtr("getStringPtr",currentBB,var,index,res));
        currentFunction.getSymbolTable().put(res.getName(),res);
        node.setResult(res);
        node.setAddr(null);
    }
    @Override
    public void  visit(ThisExprNode node) throws Exception{
        Register thisAddr = (Register)currentFunction.getSymbolTable().get("this$addr");
        IRType type = ((PtrType)thisAddr.getType()).getPointerType();
        Register res = new VirtualReg("this",type);
        currentBB.addInst(new Load("loadptr",currentBB,type,thisAddr,res));

        node.setResult(res);
        node.setAddr(null);
        currentFunction.getSymbolTable().put(res.getName(),res);

    }

    private void newArrayMalloc(NewExprNode node) throws Exception{
//        VariableTypeNode type = (ArrayTypeNode)node.getVariableType();

        IRType irType = program.getTypeTable().transport(
                ((ArrayTypeNode) node.getVariableType()).getBaseType()
        );
        int dim = ((ArrayTypeNode) node.getVariableType()).getDim();

        ArrayList<Operand> sizeList = new ArrayList<>();//save backwards

        VariableTypeNode arrayType = node.getVariableType();
        //initialize
        for(int i = 0;i < dim; i++) {
            irType = new PtrType(irType);

            if(((ArrayTypeNode)arrayType).getSize() != null) {
                ((ArrayTypeNode) arrayType).getSize().accept(this);
                sizeList.add(((ArrayTypeNode) arrayType).getSize().getResult());
            }
            arrayType = ((ArrayTypeNode) arrayType).getInnerTypeNode();
//            type = ((ArrayTypeNode)type).getBaseType();
        }

        //recursive malloc

        IRFunction malloc =  program.getFunction("malloc");
        Operand mallocRes = recursiveMalloc(0 ,sizeList,irType,malloc);
        node.setResult(mallocRes);
        node.setAddr(null);

    }
    private Operand recursiveMalloc(int cnt, ArrayList<Operand> sizeList, IRType irType, IRFunction malloc) {
        Operand size = sizeList.get(cnt);
        ArrayList<Operand> parameters = new ArrayList<>();

        int baseSize = irType.getByteWidth();


        Register mulByte = new VirtualReg("mul",new IntIRType(IntIRType.intType.i32));
        Register bytes = new VirtualReg("add",  new IntIRType(IntIRType.intType.i32));

        currentFunction.getSymbolTable().put(mulByte.getName(),mulByte);
        currentFunction.getSymbolTable().put(bytes.getName(),bytes);


        Instruction mul = new BinaryOp(
                currentBB,mulByte, BinaryOp.BinOp.MUL,size,new ConstInt(baseSize, IntIRType.intType.i32)
        );

        Instruction add = new BinaryOp(
                currentBB,bytes, BinaryOp.BinOp.ADD,mulByte,new ConstInt(4, IntIRType.intType.i32)
        );


        currentBB.addInst(mul);
        currentBB.addInst(add);
        parameters.add(bytes);

        Register mallocRes  = new VirtualReg(
                "malloc", new PtrType(new IntIRType(IntIRType.intType.i8))
        );
        currentFunction.getSymbolTable().put(mallocRes.getName(),mallocRes);

        Instruction mallocCall = new CallFunction("malloc",currentBB,malloc,parameters, mallocRes);

        currentBB.addInst(mallocCall);

        Register malloc32 = new VirtualReg("mallocCast", new PtrType(new IntIRType(IntIRType.intType.i32)));
        currentFunction.getSymbolTable().put(malloc32.getName(),malloc32);

        Instruction bitCast = new BitCast("bitcast",currentBB,mallocRes,malloc32.getType(),malloc32);
        currentBB.addInst(bitCast);

        Instruction store = new Store("storeMalloc",currentBB,size,malloc32);
        currentBB.addInst(store);

        ArrayList<Operand> gepIndex = new ArrayList<>();
        gepIndex.add(new ConstInt(1, IntIRType.intType.i32));
        Register arrayHead32 =  new VirtualReg("arrayHead32",new PtrType(new IntIRType(IntIRType.intType.i32)));
        Instruction getHead = new GetPtr("getHeadPtr",currentBB,malloc32,gepIndex,arrayHead32);
        currentFunction.getSymbolTable().put(arrayHead32.getName(),arrayHead32);

        currentBB.addInst(getHead);

        Register arrayHead = new VirtualReg("arrayHead",irType);

        Instruction castHead = new BitCast("castArrayHead",currentBB,arrayHead32,irType,arrayHead);
        currentFunction.getSymbolTable().put(arrayHead.getName(),arrayHead);

        currentBB.addInst(castHead);

        if(cnt != sizeList.size() - 1) {
            BasicBlock for_condBB = new BasicBlock("for.cond",currentFunction);
            BasicBlock for_loopBB = new BasicBlock("for.loop",currentFunction);
//            BasicBlock for_incBB = new BasicBlock("for.inc",currentFunction);
            BasicBlock for_endBB = new BasicBlock("for.end",currentFunction);
            currentFunction.getSymbolTable().put(for_condBB.getName(),for_condBB);
            currentFunction.getSymbolTable().put(for_loopBB.getName(),for_loopBB);
//            currentFunction.getSymbolTable().put(for_incBB.getName(),for_incBB);
            currentFunction.getSymbolTable().put(for_endBB.getName(),for_endBB);
            ////preparation//////
            ArrayList<Operand> index = new ArrayList<>();
            index.add(size);
            Register arrayTail = new VirtualReg("arraytail",irType);
            currentFunction.getSymbolTable().put(arrayTail.getName(),arrayTail);
            Instruction getTail = new GetPtr("getTail",currentBB,arrayHead,index,arrayTail);
            currentBB.addInst(getTail);


            Register arrayPtrAddr = new VirtualReg("arrayPtr",new PtrType(irType));
            currentFunction.getSymbolTable().put(arrayPtrAddr.getName(),arrayPtrAddr);
            Instruction alloca =  new AllocateInst(currentFunction.getEntranceBB(),"allocaArray",arrayPtrAddr,irType);
            currentFunction.getEntranceBB().addFirstInst(alloca);

            Instruction storePtr = new Store("storePtr1",currentBB,arrayHead,arrayPtrAddr);
            currentBB.addInst(storePtr);

            currentBB.addInst(
                    new BranchJump("jump_loop",currentBB,null,for_condBB,null)
            );
            //////cond////////
            currentBB = for_condBB;
            currentFunction.addBB(for_condBB);
            Register loadPtr = new VirtualReg("load",irType);
            currentBB.addInst(new Load("loadPtr",currentBB, irType, arrayPtrAddr, loadPtr));
            Register cmp = new VirtualReg("cmp",new IntIRType(IntIRType.intType.i1));
            currentBB.addInst(new Icmp(currentBB,cmp, Icmp.CompareOp.LESS,loadPtr,arrayTail));
            currentFunction.getSymbolTable().put(loadPtr.getName(),loadPtr);
            currentFunction.getSymbolTable().put(cmp.getName(),cmp);

            currentBB.addInst(new BranchJump("br_end_loop",currentBB,cmp,for_loopBB,for_endBB));

            currentBB = for_loopBB;
            currentFunction.addBB(for_loopBB);
            Operand nextArrayHead = recursiveMalloc(cnt+1,sizeList,((PtrType)irType).getPointerType(),malloc);
            currentBB = for_loopBB;
            currentBB.addInst(new Store("storePtr2",currentBB,nextArrayHead,loadPtr));

            Register nextArray = new VirtualReg("nextArrayPtr",irType);
            currentFunction.getSymbolTable().put(nextArray.getName(),nextArray);
            index =  new ArrayList<>();
            index.add(new ConstInt(1, IntIRType.intType.i32));
            Instruction getNextPtr = new GetPtr("getNextPtr",currentBB,loadPtr,index,nextArray);
            currentBB.addInst(getNextPtr);

            currentBB.addInst(new Store("storePtr3",currentBB,nextArray,arrayPtrAddr));
            currentBB.addInst(new BranchJump("jump_cond",currentBB,null,for_condBB,null));

            currentBB = for_endBB;
            currentFunction.addBB(currentBB);

        }
        return arrayHead;
    }


    /////////getter and setter///////

    public Module getProgram() {
        return program;
    }

    public void setProgram(Module program) {
        this.program = program;
    }

    public GlobalScope getTopLevelScope() {
        return topLevelScope;
    }

    public void setTopLevelScope(GlobalScope topLevelScope) {
        this.topLevelScope = topLevelScope;
    }

    public BasicBlock getCurrentBB() {
        return currentBB;
    }

    public void setCurrentBB(BasicBlock currentBB) {
        this.currentBB = currentBB;
    }



    public IRFunction getCurrentFunction() {
        return currentFunction;
    }

    public void setCurrentFunction(IRFunction currentFunction) {
        this.currentFunction = currentFunction;
    }





    public IRFunction getInitialize() {
        return initialize;
    }

    public void setInitialize(IRFunction initialize) {
        this.initialize = initialize;
    }


    public Stack<BasicBlock> getLoopBreakBB() {
        return loopBreakBB;
    }

    public void setLoopBreakBB(Stack<BasicBlock> loopBreakBB) {
        this.loopBreakBB = loopBreakBB;
    }

    public Stack<BasicBlock> getLoopNextStepBB() {
        return loopNextStepBB;
    }

    public void setLoopNextStepBB(Stack<BasicBlock> loopNextStepBB) {
        this.loopNextStepBB = loopNextStepBB;
    }
}
