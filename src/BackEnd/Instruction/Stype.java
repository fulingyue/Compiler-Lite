package BackEnd.Instruction;

import BackEnd.Operands.Immidiate;
import BackEnd.Operands.RiscOperand;
import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

public class Stype extends RiscInstruction {
    private RiscRegister rs;
    private RiscOperand addr;
    private Immidiate offset;
    private BSize op;

    public enum BSize{
        sb,sw
    }


    public Stype(RiscBB parentBB, RiscRegister rs, RiscOperand addr, Immidiate offset, BSize op) {
        super(parentBB);
        this.rs = rs;
        this.addr = addr;
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
        if(addr instanceof RiscRegister){
            addUse((RiscRegister)addr);
            ((RiscRegister) addr).addUse(this);
        }
    }



    @Override
    public String print() {
        return "\t" + op.name() + "\t" + rs.print() + ", " + addr.print();
    }

    @Override
    public void replaceUse(RiscRegister old, RiscRegister newUse) {
        boolean flag = false;
        if (rs == old) {
            rs = newUse;
            flag = true;
        }
        if (addr instanceof RiscRegister && addr == old) {
            addr= newUse;
            flag = true;
        }
        if (flag) {
            old.getUse().remove(this);
            this.getUsages().remove(old);
            newUse.addUse(this);
            addUse(newUse);
        }
    }

    @Override
    public void replaceDef(RiscRegister old, RiscRegister newDef) {

    }

    public RiscRegister getRd() {
        return rs;
    }

    public void setRd(RiscRegister rd) {
        this.rs = rd;
    }

    public RiscRegister getRs() {
        return rs;
    }

    public void setRs(RiscRegister rs) {
        this.rs = rs;
    }

    public RiscOperand getAddr() {
        return addr;
    }

    public void setAddr(RiscOperand addr) {
        this.addr = addr;
    }

    public Immidiate getOffset() {
        return offset;
    }

    public void setOffset(Immidiate offset) {
        this.offset = offset;
    }
}
