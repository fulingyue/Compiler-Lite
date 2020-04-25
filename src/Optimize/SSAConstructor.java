package Optimize;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Module;

import FrontEnd.IR.Operand.*;
import FrontEnd.IR.Type.IRType;


import java.util.*;

public class SSAConstructor extends Pass{
//mem2reg

    private ArrayList<AllocateInst> allocaList;
    private HashMap<Register,Stack<Operand>> versionStack;//save those renaming registers



    public SSAConstructor(Module module) {
        super(module);
        changed = true;
    }

    @Override
    public boolean run() {
        for(IRFunction function: module.getFunctionMap().values()) {
            construct(function);
        }
        return true;
    }

    private  void construct(IRFunction function) {
        versionStack = new HashMap<>();
        //add blocks
        removeUnusedInst(function);
        allocaList = function.getAllocaInst();
        placingPhiNode(function);

        rename(function);

    }

    private void removeUnusedInst(IRFunction function) {
        BasicBlock currentBB = function.getEntranceBB();
        while(true) {
            Instruction inst = currentBB.getHead();

            while(inst != null) {
                if(inst instanceof AllocateInst) {
                    if(((AllocateInst) inst).getDest().getDefs().isEmpty()){
                        inst.remove();
                    }
                }
                if(inst instanceof Load) {
                    if(((Load) inst).getRes().getUsers().size() == 0)
                        inst.remove();
                }

                inst = inst.getNxt();
            }

            if(currentBB == function.getExitBB()) break;
            currentBB = currentBB.getNextBB();
        }
    }



    private void placingPhiNode(IRFunction function) {

        BasicBlock currentbb = function.getEntranceBB();
        while(true) {
            currentbb.setPhiMap(new HashMap<Register, Phi>());
//            if(currentbb.getName().equals("returnBlock.0")){
//                throw new RuntimeException();
//            }
            if(currentbb == function.getExitBB()) break;
            currentbb = currentbb.getNextBB();
        }

        for(AllocateInst allocate: allocaList) {
            //TODO: info.anlyzealloca:rewriteSingleStoreAlloca  and promoteSingleBlockAlloca

            Register addr = allocate.getDest();
            IRType type = allocate.getType();
            String name = ((VirtualReg)addr).getOriName();

            Set<BasicBlock> visited = new HashSet<>();
            LinkedList<BasicBlock> queue = new LinkedList<>();

//            HashMap<BasicBlock,HashMap<Register,Phi>> hasPhiInst = new HashMap<>();
            ////////body////////
            for(IRNode def: addr.getDefs()) {
                BasicBlock defBB = ((Instruction)def).getBasicBlock();
                if(!visited.contains(def)) {
                    queue.add(defBB);
                    visited.add(defBB);
                }
            }

            while(!queue.isEmpty()) {
                BasicBlock bb = queue.remove();

                for(BasicBlock df : bb.getDomianceFrontier()) {
                    if(!df.getPhiMap().containsKey(df)){
                        Register res = new VirtualReg(name,type);
                        function.getSymbolTable().put(res.getName(),res);
                        Phi  phi = new Phi("SSAphi",df,new LinkedHashSet<>(),res);
                        df.addPhi(addr,phi);
                        if(!visited.contains(df)) {
                            queue.add(df);
                            visited.add(df);
                        }
                    }
                }
            }


            if(!versionStack.containsKey(addr)){
                versionStack.put(addr,new Stack<>());
                versionStack.get(addr).push(type.getDefaultValue());
            }

            allocate.remove();
        }

    }

    private void elimateInst(IRFunction function) {
        for(BasicBlock bb = function.getEntranceBB();bb != null;bb = bb.getNextBB()){
            bb.deleteDeadInst();
        }
    }

    private void rename(IRFunction function) {
        recurRename(function.getEntranceBB());
    }

    private void recurRename(BasicBlock bb) {
        Map<Register,Phi> phiMap = bb.getPhiMap();

        for(Register reg: phiMap.keySet()) {
            Phi inst = phiMap.get(reg);
            versionStack.get(reg).push(inst.getRes());
        }

        Instruction inst = bb.getHead();
        //////replace load dests/////////
        while(inst != null) {
            if(inst instanceof Store) {
                Operand dest = ((Store)inst).getDest();
                Operand value = ((Store) inst).getValue();

                if(versionStack.get(dest) != null){
                    versionStack.get(dest).add(value);
                }

            }
            else if(inst instanceof Load) {
                Operand dest = ((Load) inst).getDest();//address

                Operand addr = ((Load) inst).getRes();

                if(versionStack.containsKey(dest)){
                    Operand newOpe = getFront(dest);
                    addr.replaceUser(newOpe);
                }


            }
            inst = inst.getNxt();
        }
        //////////add phi entries/////////
        Set<BasicBlock> successors =  bb.getSuccessors();
        for(BasicBlock succ: successors) {
            Map<Register,Phi> succPhiMap = succ.getPhiMap();
            for(Register reg: succPhiMap.keySet()) {
                Phi phi = succPhiMap.get(reg);
                if(!versionStack.get(reg).isEmpty())
                    phi.addBr(getFront(reg),bb);

            }
        }
        /////////recursion/////////
        ArrayList<BasicBlock> doms = bb.getDominance();
        for(BasicBlock child: doms) {
            recurRename(child);
        }


        for(Phi phi:bb.getPhiMap().values())  {
            bb.addFirstInst(phi);
        }

        inst = bb.getHead();
        while(inst!= null) {
            if(inst instanceof Store) {
                Operand dest = ((Store) inst).getDest();
                if(versionStack.containsKey(dest)){
                    versionStack.get(dest).pop();
                    inst.remove();
                }
            }
            else if(inst instanceof Load){
                Operand dest = ((Load) inst).getDest();
                if(versionStack.containsKey(dest))
                    inst.remove();
            }
            inst=inst.getNxt();
        }

        for(Register reg: bb.getPhiMap().keySet()){
            versionStack.get(reg).pop();
        }


    }


    private Operand getFront(Operand dest) {
        assert versionStack.containsKey(dest);
        Stack<Operand> stack = versionStack.get(dest);
        assert (!stack.isEmpty());
        return stack.peek();
    }





















}
