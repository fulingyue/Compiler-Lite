package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Type.PtrType;
import FrontEnd.IRVisitor;

import java.util.ArrayList;

public class GetPtr extends Instruction {
    // this instruction just execute address calculation, does  not access mem
    private Operand pointer;
    private ArrayList<Operand> index;
    private Register dest;

    public GetPtr(String name, BasicBlock bb, Operand pointer, ArrayList<Operand> index, Register dest) {
        super(name, bb);
        this.pointer = pointer;
        this.index = index;
        this.dest = dest;
    }
    @Override
    public void add() {
        Usages.add(pointer);
        for (Operand item: index)
            Usages.add(item);
    }

    @Override
    public String print() {
        IRType baseType;
        IRType pointerType;

        if (pointer.getType() instanceof PtrType) {
            baseType = ((PtrType) pointer.getType()).getPointerType();
            pointerType = pointer.getType();
        } else {
            baseType = pointer.getType();
            pointerType = new PtrType(baseType);
        }
        StringBuilder string = new StringBuilder();
        string.append(dest.print());
        string.append(" = ");
        string.append("getelementptr ").append(baseType.print()).append(", ");
        string.append(pointerType).append(" ").append(pointer.print());
        for (Operand item : index){
            string.append(", ").append(item.getType().print());
            string.append(" ").append(item.print());
        }
        return string.toString();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
