package BackEnd.Instruction;

import BackEnd.Operand.Immidiate;
import BackEnd.Operand.RiscOperand;
import BackEnd.Operand.RiscRegister;
import BackEnd.RiscBB;

public class ILoad extends RiscInstruction {
    private RiscRegister rd;
    private RiscOperand target;
    private Immidiate offset;

    public ILoad(RiscBB parentBB, RiscRegister rd, RiscOperand target, Immidiate offset) {
        super(parentBB);
        this.rd = rd;
        this.target = target;
        this.offset = offset;
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
