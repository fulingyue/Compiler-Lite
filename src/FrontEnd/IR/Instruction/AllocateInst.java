package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Operand.VirtualReg;
import FrontEnd.IRVisitor;
import Optimize.SCCP;

import java.util.HashSet;
import java.util.LinkedList;

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
        dest.addDef(this);
    }



    @Override
    public void removeUsers() {

    }

    @Override
    public void removeDefs() {
        dest.removeDef(this);
    }


    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) { }

    @Override
    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {

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

    public void setDest(Register dest) {
        this.dest = dest;
    }

    public IRType getType() {
        return type;
    }

    public void setType(IRType type) {
        this.type = type;
    }
}
