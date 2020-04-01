package FrontEnd.IR.Type;


import FrontEnd.IR.Operand.ConstBool;
import FrontEnd.IR.Operand.ConstInt;
import FrontEnd.IR.Operand.Operand;

public class IntIRType extends IRType {
    public enum intType  {
        i1, i8, i32
    }
    private intType intType;
    public IntIRType(intType intType) {
        this.intType = intType;
        switch (intType) {
            case i1: case i8:
                byteWidth = 1;
                break;
            case i32:
                byteWidth  = 4;
                break;
        }
    }


    @Override
    public Operand getDefaultValue() {
        switch (intType) {
            case i1:
                return new ConstBool(false);
            default:
                return new ConstInt("int", this, 0);

        }
    }
    public String print() {
        switch (intType) {
            case i32:
                return ("i32");
            case i8:
                return ("i8");
            case i1:
                return ("i1");
            default:
                return "jalfsjkajkgsdjlgjkd";
        }
    }

    //////////getter and setter//////////

    public IntIRType.intType getIntType() {
        return intType;
    }

    public void setIntType(IntIRType.intType intType) {
        this.intType = intType;
    }
}
