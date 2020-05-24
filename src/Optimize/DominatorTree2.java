package Optimize;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Module;
import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class DominatorTree2 extends Pass {
    private HashMap<BasicBlock, Pair<BasicBlock,BasicBlock>> unionFindSet;
    public DominatorTree2(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for(IRFunction function: module.getFunctionMap().values()) {
            if(function.isNotFunctional())
                return false;
        }
        for(IRFunction function: module.getFunctionMap().values()){
            check(function);
            constructDT(function);
            constructDF(function);
        }
        return true;
    }

    private void check(IRFunction function){
        HashSet<BasicBlock> visited = new HashSet<>();
        System.out.println("List:");
        for(BasicBlock bb = function.getEntranceBB(); bb != null; bb = bb.getNextBB())
            System.out.println(bb.getName());
        System.out.println("Dfs:");
        for(BasicBlock bb: function.gettDfsOder())
            System.out.println(bb.getName());

        System.out.println("SUcc:");
        for(BasicBlock bb = function.getEntranceBB(); bb != null; bb = bb.getNextBB()){
            System.out.print(bb.getName() + ":\t");
            for(BasicBlock succ: bb.getSuccessors())
                System.out.print(succ.getName() + ",\t");

            System.out.print("\n");
        }

        System.out.println("Prec:");
        for(BasicBlock bb = function.getEntranceBB(); bb != null; bb = bb.getNextBB()){
            System.out.print(bb.getName() + ":\t");
            for(BasicBlock succ: bb.getPredecessorBB())
                System.out.print(succ.getName() + ",\t");

            System.out.print("\n");
        }

    }

    //////union find utils//////
    private Pair<BasicBlock,BasicBlock>find(BasicBlock bb) {
        Pair<BasicBlock,BasicBlock> pair = unionFindSet.get(bb);
        if(bb == pair.getFirst())
            return new Pair<>(bb,bb);

        Pair<BasicBlock,BasicBlock> ret = find(pair.getFirst());
        BasicBlock father = ret.getFirst();
        BasicBlock minSemiDfsNode = ret.getValue();

        pair.setKey(father);
        if(minSemiDfsNode.getSemiDom().getDfsOrd() < pair.getValue().getSemiDom().getDfsOrd())
            pair.setValue(minSemiDfsNode);
        return pair;

    }

    private void constructDT(IRFunction function){
        ArrayList<BasicBlock> dfsBB = function.gettDfsOder();
        unionFindSet = new HashMap<>();
        for(BasicBlock bb: dfsBB){
            unionFindSet.put(bb,new Pair<>(bb,bb));
            bb.setiDom(null);
            bb.setSemiDom(bb);
            bb.setBucket(new LinkedHashSet<>());
        }

        for(int i = dfsBB.size() - 1; i > 0; i--){
            BasicBlock bb = dfsBB.get(i);
            for (BasicBlock prec: bb.getPredecessorBB()){
                if(prec.getDfsOrd() <  bb.getDfsOrd()){
                    if(prec.getDfsOrd() < bb.getSemiDom().getDfsOrd())
                        if (prec == null)
                            throw new RuntimeException();
                        bb.setSemiDom(prec);
                }
                else {
                    Pair<BasicBlock,BasicBlock> update = find(prec);
                    if(update.getSecond().getSemiDom().getDfsOrd() < bb.getSemiDom().getDfsOrd()){
                        if(update.getSecond().getSemiDom() == null)
                            throw new RuntimeException();
                        bb.setSemiDom(update.getSecond().getSemiDom());
                    }

                }
            }
            BasicBlock father = bb.getDfsFather();
            if(bb.getSemiDom() == null)
                throw new RuntimeException();
            if(bb.getSemiDom().getBucket() == null){
                System.out.println("\nwhat" + bb.getSemiDom().getName());
            }

            bb.getSemiDom().getBucket().add(bb);
            unionFindSet.get(bb).setKey(father);

            for(BasicBlock child : father.getBucket()){
                Pair<BasicBlock,BasicBlock> update = find(child);
                if(update.getSecond().getSemiDom() == child.getSemiDom())
                    child.setiDom(child.getSemiDom());
                else
                    child.setiDom(update.getSecond());
            }
        }
        for(int i =1;i < dfsBB.size();++i){
            BasicBlock bb  = dfsBB.get(i);
            if(bb.getiDom() != bb.getSemiDom())
                bb.setiDom(bb.getiDom().getiDom());
        }

        for (BasicBlock bb:dfsBB){
            HashSet<BasicBlock> strictDom = new HashSet<>();
            BasicBlock ptr = bb.getiDom();
            while(ptr != null){
                strictDom.add(ptr);
                ptr = ptr.getiDom();
            }
            bb.setStrictDominators(strictDom);
        }
    }

    private void constructDF(IRFunction function){
        for(BasicBlock bb= function.getEntranceBB(); bb != null; bb = bb.getNextBB()){
            bb.setDomianceFrontier(new HashSet<>());
        }

        for(BasicBlock bb= function.getEntranceBB(); bb != null; bb = bb.getNextBB()){
            for(BasicBlock prec : bb.getPredecessorBB()){
                BasicBlock ptr=  prec;
                while(!bb.getStrictDominators().contains(ptr) && ptr != null){
                    ptr.getDomianceFrontier().add(bb);
                    ptr = ptr.getiDom();
                }
            }
        }
    }
}
