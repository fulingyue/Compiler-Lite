package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;

public class BitCast extends Instruction {
    // cast pointer type
    private Operand src;
    private IRType type;
    private Register res;

    public BitCast(String name, BasicBlock bb, Operand src, IRType type, Register res) {
        super(name, bb);
        this.src = src;
        this.type = type;
        this.res = res;
    }

    @Override
    public void add() {
         res.setParent(this);
         Usages.add(src);
    }
    @Override
    public String print() {
        return res.print()+" = bitcast " + src.getType().print() + " " + src.print() + " to " + type.print();
    }

    @Override
    public void accept(IRVisitor  visitor) {
        visitor.visit(this);
    }
}
