package BackEnd.Instruction;

import BackEnd.Operand.Immidiate;
import BackEnd.Operand.RiscOperand;
import BackEnd.Operand.RiscRegister;
import BackEnd.RiscBB;

public class Stype extends RiscInstruction {
    private RiscRegister rd;
    private RiscOperand dest;
    private Immidiate offset;
    private BSize op;

    public enum BSize{
        sb,sw
    }


    public Stype(RiscBB parentBB, RiscRegister rd, RiscOperand dest, Immidiate offset, BSize op) {
        super(parentBB);
        this.rd = rd;
        this.dest = dest;
        this.offset = offset;
        this.op = op;
    }

    public BSize getOp() {
        return op;
    }

    public void setOp(BSize op) {
        this.op = op;
    }

    @Override
    public void add(){
        addUse(rd);
        rd.addUse(this);
        if(dest instanceof RiscRegister){
            addUse((RiscRegister)dest);
            ((RiscRegister) dest).addUse(this);
        }
    }

    public RiscRegister getRd() {
        return rd;
    }

    public void setRd(RiscRegister rd) {
        this.rd = rd;
    }

    public RiscOperand getDest() {
        return dest;
    }

    public void setDest(RiscOperand dest) {
        this.dest = dest;
    }

    public Immidiate getOffset() {
        return offset;
    }

    public void setOffset(Immidiate offset) {
        this.offset = offset;
    }
}
