package BackEnd.Instruction;

import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

public class UnaryBranch extends RiscInstruction {

    public enum UnaryBrOp{
        beqz, bnez, bltz, bgez, blez, bgtz
    }

    private UnaryBrOp op;
    private RiscRegister rs;
    private RiscBB target;

    public UnaryBranch(RiscBB parentBB, UnaryBrOp op, RiscRegister rs, RiscBB target) {
        super(parentBB);
        this.op = op;
        this.rs = rs;
        this.target = target;
    }

    @Override
    public void add() {
        rs.addUse(this);
        addUse(rs);
    }



    @Override
    public String print() {
        return "\t" + op.name() + "\t" + rs.print() + ", " + target.print();
    }

    @Override
    public void replaceUse(RiscRegister old, RiscRegister newUse) {
        if(rs == old){
            rs.getUse().remove(this);
            getUsages().remove(rs);
            rs = newUse;
            add();
        }
    }

    @Override
    public void replaceDef(RiscRegister old, RiscRegister newDef) {

    }


    public UnaryBrOp getOp() {
        return op;
    }

    public void setOp(UnaryBrOp op) {
        this.op = op;
    }

    public RiscRegister getRs() {
        return rs;
    }

    public void setRs(RiscRegister rs) {
        this.rs = rs;
    }

    public RiscBB getTarget() {
        return target;
    }

    public void setTarget(RiscBB target) {
        this.target = target;
    }
}
