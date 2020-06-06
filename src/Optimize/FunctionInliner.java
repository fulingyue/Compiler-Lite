package Optimize;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.*;
import util.Pair;

import java.util.*;

public class FunctionInliner extends Pass {
    private int instLimit = 120;
    private int instLimitAll = 2000;
    private HashMap<IRFunction, Integer> instCount;
    private HashMap<IRFunction, HashSet<IRFunction>> calleeMap;

    private HashMap<Operand,Operand> renameMap;
    public FunctionInliner(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        changed = false;

        removeUnusedFunc();
        instCount = new HashMap<>();
        calleeMap = new HashMap<>();

        for(IRFunction func: module.getFunctionMap().values())
            calleeMap.put(func,new HashSet<>());


        for(IRFunction func: module.getFunctionMap().values()){
            countInst(func);
        }

        for(IRFunction func: module.getFunctionMap().values()){
            computeCallee(func);
        }

        changed = notRecurInline();

        changed |= recurInline();
        removeUnusedFunc();
        return changed;
    }

    private void countInst(IRFunction function){
        int instructionCnt = 0;

        for (BasicBlock bb= function.getEntranceBB(); bb != null;  bb = bb.getNextBB()) {
            Instruction ptr = bb.getHead();
            while (ptr != null) {
                instructionCnt++;
                if (ptr instanceof CallFunction) {
                    IRFunction callee = ((CallFunction) ptr).getFunction();
                    if (!module.getExternalFuncMap().containsValue(callee)) {
                        calleeMap.get(function).add(callee);
                    }
                }
                ptr = ptr.getNxt();
            }
        }
        instCount.put(function, instructionCnt);
    }



    private void computeCallee(IRFunction function){
        Queue<IRFunction> queue = new LinkedList<>();
        HashSet<IRFunction> callees = calleeMap.get(function);
        for(IRFunction callee: callees)
            queue.offer(callee);

        while (!queue.isEmpty()){
            IRFunction poll = queue.poll();
            for(IRFunction callee: calleeMap.get(poll)){
                if(!callees.contains(callee)){
                    callees.add(callee);
                    queue.offer(callee);
                }
            }
        }

    }

    private boolean notRecurInline(){
        boolean changed = false;
        while (true){
            boolean whileChanged = false;
            for(IRFunction func:module.getFunctionMap().values()){
                for(BasicBlock  bb = func.getEntranceBB(); bb != null; bb = bb.getNextBB()){
                    Instruction inst = bb.getHead();
                    while(inst != null){
                        Instruction next = inst.getNxt();
                        if(inst instanceof CallFunction){
                            IRFunction callee = ((CallFunction) inst).getFunction();
                            if(module.getFunctionMap().containsValue(callee) && canBeNotRecInline(callee,func)){
                                next = inlineFunc((CallFunction)inst,func);
                                instCount.replace(func, instCount.get(func) + instCount.get(callee) -2);
                                whileChanged = true;
                            }
                        }
                        inst = next;
                    }
                }
            }

            if (whileChanged)
                changed = true;
            else break;
        }
        return changed;
    }

    private boolean recurInline(){
        boolean changed = false;
        int depth = 1;
        for(int i =0; i < depth; i++){
            for(IRFunction func:module.getFunctionMap().values()) {
                for (BasicBlock bb:func.getBlocks()) {
                    Instruction inst = bb.getHead();
                    while (inst != null) {
                        Instruction next = inst.getNxt();
                        if(inst instanceof CallFunction){
                            IRFunction callee =  ((CallFunction) inst).getFunction();
                            if(module.getFunctionMap().containsValue(callee) &&
                            canBeRecurInline(callee,func)){
                                next = inlineFunc((CallFunction)inst, func);
                                instCount.replace(func, instCount.get(func) + instCount.get(callee) -2);
                                changed = true;
                            }
                        }

                        inst = next;
                    }
                }
            }
        }
        return changed;
    }

    private Instruction inlineFunc(CallFunction callInst,IRFunction caller){
        System.out.println("\ninline "  + callInst.getFunction().getName() + " into " + caller.getName());
        IRFunction callee = callInst.getFunction();
        renameMap = new HashMap<>();

        Pair<ArrayList<BasicBlock>, Return> clone = cloneCallee(caller,callee,callInst);
        ArrayList<BasicBlock> cloneList = clone.getFirst();
        Return retInst = clone.getSecond();

        BasicBlock currentBB = callInst.getBasicBlock();
        BasicBlock splitBB = currentBB.split(callInst);
        int size = cloneList.size();
        BasicBlock entranceBB = cloneList.get(0);

        currentBB.setNextBB(entranceBB);
        entranceBB.setPrevBB(currentBB);

        BasicBlock tailBB = cloneList.get(size-1);
        splitBB.setPrevBB(tailBB);
        tailBB.setNextBB(splitBB);

        if(callee.getReturnVal() != null){
            callInst.getResult().replaceUser(retInst.getReturnVal());
        }

        retInst.remove();
        callInst.remove();

        currentBB.addInst(new BranchJump(currentBB,null,entranceBB,null));
        tailBB.addInst(new BranchJump(tailBB, null, splitBB, null));
        System.out.println("inline finish!!!");
        return splitBB.getHead();
    }

    private Pair<ArrayList<BasicBlock>, Return> cloneCallee(IRFunction caller,IRFunction callee, CallFunction callInst){

        HashMap<BasicBlock,BasicBlock> cloneBB = new LinkedHashMap<>();
        LinkedList<BasicBlock> blocks = callee.getBlocks();
        ArrayList<Parameter> parameters = callee.getParaList();
        ArrayList<Operand> arguments = callInst.getParameters();

        for(int i = 0; i < arguments.size(); ++i){
            renameMap.put(parameters.get(i), arguments.get(i));
        }



        for(BasicBlock bb:blocks){
            if(!cloneBB.containsKey(bb)) {
                BasicBlock newBB = new BasicBlock(bb.getName().split("\\.")[0] + ".copy", caller);
                cloneBB.put(bb, newBB);
            }
        }
        Return returnInst =null;

        for(BasicBlock block: blocks) {
            BasicBlock bb = cloneBB.get(block);
            for (Instruction inst = block.getHead(); inst != null; inst = inst.getNxt()) {
                if (inst instanceof AllocateInst) {
                    bb.addInst(new AllocateInst(bb, "alloca_copy", (Register) getName(caller, ((AllocateInst) inst).getDest()),
                            ((AllocateInst) inst).getType()));
                } else if (inst instanceof BinaryOp) {
                    bb.addInst(new BinaryOp(bb, (Register) getName(caller, ((BinaryOp) inst).getDest()),
                            ((BinaryOp) inst).getOp(), getName(caller, ((BinaryOp) inst).getLhs()),
                            getName(caller, ((BinaryOp) inst).getRhs())));
                } else if (inst instanceof BitCast) {
                    bb.addInst(new BitCast("bitcast_copy", bb, getName(caller, ((BitCast) inst).getSrc()),
                            ((BitCast) inst).getType(), (Register) getName(caller, ((BitCast) inst).getRes())));
                } else if (inst instanceof BranchJump) {
                    Operand cond = ((BranchJump) inst).getCondition();
                    if (cond == null) {
                        bb.addInst(new BranchJump(bb, null, cloneBB.get(((BranchJump) inst).getThenBlock()), null));
                    } else {
                        bb.addInst(new BranchJump(bb, getName(caller, cond),
                                cloneBB.get(((BranchJump) inst).getThenBlock()), cloneBB.get(((BranchJump) inst).getElseBlock())));
                    }
                } else if (inst instanceof CallFunction) {
                    ArrayList<Operand> oldArgu = ((CallFunction) inst).getParameters();
                    ArrayList<Operand> newArgu = new ArrayList<>();
                    for (Operand arg : oldArgu) {
                        newArgu.add(getName(caller, arg));
                    }
                    Register res = ((CallFunction) inst).getResult();
                    if (res == null) {
                        bb.addInst(new CallFunction(bb, ((CallFunction) inst).getFunction(), newArgu, null));
                    } else
                        bb.addInst(new CallFunction(bb, ((CallFunction) inst).getFunction(), newArgu,
                                (Register) getName(caller, res)));
                } else if (inst instanceof GetPtr) {
                    ArrayList<Operand> oldIdx = ((GetPtr) inst).getIndex();
                    ArrayList<Operand> newIdx = new ArrayList<>();
                    for (Operand idx : oldIdx) {
                        newIdx.add(getName(caller, idx));
                    }
                    bb.addInst(new GetPtr(bb, getName(caller, ((GetPtr) inst).getPointer()),
                            newIdx, (Register) getName(caller, ((GetPtr) inst).getDest())));
                } else if (inst instanceof Icmp) {
                    bb.addInst(new Icmp(bb, (Register) getName(caller, ((Icmp) inst).getDest()), ((Icmp) inst).getOp(),
                            getName(caller, ((Icmp) inst).getLhs()), getName(caller, ((Icmp) inst).getRhs())));
                } else if (inst instanceof Load) {
                    bb.addInst(new Load(bb, getName(caller, ((Load) inst).getDest()),
                            (Register) getName(caller, ((Load) inst).getRes())));
                } else if (inst instanceof MoveInst) {
                    bb.addInst(new MoveInst("move_copy", bb,
                            getName(caller, ((MoveInst) inst).getSrc()),
                            (Register) getName(caller, ((MoveInst) inst).getRes())));
                } else if (inst instanceof Phi) {
                    Set<Pair<Operand, BasicBlock>> oldBranches = ((Phi) inst).getBranches();
                    HashSet<Pair<Operand, BasicBlock>> newBranches = new HashSet<>();
                    for (Pair<Operand, BasicBlock> pair : oldBranches) {
                        newBranches.add(new Pair<>(getName(caller, pair.getFirst()), cloneBB.get(pair.getSecond())));
                    }
                    bb.addInst(new Phi(bb, newBranches, (Register) getName(caller, ((Phi) inst).getRes())));
                } else if (inst instanceof Return) {
                    Return cloned_return =
                            new Return("return_copy", bb, ((Return) inst).getType(), getName(caller, ((Return) inst).getReturnVal()));
                    bb.addInst(cloned_return);
                    if (((Return) inst).getReturnVal() != null) {
                        renameMap.put(((Return) inst).getReturnVal(), cloned_return.getReturnVal());
                    }
                    returnInst = cloned_return;
                } else if (inst instanceof Store) {
                    bb.addInst(new Store("store_copy", bb, getName(caller, ((Store) inst).getValue()),
                            getName(caller, ((Store) inst).getDest())));
                }

            }

        }
        ArrayList<BasicBlock> clonedBBList = new ArrayList<>();
        for (BasicBlock bb : blocks) {
            BasicBlock clonedBB = cloneBB.get(bb);
            if(bb.getPrevBB() != null) {
                clonedBB.setPrevBB(cloneBB.get(bb.getPrevBB()));
            }
            if(bb.getNextBB() != null) {
                clonedBB.setNextBB(cloneBB.get(bb.getNextBB()));
            }
            clonedBBList.add(clonedBB);
            caller.getSymbolTable().put(clonedBB.getName(),clonedBB);
            for (Instruction i = bb.getHead(); i != null; i = i.getNxt()) {
                caller.getSymbolTable().put(i.getName(), i);
            }
        }

        if(returnInst == null) {
            throw new RuntimeException();
        }

        return new Pair<>(clonedBBList,returnInst);

    }

    ///////////utils///////////


    private boolean canBeNotRecInline(IRFunction callee, IRFunction caller){
        if(caller.isNotFunctional() || callee.isNotFunctional())
            return false;
        if(callee == caller) return false;
        if(calleeMap.get(caller).contains(callee))return false;
        return instCount.get(callee) < instLimit
                || instCount.get(callee) + instCount.get(caller) < instLimitAll;
    }

    private boolean canBeRecurInline(IRFunction callee, IRFunction caller){
        if(caller.isNotFunctional() || callee.isNotFunctional())
            return false;

        return instCount.get(callee) < instLimitAll && callee == caller;
    }

    private Operand getName(IRFunction caller, Operand operand){
        if(operand  instanceof StaticVar)
            return operand;
        else if(operand instanceof Register){
            if(renameMap.containsKey(operand))
                return renameMap.get(operand);
            Register res = new VirtualReg(operand.getName().split("\\.")[0], operand.getType());
            caller.getSymbolTable().put(res.getName(),res);
            renameMap.put(operand,res);
            return res;
        }
        else  if(operand instanceof Parameter){
           if(! renameMap.containsKey(operand)){
               throw new RuntimeException("parameter map fail!");
           }
            return renameMap.get(operand);
        }
        else  return operand;
    }

    private void removeUnusedFunc(){
        visited = new HashSet<>();
        visited.add(module.getFunctionMap().get("__init__"));
        dfsCallee(module.getFunctionMap().get("main"));
        HashSet<IRFunction> functions = new HashSet<>(module.getFunctionMap().values());
        for(IRFunction removeFunc: functions){
            if(!visited.contains(removeFunc)){
                for(BasicBlock bb:removeFunc.getBlocks())
                    bb.deleteItself();
                module.getFunctionMap().remove(removeFunc.getName());
            }
        }

    }

    private HashSet<IRFunction> visited;
    private void dfsCallee(IRFunction function){
        visited.add(function);
        for(BasicBlock bb:function.getBlocks()){
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNxt()){
                if(inst instanceof CallFunction) {
                    IRFunction callee = ((CallFunction) inst).getFunction();
                    if(!visited.contains(callee))
                        dfsCallee(callee);
                }
            }
        }
    }
}
