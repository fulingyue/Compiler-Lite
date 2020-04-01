package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Type.VoidType;
import FrontEnd.IRVisitor;

public class Return extends Instruction {
    private Operand returnVal;
    private IRType type;

    public Return(String name, BasicBlock bb,IRType type, Operand returnVal) {
        super(name, bb);
        this.returnVal  = returnVal;
        this.type = type;
    }

    @Override
    public void add()  {
        if(returnVal != null) {
            Usages.add(returnVal);
        }
    }

    @Override
    public String print() {
        if(!(type instanceof VoidType)) {
            return "ret " + type.print() + " " + returnVal.print();
        }
        else {
            return "ret void";
        }
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    /////getter and setter////////

    public Operand getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(Operand returnVal) {
        this.returnVal = returnVal;
    }

    public IRType getType() {
        return type;
    }

    public void setType(IRType type) {
        this.type = type;
    }
}
