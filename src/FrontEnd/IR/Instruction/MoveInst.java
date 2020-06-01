package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Constant;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Parameter;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IRVisitor;
import Optimize.SCCP;

import java.util.HashSet;
import java.util.LinkedList;

public class MoveInst extends Instruction {
    private Operand src;
    private Register res;

    public MoveInst(String name, BasicBlock bb, Operand src, Register res) {
        super(name, bb);
        this.src = src;
        this.res = res;

        assert src != res;
        assert src.getType().equals(res.getType());
        assert src instanceof Register || src instanceof Parameter || src instanceof Constant;
    }


    @Override
    public void add() {
        res.addDef(this);
        src.addUser(this);
    }

    @Override
    public void removeUsers() {
        src.getUsers().remove(this);
    }

    @Override
    public void removeDefs() {
        res.getDefs().remove(this);
    }

    @Override
    public void accept(IRVisitor vistor) {
        vistor.visit(this);
    }

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        if(src == oldUser){
            src = (Operand)newUser;
            src.addUser(this);
        }
    }

    @Override
    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {
        //no use here
    }

    @Override
    public String print(){
        return "move" + res.print() + " " + src.print();
    }

    @Override
    public boolean replaceConst(SCCP sccp) {
        return false;
    }



    public Operand getSrc() {
        return src;
    }

    public void setSrc(Operand src) {
        this.src = src;
    }

    public Register getRes() {
        return res;
    }

    public void setRes(Register res) {
        this.res = res;
    }
}
