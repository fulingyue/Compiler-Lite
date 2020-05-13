package FrontEnd.IR.Operand;

import BackEnd.Operands.RiscRegister;
import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Type.IRType;

import java.util.HashSet;
import java.util.LinkedList;

public abstract class Register extends Operand {
    private Instruction defInst;//strict prior definition

    private RiscRegister riscRegister = null;

    public RiscRegister getRiscRegister() {
        return riscRegister;
    }

    public void setRiscRegister(RiscRegister riscRegister) {
        this.riscRegister = riscRegister;
    }

    public abstract String print();

    public Register(String name, IRType type, Instruction parent) {
        super(name,type,parent);
    }

    public Register(String name, IRType type) {
        super(name,type);
    }
//    public abstract RegVal copy();

    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {
        if(!alive.contains(defInst)){
            alive.add(defInst);
            workList.add(defInst);
        }
    };
    public Instruction getDefInst() {
        return defInst;
    }

    public void setDefInst(Instruction defInst) {
        this.defInst = defInst;
    }
}
