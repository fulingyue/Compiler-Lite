package BackEnd;

import BackEnd.Instruction.*;
import BackEnd.Operands.*;
import util.Defines;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Stack;

public class RegisterAlloca {
    public static class Edge{
        private RiscRegister first,second;

        public Edge(RiscRegister first, RiscRegister second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int hashCode() {
            return first.hashCode() ^ second.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof Edge)
                    && first.equals(((Edge) other).first)
                    && second.equals(((Edge) other).second);
        }

        public RiscRegister getFirst() {
            return first;
        }

        public void setFirst(RiscRegister first) {
            this.first = first;
        }

        public RiscRegister getSecond() {
            return second;
        }

        public void setSecond(RiscRegister second) {
            this.second = second;
        }
    }

    private int K = RegisterTable.allocableSize;

    private RiscModule module;
    private HashSet<RiscRegister> initial;
    private LinkedHashSet<RiscRegister> simplifyList;
    private LinkedHashSet<RiscRegister> freezeList;
    private LinkedHashSet<RiscRegister> spillList;
    private HashSet<RiscRegister> spilledRegs;
    private HashSet<RiscRegister> coalescentRegs;
    private HashSet<RiscRegister> coloredRegs;
    private Stack<RiscRegister> selectStack;


    private HashSet<Move> coalescentMoves;
    private HashSet<Move> constrainedMoves;
    private HashSet<Move> freezeMoves;
    private LinkedHashSet<Move> workListMoves;
    private HashSet<Move> activeMoves;

    private HashSet<Edge> adjSet;

    public RegisterAlloca(RiscModule module) {
        this.module = module;
    }

    public void run(){
        for(RiscFunction function: module.getFunctionList()){
            while(true){
                init(function);
                calcSpillCost(function);
                new LivenessAnalysis(function).run();
                build(function);
                makeWorkList();
                while(!simplifyList.isEmpty() ||
                !workListMoves.isEmpty() ||
                !freezeList.isEmpty() ||
                !spillList.isEmpty()){
                    if(!simplifyList.isEmpty()) simplify();
                    else if(!workListMoves.isEmpty()) coalesce();
                    else if(!freezeList.isEmpty()) freeze();
                    else  selectSpill();
                }

                assignColors();
                if(spilledRegs.isEmpty())
                    break;
                else rewrite(function);
            }

            removeRedundantMove(function);
            stackSlotAlloca(function);
        }
    }

    private void calcSpillCost(RiscFunction function){
        for(RiscBB bb: function.getBlocks()){
            bb.setSpillCost((bb.getSuccessors().size() + bb.getPredecessors().size()/2));
        }
    }


    public void init(RiscFunction function){
        initial = new HashSet<>();
        simplifyList = new LinkedHashSet<>();
        freezeList = new LinkedHashSet<>();
        spillList = new LinkedHashSet<>();
        spilledRegs = new HashSet<>();
        coalescentRegs = new HashSet<>();
        coloredRegs = new HashSet<>();
        selectStack = new Stack<>();

        coalescentMoves = new HashSet<>();
        constrainedMoves = new HashSet<>();
        freezeMoves = new HashSet<>();
        workListMoves = new LinkedHashSet<>();
        activeMoves = new HashSet<>();
        adjSet = new HashSet<>();


        initial.addAll(function.getVirtualSet());
        for(RiscRegister reg: initial){
            reg.clearColor();
        }

        for(int i = 0;i < 32; ++i){
            RegisterTable.registers[i].setDegree(Defines.INF);
        }

    }


    private void build(RiscFunction function){
        for(RiscBB bb:function.getDfs()){
            HashSet<RiscRegister> live = bb.getLiveOut();

            for(RiscInstruction inst = bb.getTail(); inst != null; inst = inst.getPrev()){
                HashSet<RiscRegister> use = inst.getUsages(), def = inst.getDef();
                if(inst instanceof Move){
                    live.removeAll(use);
                    HashSet<RiscRegister> bing = new HashSet<>(use);
                    bing.addAll(def);
                    for(RiscRegister reg: bing){
                        reg.addMove((Move)inst);
                    }
                    workListMoves.add((Move)inst);
                }
                live.add(RegisterTable.zero);
                live.addAll(def);

                for(RiscRegister reg: def){
                    for(RiscRegister l: live){
                        addEdge(l,reg);
                    }
                }

                live.removeAll(def);
                live.addAll(use);
            }
        }
    }

    private void addEdge(RiscRegister u,RiscRegister v){
        if(u != v && !adjSet.contains(new Edge(u,v))){
            adjSet.add(new Edge(u,v));
            adjSet.add(new Edge(v,u));
            if(!preColored(u)){
                u.addAdj(v);
                u.addDegree();
            }
            if(!preColored(v)){
                v.addAdj(u);
                v.addDegree();
            }
        }
    }

    private boolean preColored(RiscRegister reg){
        return reg instanceof PhysicalReg;
    }

    private void makeWorkList(){
        for(RiscRegister reg: initial){
            if(reg.getDegree() >= K){
                spillList.add(reg);
            }
            else if(moveRelated(reg))
                freezeList.add(reg);
            else simplifyList.add(reg);
        }
    }

    private boolean moveRelated(RiscRegister reg){
        assert reg != null;
        return !nodeMoves(reg).isEmpty();
    }
    private HashSet<Move> nodeMoves(RiscRegister n){
        assert n != null;
        HashSet<Move> tmp = new HashSet<>(activeMoves);
        tmp.addAll(workListMoves);
        tmp.retainAll(n.getMoveList());
        return tmp;
    }

    private HashSet<RiscRegister> adjacent(RiscRegister reg){
        assert reg != null;
        HashSet<RiscRegister> res = new HashSet<>(reg.getAdjList());
        res.removeAll(selectStack);
        res.removeAll(coalescentRegs);
        return res;
    }

    private void simplify(){
        assert !simplifyList.isEmpty();
        RiscRegister reg = simplifyList.iterator().next();
        simplifyList.remove(reg);
        selectStack.push(reg);
        for(RiscRegister m: adjacent(reg))
            decrementDegree(m);
    }

    private void decrementDegree(RiscRegister m){
        int d = m.getDegree();
        m.setDegree(d-1);
        if(d == K){
            HashSet<RiscRegister> nodes = new HashSet<>(adjacent(m));
            nodes.add(m);
            enableMoves(nodes);
            spillList.remove(m);
            if(moveRelated(m)){
                freezeList.add(m);
            }
            else simplifyList.add(m);
        }
    }

    private void enableMoves(HashSet<RiscRegister> nodes){
        for(RiscRegister n: nodes){
            for(Move m: nodeMoves(n)){
                if(activeMoves.contains(m)){
                    activeMoves.remove(m);
                    workListMoves.add(m);
                }
            }
        }
    }

    private void addWorkList(RiscRegister u){
        assert u!=null;
        if(!preColored(u) && !(moveRelated(u)) && u.getDegree() <  K){
            freezeList.remove(u);
            simplifyList.add(u);
        }
    }

    private boolean OK(RiscRegister t,RiscRegister r){
        assert t != null && r != null;
        return t.getDegree()< K || preColored(t) || adjSet.contains(new Edge(t,r));
    }

    private boolean conservative(HashSet<RiscRegister> nodes){
        int k = 0;
        for(RiscRegister n:nodes){
            if(n.getDegree() >= K)
                k++;
        }
        return k < K;
    }

    private void coalesce(){
        Move m = workListMoves.iterator().next();
        workListMoves.remove(m);
        assert m != null;
        RiscRegister x = getAlias(m.getRd());
        RiscRegister y = getAlias(m.getRs());
        RiscRegister u,v;
        if(preColored(y)){
            u =  y;
            v = x;
        }
        else {
            u = x;
            v = y;
        }


        if(u  == v){
            coalescentMoves.add(m);
            addWorkList(u);
        }
        else if(preColored(v) || adjSet.contains(new Edge(u,v))){
            constrainedMoves.add(m);
            addWorkList(u);
            addWorkList(v);
        }
        else if((preColored(u) && alltIsOK(v,u)) || (!preColored(u)  && conservative(unionAdj(u,v)))){
            coalescentMoves.add(m);
            combine(u,v);
            addWorkList(u);
        }
        else activeMoves.add(m);

    }

    private void combine(RiscRegister u,RiscRegister v){
        if(freezeList.contains(v)){
            freezeList.remove(v);
        }
        else spillList.remove(v);
        coalescentRegs.add(v);
        v.setAlias(u);
        u.getMoveList().addAll(v.getMoveList());
        HashSet<RiscRegister> nodes = new HashSet<>();
        nodes.add(v);
        enableMoves(nodes);

        for(RiscRegister t: adjacent(v) ){
            addEdge(t,u);
            decrementDegree(t);
        }
        if(u.getDegree() >=K && freezeList.contains(u)){
            freezeList.remove(u);
            spillList.add(u);
        }
    }
    private boolean alltIsOK(RiscRegister v, RiscRegister u){
        for(RiscRegister t: adjacent(v)){
            if(!OK(t,u))  return false;
        }
        return true;
    }

    private HashSet<RiscRegister> unionAdj(RiscRegister u,RiscRegister v){
        HashSet<RiscRegister> ret= new HashSet<>(adjacent(u));
        ret.addAll(adjacent(v));
        return ret;
    }

    private RiscRegister getAlias(RiscRegister n){
        if(coalescentRegs.contains(n)){
            RiscRegister alias = getAlias(n.getAlias());
            n.setAlias(alias);
            return alias;
        }
        else return n;
    }
    private void freeze(){
        RiscRegister u = freezeList.iterator().next();
        freezeList.remove(u);
        simplifyList.add(u);
        freezeMoves(u);
    }

    private void freezeMoves(RiscRegister u){
        for(Move m: nodeMoves(u)){
            RiscRegister x = m.getRd();
            RiscRegister y = m.getRs();

            RiscRegister v;
            if(getAlias(y) == getAlias(u)){
                v = getAlias(x);
            }
            else
                v = getAlias(y);

            activeMoves.remove(m);
            freezeMoves.add(m);

            if(freezeList.contains(v) && nodeMoves(v).isEmpty()){
                freezeList.remove(v);
                simplifyList.add(v);
            }
        }
    }
    private void selectSpill(){
        RiscRegister m = regSelectSpill();
        assert m != null;
        spillList.remove(m);
        simplifyList.add(m);
        freezeMoves(m);
    }

    private RiscRegister regSelectSpill(){
        double min = Double.POSITIVE_INFINITY;
        RiscRegister res = null;
        for(RiscRegister reg: spillList){
            double cost = reg.getSpilledCost();
            if(cost <= min) {
                res = reg;
                min = cost;
            }
        }
        assert res != null;

        return res;
    }

    private void assignColors(){
        while(!selectStack.isEmpty()){
            RiscRegister n = selectStack.pop();
//            if(n.getName().contains("spill"))
//                System.out.println("mo");
            LinkedHashSet<PhysicalReg> okColors = new LinkedHashSet<>();
            okColors.addAll(RegisterTable.allocSet);
            HashSet<RiscRegister> adjs = n.getAdjList();
            for(RiscRegister w:adjs){
                if(coloredRegs.contains(getAlias(w)) || preColored(getAlias(w))){
                    okColors.remove(getAlias(w).getColor());
                }
            }
            if(okColors.isEmpty())
                spilledRegs.add(n);
            else {
                coloredRegs.add(n);
                PhysicalReg c =
                    selectColor(okColors);
                n.setColor(c);
            }

        }
        for(RiscRegister n: coalescentRegs){
            n.setColor(getAlias(n).getColor());
        }
    }

    private PhysicalReg selectColor(LinkedHashSet<PhysicalReg> okColors){
        assert !okColors.isEmpty();
        for(int i = 1; i < 16; i++){
            if(okColors.contains(RegisterTable.callerSavedRegisters[i]))
                return RegisterTable.callerSavedRegisters[i];
        }
        return okColors.iterator().next();
    }

    private void rewrite(RiscFunction function){
        for(RiscRegister reg: spilledRegs){
            StackOffset stackLocation = new StackOffset(reg.getName());
            assert reg instanceof VirtualReg;
            function.getStackFrame().getTemporaryVar().put((VirtualReg)reg,stackLocation);
            HashSet<RiscInstruction> defs = new HashSet<>(reg.getDef());
            int cnt = 0;
            for(RiscInstruction def:defs){
                RiscRegister spill = function.addRegister(reg.getName() + ".spill." + cnt);
                def.replaceDef(reg,spill);
                RiscBB currentBB = def.getParentBB();
                currentBB.insertNext(def,new Stype(currentBB,spill,stackLocation, Stype.BSize.sw));

                cnt++;
            }
            HashSet<RiscInstruction> uses = new HashSet<>(reg.getUse());
            for(RiscInstruction use:uses){
                RiscRegister spill = function.addRegister(reg.getName() + ".spill." + cnt);
                use.replaceUse(reg,spill);
                RiscBB currentBB = use.getParentBB();
                cnt++;
                currentBB.insertPrev(use, new ILoad(currentBB,spill,stackLocation, ILoad.LoadType.lw));
            }

            assert reg.getDef().isEmpty() && reg.getUse().isEmpty();
            function.getVirtualSet().remove(reg);
        }

    }


    private void removeRedundantMove(RiscFunction function){
        for(RiscBB bb: function.getDfs()){
            RiscInstruction inst = bb.getHead();
            while(inst != null) {
                RiscInstruction nxt = inst.getNext();
                for(RiscRegister check: inst.getDef())
                    assert check.getColor() != null;
                for(RiscRegister check: inst.getUsages())
                    assert check.getColor() != null;
                if(inst instanceof Move && ((Move) inst).getRd().getColor() ==  ((Move) inst).getRs().getColor()){
                    ((Move) inst).remove();
                }
                inst = nxt;
            }
        }
    }

    private void stackSlotAlloca(RiscFunction function){
        function.getStackFrame().compute();
        int frameSize = function.getStackFrame().getSize();
        if(frameSize == 0) return;
        RiscRegister sp = RegisterTable.sp;
        RiscBB currentBB = function.getEntranceBB();
        currentBB.insertPrev(currentBB.getHead(), new ImmOperation(
                currentBB, ImmOperation.IOp.addi, sp, sp, new Immidiate(-frameSize * 4)
        ));

        for(RiscBB bb:function.getBlocks()){
            if(bb.getTail() instanceof Return){
                bb.insertPrev(bb.getTail(), new ImmOperation(bb, ImmOperation.IOp.addi,sp,sp,new Immidiate(frameSize * 4)));
            }
        }
    }

}
