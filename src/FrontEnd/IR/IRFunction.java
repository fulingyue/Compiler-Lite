package FrontEnd.IR;

import BackEnd.RiscFunction;
import FrontEnd.AstNode.FunctionDefNode;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Operand.Parameter;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Operand.VirtualReg;
import FrontEnd.IR.Type.FunctionType;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Type.PtrType;
import FrontEnd.IR.Type.VoidType;
import FrontEnd.IRVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class IRFunction extends IRNode {

    private FunctionDefNode functionEntity;
    private FunctionType functionType;
    private Module parent;
    private BasicBlock entranceBB = null, exitBB = null;
    private ArrayList<Parameter> paraList = new ArrayList<>();
    private HashSet<IRFunction> usedFunctions = new LinkedHashSet<>();
    //once  a register cannot be used any more(used as left value/ cannnot be left value), then integer = 1
    private SymbolTable symbolTable = new SymbolTable();


    private BasicBlock returnBB = new BasicBlock("return", null);
    private Register returnVal = null;

    private boolean external;
    private boolean sideEffect;
    //////backend/////
    private RiscFunction riscFunction;

    public RiscFunction getRiscFunction() {
        return riscFunction;
    }

    public void setRiscFunction(RiscFunction riscFunction) {
        this.riscFunction = riscFunction;
    }

    //////////dfs///////
    private ArrayList<BasicBlock> dfsOrder = null;
    private HashSet<BasicBlock> visitedBB =  null;


    public IRFunction(String name, Module parent,
                      ArrayList<Parameter> paraList, IRType returnType,boolean isExternal) {
        super(name);
        this.parent = parent;
        this.paraList = paraList;
        //need  to create  a paralist  or not
        functionType = new FunctionType(returnType, this.paraList);
        sideEffect = true;
    }

    //    public usedVars(Func)
    public void updateRetBB() {

    }

    public void addBB(BasicBlock bb) {
        if (entranceBB == null) {
            entranceBB = bb;
        } else {
            bb.setPrevBB(exitBB);
            exitBB.setNextBB(bb);
        }
        exitBB = bb;
        bb.setParent(this);

        symbolTable.put(bb.getName(), bb);
        for (Instruction i = bb.getHead(); i != null; i = i.getNxt()) {
            symbolTable.put(i.getName(), i);
        }
    }

    public LinkedList<BasicBlock> getBlocks(){
        LinkedList<BasicBlock>  res = new LinkedList<>();
        for(BasicBlock bb = entranceBB; bb != null; bb = bb.getNextBB()){
            res.add(bb);
        }
        return res;
    }

    public void init() {
        BasicBlock entranceBlock = new BasicBlock("entranceBlock", this);
        addBB(entranceBlock);
        returnBB = new BasicBlock("returnBlock", this);
//        symbolTable.put(entranceBlock.getName(),entranceBlock);
//        symbolTable.put(returnBB.getName(),returnBB);

        IRType returnType = functionType.getReturnType();//for store inst
        if (returnType instanceof VoidType) {
            returnBB.addInst(new Return("ret", returnBB, new VoidType(), null));
        } else {
            //entry
            returnVal = new VirtualReg("retval", new PtrType(returnType));
            AllocateInst alloca = new AllocateInst(entranceBB, "retValAlloca", returnVal, returnType);
            entranceBB.addInst(alloca);
            entranceBB.addInst(new Store("storeRegturnVal", entranceBB, returnType.getDefaultValue(), returnVal));

            //quit
            Register loadReg = new VirtualReg("ret", returnType);
            returnBB.addInst(new Load("load", returnBB, returnType, returnVal, loadReg));
            returnBB.addInst(new Return("ret", returnBB, returnType, loadReg));

            symbolTable.put(returnVal.getName(), returnVal);
            symbolTable.put(loadReg.getName(), loadReg);

        }

    }

    //    public void setReturn() {
//        this.returnLink =  false;
//        addBB("entry");
//        if(type.getReturnType() instanceof VoidType){
//            returnBB.addInst(new Return(returnBB, null));
//        } else {
//            returnVal = new AllocateInst(returnBB, name, )
//        }
//    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public String printDef() {
        StringBuilder stringBuilder = new StringBuilder("define ");
        stringBuilder.append(functionType.getReturnType().print()).append(" @").append(name);
        stringBuilder.append("(");

        if(paraList.size() == 0)
            stringBuilder.append(", ");
        for(int i = 0;i < paraList.size();++i) {
            Parameter para = paraList.get(i);
            stringBuilder.append(para.getType().print()).append(" ");
            stringBuilder.append(para.print()).append(", ");
        }
        stringBuilder.delete(stringBuilder.length()-2,stringBuilder.length());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public String printDeclare() {
        StringBuilder stringBuilder = new StringBuilder("declare ");
        stringBuilder.append(functionType.getReturnType().print()).append(" @").append(name).append("(");
        for (int i =0;i < paraList.size();++i) {
            Parameter parameter = paraList.get(i);
            stringBuilder.append(parameter.getType().print()).append(" ").append(parameter.print());
            if(i != paraList.size() -1)
                stringBuilder.append(", ");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }


    public ArrayList<AllocateInst> getAllocaInst() {
        ArrayList<AllocateInst> ret = new ArrayList<>();
        Instruction nowInst = entranceBB.getHead();
        while(nowInst !=  null) {
            if(nowInst instanceof AllocateInst){
                    ret.add((AllocateInst) nowInst);
            }

            nowInst = nowInst.getNxt();
        }
        return ret;
    }

    public ArrayList<BasicBlock> gettDfsOder() {
        dfsOrder = new ArrayList<>();
        visitedBB = new LinkedHashSet<>();
        entranceBB.setDfsFather(null);
        dfs(entranceBB);
        return dfsOrder;
    }

    private void dfs(BasicBlock bb) {
        visitedBB.add(bb);
        bb.setDfsOrd(dfsOrder.size());
        dfsOrder.add(bb);

        for(BasicBlock item: bb.getSuccessors()) {
            if(!visitedBB.contains(item)) {
                item.setDfsFather(bb);
                dfs(item);
            }
        }

    }
    public void calcSideEffect(){

        for(BasicBlock bb = entranceBB;bb != null; bb = bb.getNextBB()){
            if(bb.getSideEffect()) {
                sideEffect = true;
                return;
            }
        }
        sideEffect = false;
    }
    //////getter and  setter///////

    public FunctionDefNode getFunctionEntity() {
        return functionEntity;
    }

    public void setFunctionEntity(FunctionDefNode functionEntity) {
        this.functionEntity = functionEntity;
    }

    public FunctionType getFunctionType() {
        return functionType;
    }

    public void setFunctionType(FunctionType functionType) {
        this.functionType = functionType;
    }

    public Module getParent() {
        return parent;
    }

    public void setParent(Module parent) {
        this.parent = parent;
    }

    public void setParaList(ArrayList<Parameter> paraList) {
        this.paraList = paraList;
    }

    public ArrayList<Parameter> getParaList() {
        return paraList;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public BasicBlock getReturnBB() {
        return returnBB;
    }

    public void setReturnBB(BasicBlock returnBB) {
        this.returnBB = returnBB;
    }

    public Register getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(Register returnVal) {
        this.returnVal = returnVal;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public BasicBlock getEntranceBB() {
        return entranceBB;
    }

    public void setEntranceBB(BasicBlock entranceBB) {
        this.entranceBB = entranceBB;
    }

    public BasicBlock getExitBB() {
        return exitBB;
    }

    public void setExitBB(BasicBlock exitBB) {
        this.exitBB = exitBB;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public HashSet<IRFunction> getUsedFunctions() {
        return usedFunctions;
    }

    public void setUsedFunctions(HashSet<IRFunction> usedFunctions) {
        this.usedFunctions = usedFunctions;
    }


    public boolean isSideEffect() {
        return sideEffect;
    }

    public void setSideEffect(boolean sideEffect) {
        this.sideEffect = sideEffect;
    }




    public HashSet<BasicBlock> getVisitedBB() {
        return visitedBB;
    }

    public void setVisitedBB(HashSet<BasicBlock> visitedBB) {
        this.visitedBB = visitedBB;
    }

    public boolean isNotFunctional(){
        int retCnt = 0;
        Return retInst = null;
        for (BasicBlock bb = entranceBB; bb != null; bb =  bb.getNextBB()){
            if(notEndWithTer(bb)){
                return true;
            }
            if(bb.getTail() instanceof Return){
                retInst = (Return)bb.getTail();
                retCnt ++;
            }
        }

        if(retCnt != 1)
            return true;

        BasicBlock bb = retInst.getBasicBlock();
        IRFunction function = bb.getParent();
        if(bb != function.getExitBB())
            moveExit(bb);
        return false;
    }

    private void moveExit(BasicBlock bb){
        if(bb.getPrevBB() == null){
            this.setEntranceBB(bb.getNextBB());
        } else
            bb.getPrevBB().setNextBB(bb.getNextBB());

        if(bb.getNextBB() == null)
            this.setExitBB(bb.getPrevBB());
        else bb.getNextBB().setPrevBB(bb.getPrevBB());

        bb.setPrevBB(null);
        bb.setNextBB(null);
        this.setExitBB(bb);
    }
    private boolean notEndWithTer(BasicBlock bb){
        return bb.getTail() == null || !bb.getTail().isTerminator();
    }
}
