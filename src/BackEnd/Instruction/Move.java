package BackEnd.Instruction;

import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

public class Move extends RiscInstruction {
    RiscRegister rs,rd;

    public Move(RiscBB parentBB, RiscRegister rs, RiscRegister rd) {
        super(parentBB);
        this.rs = rs;
        this.rd = rd;
    }

    @Override
    public void add(){
        addDef(rd);
        addUse(rs);
        rd.addDef(this);
        rs.addUse(this);
    }

    public RiscRegister getRs() {
        return rs;
    }

    public void setRs(RiscRegister rs) {
        this.rs = rs;
    }

    public RiscRegister getRd() {
        return rd;
    }

    public void setRd(RiscRegister rd) {
        this.rd = rd;
    }
}
