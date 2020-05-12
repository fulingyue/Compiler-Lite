package BackEnd.Instruction;

import BackEnd.Operand.RiscRegister;
import BackEnd.RiscBB;


public class Branch extends RiscInstruction {
    public enum BranchOp {
        beq, bne, ble, bge, blt, bgt;
    }

    private BranchOp op;
    private RiscRegister rs, rt;
    private RiscBB target;

    public Branch(RiscBB parentBB, BranchOp op, RiscRegister rs, RiscRegister rt, RiscBB target) {
        super(parentBB);
        this.op = op;
        this.rs = rs;
        this.rt = rt;
        this.target = target;
    }
    
    @Override
    public void add(){
        addUse(rs);
        addUse(rt);
        rs.addUse(this);
        rt.addUse(this);
        parentBB.addSucc(target);
    }
    
}
