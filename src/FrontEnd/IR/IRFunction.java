package FrontEnd.IR;

import FrontEnd.AstNode.FunctionDefNode;
import FrontEnd.IR.Instruction.*;

import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Parameter;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Operand.VirtualReg;
import FrontEnd.IR.Type.FunctionType;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Type.PtrType;
import FrontEnd.IR.Type.VoidType;
import FrontEnd.IRVisitor;


import java.util.*;

public class IRFunction extends IRNode {

    private FunctionDefNode functionEntity;
    private FunctionType functionType;
    private Module parent;
    private BasicBlock entranceBB = null, exitBB = null;
    private ArrayList<IRType> paraList = new ArrayList<>();
    private HashSet<IRFunction> usedFunctions = new HashSet<>();
    //once  a register cannot be used any more(used as left value/ cannnot be left value), then integer = 1
    private SymbolTable symbolTable = new SymbolTable();


    private BasicBlock returnBB = new BasicBlock("return", null);
    private Register returnVal = null;


    private boolean external;


    public IRFunction(String name, Module parent,
                      ArrayList<Parameter> paraList, IRType returnType,boolean isExternal) {
        super(name);
        this.parent = parent;
        for (Parameter item : paraList) {
            this.paraList.add(item.getType());
            item.setFuncParent(this);
        }
        //need  to create  a paralist  or not
        functionType = new FunctionType(returnType, this.paraList);
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

    public void addBB(String name) {
        BasicBlock newbb = new BasicBlock(name, this);
        addBB(newbb);
        symbolTable.put(name, newbb);
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
            returnVal.setParent(alloca);
            entranceBB.addInst(new Store("storeRegturnVal", entranceBB, returnType.getDefaultValue(), returnVal));

            //quit
            Register loadReg = new VirtualReg("ret", returnType);
            returnBB.addInst(new Load("load", returnBB, returnType, returnVal, loadReg));
            returnBB.addInst(new Return("ret", returnBB, returnType, loadReg));

            symbolTable.put(returnVal.getName(), returnVal);
            symbolTable.put(loadReg.getName(), loadReg);

            ////how can I save the register information !!!!!
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

    public void setParaList(ArrayList<IRType> paraList) {
        this.paraList = paraList;
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

    public ArrayList<IRType> getParaList() {
        return paraList;
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


}
