package FrontEnd.IR.Operand;

import FrontEnd.IR.IRNode;
import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;



public abstract class Operand extends IRNode {
    IRType type;


    public IRType getType() {
        return type;
    }

    public void setType(IRType type) {
        this.type = type;
    }

    public Operand(String name, IRType type, Instruction parent) {
        super(name);
        this.addDef(parent);
    }


    public Operand(String name, IRType type) {
        super(name);
        this.type = type;

    }
    public Operand(){
        type = null;

    }





    public abstract String print();
}
