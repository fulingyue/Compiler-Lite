package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;
import Optimize.SCCP;

import java.util.HashSet;
import java.util.LinkedList;

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
         res.addDef(this);
         src.addUser(this);
         res.setDefInst(this);
    }

    @Override
    public void removeUsers() {
        src.removeUser(this);
    }

    @Override
    public void removeDefs() {
        res.removeDef(this);
    }



    @Override
    public String print() {
        return res.print()+" = bitcast " + src.getType().print() + " " + src.print() + " to " + type.print();
    }

    @Override
    public void accept(IRVisitor  visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        if(src == oldUser) {
//            src.removeUser(this);
            assert newUser instanceof Operand;
            src = (Operand)newUser;
            src.addUser(this);
        }
    }

    @Override
    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {
        src.markLive(workList,alive);
    }

    @Override
    public boolean replaceConst(SCCP sccp) {
        SCCP.LatticeVal latticeVal = sccp.getStatus(res);
        if(latticeVal.getType() == SCCP.LatticeVal.LatticaValType.constant){
            res.replaceUser(latticeVal.getOperand());
            this.remove();
            return true;
        }
        return false;
    }

    public Operand getSrc() {
        return src;
    }

    public void setSrc(Operand src) {
        this.src = src;
    }

    public IRType getType() {
        return type;
    }

    public void setType(IRType type) {
        this.type = type;
    }

    public Register getRes() {
        return res;
    }

    public void setRes(Register res) {
        this.res = res;
    }
}
