package BackEnd.Instruction;

import BackEnd.Operands.Immidiate;
import BackEnd.Operands.RiscRegister;
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


    @Override
    public String print() {
        return "\tlui\t" + rd.print() + ", " + imm.print();
    }

    @Override
    public void replaceUse(RiscRegister old, RiscRegister newUse) {

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
