package BackEnd;

import BackEnd.Instruction.*;
import BackEnd.Operands.*;
import util.Defines;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Stack;

public class AllSpilledAlooca {


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

        private HashSet<BackEnd.RegisterAlloca.Edge> adjSet;



    public AllSpilledAlooca(RiscModule module) {
        this.module = module;
    }

    public void run(){
            for(RiscFunction function: module.getFunctionList()){
                init(function);
                rewrite(function);
                assignColors(function);
                removeRedundantMove(function);
                stackSlotAlloca(function);
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
            spilledRegs.addAll(function.getVirtualSet());

        }



        private void addEdge(RiscRegister u,RiscRegister v){
            if(u != v && !adjSet.contains(new BackEnd.RegisterAlloca.Edge(u,v))){
                adjSet.add(new BackEnd.RegisterAlloca.Edge(u,v));
                adjSet.add(new BackEnd.RegisterAlloca.Edge(v,u));
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


        private RiscRegister getAlias(RiscRegister n) {
            if (coalescentRegs.contains(n)) {
                RiscRegister alias = getAlias(n.getAlias());
                n.setAlias(alias);
                return alias;
            } else return n;
        }

        private void assignColors(RiscFunction function){
            selectStack.addAll(function.getVirtualSet());
            while(!selectStack.isEmpty()){
                RiscRegister n = selectStack.pop();
            if(n.getName().equals("sub.0"))
                System.out.println("mo");
                HashSet<PhysicalReg> okColors = new LinkedHashSet<>(RegisterTable.allocSet);
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
                    PhysicalReg c = okColors.iterator().next();
//                        selectColor(okColors);
                    n.setColor(c);
                }

            }

        }


        private void rewrite(RiscFunction function){
            for(RiscRegister reg: spilledRegs){
//                if(reg.getName().equals("sub.0"))
//                    System.out.print("anaf");
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
