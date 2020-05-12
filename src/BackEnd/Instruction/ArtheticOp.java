package BackEnd.Instruction;

import BackEnd.Operand.RiscRegister;
import BackEnd.RiscBB;

public class ArtheticOp extends RiscInstruction{
    public enum ROp{
        add,sub, mul,div, rem,  and, or, xor, sll,sltu,slt,sra
    }

    private ROp op;
    private RiscRegister rs1,rs2,rd;


    public ArtheticOp(RiscBB parentBB, ROp op, RiscRegister rs1, RiscRegister rs2, RiscRegister rd) {
        super(parentBB);
        this.op = op;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.rd = rd;
    }

    @Override
    public void add(){
        addDef(rd);
        addUse(rs1);
        addUse(rs2);
        rd.addDef(this);
        rs1.addDef(this);
        rs2.addDef(this);
    }

    public ROp getOp() {
        return op;
    }

    public void setOp(ROp op) {
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

    public RiscRegister getRd() {
        return rd;
    }

    public void setRd(RiscRegister rd) {
        this.rd = rd;
    }
}
