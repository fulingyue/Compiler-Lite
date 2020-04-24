package FrontEnd.IR;

import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IRVisitor;
import util.Pair;

import java.util.*;

public class BasicBlock extends IRNode{
    private IRFunction functionParent;
    private Instruction tail;
    private Instruction head;
    private BasicBlock prevBB;
    private BasicBlock nextBB;

    private Set<BasicBlock> predecessorBB = new HashSet<>();
    private HashSet<BasicBlock> successors = new HashSet<>();

    private BasicBlock dfsFather;
    private int dfsOrd;
    private BasicBlock semiDom;//semi[x] = min{v| path v->x: dfsn[v_i] > dfsn[x]}
    private BasicBlock iDom = null;//closest stirct dominator bb, deepest dominator_the father of bb in the DT
    private HashSet<BasicBlock> bucket; 
    private HashSet<BasicBlock> strictDominators = new HashSet<>();
    private HashSet<BasicBlock> domianceFrontier = new HashSet<>();
    private Map<Register, Phi> phiMap;
    private ArrayList<BasicBlock> dominance = new ArrayList<>();


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
            inst.add();
        } else if (!tail.isTerminator()) {
            tail.setNxt(inst);
            inst.setPrev(tail);
            tail = inst;
            inst.add();
        } else {
            System.out.print("instruction is not successfully added\n");
            System.out.print(inst.print());
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

    public void deleteDeadInst() {//TODO
        Instruction inst = this.getHead();
        while(true) {
            if(inst instanceof AllocateInst) {
                if(((AllocateInst) inst).getDest().getDefs().isEmpty()){
                    inst.remove();
                }
            }
            if(inst instanceof Load) {
                if(((Load) inst).getRes().getUsers().isEmpty())
                    inst.remove();
            }
//            if(inst instanceof Store) {
//                    if(((Store) inst).getDest().getUsers().isEmpty())
//                        inst.remove();
//            }
            //TODO other types Instruction

            if(inst == this.getTail())
                break;
            else inst = inst.getNxt();
        }
    }

    public void addSemiDom(BasicBlock bb ){
        bucket.add(bb);
    }

    public void addPhi(Register bb, Phi phi) {
        phiMap.put(bb,phi);
    }

    @Override
    public void accept(IRVisitor visitor)  {
        visitor.visit(this);
    }
    ///////setters and getters///////


    public Map<Register, Phi> getPhiMap() {
        return phiMap;
    }

    public void setPhiMap(Map<Register, Phi> phiMap) {
        this.phiMap = phiMap;
    }

    public HashSet<BasicBlock> getBucket() {
        return bucket;
    }

    public void setBucket(HashSet<BasicBlock> bucket) {
        this.bucket = bucket;
    }

    public HashSet<BasicBlock> getStrictDominators() {
        return strictDominators;
    }

    public void setStrictDominators(HashSet<BasicBlock> strictDominators) {
        this.strictDominators = strictDominators;
    }

    public HashSet<BasicBlock> getDomianceFrontier() {
        return domianceFrontier;
    }

    public void setDomianceFrontier(HashSet<BasicBlock> domianceFrontier) {
        this.domianceFrontier = domianceFrontier;
    }

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

    public IRFunction getFunctionParent() {
        return functionParent;
    }

    public void setFunctionParent(IRFunction functionParent) {
        this.functionParent = functionParent;
    }

    public Set<BasicBlock> getPredecessorBB() {
        return predecessorBB;
    }

    public void addPredecessorBB(BasicBlock bb) {
        predecessorBB.add(bb);
    }
    public void setPredecessorBB(Set<BasicBlock> predecessorBB) {
        this.predecessorBB = predecessorBB;
    }

    public HashSet<BasicBlock> getSuccessors() {
        return successors;
    }

    public void addSuccessorBB(BasicBlock bb)  {
        successors.add(bb);
    }
    public void setSuccessors(HashSet<BasicBlock> successors) {
        this.successors = successors;
    }

    public BasicBlock getDfsFather() {
        return dfsFather;
    }

    public void setDfsFather(BasicBlock dfsFather) {
        this.dfsFather = dfsFather;
    }

    public int getDfsOrd() {
        return dfsOrd;
    }

    public void setDfsOrd(int dfsOrd) {
        this.dfsOrd = dfsOrd;
    }

    public BasicBlock getSemiDom() {
        return semiDom;
    }

    public void setSemiDom(BasicBlock semiDom) {
        this.semiDom = semiDom;
    }

    public BasicBlock getiDom() {
        return iDom;
    }

    public void setiDom(BasicBlock iDom) {
        this.iDom = iDom;
    }

    public ArrayList<BasicBlock> getDominance() {
        return dominance;
    }

    public void addDominance(BasicBlock bb) {
        dominance.add(bb);
    }
    public void setDominance(ArrayList<BasicBlock> dominance) {
        this.dominance = dominance;
    }
}

