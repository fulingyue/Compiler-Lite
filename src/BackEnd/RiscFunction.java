package BackEnd;

import BackEnd.Operands.VirtualReg;
import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;

import java.util.ArrayList;
import java.util.HashSet;

public class RiscFunction {
    public String name,label;
    private ArrayList<RiscBB> blocks;
    private RiscBB entranceBB, exitBB;
    private int paraNum;
    private HashSet<VirtualReg> virtualSet = new HashSet<>();
    private IRFunction function;
    private StackFrame stackFrame=null;


    static int cnt =0;

    public RiscFunction(String name, int paraNum, IRFunction function) {
        this.name = name;
        this.function = function;
        if(function == null) return;

        this.paraNum = paraNum;
        blocks = new ArrayList<>();
        int bbcnt=0;
        for(BasicBlock bb=function.getEntranceBB();bb != null;bb=bb.getNextBB()){
            RiscBB riscBB=new RiscBB(bb.getName(),".LBB"+cnt+"_"+bbcnt,this,bb);
            this.addBB(riscBB);
            bb.setRiscBB(riscBB);
            bbcnt++;
        }

        for(BasicBlock bb=function.getEntranceBB();bb != null;bb=bb.getNextBB()){
            RiscBB riscBB = bb.getRiscBB();
            for(BasicBlock pree:bb.getPredecessorBB())
                riscBB.addPre(pree.getRiscBB());
            for(BasicBlock succ:bb.getSuccessors())
                riscBB.addSucc(succ.getRiscBB());
        }
        cnt++;

    }


    public VirtualReg addRegister(String name){
        VirtualReg reg = new VirtualReg(name);
        virtualSet.add(reg);
        return reg;
    }

    public void addBB(RiscBB bb) {

        if(blocks.size() == 0) entranceBB = bb;
        blocks.add(bb);
        exitBB = blocks.get(blocks.size() -1);
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public HashSet<VirtualReg> getVirtualSet() {
        return virtualSet;
    }

    public void setVirtualSet(HashSet<VirtualReg> virtualSet) {
        this.virtualSet = virtualSet;
    }

    public static int getCnt() {
        return cnt;
    }

    public static void setCnt(int cnt) {
        RiscFunction.cnt = cnt;
    }
}
