package BackEnd.Instruction;

import BackEnd.Operands.RiscRegister;
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

    @Override
    public String print() {
        return null;
    }

    @Override
    public void replaceUse(RiscRegister old, RiscRegister newUse) {
        if(old == rs){
            old.getUse().remove(this);
            getUsages().remove(old);
            rs = newUse;
            newUse.addUse(this);
            addUse(newUse);
        }
    }

    @Override
    public void replaceDef(RiscRegister old, RiscRegister newDef) {

    }


    public RiscRegister getRs() {
        return rs;
    }

    public void setRs(RiscRegister rs) {
        this.rs = rs;
    }


}
