package FrontEnd.IR.Type;

import FrontEnd.IR.Operand.ConstNull;
import FrontEnd.IR.Operand.Operand;

public class PtrType  extends IRType {
    private IRType pointerType;
    public PtrType(IRType pointerType) {
        this.pointerType = pointerType;
        byteWidth = 8;
    }


    @Override
    public String print() {
        return pointerType.print() + "*";
    }

    @Override
    public Operand getDefaultValue() {
        return new ConstNull();
    }

    /////setter and getter/////

    public IRType getPointerType() {
        return pointerType;
    }

    public void setPointerType(IRType pointerType) {
        this.pointerType = pointerType;
    }
}
