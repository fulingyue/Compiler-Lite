package Optimize;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.Parameter;
import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class DominatorTree extends Pass{

    private HashMap<BasicBlock, Pair<BasicBlock,BasicBlock>> unionFindSet;

    public DominatorTree(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for(IRFunction function: module.getFunctionMap().values()) {
//            printBBs(function);
            TarjanBuild(function);
            calcDominanceFrontier(function);
        }
        return true;
    }

///Lengauer Tarjan Alg
    void TarjanBuild(IRFunction function) {
        ArrayList<BasicBlock> dfsOrd = function.gettDfsOder();
        unionFindSet = new HashMap<>();
//        ////init//////
        for (BasicBlock bb : dfsOrd) {
            unionFindSet.put(bb, new Pair<>(bb, bb));
            bb.setiDom(null);
            bb.setBucket(new HashSet<>());
            bb.setSemiDom(bb);
        }
//
        for (int i = dfsOrd.size() - 1; i > 0; --i) {
            BasicBlock bb = dfsOrd.get(i);
            Set<BasicBlock> prede = bb.getPredecessorBB();
            for (BasicBlock item : prede) {
                Pair<BasicBlock, BasicBlock> u = find(item);
                if (u.getValue().getSemiDom().getDfsOrd() < bb.getSemiDom().getDfsOrd()) {
                    bb.setSemiDom(u.getValue().getSemiDom());
                }
            }

            bb.getSemiDom().getBucket().add(bb);
            BasicBlock parent = bb.getDfsFather();
            link(parent, bb);
            for(BasicBlock v: bb.getDfsFather().getBucket()){
                Pair<BasicBlock,BasicBlock> u = find(v);
                if(u.getValue().getSemiDom().getDfsOrd() < v.getSemiDom().getDfsOrd()){
                    v.setiDom(u.getValue());
                }
                else {
                    v.setiDom(parent);
                }
            }
            parent.getBucket().clear();
        }

        for(BasicBlock w: dfsOrd) {
            if(w.getDfsOrd() == 0) continue;
            BasicBlock idom = w.getiDom();
            if(w.getiDom() != w.getSemiDom()) {
                idom = w.getiDom().getiDom();
                w.setiDom(idom);
            }
            idom.addDominance(w);
            while(idom!= null) {
                w.getStrictDominators().add(idom);
                idom = idom.getiDom();
            }
        }
    }

    private void link(BasicBlock v, BasicBlock w) {
        unionFindSet.get(v).setKey(w);
    }
    private void calcDominanceFrontier(IRFunction function) {
        BasicBlock currentBB = function.getEntranceBB();

        while(currentBB != null) {
            for (BasicBlock predecessor : currentBB.getPredecessorBB()) {
                while (predecessor != null && !currentBB.getStrictDominators().contains(predecessor)) {
                    predecessor.getDomianceFrontier().add(currentBB);
                    predecessor = predecessor.getiDom();
                }
            }

            currentBB = currentBB.getNextBB();
        }
    }

    public void print() {
        for(IRFunction function: module.getFunctionMap().values()) {
            BasicBlock bb = function.getEntranceBB();
            System.out.print(function.getName() + ":\n");
            while(true) {
                System.out.print(bb.getName());
                System.out.print("----->");
                for(BasicBlock domiance: bb.getDominance()) {
                    System.out.print(",\t" + domiance.getName());
                }
                System.out.print("\n");
                if(bb == function.getExitBB()) break;
                bb = bb.getNextBB();
                System.out.print("next:" +bb.getName()+"\n");
            }
        }
    }



    //////union find utils//////
    private Pair<BasicBlock,BasicBlock>find(BasicBlock bb) {
        Pair<BasicBlock,BasicBlock> parent = unionFindSet.get(bb);
        if(bb == parent.getKey())
            return parent;
        Pair<BasicBlock,BasicBlock> ret = find(parent.getKey());
        BasicBlock minSemiDfsNode = ret.getValue();

        parent.setKey(ret.getKey());
        if(minSemiDfsNode.getSemiDom().getDfsOrd() < parent.getValue().getSemiDom().getDfsOrd())
            parent.setValue(minSemiDfsNode);
        return parent;

    }

    ////////debug use////////
    void printBBs(IRFunction function) {
        StringBuilder stringBuilder = new StringBuilder(function.getName());
        stringBuilder.append("\n");
        BasicBlock bb = function.getEntranceBB();
        while(true) {
            stringBuilder.append("    ").append(bb.getName()).append("\t precessdors:");
            for (BasicBlock item: bb.getPredecessorBB()){
                stringBuilder.append("\t").append(item.getName());
            }
            stringBuilder.append("\n");
            if(bb == function.getExitBB()) break;
            bb=bb.getNextBB();
        }
        System.out.print(stringBuilder.toString());
    }




}
