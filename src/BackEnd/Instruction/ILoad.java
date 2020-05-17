package BackEnd.Instruction;

import BackEnd.Operands.Immidiate;
import BackEnd.Operands.RiscOperand;
import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

public class ILoad extends RiscInstruction {

    public enum LoadType{
        lb,lw
    }
    private RiscRegister rd;
    private RiscOperand target;//address
    private Immidiate offset;
    private LoadType op;


    public ILoad(RiscBB parentBB, RiscRegister rd, RiscOperand target, Immidiate offset,LoadType op) {
        super(parentBB);
        this.rd = rd;
        this.target = target;
        this.offset = offset;
        this.op = op;
    }

    public ILoad(RiscBB parentBB, RiscRegister rd, RiscOperand target, LoadType op) {
        super(parentBB);
        this.rd = rd;
        this.target = target;
        this.offset = new Immidiate(0);
        this.op = op;
    }

    @Override
    public void add() {
        addDef(rd);
        rd.addDef(this);
        if(target instanceof RiscRegister){
            addUse((RiscRegister)target);
            ((RiscRegister) target).addUse(this);
        }
    }


    @Override
    public String print() {
        return "\t" + op.name() + "\t" + rd.print() + ", " + target.print();
    }

    @Override
    public void replaceUse(RiscRegister old, RiscRegister newUse) {
        if(target instanceof RiscRegister && old == target){
            old.getUse().remove(this);
            getUsages().remove(old);
            target = newUse;
            newUse.addUse(this);
            addUse(newUse);
        }
    }

    @Override
    public void replaceDef(RiscRegister old, RiscRegister newDef) {
        if(old == rd){
            old.getDef().remove(this);
            getDef().remove(old);
            rd = newDef;
            newDef.addDef(this);
            addDef(newDef);
        }
    }


    public RiscRegister getRd() {
        return rd;
    }

    public void setRd(RiscRegister rd) {
        this.rd = rd;
    }

    public RiscOperand gettarget() {
        return target;
    }

    public void settarget(RiscOperand target) {
        this.target = target;
    }

    public Immidiate getOffset() {
        return offset;
    }

    public void setOffset(Immidiate offset) {
        this.offset = offset;
    }
}
