package BackEnd.Instruction;

import BackEnd.Operands.Immidiate;
import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

public class ImmOperation extends RiscInstruction {
    public enum IOp{
        addi,andi,ori,xori, slli,srai,slti,sltiu
    }

    private IOp op;
    private RiscRegister rd,rs;
    private Immidiate imm;

    public ImmOperation(RiscBB parentBB, IOp op, RiscRegister rd, RiscRegister rs, Immidiate imm) {
        super(parentBB);
        this.op = op;
        this.rd = rd;
        this.rs = rs;
        this.imm = imm;
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
        return "\t" + op.name() + "\t"
                + getRd().print() + ", " + getRs().print() + ", " + imm.print();
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


    public IOp getOp() {
        return op;
    }

    public void setOp(IOp op) {
        this.op = op;
    }

    public RiscRegister getRd() {
        return rd;
    }

    public void setRd(RiscRegister rd) {
        this.rd = rd;
    }

    public RiscRegister getRs() {
        return rs;
    }

    public void setRs(RiscRegister rs) {
        this.rs = rs;
    }

    public Immidiate getImm() {
        return imm;
    }

    public void setImm(Immidiate imm) {
        this.imm = imm;
    }
}
