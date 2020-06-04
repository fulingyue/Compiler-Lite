package Optimize;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.*;
import util.Pair;

import java.util.*;

public class FunctionInliner extends Pass {
    private static int instLimit = 120;
    private  static int instLimitAll = 2000;
    private HashMap<IRFunction, Integer> instCount;
    private HashMap<IRFunction, Integer> callCount;

    private HashMap<IRFunction,IRFunction> funcfuncMap = new HashMap<>();
    private HashMap<IRFunction, Integer> funcCount;

    private HashMap<IRFunction, HashSet<IRFunction>> calleeMap;
    private HashMap<IRFunction,HashSet<IRFunction>> recurCalleeMap;

    HashMap<Operand,Operand> renameMap;
    public FunctionInliner(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        changed = false;
        instCount = new HashMap<>();
        callCount = new HashMap<>();
        funcfuncMap = new HashMap<>();
        funcCount = new HashMap<>();

        recurCalleeMap = new HashMap<>();
        calleeMap = new HashMap<>();
        for(IRFunction func: module.getFunctionMap().values()){
            calleeMap.put(func,new HashSet<>());

            recurCalleeMap.put(func,new HashSet<>());
            callCount.put(func,0);
        }

        for(IRFunction func: module.getFunctionMap().values()){
            countInst(func);
        }


        changed = notRecurInline();
        removeUnusedFunc();

        computeCallee();

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

                        callCount.put(callee,callCount.get(callee)+1);
                    }
                }
                ptr = ptr.getNxt();
            }
        }
        instCount.put(function, instructionCnt);
    }



    private void computeCallee(){
        for(IRFunction function: module.getFunctionMap().values()) {
            for (BasicBlock bb : function.getBlocks()) {
                for (Instruction inst = bb.getHead(); inst != null; inst = inst.getNxt()) {
                    if (inst instanceof CallFunction) {
                        calleeMap.get(function).add(((CallFunction) inst).getFunction());
                    }
                }
            }
        }
        ////////recursive computation//////////////
        boolean ischanged  = true;
        while(ischanged){
            ischanged=  false;
            for(IRFunction function: module.getFunctionMap().values()){
                HashSet<IRFunction> res = new HashSet<>(calleeMap.get(function));
                for(IRFunction f:  calleeMap.get(function)){
                    res.addAll(calleeMap.get(f));
                }
                if(!res.equals(recurCalleeMap.get(function))){
                    recurCalleeMap.remove(function);
                    recurCalleeMap.put(function,res);
                    ischanged = true;
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
                                callCount.put(callee, callCount.get(callee)  -1);
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
        int depth = 3;
        for(int i =0; i < depth; i++){
            for(IRFunction func:module.getFunctionMap().values()) {
                for (BasicBlock bb = func.getEntranceBB(); bb != null; bb = bb.getNextBB()) {
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


    ///////////utils///////////
    private Instruction inlineFunc(CallFunction callInst,IRFunction caller){
        IRFunction callee = callInst.getFunction();
        renameMap = new HashMap<>();
        HashMap<BasicBlock,BasicBlock> cloneBB = new HashMap<>();

        BasicBlock currentBB = callInst.getBasicBlock();
        BasicBlock splitBB = currentBB.split(callInst);

        cloneBB.put(callee.getEntranceBB(), currentBB);
        cloneBB.put(callee.getExitBB(),splitBB);

        LinkedList<BasicBlock> blocks = callee.getBlocks();



        for(BasicBlock bb:blocks){
            if(!cloneBB.containsKey(bb)) {
                BasicBlock newBB = new BasicBlock(bb.getName().split("\\.")[0], caller);
                caller.addBB(newBB);
                cloneBB.put(bb, newBB);
            }
        }

        ArrayList<Parameter> parameters = callee.getParaList();
        ArrayList<Operand> arguments = callInst.getParameters();
        for(int i = 0; i < arguments.size(); ++i){
            renameMap.put(parameters.get(i), arguments.get(i));
        }

        if(callInst.getResult() != null)
            renameMap.put(callInst.getResult(),callInst.getResult());


        BasicBlock entranceBB = cloneBB.get(callee.getEntranceBB());
        BasicBlock exitBB = cloneBB.get(callee.getExitBB());



        for(BasicBlock block: blocks){
            BasicBlock bb = cloneBB.get(block);
            for(Instruction inst = block.getHead(); inst != null; inst = inst.getNxt()){
                if(inst instanceof AllocateInst){
                    bb.addInst(new AllocateInst(bb,"alloca_copy", (Register)getName(caller,((AllocateInst) inst).getDest()),
                            ((AllocateInst) inst).getType()));
                }
                else if(inst instanceof BinaryOp){
                    bb.addInst(new BinaryOp(bb,(Register)getName(caller,((BinaryOp) inst).getDest()),
                            ((BinaryOp) inst).getOp(),getName(caller,((BinaryOp) inst).getLhs()),
                            getName(caller,((BinaryOp) inst).getRhs())));
                }
                else if(inst instanceof BitCast){
                    bb.addInst(new BitCast("bitcast_copy",bb,getName(caller,((BitCast) inst).getSrc()),
                            ((BitCast) inst).getType(),(Register) getName(caller,((BitCast) inst).getRes())));
                }
                else if(inst instanceof BranchJump){
                    Operand cond = ((BranchJump) inst).getCondition();
                    if(cond == null){
                        bb.addInst(new BranchJump(bb, null, cloneBB.get(((BranchJump) inst).getThenBlock()),null));
                    }
                    else {
                        bb.addInst(new BranchJump(bb, getName(caller,cond),
                                cloneBB.get(((BranchJump) inst).getThenBlock()),  cloneBB.get(((BranchJump) inst).getElseBlock())));
                    }
                }
                else if(inst instanceof CallFunction){
                    ArrayList<Operand> oldArgu  = ((CallFunction) inst).getParameters();
                    ArrayList<Operand> newArgu = new ArrayList<>();
                    for(Operand arg: oldArgu){
                        newArgu.add(getName(caller,arg));
                    }
                    Register res = ((CallFunction) inst).getResult();
                    if(res == null){
                        bb.addInst(new CallFunction(bb,((CallFunction) inst).getFunction(),newArgu, null));
                    }
                    else
                        bb.addInst(new CallFunction(bb,((CallFunction) inst).getFunction(),newArgu,
                                (Register)getName(caller,res)));
                }
                else if(inst instanceof GetPtr){
                    ArrayList<Operand> oldIdx = ((GetPtr) inst).getIndex();
                    ArrayList<Operand> newIdx = new ArrayList<>();
                    for(Operand idx: oldIdx){
                        newIdx.add(getName(caller,idx));
                    }
                    bb.addInst(new GetPtr(bb,getName(caller,((GetPtr) inst).getPointer()),
                            newIdx, (Register)getName(caller,((GetPtr) inst).getDest())));
                }
                else if(inst instanceof Icmp){
                    bb.addInst(new Icmp(bb,(Register)getName(caller,((Icmp) inst).getDest()),((Icmp) inst).getOp(),
                            getName(caller,((Icmp) inst).getLhs()),getName(caller,((Icmp) inst).getRhs())));
                }
                else if(inst instanceof Load){
                    bb.addInst(new Load(bb, getName(caller,((Load) inst).getDest()),
                            (Register)getName(caller,((Load) inst).getRes())));
                }
                else if(inst instanceof MoveInst){
                    bb.addInst(new MoveInst("move_copy",bb,
                            getName(caller,((MoveInst) inst).getSrc()),
                            (Register)getName(caller,((MoveInst) inst).getRes())));
                }
                else if(inst instanceof Phi){
                    Set<Pair<Operand,BasicBlock>> oldBranches = ((Phi) inst).getBranches();
                    HashSet<Pair<Operand,BasicBlock>> newBranches = new HashSet<>();
                    for(Pair<Operand,BasicBlock> pair:oldBranches){
                        newBranches.add(new Pair<>(getName(caller,pair.getFirst()),cloneBB.get(pair.getSecond())));
                    }
                    bb.addInst(new Phi(bb,newBranches, (Register)getName(caller,((Phi) inst).getRes())));
                }
                else if(inst instanceof Return){
                    Operand val = ((Return) inst).getReturnVal();
                    if(val != null){
                        bb.addInst(new MoveInst("moveRetVal",  bb, val, callInst.getResult()));
                    }
                    exitBB.addInst(new BranchJump(exitBB,null,splitBB,null));
                }
                else if(inst instanceof Store){
                    bb.addInst(new Store("strer",  bb, getName(caller,((Store) inst).getValue()),
                            getName(caller, ((Store) inst).getDest())));
                }

            }
            currentBB = bb;
        }

        currentBB.addInst(new BranchJump("jump_split", currentBB,null,entranceBB,null));

        return splitBB.getHead();
    }

    private boolean canBeNotRecInline(IRFunction callee, IRFunction caller){
        if(caller.isNotFunctional() || callee.isNotFunctional())
            return false;
        if(!callCount.containsKey(caller)) return false;
        if(recurCalleeMap.get(caller).contains(callee))return false;
        return instCount.get(callee) < instLimit
                || instCount.get(callee) + instCount.get(caller) < instLimitAll;
    }

    private boolean canBeRecurInline(IRFunction callee, IRFunction caller){
        if(caller.isNotFunctional() || callee.isNotFunctional())
            return false;

        if(!callCount.containsKey(caller)) return false;

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
        else  return operand;
    }

    private void removeUnusedFunc(){
//        visited = new HashSet<>();
//        dfsCallee(module.getFunctionMap().get("main"));

        for(IRFunction removeFunc: module.getFunctionMap().values()){
            if(callCount.get(removeFunc) == 0 && !removeFunc.getName().equals("__init__")) {
                for (BasicBlock bb = removeFunc.getEntranceBB(); bb != null; bb = bb.getNextBB())
                    for (Instruction inst = bb.getHead(); inst != null; inst = inst.getNxt())
                        inst.remove();
                changed = true;
                module.getFunctionMap().remove(removeFunc.getName());
            }
        }
    }

//    private HashSet<IRFunction> visited;
//    private void dfsCallee(IRFunction function){
//        visited.add(function);
//        for(IRFunction callee: calleeMap.get(function)){
//            if(!visited.contains(callee))
//                dfsCallee(callee);
//        }
//    }
}
