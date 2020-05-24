package FrontEnd.IR.Operand;

import BackEnd.Operands.RiscRegister;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Type.IRType;

public class Parameter extends Operand {
    private IRFunction funcParent;
    private RiscRegister riscRegister;

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


    public RiscRegister getRiscRegister() {
        return riscRegister;
    }

    public void setRiscRegister(RiscRegister riscRegister) {
        this.riscRegister = riscRegister;
    }

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
