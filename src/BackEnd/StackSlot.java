package BackEnd;

import BackEnd.Operand.RiscOperand;
import BackEnd.Operand.VirtualReg;

public class StackSlot extends RiscOperand {
    private RiscFunction function;
    int index;

    public StackSlot(RiscFunction function) {
        this.function = function;
        this.index = function.getStackSize();
        function.setStackSize(index + 1);
    }

    public StackSlot(RiscFunction function, int index) {
        this.function = function;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public RiscFunction getFunction() {
        return function;
    }

    public void setFunction(RiscFunction function) {
        this.function = function;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize(){return 4;}

    @Override
    public String print() {
       return  4* index + "sp";
    }


}
