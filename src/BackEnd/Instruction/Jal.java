package BackEnd.Instruction;

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
    }



    public RiscBB getTarget() {
        return target;
    }

    public void setTarget(RiscBB target) {
        this.target = target;
    }
}
