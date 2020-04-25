package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IRVisitor;

import java.util.HashSet;
import java.util.LinkedList;

public abstract class Instruction extends IRNode {
    private BasicBlock basicBlock;
    private Instruction prev;
    private Instruction nxt;


    public Instruction(String name,BasicBlock bb){
        super(name);
        this.basicBlock =  bb;
        prev = null;
        nxt = null;
//        moved = false;
    }
    public boolean isTerminator() {
        if(this instanceof BranchJump || this instanceof Return)
            return true;
        return false;
    }

    public void append(Instruction inst) {
        inst.prev = null;
        inst.nxt = null;
        if(this.nxt == null) {
            inst.prev = this;
            this.nxt = inst;
            basicBlock.setTail(inst);
        } else {
            this.nxt.prev = inst;
            inst.prev = this;
            inst.nxt = this.nxt;
            this.nxt = inst;
        }
    }

    public void remove() {
        if(prev == null) {
            basicBlock.setHead(nxt);
        } else  {
            prev.nxt = nxt;
        }

        if(nxt == null) {
            basicBlock.setTail(prev);
        } else {
            nxt.prev = prev;
        }
        removeDefs();
        removeUsers();
    }

    public boolean isUnused() {
        return this.getUsers().size() == 0;
    }


    public String print() {
        return "inst";
    }


    public abstract void add();
    public abstract void removeUsers();
    public abstract void removeDefs();
    public abstract void accept(IRVisitor vistor);
    public abstract void replaceUse(IRNode oldUser, IRNode newUser);
    public abstract void markLive(LinkedList<Instruction> workList,HashSet<Instruction> alive);
    //////getter and setter//////

    public BasicBlock getBasicBlock() {
        return basicBlock;
    }

    public void setBasicBlock(BasicBlock basicBlock) {
        this.basicBlock = basicBlock;
    }

    public Instruction getPrev() {
        return prev;
    }

    public void setPrev(Instruction prev) {
        this.prev = prev;
    }

    public Instruction getNxt() {
        return nxt;
    }

    public void setNxt(Instruction nxt) {
        this.nxt = nxt;
    }


}
