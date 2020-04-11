package FrontEnd.IR.Type;

import FrontEnd.IR.Operand.Operand;

public abstract class IRType {
    protected int byteWidth;


    public int getByteWidth() {
        return byteWidth;
    }

    public void setByteWidth(int byteWidth) {
        this.byteWidth = byteWidth;
    }

    public abstract String print();
    public abstract Operand getDefaultValue();

    public boolean equals(Object object) {
        if(!(object instanceof IRType))
            return false;
        else
            return print().equals(((IRType) object).print());
    }
}
