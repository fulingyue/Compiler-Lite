package BackEnd;

import BackEnd.Instruction.*;
import BackEnd.Operand.*;
import BackEnd.Operand.VirtualReg;
import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.*;
import FrontEnd.IR.Operand.Address;
import FrontEnd.IR.Type.ClassIRType;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Type.IntIRType;
import FrontEnd.IR.Type.PtrType;
import FrontEnd.IRVisitor;
import util.Defines;

import java.util.ArrayList;

public class InstructionSelection implements IRVisitor {
    public RiscModule module;
    private Module IRModule;
    private RiscFunction currentFunction;
    private RiscBB currentBB;
    private VirtualReg[] calleeSaved;

    public InstructionSelection(Module irModule) {
        IRModule = irModule;
        module = new RiscModule();

    }

    public void run(){
        visit(IRModule);
    }

    @Override
    public void visit(Module root) {
        for(IRFunction function: root.getFunctionMap().values()){
            RiscFunction newFunc = new RiscFunction(function.getName(), function.getParaList().size(),function);
            function.setRiscFunction(newFunc);
            module.addFunction(newFunc);
        }

        for(String str: root.getStaticStrings().keySet()){//register global strings
            StaticVar globalString = root.getStaticStrings().get(str);
            assert globalString.getInit() instanceof ConstString;
            ConstStrings riscString = new ConstStrings(globalString.getName(),str);
            ((ConstString) globalString.getInit()).setRiscGS(riscString);
            module.addString(riscString);
        }

        for(StaticVar global: root.getStaticVariableMap().values()){
            visit(global);
        }

        for(IRFunction function: root.getFunctionMap().values()){
            visit(function);
        }

    }



    @Override
    public void visit(IRFunction function) {

        currentFunction = function.getRiscFunction();
        for(BasicBlock bb = function.getEntranceBB(); bb != null; bb = bb.getNextBB()){
            RiscBB riscBB = new RiscBB(bb.getName(),currentFunction,bb);
            bb.setRiscBB(riscBB);
            currentFunction.addBB(riscBB);
        }

        BasicBlock entranceBB = function.getEntranceBB();
        currentBB = entranceBB.getRiscBB();
        currentFunction.setEntranceBB(currentBB);

        calleeSaved[12] = currentFunction.addRegister("ra");
        currentBB.addInst(new Move(currentBB,calleeSaved[12],RegisterTable.ra));
        for(int i = 0;i < 12; ++i) {
            PhysicalReg reg = RegisterTable.calleeSavedRegisters[i];
            calleeSaved[i] = currentFunction.addRegister("calleeSaved");
            currentBB.addInst(new Move(currentBB,calleeSaved[i],reg));
        }
        //TODO

    }
    @Override
    public void visit(BasicBlock bb) {
        currentBB = bb.getRiscBB();
        for(Instruction instruction = bb.getHead();instruction!= null;instruction = instruction.getNxt()){
            instruction.accept(this);
        }
    }


    private void visit(StaticVar var){
        GlobalVar gv = new GlobalVar(var.getName());
        var.setRiscGV(gv);
        module.addGV(gv);

        Operand init = var.getInit();
        if(init instanceof ConstString){
            ConstStrings cs = ((ConstString) init).getRiscGS();
            gv.setVal(cs);
        }
        else if(init instanceof ConstInt){
            Immidiate imm = new Immidiate(((ConstInt) init).getValue());
            gv.setVal(imm);
        }

    }



    @Override
    public void visit(AllocateInst allocateInst) {
        //do nothing
    }

    private Immidiate toImm(int val){
        if(val >= Defines.IMM_MAX || val <= Defines.IMM_MIN){
            return null;
        }
        else return new Immidiate(val);
    }

    @Override
    public void visit(BinaryOp inst) {
        Register irReg = inst.getDest();
        RiscRegister rvReg = toRiscRegister(irReg);
        Operand lhs = inst.getLhs(), rhs = inst.getRhs();
        switch (inst.getOp()){
            case ADD://no const + const occurs
                if(lhs instanceof ConstInt) {
                    Operand tmp = lhs;
                    lhs = rhs;
                    rhs = tmp;
                }
                if(rhs instanceof ConstInt) {
                    int val = ((ConstInt) rhs).getValue();
                    Immidiate imm = toImm(val);
                    if(imm != null){
                        currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.addi, rvReg, toRiscRegister(lhs), imm));
                    }
                    else {
                        currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.add, rvReg,toRiscRegister(lhs),toRiscRegister(rhs)));
                    }
                }
                else {
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.add, rvReg,toRiscRegister(lhs),toRiscRegister(rhs)));
                }
                break;
            case SUB:
                if(rhs instanceof ConstInt){
                    int val = -((ConstInt) rhs).getValue();
                    Immidiate imm = toImm(val);
                    if(imm != null){
                        currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.addi, rvReg, toRiscRegister(lhs), imm));
                    }
                    else {
                        currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.sub, rvReg,toRiscRegister(lhs),toRiscRegister(rhs)));
                    }
                }
                else {
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.sub, rvReg,toRiscRegister(lhs),toRiscRegister(rhs)));
                }
                break;
            case MUL:
                currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.mul, rvReg,toRiscRegister(lhs),toRiscRegister(rhs)));
                break;
            case DIV:
                currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.div, rvReg,toRiscRegister(lhs),toRiscRegister(rhs)));
                break;
            case MOD:
                currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.rem, rvReg,toRiscRegister(lhs),toRiscRegister(rhs)));
                break;
            case XOR:
                if(rhs instanceof ConstInt){
                    Immidiate  imm = toImm(((ConstInt) rhs).getValue());
                    RiscRegister reg = toRiscRegister(lhs);
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.xori,rvReg,reg,imm));
                }
                else if(lhs instanceof ConstInt){
                    Immidiate imm = toImm(((ConstInt) lhs).getValue());
                    RiscRegister reg = toRiscRegister(rhs);
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.xori,rvReg,reg,imm));
                }
                else {
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.xor, toRiscRegister(lhs),toRiscRegister(rhs),rvReg));
                }
                break;
            case AND:
                if(rhs instanceof ConstInt){
                    Immidiate  imm = toImm(((ConstInt) rhs).getValue());
                    RiscRegister reg = toRiscRegister(lhs);
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.andi,rvReg,reg,imm));
                }
                else if(lhs instanceof ConstInt){
                    Immidiate imm = toImm(((ConstInt) lhs).getValue());
                    RiscRegister reg = toRiscRegister(rhs);
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.andi,rvReg,reg,imm));
                }
                else {
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.and, toRiscRegister(lhs),toRiscRegister(rhs),rvReg));
                }
                break;
            case OR:
                if(rhs instanceof ConstInt){
                    Immidiate  imm = toImm(((ConstInt) rhs).getValue());
                    RiscRegister reg = toRiscRegister(lhs);
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.ori,rvReg,reg,imm));
                }
                else if(lhs instanceof ConstInt){
                    Immidiate imm = toImm(((ConstInt) lhs).getValue());
                    RiscRegister reg = toRiscRegister(rhs);
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.ori,rvReg,reg,imm));
                }
                else {
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.or, toRiscRegister(lhs),toRiscRegister(rhs),rvReg));
                }
                break;
            case SAR:
                if(rhs instanceof ConstInt){
                    Immidiate  imm = toImm(((ConstInt) rhs).getValue());
                    RiscRegister reg = toRiscRegister(lhs);
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.srai,rvReg,reg,imm));
                }
                else {
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.sra, toRiscRegister(lhs),toRiscRegister(rhs),rvReg));
                }
                break;
            case SAL:
                if(rhs instanceof ConstInt){
                    Immidiate  imm = toImm(((ConstInt) rhs).getValue());
                    RiscRegister reg = toRiscRegister(lhs);
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.slli,rvReg,reg,imm));
                }
                else {
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.sll, toRiscRegister(lhs),toRiscRegister(rhs),rvReg));
                }
                break;
            default:
                throw new RuntimeException("oh!Shasha!");
        }
    }

    @Override
    public void visit(BranchJump branchJump) {
        Operand cond = branchJump.getCondition();
        if(cond ==  null){
            currentBB.addInst(new Jal(currentBB, branchJump.getThenBlock().getRiscBB()));
        }
        else {
            RiscInstruction instruction =  branchJump.getRiscInstruction();
            if(instruction != null){
                instruction.setParentBB(currentBB);
                currentBB.addInst(instruction);
            }else {
                currentBB.addInst(new Branch(currentBB, Branch.BranchOp.bne,toRiscRegister(cond),RegisterTable.zero,branchJump.getThenBlock().getRiscBB()));
            }
            currentBB.addInst(new Jal(currentBB,branchJump.getElseBlock().getRiscBB()));
        }
    }

    private void icmpAndBranch(Icmp icmp){
        Branch.BranchOp op = null;
        switch (icmp.getOp()){
            case EQUAL:op = Branch.BranchOp.beq;break;
            case NOTEQUAL:op = Branch.BranchOp.bne;break;
            case GREATER:op = Branch.BranchOp.bgt;break;
            case LEQ:op = Branch.BranchOp.ble;break;
            case LESS:op = Branch.BranchOp.blt;break;
            case GEQ:op = Branch.BranchOp.bge;break;
        }
        for(IRNode inst: icmp.getDest().getUsers()){
            assert inst instanceof BranchJump;
            ((BranchJump) inst).setRiscInstruction(new Branch(currentBB,op,toRiscRegister(icmp.getLhs()),toRiscRegister(icmp.getRhs()),
                    ((BranchJump) inst).getThenBlock().getRiscBB()));

        }
    }

    @Override
    public void visit(Icmp icmp) {
        if(icmp.onlyInBranch()){
            icmpAndBranch(icmp);
        }
        else {
            if(icmp.getOp() == Icmp.CompareOp.EQUAL) {
                RiscRegister lhs = toRiscRegister(icmp.getLhs());
                RiscRegister rhs = toRiscRegister(icmp.getRhs());
                RiscRegister res = toRiscRegister(icmp.getDest());
                VirtualReg vreg = currentFunction.addRegister("tmp");
                currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.xor, lhs, rhs, vreg));
                currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.sltiu, res, vreg, new Immidiate(1)));
            }
            else if(icmp.getOp() == Icmp.CompareOp.NOTEQUAL) {
                RiscRegister lhs = toRiscRegister(icmp.getLhs());
                RiscRegister rhs = toRiscRegister(icmp.getRhs());
                RiscRegister res = toRiscRegister(icmp.getDest());
                VirtualReg tmp = currentFunction.addRegister("tmp");
                currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.xor, lhs, rhs, tmp));
                currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.sltu, RegisterTable.zero, tmp, res));
            }
            else {
                Immidiate imm = null;
                RiscRegister res = toRiscRegister(icmp.getDest());
                if(icmp.getOp() == Icmp.CompareOp.LESS) {
                    if (icmp.getRhs() instanceof ConstInt) {
                        imm = toImm(((ConstInt) icmp.getRhs()).getValue());
                    }
                    if (imm != null) {
                        currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.slti,res,toRiscRegister(icmp.getLhs()),imm));
                    }
                    else {
                        RiscRegister lhs = toRiscRegister(icmp.getLhs());
                        RiscRegister rhs = toRiscRegister(icmp.getRhs());
                        currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.slt, lhs,rhs,res));
                    }
                }
                else if(icmp.getOp() == Icmp.CompareOp.GREATER){
                    if(icmp.getLhs() instanceof ConstInt){
                        imm = toImm(((ConstInt) icmp.getLhs()).getValue());
                    }
                    if(imm != null){
                        currentBB.addInst(new ImmOperation(currentBB,  ImmOperation.IOp.slti,res,toRiscRegister(icmp.getRhs()),imm));
                    }
                    else {
                        RiscRegister lhs = toRiscRegister(icmp.getLhs());
                        RiscRegister rhs = toRiscRegister(icmp.getRhs());
                        currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.slt, rhs,lhs,res));
                    }
                }
                else if(icmp.getOp() == Icmp.CompareOp.GEQ){
                    if (icmp.getRhs() instanceof ConstInt) {
                        imm = toImm(((ConstInt) icmp.getRhs()).getValue());
                    }
                    if (imm != null) {
                        currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.slti,res,toRiscRegister(icmp.getLhs()),imm));

                    }
                    else {
                        RiscRegister lhs = toRiscRegister(icmp.getLhs());
                        RiscRegister rhs = toRiscRegister(icmp.getRhs());
                        currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.slt, lhs,rhs,res));
                    }
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.xori,res,res,new Immidiate(1)));
                }
                else if(icmp.getOp() == Icmp.CompareOp.LEQ){
                    if(icmp.getLhs() instanceof ConstInt){
                        imm = toImm(((ConstInt) icmp.getLhs()).getValue());
                    }
                    if(imm != null){
                        currentBB.addInst(new ImmOperation(currentBB,  ImmOperation.IOp.slti,res,toRiscRegister(icmp.getRhs()),imm));
                    }
                    else {
                        RiscRegister lhs = toRiscRegister(icmp.getLhs());
                        RiscRegister rhs = toRiscRegister(icmp.getRhs());
                        currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.slt, rhs,lhs,res));
                    }
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.xori,res,res,new Immidiate(1)));
                }

                else {
                    throw new RuntimeException("nononono!");
                }

            }
        }
    }

    @Override
    public void visit(Load load) {
        RiscInstruction riscInst = load.getRiscInst();
        if(riscInst != null){
            riscInst.setParentBB(currentBB);
            currentBB.addInst(riscInst);
            return;
        }
        else {
            Operand ptr = load.getDest();
            if(ptr instanceof StaticVar && ((StaticVar) ptr).getInit() instanceof ConstString){
                VirtualReg tmp = currentFunction.addRegister("tmp");
                ConstStrings string = ((ConstString) ((StaticVar) ptr).getInit()).getRiscGS();
                currentBB.addInst(new Lui(currentBB,tmp,new BackEnd.Operand.Address(RegisterTable.hi,string)));
                currentBB.addInst(new ILoad(currentBB,toRiscRegister(load.getRes()),tmp,new BackEnd.Operand.Address(RegisterTable.lo,string)));
            }
            else if(ptr instanceof StaticVar){
                VirtualReg tmp = currentFunction.addRegister("gv");
                GlobalVar globalVar = ((StaticVar) ptr).getRiscGV();
                currentBB.addInst(new Lui(currentBB,tmp,new BackEnd.Operand.Address(RegisterTable.hi,globalVar)));
                currentBB.addInst(new ILoad(currentBB,toRiscRegister(load.getRes()),tmp,new BackEnd.Operand.Address(RegisterTable.lo,globalVar)));
            }
            else {
                currentBB.addInst(new ILoad(currentBB,toRiscRegister(load.getRes()),toRiscRegister(load.getDest()),new Immidiate(0)));
            }
        }
    }

    @Override
    public void visit(Return retJump) {

    }

    @Override
    public void visit(Store store) {
        IRType storeType = store.getValue().getType();
        assert storeType instanceof IntIRType || storeType instanceof PtrType;
        Stype.BSize op = Stype.BSize.sw;
        if(storeType.getByteWidth() == 1){
            op = Stype.BSize.sb;
        }

        if(store.getDest() instanceof StaticVar) {
            if (!(((StaticVar) store.getDest()).getInit() instanceof ConstString)) {
                GlobalVar globalVar = ((StaticVar) store.getDest()).getRiscGV();
                VirtualReg lui = currentFunction.addRegister("luihigh");
                currentBB.addInst(new Lui(currentBB, lui, new BackEnd.Operand.Address(RegisterTable.hi, globalVar)));
                currentBB.addInst(new Stype(
                        currentBB, toRiscRegister(store.getValue()), lui, new BackEnd.Operand.Address(RegisterTable.lo, globalVar), op));

            }
            else {//string
                ConstStrings globalVar = ((ConstString) ((StaticVar) store.getDest()).getInit()).getRiscGS();
                VirtualReg lui = currentFunction.addRegister("luihigh");
                currentBB.addInst(new Lui(currentBB, lui, new BackEnd.Operand.Address(RegisterTable.hi, globalVar)));
                currentBB.addInst(new Stype(
                        currentBB, toRiscRegister(store.getValue()), lui, new BackEnd.Operand.Address(RegisterTable.lo, globalVar), op));
            }
        }
        else {
            currentBB.addInst(new Stype(
                    currentBB,toRiscRegister(store.getValue()),toRiscRegister(store.getDest()),new Immidiate(0),op));
        }
    }

    @Override
    public void visit(CallFunction callFunction) {
        ArrayList<Operand> paras = callFunction.getParameters();
        for(int i = 0; i< 8 && i < paras.size();  ++i){
            PhysicalReg reg = RegisterTable.argumentRegisters[i];
            currentBB.addInst(new Move(currentBB,reg,toRiscRegister(paras.get(i))));
        }

        ArrayList<StackSlot>  slots = new ArrayList<>();
        for( int i = 8; i < paras.size(); ++i){
//            StackSlot slot = new StackSlot();//TODO

//            currentBB.addInst(new Stype(currentBB,toRiscRegister(paras.get(i)),slot,));
//            slots.add(slot);
        }
    }

    private void getPtrAndLoadStore(GetPtr getPtr, Immidiate offset){
        for(IRNode inst: getPtr.getDest().getUsers()){
            assert inst instanceof Instruction;
            if(inst instanceof Load){
                ((Load) inst).setRiscInst(new ILoad(currentBB,toRiscRegister(((Load) inst).getRes()),
                        toRiscRegister(getPtr.getPointer()),offset));
            }
            else{

                assert inst instanceof Store;
                IRType storeType = ((Store) inst).getValue().getType();
                Stype.BSize op = Stype.BSize.sw;
                if(storeType.getByteWidth() == 1){
                    op = Stype.BSize.sb;
                }
                ((Store) inst).setRiscInst(new Stype(currentBB,toRiscRegister(((Store) inst).getValue()),toRiscRegister(getPtr.getDest()),offset,op));
            }
        }
    }
    @Override
    public void visit(GetPtr getPtr) {
         Register res = getPtr.getDest();
         Operand ptr = getPtr.getPointer();
         if(getPtr.getPointer() instanceof StaticVar &&
                ((StaticVar) getPtr.getPointer()).getInit() instanceof ConstString){
             VirtualReg tmp = currentFunction.addRegister("tmp");
             ConstStrings str = ((ConstString) ((StaticVar) getPtr.getPointer()).getInit()).getRiscGS();
             currentBB.addInst(new Lui(currentBB,tmp,new Address(RegisterTable.hi,str)));
             currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.addi, toRiscRegister(res),tmp,new Address(RegisterTable.lo, str)));
             return;
         }//TODO :I  don't know actually


         if(getPtr.getIndex().size()  == 1){//array
             Operand ind = getPtr.getIndex().get(0);
             if(ind instanceof ConstInt){
                 int offset = ((ConstInt) ind).getValue() * 4;
                 Immidiate imm = toImm(offset);
                 if(imm != null){
                     if(getPtr.onlyUseForLoadStore()){
                         getPtrAndLoadStore(getPtr, imm);
                         return;
                     }
                     currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.addi,toRiscRegister(res),toRiscRegister(ptr),imm));
                 }
                 else {
                     RiscRegister off = toRiscRegister(new ConstInt(offset, IntIRType.intType.i32));
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.add, toRiscRegister(ptr), off,toRiscRegister(res)));
                 }

             } else {
                 RiscRegister rs1 = toRiscRegister(ind);
                 RiscRegister rs2 = currentFunction.addRegister("slli");
                 currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.slli, rs2,rs1, new Immidiate(2)));
                 currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.add,rs1,rs2,toRiscRegister(res)));

             }
         }
         else {//class type
             assert getPtr.getIndex().size() == 2;
             assert getPtr.getIndex().get(0) instanceof ConstInt;
             assert ((ConstInt) getPtr.getIndex().get(0)).getValue() == 0;
             assert getPtr.getIndex().get(1) instanceof ConstInt;
             if(ptr instanceof ConstNull){
                 currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.addi, toRiscRegister(res),
                         RegisterTable.zero, new Immidiate(((ConstInt) getPtr.getIndex().get(1)).getValue())));
             }
             else {
                 assert ptr.getType()  instanceof PtrType;
                 assert ((PtrType) ptr.getType()).getPointerType() instanceof ClassIRType;
                 RiscRegister rptr = toRiscRegister(ptr);

                 ClassIRType classtype = (ClassIRType)((PtrType) ptr.getType()).getPointerType();
                 int index = ((ConstInt) getPtr.getIndex().get(1)).getValue();
                 int offset = classtype.getOffset(index);
                 Immidiate imm = toImm(offset);

                 if(imm != null) {
                     if (getPtr.onlyUseForLoadStore()) {
                         getPtrAndLoadStore(getPtr, imm);
                     }
                     else {
                         if(offset == 0){
                             currentBB.addInst(new Move(currentBB,toRiscRegister(res),rptr));
                         }
                         else currentBB.addInst(
                                 new ImmOperation(currentBB, ImmOperation.IOp.addi,toRiscRegister(res),rptr,imm));
                     }
                 }
                 else {//TODO can be optimized if ptr.getPtrType is pointer again
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.add,rptr,
                            toRiscRegister(new ConstInt(offset, IntIRType.intType.i32)),toRiscRegister(res)));
                 }
             }
         }

    }

    @Override
    public void visit(Phi phi) {
//do nothing
    }



    @Override
    public void visit(BitCast bitCast) {
        currentBB.addInst(new Move(currentBB,toRiscRegister(bitCast.getRes()),toRiscRegister(bitCast.getSrc())));
    }

    @Override
    public void visit(MoveInst move) {
        currentBB.addInst(new Move(currentBB,toRiscRegister(move.getSrc()),toRiscRegister(move.getRes())));
    }

    private RiscRegister toRiscRegister(Operand irOperand){
        if(irOperand instanceof ConstInt){
            int val = ((ConstInt) irOperand).getValue();
            VirtualReg vreg = currentFunction.addRegister("tmp");
            currentBB.addInst(new Li(currentBB,vreg,new Immidiate(val)));
            return vreg;
        }
        else if(irOperand instanceof ConstBool){
            boolean val = ((ConstBool) irOperand).isValue();
            if(!val) return RegisterTable.zero;
            else {
                VirtualReg vreg = currentFunction.addRegister("tmp");
                currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.addi,vreg, RegisterTable.zero,new Immidiate(1)));
                return vreg;
            }
        }
        else if(irOperand instanceof ConstNull){
            return RegisterTable.zero;
        }
        else if(irOperand instanceof Register){
            RiscRegister reg = ((Register) irOperand).getRiscRegister();
            if(reg!= null) return reg;
            else {
                reg = currentFunction.addRegister(irOperand.getName());
                return reg;
            }
        }
        return null;
    }

}
