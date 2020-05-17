package BackEnd.Instruction;

import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

import java.util.HashSet;

public abstract class RiscInstruction {
    private RiscInstruction prev,next;
    protected RiscBB parentBB;
    private HashSet<RiscRegister> def, usages;

    public RiscInstruction(RiscBB parentBB) {
        this.parentBB = parentBB;
        def = new HashSet<>();
        usages = new HashSet<>();
    }

    public abstract void add();
    public void addDef(RiscRegister reg){
        def.add(reg);
        reg.setSpilledCost(reg.getSpilledCost() + 1);
    }
    public void addUse(RiscRegister reg){
        usages.add(reg);
        reg.setSpilledCost(reg.getSpilledCost() + 1);
    }
    public abstract String print();

    public void  deleteItself(){
        if(this.parentBB!= null){
            if(prev!=null){
                prev.next = next;
            }
            else parentBB.setHead(next);

            if(next != null){
                next.prev = prev;
            }
            else parentBB.setTail(prev);
        }

    }

    public abstract void replaceUse(RiscRegister old, RiscRegister newUse);
    public abstract void replaceDef(RiscRegister old, RiscRegister newDef);

    public RiscInstruction getPrev() {
        return prev;
    }

    public void setPrev(RiscInstruction prev) {
        this.prev = prev;
    }

    public RiscInstruction getNext() {
        return next;
    }

    public void setNext(RiscInstruction next) {
        this.next = next;
    }

    public RiscBB getParentBB() {
        return parentBB;
    }

    public void setParentBB(RiscBB parentBB) {
        this.parentBB = parentBB;
    }

    public HashSet<RiscRegister> getDef() {
        return def;
    }

    public void setDef(HashSet<RiscRegister> def) {
        this.def = def;
    }

    public HashSet<RiscRegister> getUsages() {
        return usages;
    }

    public void setUsages(HashSet<RiscRegister> usages) {
        this.usages = usages;
    }
}
