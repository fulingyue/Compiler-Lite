package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Type.PtrType;
import FrontEnd.IRVisitor;
import Optimize.SCCP;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

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
        pointer.addUser(this);
        dest.addDef(this);
        dest.setDefInst(this);
        for (Operand item: index)
           item.addUser(this);
    }

    @Override
    public void removeUsers() {
        pointer.removeUser(this);

        for (Operand item: index)
            item.removeUser(this);
    }

    @Override
    public void removeDefs() {
        dest.removeDef(this);
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
        string.append(pointerType.print()).append(" ").append(pointer.print());

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

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        if(pointer == oldUser) {
            assert newUser instanceof Operand;
//            pointer.removeUser(this);
            pointer = (Operand)newUser;
            pointer.addUser(this);
        }
        for(int i = 0; i < index.size();++i) {
            Operand item = index.get(i);
            if(item  ==  oldUser) {
                assert newUser instanceof Operand;
                item.addUser(this);
                index.set(i,(Operand)newUser);
            }
        }
    }

    @Override
    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {
        pointer.markLive(workList,alive);
        for(Operand op:index){
            op.markLive(workList,alive);
        }
    }

    @Override
    public boolean replaceConst(SCCP sccp) {
        SCCP.LatticeVal latticeVal = sccp.getStatus(dest);
        if(latticeVal.getType() == SCCP.LatticeVal.LatticaValType.constant){
            dest.replaceUser(latticeVal.getOperand());
            this.remove();
            return true;
        }
        return false;
    }

    public Operand getPointer() {
        return pointer;
    }

    public void setPointer(Operand pointer) {
        this.pointer = pointer;
    }

    public ArrayList<Operand> getIndex() {
        return index;
    }

    public void setIndex(ArrayList<Operand> index) {
        this.index = index;
    }

    public Register getDest() {
        return dest;
    }

    public void setDest(Register dest) {
        this.dest = dest;
    }
}
