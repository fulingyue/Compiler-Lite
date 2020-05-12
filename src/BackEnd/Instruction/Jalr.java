package BackEnd.Instruction;

import BackEnd.Operand.RiscRegister;
import BackEnd.RiscBB;

public class Jalr extends RiscInstruction {
    private RiscRegister rs;

    public Jalr(RiscBB parentBB, RiscRegister rs) {
        super(parentBB);
        this.rs = rs;
    }

    public void add(){
        addUse(rs);
        rs.addUse(this);
    }
    public RiscRegister getRs() {
        return rs;
    }

    public void setRs(RiscRegister rs) {
        this.rs = rs;
    }
}
