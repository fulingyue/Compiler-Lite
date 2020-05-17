package BackEnd.Instruction;

import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;


public class BinaryBranch extends RiscInstruction {
    public enum BranchOp {
        beq, bne, ble, bge, blt, bgt;
    }

    private BranchOp op;
    private RiscRegister rs1, rs2;
    private RiscBB target;

    public BinaryBranch(RiscBB parentBB, BranchOp op, RiscRegister rs1, RiscRegister rs2, RiscBB target) {
        super(parentBB);
        this.op = op;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.target = target;
    }

    
    @Override
    public void add(){
        addUse(rs1);
        addUse(rs2);
        rs1.addUse(this);
        rs2.addUse(this);
        parentBB.addSucc(target);
        target.addPre(parentBB);
    }



    @Override
    public String print() {
        return  "\t" + op.name() + "\t"
                + getRs1().print() + ", " + rs2.print() + ", " + target.print();
    }

    @Override
    public void replaceUse(RiscRegister old, RiscRegister newUse) {
        if(old == rs1){
            old.getUse().remove(this);
            getUsages().remove(old);
            rs1 = newUse;
            newUse.addUse(this);
            addUse(newUse);
        }
        if(old == rs2){
            old.getUse().remove(this);
            getUsages().remove(old);
            rs2 = newUse;
            newUse.addUse(this);
            addUse(newUse);
        }
    }

    @Override
    public void replaceDef(RiscRegister old, RiscRegister newDef) {

    }

    public BranchOp getOp() {
        return op;
    }

    public void setOp(BranchOp op) {
        this.op = op;
    }

    public RiscRegister getRs1() {
        return rs1;
    }

    public void setRs1(RiscRegister rs1) {
        this.rs1 = rs1;
    }

    public RiscRegister getRs2() {
        return rs2;
    }

    public void setRs2(RiscRegister rs2) {
        this.rs2 = rs2;
    }

    public RiscBB getTarget() {
        return target;
    }

    public void setTarget(RiscBB target) {
        this.target = target;
    }
}
