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

    @Override
    public String print() {
        return "\tmv\t" + rd.print() + ", " + rs.print();
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
        if(old == rd){
            old.getDef().remove(this);
            getDef().remove(old);
            rd = newDef;
            newDef.addDef(this);
            addDef(newDef);
        }
    }


    public void remove(){
        rs.getUse().remove(this);
        rd.getDef().remove(this);
        this.getUsages().remove(rs);
        this.getDef().remove(rd);

        parentBB.removeInst(this);
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
