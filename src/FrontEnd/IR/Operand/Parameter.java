package FrontEnd.IR.Operand;

import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;

public class Parameter extends Operand {
    private IRFunction funcParent;

    public Parameter(String name, IRType type, IRFunction parent) {
        super(name,type);
        this.funcParent = parent;
    }

    public Parameter(String name, IRType type) {
        super(name,type);
    }

//    @Override
//    public void accept(IRVisitor visitor) {
//        visitor.visit(this);
//    }

    public IRFunction getFuncParent() {
        return funcParent;
    }

    public void setFuncParent(IRFunction funcParent) {
        this.funcParent = funcParent;
    }
    @Override
    public String print() {
        return "%" + name;
    }

}
