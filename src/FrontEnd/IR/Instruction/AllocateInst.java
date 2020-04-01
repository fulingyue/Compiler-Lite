package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Operand.VirtualReg;
import FrontEnd.IRVisitor;

public class AllocateInst extends Instruction{
    //Assign
    private  Register dest;
    private IRType type;

    public AllocateInst(BasicBlock bb, String name, Register dest, IRType type) {
        super(name,bb);
        this.dest = dest;
        this.type = type;
    }

    @Override
    public void add() {
        dest.setParent(this);
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Register getDest() {
        return dest;
    }

    public void setDest(VirtualReg dest) {
        this.dest = dest;
    }



    @Override
    public String print()  {
        return dest.print() + " = alloca " + type.print();
    }
}
