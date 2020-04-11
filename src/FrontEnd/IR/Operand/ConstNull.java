package FrontEnd.IR.Operand;

import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Type.PtrType;
import FrontEnd.IR.Type.VoidType;
import FrontEnd.IRVisitor;

public class ConstNull  extends Constant {
    public ConstNull(String name, IRType type) {
        super(name,type);
    }
    public ConstNull(){
        super("null",new PtrType(new VoidType()));
    }


    @Override
    public String print() {
        return "null";
    }

}
