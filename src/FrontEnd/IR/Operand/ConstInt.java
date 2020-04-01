package FrontEnd.IR.Operand;

import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Type.IntIRType;
import FrontEnd.IRVisitor;

public class ConstInt extends Constant {

    private int value;

    public ConstInt(String name, IRType type, int imm) {
        super(name,type);
        this.value = imm;
    }

    public ConstInt(int value,IntIRType.intType type) {
        this.value = value;
        name  = "int";
        this.type = new IntIRType(type);
    }

    //////getter and setter/////

    @Override
    public String print() {
        return String.valueOf(value);
    }
    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
