package FrontEnd.IR.Operand;

import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;

public class StaticString extends Constant{
    private String value;
//    private int size;
    public StaticString(String name, IRType type, String string) {
        super(name,type);
        this.value = string;
    }
    @Override
    public String print() {
        return value;
    }


    /////getter and setter////////

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
