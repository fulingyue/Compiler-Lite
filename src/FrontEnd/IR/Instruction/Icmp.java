package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.ConstNull;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;
import Optimize.SCCP;

import java.util.HashSet;
import java.util.LinkedList;

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
        if(!(lhs instanceof ConstNull))
            type = lhs.getType();
        else if(!(rhs instanceof ConstNull)){
            type = rhs.getType();
        }
        else {
            throw new RuntimeException("icmp cannot be null == null");
        }
    }

    public boolean isCommutative(){
        return op == CompareOp.EQUAL || op == CompareOp.NOTEQUAL;
    }

    public CompareOp getCommutativeOp(){
        if(op == CompareOp.LEQ) return CompareOp.GREATER;
        else if(op == CompareOp.LESS) return CompareOp.GEQ;
        else if(op == CompareOp.GEQ) return CompareOp.LESS;
        else if(op == CompareOp.GREATER) return CompareOp.LEQ;
        else return null;
    }
    @Override
    public void add() {
        dest.addDef(this);
        dest.setDefInst(this);
        lhs.addUser(this);
        rhs.addUser(this);
    }

    @Override
    public void removeUsers() {
        lhs.removeUser(this);
        rhs.removeUser(this);
    }

    @Override
    public void removeDefs() {
        dest.removeDef(this);
    }



    @Override
    public String print() {
        String opName;
        switch (op) {
            case EQUAL:
                opName = "eq";
                break;
            case NOTEQUAL:
                opName = "ne";
                break;
            case LEQ:
                opName = "sle";
                break;
            case LESS:
                opName =  "slt";
                break;
            case GEQ:
                opName = "sge";
                break;
            default:
                opName = "sgt";
        }
        return dest.print() + " = icmp "+ opName + " " +
                type.print() + " " + lhs.print() + ", " + rhs.print();
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        if(lhs == oldUser) {
            assert newUser instanceof Operand;
//            lhs.removeUser(this);
            lhs = (Operand)newUser;
            lhs.addUser(this);
        }
        if (rhs == oldUser) {
            assert newUser instanceof Operand;
//            rhs.removeUser(this);
            rhs = (Operand)newUser;
            rhs.addUser(this);
        }
    }

    @Override
    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {
        lhs.markLive(workList,alive);
        rhs.markLive(workList,alive);
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

    //////utils////////
    public boolean onlyInBranch(){
        for(IRNode irNode: dest.getUsers()){
            assert irNode instanceof Instruction;
            if(! (irNode instanceof BranchJump)){
                return false;
            }
        }
        return true;
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
