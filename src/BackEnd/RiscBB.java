package BackEnd;

import BackEnd.Instruction.RiscInstruction;
import BackEnd.Operands.RiscRegister;
import FrontEnd.IR.BasicBlock;

import java.util.ArrayList;
import java.util.HashSet;

public class RiscBB {
    private String label;
    private RiscFunction function;
    private RiscInstruction head, tail;
    private BasicBlock IRBB;
    private RiscBB prev, next;
    private ArrayList<RiscBB> successors, predecessors;

    private HashSet<RiscRegister> liveIn, liveOut;
    private HashSet<RiscRegister> def,useExceptDef;

    public RiscBB(String label, RiscFunction function, BasicBlock IRBB) {
        this.label = function.name + label;
        this.function = function;
        this.IRBB = IRBB;
        successors = new ArrayList<>();
        predecessors = new ArrayList<>();
        head = tail = null;
        liveIn = new HashSet<>();
        liveOut = new HashSet<>();
        def = new HashSet<>();
        useExceptDef = new HashSet<>();
    }

    public String print(){
        return label;
    }

    public void clear(){
        liveOut.clear();
        liveOut.clear();
        def.clear();
        useExceptDef.clear();
    }


    public void addSucc(RiscBB bb){
        successors.add(bb);
    }

    public void addPre(RiscBB bb){
        predecessors.add(bb);
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

    public void removeInst(RiscInstruction inst){
        if(inst.getPrev() == null){
            head = inst.getNext();
        }
        else {
            inst.getPrev().setNext(inst.getNext());
        }
        if(inst.getNext() == null){
            tail = inst.getPrev();
        }
        else {
            inst.getNext().setPrev(inst.getPrev());
        }
    }
    public void dfs(ArrayList<RiscBB> dfsOrd, HashSet<RiscBB> visited){
        dfsOrd.add(this);
        visited.add(this);
        for(RiscBB nxt:successors){
            if(!visited.contains(nxt))
                nxt.dfs(dfsOrd,visited);
        }
    }

    public void insertNext(RiscInstruction inst, RiscInstruction insert){
        if(inst == tail){
            insert.setPrev(inst);
            insert.setNext(null);
            inst.setNext(insert);
            tail = insert;
        }
        else {
            insert.setPrev(inst);
            insert.setNext(inst.getNext());
            inst.getNext().setPrev(insert);
            inst.setNext(insert);
        }
    }

    public void insertPrev(RiscInstruction inst, RiscInstruction insert){
        if(inst == head){
            insert.setNext(inst);
            insert.setPrev(null);
            head = insert;
            inst.setPrev(insert);
        }else {
            insert.setNext(inst);
            insert.setPrev(inst.getPrev());
            inst.getPrev().setNext(insert);
            inst.setPrev(insert);
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

    public HashSet<RiscRegister> getLiveIn() {
        return liveIn;
    }

    public void setLiveIn(HashSet<RiscRegister> liveIn) {
        this.liveIn = liveIn;
    }

    public HashSet<RiscRegister> getLiveOut() {
        return liveOut;
    }

    public void setLiveOut(HashSet<RiscRegister> liveOut) {
        this.liveOut = liveOut;
    }

    public HashSet<RiscRegister> getDef() {
        return def;
    }

    public void setDef(HashSet<RiscRegister> def) {
        this.def = def;
    }

    public HashSet<RiscRegister> getUseExceptDef() {
        return useExceptDef;
    }

    public void setUseExceptDef(HashSet<RiscRegister> useExceptDef) {
        this.useExceptDef = useExceptDef;
    }
}
