package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.StaticVar;
import FrontEnd.IR.Type.PtrType;
import FrontEnd.IRVisitor;

public class Store extends Instruction{
    private Operand value;
    private Operand dest;

    public Store(String name, BasicBlock bb, Operand value, Operand dest) {
        super(name, bb);
        this.value = value;
        this.dest = dest;
    }

//    public Store(String name, BasicBlock bb, Operand dest) {
//        super(name, bb);
//        this.dest = dest;
//    }

    @Override
    public void add() {
        Usages.add(value);
        Usages.add(dest);
    }

    @Override
    public String print() {
        if(dest instanceof StaticVar) {
            return "store " + dest.getType().print() + " " + value.print() +
                    ", " + (new PtrType(dest.getType())).print() + " " + dest.print();
        }
        return "store " + ((PtrType)(dest.getType())).getPointerType().print() + " " + value.print() +
                ", " + dest.getType().print() + " " + dest.print();

    }
    @Override
    public void accept(IRVisitor vistor) {
        vistor.visit(this);
    }
}
