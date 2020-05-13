package BackEnd;

import BackEnd.Operands.StackOffset;
import BackEnd.Operands.VirtualReg;

import java.util.ArrayList;
import java.util.HashMap;

public class StackFrame {
    private RiscFunction function;

    int size;
    private HashMap<VirtualReg, StackOffset> temporaryVar;
    private ArrayList<StackOffset> formalPara;
    private HashMap<RiscFunction,ArrayList<StackOffset>> paras;

    public StackFrame(RiscFunction function) {
        this.function = function;
        size = 0;
        temporaryVar = new HashMap<>();
        formalPara = new ArrayList<>();
        paras =  new HashMap<>();
    }

    public void addFormalPara(StackOffset stackOffset){
        formalPara.add(stackOffset);
    }


    public RiscFunction getFunction() {
        return function;
    }

    public void setFunction(RiscFunction function) {
        this.function = function;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public HashMap<VirtualReg, StackOffset> getTemporaryVar() {
        return temporaryVar;
    }

    public ArrayList<StackOffset> getFormalPara() {
        return formalPara;
    }


    public HashMap<RiscFunction, ArrayList<StackOffset>> getParas() {
        return paras;
    }

}
