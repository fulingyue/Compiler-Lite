package Optimize;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Operand.VirtualReg;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Type.PtrType;

import java.util.*;

public class SSAConstructor2 extends Pass {

    private Map<Operand, Stack<Operand>> renameTable;

    public SSAConstructor2(Module module) {
        super(module);
    }


    @Override
    public boolean run() {
        renameTable = new HashMap<>();
        for(IRFunction function: module.getFunctionMap().values())
            construct(function);
        return false;
    }

    private void construct(IRFunction function) {

        for (BasicBlock bb = function.getEntranceBB(); bb != null; bb = bb.getNextBB()) {
            for (Instruction inst = bb.getHead(); inst != null; inst = inst.getNxt()) {
                if (inst instanceof Load) {
                    if (((Load) inst).getRes().getDefs().isEmpty())
                        inst.remove();
                } else if (inst instanceof AllocateInst) {
                    Register addr = ((AllocateInst) inst).getDest();
                    String name = addr.getName().split("\\$")[0];
                    IRType type = ((PtrType) addr.getType()).getPointerType();

                    if (addr.getDefs().isEmpty())
                        inst.remove();

                    ArrayList<IRNode> useList = addr.getUsers();
                    Queue<BasicBlock> queue = new LinkedList<>();
                    HashSet<BasicBlock> visited = new HashSet<>();
                    HashSet<BasicBlock> phiSet = new HashSet<>();
                    for (IRNode use : useList) {
                        assert use instanceof Instruction;
                        if (use instanceof Store && ((Store) use).getDest() == addr) {
                            BasicBlock defBB = ((Store) use).getBasicBlock();
                            if (!visited.contains(defBB)) {
                                queue.add(defBB);
                                visited.add(defBB);
                            }
                        }
                    }

                    while (!queue.isEmpty()) {
                        BasicBlock front = queue.poll();
                        HashSet<BasicBlock> domianceFrontier = front.getDomianceFrontier();
                        for (BasicBlock df : domianceFrontier) {
                            if (!phiSet.contains(df)) {
                                VirtualReg res = new VirtualReg(name, type);
                                function.getSymbolTable().put(res.getName(), res);
                                Phi phi = new Phi("", df, new LinkedHashSet<>(), res);
                                df.addPhi(addr, phi);
                                queue.add(df);
                                phiSet.add(df);
                            }
                        }
                    }
                    renameTable.put(addr, new Stack<>());
                    push(addr, type.getDefaultValue());
                    inst.remove();
                }

            }
        }

        rename(function.getEntranceBB());

        for(BasicBlock bb = function.getEntranceBB(); bb != null; bb = bb.getNextBB()){
            bb.mergePhiMap();
        }

    }
    private void push(Operand address, Operand res) {
        renameTable.get(address).push(res);
    }

    private Operand getFront(Operand address){
        Stack<Operand> stack = renameTable.get(address);
        return stack.peek();
    }

    private void rename(BasicBlock bb){
        Map<Register,Phi> phiMap = bb.getPhiMap();

        for(Register reg: phiMap.keySet()) {
            Phi inst = phiMap.get(reg);
            push(reg, inst.getRes());
        }

        Instruction inst = bb.getHead();
        //////replace load dests/////////
        while(inst != null) {
            if(inst instanceof Store) {
                Operand dest = ((Store)inst).getDest();
                Operand value = ((Store) inst).getValue();

                if(renameTable.containsKey(dest)){
                    push(dest,value);
                }

            }
            else if(inst instanceof Load) {
               Operand dest = ((Load) inst).getDest();//address

                Operand addr = ((Load) inst).getRes();

                if(renameTable.containsKey(dest)){
                    Operand newOpe = getFront(dest);
                    addr.replaceUser(newOpe);
                }


            }
            inst = inst.getNxt();
        }

        Set<BasicBlock> successors =  bb.getSuccessors();
        for(BasicBlock succ: successors) {
            Map<Register,Phi> succPhiMap = succ.getPhiMap();
            for(Register reg: succPhiMap.keySet()) {
                Phi phi = succPhiMap.get(reg);
                if(!renameTable.get(reg).isEmpty())
                    phi.addBr(getFront(reg),bb);

            }
        }

        ArrayList<BasicBlock> doms = bb.getDominance();
        for(BasicBlock child: doms) {
            rename(child);
        }
        inst = bb.getHead();

        for(; inst != null; inst = inst.getNxt()){
            if(inst instanceof Store) {
               Operand dest = ((Store) inst).getDest();
                if(renameTable.containsKey(dest)){
                    renameTable.get(dest).pop();
                    inst.remove();
                }
            }
            else if(inst instanceof Load){
                Operand dest = ((Load) inst).getDest();
                if(renameTable.containsKey(dest))
                    inst.remove();
            }

        }

        for(Register reg: bb.getPhiMap().keySet()){
            renameTable.get(reg).pop();
        }



    }


}
