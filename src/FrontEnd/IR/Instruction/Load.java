package FrontEnd.IR.Instruction;

import BackEnd.Instruction.RiscInstruction;
import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;
import Optimize.SCCP;

import java.util.HashSet;
import java.util.LinkedList;

public class Load extends Instruction {

    private IRType type;
    private Operand dest;
    private Register res;



    private RiscInstruction riscInst;

    public RiscInstruction getRiscInst() {
        return riscInst;
    }

    public void setRiscInst(RiscInstruction riscInst) {
        this.riscInst = riscInst;
    }

    public Load(String name, BasicBlock bb, IRType type, Operand dest, Register res) {
        super(name, bb);
        this.type = type;
        this.dest = dest;
        this.res = res;
    }

    public Load(String name, BasicBlock bb, Operand dest, Register res) {
        super(name, bb);
        this.dest = dest;
        this.res = res;
        this.type = res.getType();
    }

    public Load(BasicBlock bb, Operand dest, Register res) {
        super("load", bb);
        this.dest = dest;
        this.res = res;
        this.type = res.getType();
    }

    @Override
    public void add() {
        res.addDef(this);
        res.setDefInst(this);
        dest.addUser(this);
    }

    @Override
    public void removeUsers() {
        dest.removeUser(this);
    }

    @Override
    public void removeDefs() {
        res.removeDef(this);
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

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        if(dest == oldUser) {
            assert newUser instanceof Operand;
            dest = (Operand)newUser;
            dest.addUser(this);
        }
    }



    @Override
    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {
        dest.markLive(workList,alive);
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

    public IRType getType() {
        return type;
    }

    public void setType(IRType type) {
        this.type = type;
    }

    public Operand getDest() {
        return dest;
    }

    public void setDest(Operand dest) {
        this.dest = dest;
    }

    public Register getRes() {
        return res;
    }

    public void setRes(Register res) {
        this.res = res;
    }
}
