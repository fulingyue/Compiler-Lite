package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;

public class Load extends Instruction {
    private IRType type;
    private Operand dest;
    private Register res;

    public Load(String name, BasicBlock bb, IRType type, Operand dest, Register res) {
        super(name, bb);
        this.type = type;
        this.dest = dest;
        this.res = res;
    }

    @Override
    public void add() {
        res.setParent(this);
        Usages.add(dest);
    }

    @Override
    public String print() {
        return res.print()  + " = load " + type.print() + ", " +
                dest.getType().print() + " " + dest.print();
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
