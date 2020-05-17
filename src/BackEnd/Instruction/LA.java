package BackEnd.Instruction;

import BackEnd.Operands.GlobalVar;
import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

public class LA  extends RiscInstruction {
    private RiscRegister rd;
    private GlobalVar globalVar;

    public LA(RiscBB parentBB, RiscRegister rd, GlobalVar globalVar) {
        super(parentBB);
        this.rd = rd;
        this.globalVar = globalVar;
    }


    @Override
    public void add() {
        rd.addDef(this);
        addDef(rd);
    }


    @Override
    public String print() {
        return "\tla\t" + rd.print() + ", " + globalVar.getName();
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
}
