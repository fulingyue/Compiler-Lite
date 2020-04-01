package FrontEnd.IR.Operand;

import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;

public abstract class Register extends Operand {
    public abstract String print();

    public Register(String name, IRType type, Instruction parent) {
        super(name,type,parent);
    }

    public Register(String name, IRType type) {
        super(name,type);
    }
//    public abstract RegVal copy();
}
