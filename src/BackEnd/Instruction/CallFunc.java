package BackEnd.Instruction;

import BackEnd.RiscBB;
import BackEnd.RiscFunction;

public class CallFunc extends RiscInstruction {
    RiscFunction function;
    private int paraNum;

    public CallFunc(RiscBB parentBB, RiscFunction function, int paraNum) {
        super(parentBB);
        this.function = function;
        this.paraNum = paraNum;
    }
    @Override
    public void add(){
        for(int i = 0; i < paraNum  && i  < 8; ++i){
            //TODO
        }
    }

    public RiscFunction getFunction() {
        return function;
    }

    public void setFunction(RiscFunction function) {
        this.function = function;
    }
}
