package Optimize;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.*;
import FrontEnd.IR.Type.IntIRType;
import FrontEnd.IR.Type.VoidType;
import FrontEnd.IRVisitor;
import util.Pair;

import java.util.*;

public class SCCP extends Pass implements IRVisitor {
    public static class LatticeVal  {
        //This class represents the different lattice values that an LLVM value may occupy.
        // It is a simple class with value semantics.
        public enum LatticaValType {
            unknown, constant, mutiDefined
        }
        private LatticaValType type;
        private Operand operand;

        public LatticeVal(LatticaValType type, Operand operand) {
            this.type = type;
            this.operand = operand;
        }
        public LatticaValType getType() {
            return type;
        }

        public void setType(LatticaValType type) {
            this.type = type;
        }

        public Operand getOperand() {
            return operand;
        }

        public void setOperand(Operand operand) {
            this.operand = operand;
        }

    }
    private Map<Operand, LatticeVal> valueState; // The state each value is in.
    private Set<BasicBlock> BBExecutable;// The BBs that are executable.
    private LinkedList<BasicBlock> BBWorkList;
    private LinkedList<Register> regWorkList;
    
    public SCCP(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        changed = false;
        for(IRFunction function: module.getFunctionMap().values())
            visit(function);
        return changed;
    }

    private void markExecutable(BasicBlock bb) {
        if(BBExecutable.contains(bb)) {
            for(Instruction inst = bb.getHead();inst instanceof Phi; inst = inst.getNxt()) {
                inst.accept(this);
            }
        }
        else {
            BBExecutable.add(bb);
            BBWorkList.add(bb);
        }
    }

    private void markMutiDefined(Register reg ) {
        LatticeVal  latticeVal = getStatus(reg);
        if(latticeVal.getType() != LatticeVal.LatticaValType.mutiDefined) {
            valueState.replace(reg, new LatticeVal(LatticeVal.LatticaValType.mutiDefined,null));
            regWorkList.add(reg);
        }
    }

    private void markConst(Register reg, Operand constant) {
        LatticeVal lattice = new LatticeVal(LatticeVal.LatticaValType.constant,constant);
        LatticeVal old = getStatus(reg);
        if(old.getType() == LatticeVal.LatticaValType.unknown) {
            valueState.replace(reg, lattice);
            regWorkList.add(reg);
        }

    }
    @Override
    public void visit(BasicBlock bb) {
        for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNxt()) {
            inst.accept(this);
        }
    }

    @Override
    public void visit(Module root) {
        //do nothing
        throw new RuntimeException();
    }

    @Override
    public void visit(IRFunction function) {
        valueState = new HashMap<>();
        BBExecutable = new HashSet<>();
        BBWorkList = new LinkedList<>();
        regWorkList = new LinkedList<>();
        markExecutable(function.getEntranceBB());
        while (!regWorkList.isEmpty() || !BBWorkList.isEmpty()) {
            while (!BBWorkList.isEmpty()) {
                BasicBlock block = BBWorkList.pop();
                block.accept(this); // visit BasicBlock
            }

            while (!regWorkList.isEmpty()) {
                Register register = regWorkList.pop();
                assert valueState.containsKey(register);
                for (IRNode instruction : register.getUsers()) {
                    assert instruction  instanceof Instruction;
                    instruction.accept(this); // visit IRInstruction
                }
            }
        }
        boolean cchanged = false;
        for(BasicBlock bb:function.getBlocks()) {
//            if(!BBExecutable.contains(bb)){
//                bb.deleteItself();
//                cchanged =true;
//            }
//            else{
                for(Instruction inst = bb.getHead(); inst!=null; inst = inst.getNxt()) {
                    cchanged |= inst.replaceConst(this);
                }
//            }

        }
        changed |= cchanged;
    }

    public LatticeVal getStatus(Operand operand) {
        if(valueState.containsKey(operand))
            return valueState.get(operand);
        LatticeVal ret;
        if(operand instanceof Constant)
            ret  = new LatticeVal(LatticeVal.LatticaValType.constant, operand);
        else if(operand instanceof Parameter)
            ret = new LatticeVal(LatticeVal.LatticaValType.mutiDefined, null);
        else ret = new LatticeVal(LatticeVal.LatticaValType.unknown, null);
        valueState.put(operand,ret);
        return ret;
    }

    @Override
    public void visit(AllocateInst allocateInst) {
        markMutiDefined(allocateInst.getDest());
    }

    private Constant calcBinary(Instruction instruction,  LatticeVal lhs, LatticeVal rhs) {
        assert  instruction instanceof BinaryOp;
        Constant ret = null;
        if(lhs.getOperand() instanceof ConstInt && rhs.getOperand() instanceof  ConstInt) {
            int val;
            switch (((BinaryOp) instruction).getOp()) {
                case ADD:
                    val = ((ConstInt) lhs.getOperand()).getValue() + ((ConstInt) rhs.getOperand()).getValue();
                    break;
                case SUB:
                    val = ((ConstInt) lhs.getOperand()).getValue() - ((ConstInt) rhs.getOperand()).getValue();
                    break;
                case MOD:
                    if(((ConstInt) rhs.getOperand()).getValue() == 0){
                        return null;
                    }
                    val = ((ConstInt) lhs.getOperand()).getValue() % ((ConstInt) rhs.getOperand()).getValue();
                    break;
                case MUL:
                    val = ((ConstInt) lhs.getOperand()).getValue() * ((ConstInt) rhs.getOperand()).getValue();
                    break;
                case DIV:
                    if(((ConstInt) rhs.getOperand()).getValue() == 0){
                        return null;
                    }
                    val = ((ConstInt) lhs.getOperand()).getValue() / ((ConstInt) rhs.getOperand()).getValue();
                    break;
                case OR:
                    val = ((ConstInt) lhs.getOperand()).getValue() | ((ConstInt) rhs.getOperand()).getValue();
                    break;
                case XOR:
                    val = ((ConstInt) lhs.getOperand()).getValue() ^ ((ConstInt) rhs.getOperand()).getValue();
                    break;
                case SAL:
                    val = ((ConstInt) lhs.getOperand()).getValue() << ((ConstInt) rhs.getOperand()).getValue();
                    break;
                case SAR:
                    val = ((ConstInt) lhs.getOperand()).getValue() >> ((ConstInt) rhs.getOperand()).getValue();
                    break;
                case AND:
                    val = ((ConstInt) lhs.getOperand()).getValue() & ((ConstInt) rhs.getOperand()).getValue();
                    break;
                default:
                    throw new RuntimeException();
            }
            ret = new ConstInt(val, IntIRType.intType.i32);
        } else if(lhs.getOperand() instanceof ConstBool && rhs.getOperand() instanceof ConstBool){
            boolean val;
            switch (((BinaryOp) instruction).getOp()){
                case OR:
                    val = ((ConstBool) lhs.getOperand()).isValue() | ((ConstBool) rhs.getOperand()).isValue();
                    break;
                case AND:
                    val = ((ConstBool) lhs.getOperand()).isValue() & ((ConstBool) rhs.getOperand()).isValue();
                    break;
                case XOR:
                    val = ((ConstBool) lhs.getOperand()).isValue()  ^ ((ConstBool) rhs.getOperand()).isValue();
                    break;
                default:
                    throw new RuntimeException();
            }
            ret = new ConstBool(val);
        }
        return ret;
    }
    @Override
    public void visit(BinaryOp inst) {
        Operand lhs = inst.getLhs();
        Operand rhs = inst.getRhs();
        LatticeVal lhss = getStatus(lhs);
        LatticeVal rhss = getStatus(rhs);
        if(lhss.getType()== LatticeVal.LatticaValType.constant && rhss.getType() == LatticeVal.LatticaValType.constant) {
            Constant res = calcBinary(inst,lhss,rhss);
            if(res != null) {
                markConst(inst.getDest(), res);
            }
        }
        else if(lhss.getType() == LatticeVal.LatticaValType.mutiDefined || rhss.getType() == LatticeVal.LatticaValType.mutiDefined) {
            markMutiDefined(inst.getDest());
        }
    }

    @Override
    public void visit(BranchJump branchJump) {
        if(branchJump.getCondition() != null) {
            Operand cond = branchJump.getCondition();
            LatticeVal condd = getStatus(cond);
            if(condd.getType() == LatticeVal.LatticaValType.constant) {
                assert condd.getOperand() instanceof ConstBool;
                if(((ConstBool) condd.getOperand()).isValue()){
                    markExecutable(branchJump.getThenBlock());
                }
                else markExecutable(branchJump.getElseBlock());
            } else if(condd.getType() == LatticeVal.LatticaValType.mutiDefined) {
                markExecutable(branchJump.getThenBlock());
                markExecutable(branchJump.getElseBlock());
            }
        }
        else {
            markExecutable(branchJump.getThenBlock());
        }
    }

    private ConstBool calcCmp(Icmp instruction,LatticeVal lhs, LatticeVal rhs) {
        ConstBool retVal;
        if(lhs.getOperand() instanceof ConstInt && rhs.getOperand() instanceof ConstInt){
            switch (instruction.getOp()){
                case GEQ:
                    retVal = new ConstBool(((ConstInt) lhs.getOperand()).getValue() >= ((ConstInt) rhs.getOperand()).getValue());
                    break;
                case LESS:
                    retVal = new ConstBool(((ConstInt) lhs.getOperand()).getValue() < ((ConstInt) rhs.getOperand()).getValue());
                    break;
                case LEQ:
                    retVal = new ConstBool(((ConstInt) lhs.getOperand()).getValue() <= ((ConstInt) rhs.getOperand()).getValue());
                    break;
                case EQUAL:
                    retVal = new ConstBool(((ConstInt) lhs.getOperand()).getValue() == ((ConstInt) rhs.getOperand()).getValue());
                    break;
                case NOTEQUAL:
                    retVal = new ConstBool(((ConstInt) lhs.getOperand()).getValue() != ((ConstInt) rhs.getOperand()).getValue());
                    break;
                case GREATER:
                    retVal = new ConstBool(((ConstInt) lhs.getOperand()).getValue() > ((ConstInt) rhs.getOperand()).getValue());
                    break;
                default:
                    throw new RuntimeException("no Icmp op!");
            }
        }
        else if(lhs.getOperand() instanceof ConstBool && rhs.getOperand() instanceof ConstBool) {
            switch (instruction.getOp()) {
                case EQUAL:
                    retVal = new ConstBool(((ConstBool) lhs.getOperand()).isValue() == ((ConstBool) rhs.getOperand()).isValue());
                    break;
                case NOTEQUAL:
                    retVal = new ConstBool(((ConstBool) lhs.getOperand()).isValue() != ((ConstBool) rhs.getOperand()).isValue());
                    break;
                default:
                    throw new RuntimeException("nononono!");
            }
        }
        else if(lhs.getOperand() instanceof ConstNull && rhs.getOperand() instanceof  ConstNull) {
            if(instruction.getOp() == Icmp.CompareOp.EQUAL)
                retVal = new ConstBool(true);

            else if(instruction.getOp() == Icmp.CompareOp.NOTEQUAL)
                retVal = new ConstBool(false);
            else
                throw new RuntimeException("what??");
        }
        else{
            throw new RuntimeException("I dont know");
        }

        return retVal;
    }
    @Override
    public void visit(Icmp icmp) {
        Operand lhs= icmp.getLhs();
        Operand rhs = icmp.getRhs();
        LatticeVal lhss = getStatus(lhs);
        LatticeVal rhss = getStatus(rhs);
        if(lhss.getType() == LatticeVal.LatticaValType.constant && rhss.getType() == LatticeVal.LatticaValType.constant){
            ConstBool ret = calcCmp(icmp,lhss,rhss);
            markConst(icmp.getDest(),ret);
        }
        else if(lhss.getType() == LatticeVal.LatticaValType.mutiDefined || rhss.getType() == LatticeVal.LatticaValType.mutiDefined){
            markMutiDefined(icmp.getDest());
        }
    }

    @Override
    public void visit(Load load) {
        markMutiDefined(load.getRes());
    }

    @Override
    public void visit(Return retJump) { }

    @Override
    public void visit(Store store) {
//do nothing
    }

    @Override
    public void visit(CallFunction callFunction) {
        if(!(callFunction.getFunction().getFunctionType().getReturnType() instanceof VoidType)){
            markMutiDefined(callFunction.getResult());
        }
    }

    @Override
    public void visit(GetPtr getPtr) {
        markMutiDefined(getPtr.getDest());
    }

    @Override
    public void visit(Phi phi) {
        Constant constant = null;
        for(Pair<Operand,BasicBlock> item: phi.getBranches()) {
            if(BBExecutable.contains(item.getValue())) {
                LatticeVal lattice = getStatus(item.getKey());
                if(lattice.getType() == LatticeVal.LatticaValType.mutiDefined) {
                    markMutiDefined(phi.getRes());
                    return;
                }
                if(lattice.getType() == LatticeVal.LatticaValType.constant) {
                    if (constant == null)
                        constant = (Constant)lattice.operand;
                    else {
                        if(!constant.equals(item.getFirst())) {
                            markMutiDefined(phi.getRes());
                            return;
                        }
                    }
                }
            }

        }
        if(constant != null)
            markConst(phi.getRes(),constant);
    }

    @Override
    public void visit(BitCast bitCast) {
        LatticeVal src = getStatus(bitCast.getSrc());
        if(src.getType() == LatticeVal.LatticaValType.constant){
            Constant constant;
            if(src.getOperand() instanceof ConstNull)
                constant = new ConstNull();
        } else if(src.getType() == LatticeVal.LatticaValType.mutiDefined)
            markMutiDefined(bitCast.getRes());
    }

    @Override
    public void visit(MoveInst move) {

    }


    public Map<Operand, LatticeVal> getValueState() {
        return valueState;
    }

    public void setValueState(Map<Operand, LatticeVal> valueState) {
        this.valueState = valueState;
    }


}
