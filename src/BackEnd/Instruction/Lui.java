package BackEnd.Instruction;

import BackEnd.Operand.Immidiate;
import BackEnd.Operand.RiscRegister;
import BackEnd.RiscBB;

public class Lui extends RiscInstruction{
    private RiscRegister rd;
    private Immidiate imm;

    public Lui(RiscBB parentBB, RiscRegister rd, Immidiate imm ) {
        super(parentBB);
        this.rd = rd;
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
