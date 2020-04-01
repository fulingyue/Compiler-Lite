package FrontEnd.IR.Operand;

import FrontEnd.IR.IRNode;
import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;



public abstract class Operand extends IRNode {
    IRType type;
    Instruction instParent;


    public IRType getType() {
        return type;
    }

    public void setType(IRType type) {
        this.type = type;
    }

    public Operand(String name, IRType type, Instruction parent) {
        super(name);
        this.type = type;
        this.instParent = parent;
    }

    public Operand(String name, IRType type) {
        super(name);
        this.type = type;
        this.instParent = null;
    }
    public Operand(){
        type = null;
        instParent = null;
    }

    public Instruction getParent() {
        return instParent;
    }

    public void setParent(Instruction parent) {
        this.instParent = parent;
    }

    public abstract String print();
}
