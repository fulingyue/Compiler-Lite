package FrontEnd.IR.Operand;

import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;

public class ConstNull  extends Constant {
    public ConstNull(String name, IRType type) {
        super(name,type);
    }
    public ConstNull(){
        super("null",null);
    }


    @Override
    public String print() {
        return "null";
    }

}
