package Optimize;

import BackEnd.Instruction.Move;
import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Instruction.BranchJump;
import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Instruction.MoveInst;
import FrontEnd.IR.Instruction.Phi;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Operand.VirtualReg;
import util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SSADestructor extends Pass {


    public SSADestructor(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for(IRFunction function: module.getFunctionMap().values()){
            CriticalEdgeSplit(function);
            SequentializeParallelCopy(function);
        }
        return false;
    }

    private void CriticalEdgeSplit(IRFunction function){
        for(BasicBlock bb = function.getEntranceBB(); bb != null; bb = bb.getNextBB()){
            if(bb.getPredecessorBB().size() == 0) continue;
            Set<BasicBlock> precs = new HashSet<>(bb.getPredecessorBB());
            ArrayList<Phi> phiList = bb.getPhiList();

            if(phiList.size() == 0) continue;
            else if(precs.size() == 1){
                for(Phi phi: phiList){
                    assert phi.getBranches().size() == 1;
                    phi.getRes().replaceUser(phi.getBranches().iterator().next().getKey());
                    phi.remove();
                }
                continue;
            }

            for(Phi phi: phiList){
                Register res = phi.getRes();
                for(Pair<Operand,BasicBlock> item: phi.getBranches()) {
                    BasicBlock prec = item.getValue();
                    Operand  op = item.getKey();
                    if(prec.getSuccessors().size()  ==  1){
                        prec.addMove(new MoveInst("move",prec,op,res));
                    }
                    else {
                        BasicBlock newBB = new BasicBlock("criticalEdge", function);
                        function.addBB(newBB);
                        newBB.addMove(new MoveInst("movee",newBB, op,res));
                        newBB.addInst(new BranchJump("brPrev",newBB,null,bb,null));

                        ///relink////
                        prec.getSuccessors().remove(bb);
                        prec.addSuccessorBB(newBB);
                        bb.getPredecessorBB().remove(prec);
                        bb.addPredecessorBB(newBB);

                        for(Instruction inst= bb.getHead(); inst instanceof Phi; inst = inst.getNxt()){
                            inst.replaceUse(prec,newBB);
                        }

                        assert bb.getTail() instanceof BranchJump;
                        prec.getTail().replaceUse(bb,newBB);

                    }
                }

                phi.remove();
            }
        }
    }

    private void SequentializeParallelCopy(IRFunction function){
        for(BasicBlock bb = function.getEntranceBB(); bb!= null; bb =  bb.getNextBB()){
            while(bb.getMoveList().size() !=0){
                MoveInst move = bb.findValidMove();
                if(move != null){
                    bb.mergeMove(move);
                }
                else {
                    move = bb.getMoveList().get(0);
                    Operand src =  move.getSrc();
                    Register reg = new VirtualReg("src",src.getType());
                    function.getSymbolTable().put(reg.getName(),reg);
                    bb.mergeMove(new MoveInst("tmpmove",bb,src,reg));
                    move.setSrc(reg);
                }
            }
        }
    }
}
