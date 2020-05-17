package BackEnd.Instruction;

import BackEnd.Operands.RiscRegister;
import BackEnd.RiscBB;

public class Jal extends RiscInstruction{
    private RiscBB target;

    public Jal(RiscBB parentBB, RiscBB target) {
        super(parentBB);
        this.target = target;
    }

    @Override
    public void add(){
        target.addPre(parentBB);
        parentBB.addSucc(target);
    }

    @Override
    public String print() {
        assert target != null;
        return "\tj\t" + target.print();
    }

    @Override
    public void replaceUse(RiscRegister old, RiscRegister newUse) {

    }

    @Override
    public void replaceDef(RiscRegister old, RiscRegister newDef) {

    }


    public RiscBB getTarget() {
        return target;
    }

    public void setTarget(RiscBB target) {
        this.target = target;
    }
}
