package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;

import java.util.PropertyResourceBundle;

public class Icmp extends Instruction {
    public enum CompareOp {
        GREATER, LESS, LEQ, GEQ, EQUAL , NOTEQUAL
    }

    private CompareOp op;
    private Register dest;
    private Operand lhs, rhs;
    private IRType type;

    public Icmp(BasicBlock bb, Register dest, CompareOp op, Operand lhs, Operand rhs) {
        super(op.name(),bb);
        this.dest = dest;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void add() {
        dest.setParent(this);
        Usages.add(lhs);
        Usages.add(rhs);
    }
    @Override
    public String print() {
        return dest.print() + " = icmp "+ op.name() + " " +
                type.print() + " " + lhs.print() + ", " + rhs.print();
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    /////getter and setter////////

    public CompareOp getOp() {
        return op;
    }

    public void setOp(CompareOp op) {
        this.op = op;
    }

    public Register getDest() {
        return dest;
    }

    public void setDest(Register dest) {
        this.dest = dest;
    }

    public Operand getLhs() {
        return lhs;
    }

    public void setLhs(Operand lhs) {
        this.lhs = lhs;
    }

    public Operand getRhs() {
        return rhs;
    }

    public void setRhs(Operand rhs) {
        this.rhs = rhs;
    }
}
