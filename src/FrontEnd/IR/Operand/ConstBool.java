package FrontEnd.IR.Operand;

import FrontEnd.IR.Type.IntIRType;

public class ConstBool  extends Constant {
    private boolean value;

    public ConstBool(boolean value) {
        this.value = value;
        name  = "bool";
        type = new IntIRType(IntIRType.intType.i1);
    }




    @Override
    public String print() {
        return String.valueOf(value);
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

}
