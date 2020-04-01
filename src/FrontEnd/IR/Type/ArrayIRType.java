package FrontEnd.IR.Type;

import FrontEnd.IR.Operand.Operand;

public class ArrayIRType extends IRType {
    private IRType innerIRType;
    private int size;

    public ArrayIRType(int size, IRType innerIRType) {
        this.size = size;
        this.innerIRType = innerIRType;
        this.setByteWidth(size * innerIRType.getByteWidth());
    }
    @Override
    public Operand getDefaultValue() {
        throw new RuntimeException();
    }
@Override
    public String  print() {
        return ("[" + size + " x "  + innerIRType.print() + "]");
    }
    ////////getter and setter//////////
    public IRType getInnerIRType() {
        return innerIRType;
    }

    public void setInnerIRType(IRType innerIRType) {
        this.innerIRType = innerIRType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
