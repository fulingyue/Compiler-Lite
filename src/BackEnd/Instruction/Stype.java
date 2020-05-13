package BackEnd.Instruction;

import BackEnd.Operands.Immidiate;
import BackEnd.Operands.RiscOperand;
import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

public class Stype extends RiscInstruction {
    private RiscRegister rs;
    private RiscOperand dest;
    private Immidiate offset;
    private BSize op;

    public enum BSize{
        sb,sw
    }


    public Stype(RiscBB parentBB, RiscRegister rs, RiscOperand dest, Immidiate offset, BSize op) {
        super(parentBB);
        this.rs = rs;
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
        addUse(rs);
        rs.addUse(this);
        if(dest instanceof RiscRegister){
            addUse((RiscRegister)dest);
            ((RiscRegister) dest).addUse(this);
        }
    }

    public RiscRegister getRd() {
        return rs;
    }

    public void setRd(RiscRegister rd) {
        this.rs = rd;
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
