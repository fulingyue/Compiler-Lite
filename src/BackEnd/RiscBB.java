package BackEnd;

import BackEnd.Instruction.RiscInstruction;
import FrontEnd.IR.BasicBlock;

import java.util.ArrayList;

public class RiscBB {
    private String label;
    private RiscFunction function;
    private RiscInstruction head, tail;
    private BasicBlock IRBB;
    private RiscBB prev, next;
    private ArrayList<RiscBB> successors, predecessors;

    public RiscBB(String label, RiscFunction function, BasicBlock IRBB) {
        this.label = function.name + label;
        this.function = function;
        this.IRBB = IRBB;
        successors = new ArrayList<>();
        predecessors = new ArrayList<>();
        head = tail = null;
    }

    public void addSucc(RiscBB bb){
        successors.add(bb);
        bb.addPre(this);
    }

    public void addPre(RiscBB bb){
        predecessors.add(bb);
        bb.addSucc(this);
    }

    public void addInst(RiscInstruction instruction){
        instruction.add();
        if(head == null){
            head = tail =  instruction;
        }
        else  {
            tail.setNext(instruction);
            instruction.setPrev(tail);
            tail = instruction;
        }
    }

    public RiscInstruction getHead() {
        return head;
    }

    public void setHead(RiscInstruction head) {
        this.head = head;
    }

    public RiscInstruction getTail() {
        return tail;
    }

    public void setTail(RiscInstruction tail) {
        this.tail = tail;
    }

    public BasicBlock getIRBB() {
        return IRBB;
    }

    public void setIRBB(BasicBlock IRBB) {
        this.IRBB = IRBB;
    }

    public RiscBB getPrev() {
        return prev;
    }

    public void setPrev(RiscBB prev) {
        this.prev = prev;
    }

    public RiscBB getNext() {
        return next;
    }

    public void setNext(RiscBB next) {
        this.next = next;
    }

    public ArrayList<RiscBB> getSuccessors() {
        return successors;
    }

    public void setSuccessors(ArrayList<RiscBB> successors) {
        this.successors = successors;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public RiscFunction getFunction() {
        return function;
    }

    public void setFunction(RiscFunction function) {
        this.function = function;
    }

    public ArrayList<RiscBB> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(ArrayList<RiscBB> predecessors) {
        this.predecessors = predecessors;
    }
}
