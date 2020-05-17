package BackEnd;

import BackEnd.Instruction.RiscInstruction;
import BackEnd.Operands.RiscRegister;

import java.util.ArrayList;
import java.util.HashSet;

public class LivenessAnalysis{
    RiscFunction function;
    public LivenessAnalysis(RiscFunction function){
        this.function = function;
    }


    public void run() {
        calcLive(function);
    }

    public void calcLive(RiscFunction function){
        ArrayList<RiscBB> dfs = function.getDfs();
        for(RiscBB bb:dfs){
            bb.clear();
            for(RiscInstruction inst = bb.getHead();inst!=null; inst =  inst.getNext()){
                HashSet<RiscRegister> useExceptDef = new HashSet<>(inst.getUsages());
                useExceptDef.removeAll(bb.getDef());

                bb.getUseExceptDef().addAll(useExceptDef);
                bb.getDef().addAll(inst.getDef());
            }
        }

        boolean changed  = true;
        while (changed){
            changed = false;
            for(int i = dfs.size() - 1; i >=0; --i){
                RiscBB bb = dfs.get(i);
                HashSet<RiscRegister> liveIn = bb.getLiveIn();
                HashSet<RiscRegister> liveOut = bb.getLiveOut();
                HashSet<RiscRegister> newLiveIn = new HashSet<>(liveOut);
                HashSet<RiscRegister> newLiveOut = new HashSet<>();
                newLiveIn.removeAll(bb.getDef());
                newLiveIn.addAll(bb.getUseExceptDef());
                for(RiscBB succ:bb.getSuccessors()){
                    newLiveOut.addAll(succ.getLiveIn());
                }
                if(!liveIn.equals(newLiveIn)){
                    bb.setLiveIn(newLiveIn);
                    changed = true;
                }
                if (!liveOut.equals(newLiveOut)) {
                    bb.setLiveOut(newLiveOut);
                    changed = true;
                }
            }


        }
    }
}
