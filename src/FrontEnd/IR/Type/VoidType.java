package FrontEnd.IR.Type;

import FrontEnd.IR.Operand.Operand;

public class VoidType extends IRType {
    public VoidType() {
        byteWidth =  0;
    }

    @Override
    public String print() {
        return "void";
    }
    @Override
    public Operand getDefaultValue() {
        throw new RuntimeException();
    }
}
