package BackEnd;

import BackEnd.Operands.RiscRegister;
import BackEnd.Operands.VirtualReg;
import FrontEnd.IR.IRFunction;

import java.util.ArrayList;
import java.util.HashSet;

public class RiscFunction {
    public String name;
    private ArrayList<RiscBB> blocks;
    private RiscBB entranceBB, exitBB;
    private int paraNum;
    private ArrayList<RiscRegister> registerList;
    IRFunction function;
    StackFrame stackFrame;



    public RiscFunction(String name, int paraNum, IRFunction function) {
        this.name = name;
        this.paraNum = paraNum;
        blocks = new ArrayList<>();
        registerList = new ArrayList<>();
        this.function = function;

    }

    public VirtualReg addRegister(String name){
        VirtualReg reg = new VirtualReg(name);
        registerList.add(reg);
        return reg;
    }

    public void addBB(RiscBB bb) {
        blocks.add(bb);
    }

    public ArrayList<RiscBB> getDfs(){
        ArrayList<RiscBB> dfs = new ArrayList<>();
        HashSet<RiscBB> visited = new HashSet<>();
        entranceBB.dfs(dfs,visited);
        return dfs;
    }

    public IRFunction getFunction() {
        return function;
    }

    public void setFunction(IRFunction function) {
        this.function = function;
    }

    public StackFrame getStackFrame() {
        return stackFrame;
    }

    public void setStackFrame(StackFrame stackFrame) {
        this.stackFrame = stackFrame;
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
