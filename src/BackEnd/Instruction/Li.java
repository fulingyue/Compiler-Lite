package BackEnd.Instruction;

import BackEnd.Operands.Immidiate;
import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

public class Li extends RiscInstruction {
    private RiscRegister rd;
    private Immidiate imm;

    public Li(RiscBB parentBB, RiscRegister rd, Immidiate imm) {
        super(parentBB);
        this.rd = rd;
        this.imm = imm;
    }
    @Override
    public void add(){
        addDef(rd);
        rd.addDef(this);
    }

    public RiscRegister getRd() {
        return rd;
    }

    public void setRd(RiscRegister rd) {
        this.rd = rd;
    }

    public Immidiate getImm() {
        return imm;
    }

    public void setImm(Immidiate imm) {
        this.imm = imm;
    }
}
