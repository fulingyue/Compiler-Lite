package BackEnd.Instruction;

import BackEnd.Operands.*;
import BackEnd.RiscBB;

public class Stype extends RiscInstruction {
    private RiscRegister rs;
    private Address addr;
    private BSize op;

    public enum BSize{
        sb,sw
    }

    public Stype(RiscBB parentBB, RiscRegister rs, Address addr, BSize op) {
        super(parentBB);
        this.rs = rs;
        this.addr = addr;
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
        if(addr.getUse() != null){
            addUse(addr.getUse());
            (addr.getUse()).addUse(this);
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
        if (addr.getUse() != null && addr.getUse() == old) {
            assert addr instanceof AddrWithOffset;
            ((AddrWithOffset) addr).setBase(newUse);
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

    public void setAddr(Address addr) {
        this.addr = addr;
    }
}
