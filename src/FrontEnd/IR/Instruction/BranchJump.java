package FrontEnd.IR.Instruction;

import BackEnd.Instruction.RiscInstruction;
import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IRVisitor;
import Optimize.SCCP;

import java.util.HashSet;
import java.util.LinkedList;

public class BranchJump extends Instruction {
    private Operand condition;
    private BasicBlock  thenBlock, elseBlock;

    private RiscInstruction riscInstruction;

    public RiscInstruction getRiscInstruction() {
        return riscInstruction;
    }

    public void setRiscInstruction(RiscInstruction riscInstruction) {
        this.riscInstruction = riscInstruction;
    }


    public BranchJump(String name, BasicBlock bb, Operand condition, BasicBlock thenBB, BasicBlock elseBB) {
        super(name,bb);
        this.condition = condition;
        this.thenBlock  = thenBB;
        this.elseBlock = elseBB;

    }
    public BranchJump(BasicBlock bb, Operand condition, BasicBlock thenBB, BasicBlock elseBB) {
        super("branch_jump",bb);
        this.condition = condition;
        this.thenBlock  = thenBB;
        this.elseBlock = elseBB;
    }

//if  no conditionn  then thenbb
    @Override
    public void add() {

        thenBlock.addPredecessorBB(this.getBasicBlock());
        this.getBasicBlock().addSuccessorBB(thenBlock);
        thenBlock.addUser(this);

        if(condition != null) {
            condition.addUser(this);
            elseBlock.addPredecessorBB(getBasicBlock());
            this.getBasicBlock().addSuccessorBB(elseBlock);
            elseBlock.addUser(this);
        }
    }


    @Override
    public void removeUsers() {
        thenBlock.removeUser(this);
        if(condition!=null){
            condition.removeUser(this);
            elseBlock.removeUser(this);
        }
    }

    @Override
    public void removeDefs() { }



    @Override
    public String print() {
        if(condition != null)
            return "br i1 " + condition.print() + ", label " + thenBlock.print() + ", label " +elseBlock.print();
        return "br label " + thenBlock.print();
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        if(condition == oldUser){
//            condition.removeUser(this);
            assert newUser instanceof Operand;
            condition = (Operand)newUser;
            condition.addUser(this);
        }  else {
            if (thenBlock == oldUser) {
//                thenBlock.removeUser(this);
                assert newUser instanceof BasicBlock;
                thenBlock = (BasicBlock) newUser;
                thenBlock.addUser(this);
            }
            if (elseBlock == oldUser) {
//                elseBlock.removeUser(this);
                assert newUser instanceof BasicBlock;
                elseBlock = (BasicBlock) newUser;
                elseBlock.addUser(this);
            }
        }
    }

    @Override
    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {
        if(condition != null)
            condition.markLive(workList,alive);
    }

    @Override
    public boolean replaceConst(SCCP sccp) {
        return false;
    }


    public void changeToNoBranch(boolean bool){
        BasicBlock dest = bool? thenBlock:elseBlock;
        BasicBlock removeBB = bool? elseBlock:thenBlock;
        if(removeBB != null){
            removeBB.removeUser(this);
        }
        if(condition != null){
            condition.removeUser(this);
        }
        BasicBlock bb = getBasicBlock();
        bb.getSuccessors().remove(removeBB);
        removeBB.getPredecessorBB().remove(bb);
        removeBB.removePhiIncomeBB(bb);
        condition = null;
        this.elseBlock = null;
        thenBlock = dest;
    }
    /////getter and setter////////

    public Operand getCondition() {
        return condition;
    }

    public void setCondition(Operand condition) {
        this.condition = condition;
    }

    public BasicBlock getThenBlock() {
        return thenBlock;
    }

    public void setThenBlock(BasicBlock thenBlock) {
        this.thenBlock = thenBlock;
    }

    public BasicBlock getElseBlock() {
        return elseBlock;
    }

    public void setElseBlock(BasicBlock elseBlock) {
        this.elseBlock = elseBlock;
    }
}
