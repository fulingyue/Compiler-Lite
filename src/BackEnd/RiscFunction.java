package BackEnd;

import BackEnd.Operand.RiscRegister;
import BackEnd.Operand.VirtualReg;
import FrontEnd.IR.IRFunction;

import java.util.ArrayList;

public class RiscFunction {
    public String name;
    private ArrayList<RiscBB> blocks;
    private RiscBB entranceBB, exitBB;
    private int paraNum;
    private ArrayList<RiscRegister> registerList;
    IRFunction function;
    private ArrayList<StackSlot> callStackSlots;
    int stackSize;


    public RiscFunction(String name, int paraNum, IRFunction function) {
        this.name = name;
        this.paraNum = paraNum;
        blocks = new ArrayList<>();
        registerList = new ArrayList<>();
        this.function = function;
        callStackSlots = new ArrayList<>();
        stackSize = 0;
    }

    public VirtualReg addRegister(String name){
        VirtualReg reg = new VirtualReg(name);
        registerList.add(reg);
        return reg;
    }

    public void addBB(RiscBB bb) {
        blocks.add(bb);
    }
    public void addStackSlot(StackSlot stackSlot){
        callStackSlots.add(stackSlot);
    }


    public IRFunction getFunction() {
        return function;
    }

    public void setFunction(IRFunction function) {
        this.function = function;
    }

    public ArrayList<StackSlot> getCallStackSlots() {
        return callStackSlots;
    }

    public void setCallStackSlots(ArrayList<StackSlot> callStackSlots) {
        this.callStackSlots = callStackSlots;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<RiscBB> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<RiscBB> blocks) {
        this.blocks = blocks;
    }

    public RiscBB getEntranceBB() {
        return entranceBB;
    }

    public void setEntranceBB(RiscBB entranceBB) {
        this.entranceBB = entranceBB;
    }

    public RiscBB getExitBB() {
        return exitBB;
    }

    public void setExitBB(RiscBB exitBB) {
        this.exitBB = exitBB;
    }

    public int getParaNum() {
        return paraNum;
    }

    public void setParaNum(int paraNum) {
        this.paraNum = paraNum;
    }

    public ArrayList<RiscRegister> getRegisterList() {
        return registerList;
    }

    public void setRegisterList(ArrayList<RiscRegister> registerList) {
        this.registerList = registerList;
    }
}
