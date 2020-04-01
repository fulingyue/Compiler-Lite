package FrontEnd.IR;

import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IRVisitor;

import java.util.LinkedList;

public class BasicBlock extends IRNode{
    private IRFunction functionParent;
    private Instruction tail;
    private Instruction head;
    private BasicBlock prevBB;
    private BasicBlock nextBB;



    public BasicBlock(String name, IRFunction function) {
        this.name = name;
        this.functionParent = function;
        this.tail = null;
        this.head = null;
//        this.function.addBB(this);
        this.prevBB = null;
        this.nextBB = null;
    }

    public void addNextBB(BasicBlock bb) {
        bb.setPrevBB(this);
        bb.setNextBB(this.nextBB);
        this.nextBB.setPrevBB(bb);
        this.nextBB = bb;
    }



    public void addInst(Instruction inst) {
        if(tail  == null) {
            head =  tail  = inst;
            inst.setPrev(null);
            inst.setNxt(null);
        } else if (!tail.isTerminator()) {
            tail.setNxt(inst);
            inst.setPrev(tail);
            tail = inst;
        } else {

        }
    }

    public void addFirstInst(Instruction inst) {
        if(head == null && tail == null) {
            head =  tail  = inst;
            inst.setPrev(null);
            inst.setNxt(null);
        } else {
            head.setPrev(inst);
            inst.setNxt(head);
            head = inst;
        }
        inst.add();
    }
    public void linkNext(BasicBlock nextBB) {
        this.nextBB = nextBB;
        nextBB.setPrevBB(this);
    }

    public String print() {
        return "%" + name;
    }

    @Override
    public void accept(IRVisitor visitor)  {
        visitor.visit(this);
    }
    ///////setters and getters///////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IRFunction getParent() {
        return functionParent;
    }

    public void setParent(IRFunction function) {
        this.functionParent = function;
    }

    public Instruction getTail() {
        return tail;
    }

    public void setTail(Instruction tail) {
        this.tail = tail;
    }

    public Instruction getHead() {
        return head;
    }

    public void setHead(Instruction head) {
        this.head = head;
    }

    public BasicBlock getPrevBB() {
        return prevBB;
    }

    public void setPrevBB(BasicBlock prevBB) {
        this.prevBB = prevBB;
    }

    public BasicBlock getNextBB() {
        return nextBB;
    }

    public void setNextBB(BasicBlock nextBB) {
        this.nextBB = nextBB;
    }
}
