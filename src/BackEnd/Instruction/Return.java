package BackEnd.Instruction;

import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

public class Return extends RiscInstruction {

    public Return(RiscBB parentBB) {
        super(parentBB);
    }


    @Override
    public void add() {
    }

    @Override
    public String print() {
        return "\tret";
    }

    @Override
    public void replaceUse(RiscRegister old, RiscRegister newUse) {

    }

    @Override
    public void replaceDef(RiscRegister old, RiscRegister newDef) {

    }
}
