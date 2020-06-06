package Optimize;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Instruction.BranchJump;
import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Instruction.Phi;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.ConstBool;
import FrontEnd.IR.Operand.Operand;

import java.util.ArrayList;

public class CFGSimplifier extends Pass{

    public CFGSimplifier(Module module) {
        super(module);
        changed = false;
    }

    @Override
    public boolean run() {
        changed = false;
        for(IRFunction function: module.getFunctionMap().values()) {
            changed  |= FuncSimplifier(function);
        }
        return changed;
    }

    private boolean FuncSimplifier(IRFunction function) {
        boolean changed = false;
        while(true){
            boolean resimplify = constantFoldTerminator(function);
            resimplify |= removeUnreachableBlocks(function);
            resimplify |= eliminateSingleEntrancePHINodes(function);
            if(resimplify) changed = true;
            else break;
        }
        return changed;
    }




    private boolean removeUnreachableBlocks(IRFunction function) {
        // Remove basic blocks that have no predecessors (except the entry block)...
        // or that just have themself as a predecessor.  These are unreachable.
        boolean changed = false;
        BasicBlock bb = function.getEntranceBB();

        ArrayList<BasicBlock> defOrder = function.gettDfsOder();
        while(bb != null){
            if(!defOrder.contains(bb)){
                for(BasicBlock succ: bb.getSuccessors())
                    succ.removePhiIncomeBB(bb);
                bb.deleteItself();
                changed = true;
            }

            else if(bb.getPredecessorBB().size() == 1){
                BasicBlock pre = bb.getPredecessorBB().iterator().next();
                if(pre.getSuccessors().size() == 1){
                    if(pre == bb)
                        bb.deleteItself();
                    else pre.mergeBlock(bb);
                    changed =true;
                }
            }


            bb = bb.getNextBB();

        }
        return changed;
    }


    private boolean constantFoldTerminator(IRFunction function){
        //Check to see if we can constant propagate this terminator instruction away.
        boolean changed = false;
        for(BasicBlock bb = function.getEntranceBB(); bb.getNextBB() != null;bb = bb.getNextBB()){
            Instruction inst = bb.getTerminator();
            if(inst instanceof BranchJump){
                Operand cond  = ((BranchJump) inst).getCondition();
                if(cond instanceof ConstBool){
                    assert cond != null;
                    if(((ConstBool) cond).isValue()) ((BranchJump) inst).changeToNoBranch(true);
                    else ((BranchJump) inst).changeToNoBranch(false);
                    changed = true;
                }
                else if(((BranchJump) inst).getThenBlock() == ((BranchJump) inst).getElseBlock()){
                    ((BranchJump) inst).changeToNoBranch(true);
                    changed = true;
                }
            }
        }
        return changed;
    }
    private boolean eliminateSingleEntrancePHINodes(IRFunction function){
        //branches is a set, thus there is no duplicated entrances
        //we just need to deal with single entrance node
        boolean changed = false;
        for(BasicBlock bb = function.getEntranceBB(); bb != null; bb = bb.getNextBB()){
            Instruction inst = bb.getHead();
            //phi node only occurs at the beginning of block
            while(inst instanceof Phi){
                Instruction nxt = inst.getNxt();
                if(((Phi) inst).getBranches().size() == 1){
                    assert bb.getPredecessorBB().size() == 1;
                    ((Phi) inst).getRes().replaceUser(((Phi) inst).getBranches().iterator().next().getKey());
                    inst.remove();
                    changed = true;
                }
                inst = nxt;
            }
        }
        return changed;
    }



}
