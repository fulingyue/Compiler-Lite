package Optimize;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Instruction.CallFunction;
import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Instruction.Return;
import FrontEnd.IR.Instruction.Store;
import FrontEnd.IR.Module;

import java.util.HashSet;
import java.util.LinkedList;



public class DeadCodeElim extends Pass{
    LinkedList<Instruction> workList;
    HashSet<Instruction> alive;
    HashSet<BasicBlock> visited;
    public DeadCodeElim(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        boolean changed = false;
        for(IRFunction function: module.getFunctionMap().values()){
            changed |= run(function);
        }

        return changed;
    }

    private boolean run(IRFunction function){
        visited = new HashSet<>();
        workList = new LinkedList<>();
        alive = new HashSet<>();
        init(function);
        markLiveInst(function);
        return removeDeadInst(function);
    }


    private void init(IRFunction function){
        function.calcSideEffect();
        for(BasicBlock bb = function.getEntranceBB(); bb != null; bb = bb.getNextBB()){
            addLiveInst(bb);
        }

    }

    private void markLiveInst(IRFunction function){
        while(!workList.isEmpty()){
            Instruction inst = workList.poll();
            inst.markLive(workList,alive);

            for(BasicBlock bb: inst.getBasicBlock().getPredecessorBB()){
                if(bb.getTail() == null){
                    throw new RuntimeException("tail is null");
                }
                push(bb.getTail());
            }
        }
    }

    private boolean removeDeadInst(IRFunction function){
        boolean changed = false;
        for(BasicBlock bb = function.getEntranceBB(); bb != null; bb = bb.getNextBB()){
            changed |= removeDeadInst(bb);
        }
        return changed;
    }
    /////utils///////
    private void addLiveInst(BasicBlock bb){
        for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNxt()){
            if(inst instanceof Store || inst instanceof Return){
                push(inst);
            }
            else if(inst instanceof CallFunction){
                if(((CallFunction)inst).getFunction().isSideEffect()) {
                    push(inst);
                }
            }
        }
    }

    private boolean removeDeadInst(BasicBlock bb) {
        boolean changed = false;
        Instruction inst = bb.getHead();
        while (inst != null){
            if(!alive.contains(inst)){
                inst.remove();
                changed = true;
            }
            inst = inst.getNxt();
        }
        return changed;
    }


    private void push(Instruction instruction){
        if(!alive.contains(instruction)) {
            alive.add(instruction);
            workList.add(instruction);
            instruction.markLive(workList,alive);
        }
    }
}
