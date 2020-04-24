package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Operand.VirtualReg;
import FrontEnd.IRVisitor;

public class BinaryOp extends Instruction {
    public enum BinOp {
        ADD,
        SUB,
        MUL,
        DIV,
        MOD ,
        SAL,
        SAR,
        AND,
        OR,
        XOR
    }
    private BinOp op;
    private Operand lhs,rhs;
    private Register dest;

    public BinaryOp(BasicBlock bb, Register dest, BinOp op, Operand lhs, Operand rhs) {
        super(op.name(),bb);
        this.dest = dest;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    public void add() {
        lhs.addUser(this);
        rhs.addUser(this);
        dest.addDef(this);
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
    public void accept(IRVisitor visitor){
        visitor.visit(this);
    }

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        if(lhs == oldUser){
            assert newUser instanceof Operand;
//            lhs.removeUser(this);
            lhs = (Operand)newUser;
            lhs.addUser(this);
        }
        if(rhs == oldUser)  {
            assert newUser instanceof Operand;
//            rhs.removeUser(this);
            rhs = (Operand)newUser;
            rhs.addUser(this);
        }
    }

    //    @Override
//    public void reloadUsed() {
//
//    }
    @Override
    public String print() {
        String opName;
        switch (op) {
            case AND:
                opName = "and";
                break;
            case MUL:
                opName = "mul";
                break;
            case XOR:
                opName = "xor";
                break;
            case OR:
                opName = "or";
                break;
            case DIV:
                opName = "sdiv";
                break;
            case SUB:
                opName = "sub";
                break;
            case MOD:
                opName = "srem";
                break;
            case ADD:
                opName="add";
                break;
            case SAL:
                opName = "shl";
                break;
            default:
                opName  = "ashr";

        }
        return dest.print() + " = " + opName + " " + dest.getType().print() + " " + lhs.print() + ", " + rhs.print();
    }
    ////////private utils///////




    //////getter and setter///////

    public BinOp getOp() {
        return op;
    }

    public void setOp(BinOp op) {
        this.op = op;
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

    public Register getDest() {
        return dest;
    }

    public void setDest(VirtualReg dest) {
        this.dest = dest;
    }
}
