package BackEnd;

import BackEnd.Instruction.*;
import BackEnd.Operands.VirtualReg;
import BackEnd.Operands.*;
import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Instruction.Return;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.*;
import FrontEnd.IR.Type.ClassIRType;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Type.IntIRType;
import FrontEnd.IR.Type.PtrType;
import FrontEnd.IRVisitor;
import util.Defines;

import java.util.ArrayList;
import java.util.HashMap;

import static BackEnd.Instruction.UnaryBranch.UnaryBrOp.beqz;

public class InstructionSelection implements IRVisitor {
    public RiscModule module;
    private Module IRModule;
    private RiscFunction currentFunction;
    private RiscBB currentBB;
    private VirtualReg[] calleeSaved;
    private HashMap<String, ConstStrings> stringMap;
    private HashMap<Operand, AddrWithOffset> getPtrMap;

    public InstructionSelection(Module irModule) {
        IRModule = irModule;
        module = new RiscModule();
        calleeSaved = new VirtualReg[13];
        stringMap = new HashMap<>();
        getPtrMap = new HashMap<>();
    }

    public void run(){
        visit(IRModule);
    }

    @Override
    public void visit(Module root) {
///////////////register functions/////////////
        for(IRFunction function: root.getExternalFuncMap().values()){
            RiscFunction newFunc = new RiscFunction(function.getName(),function.getParaList().size(),null);
            function.setRiscFunction(newFunc);
            module.addExtern(newFunc);
        }
        for(IRFunction function: root.getFunctionMap().values()){
            RiscFunction newFunc = new RiscFunction(function.getName(), function.getParaList().size(),function);
            function.setRiscFunction(newFunc);
            module.addFunction(newFunc);
        }

///////////////global variables//////////////////
        for(String str: root.getStaticStrings().keySet()){//register global strings
            StaticVar globalString = root.getStaticStrings().get(str);
            assert globalString.getInit() instanceof ConstString;
            ConstStrings riscString = new ConstStrings(globalString.getName(),str);
            stringMap.put(str,riscString);
        }

        for(StaticVar global: root.getStaticVariableMap().values()){
            visit(global);
        }
 /////////////////////////visit functions////////////////

        for(IRFunction function: root.getFunctionMap().values()){

            visit(function);
        }

    }


    @Override
    public void visit(IRFunction function) {

        currentFunction = function.getRiscFunction();

        BasicBlock entranceBB = function.getEntranceBB();
        currentBB = entranceBB.getRiscBB();
        currentFunction.setEntranceBB(currentBB);

        /////////every function has a stackframe/////////////
        StackFrame stackFrame = new StackFrame(currentFunction);
        currentFunction.setStackFrame(stackFrame);

        /////////////////// preface of the function/////////////
///////////////////load ra////////////////
        calleeSaved[12] = currentFunction.addRegister("ra.save");
        currentBB.addInst(new Move(currentBB,RegisterTable.ra,calleeSaved[12]));
//////////////load callee Saved//////////////////////////////
        for(int i = 0;i < 12; ++i) {
            PhysicalReg reg = RegisterTable.calleeSavedRegisters[i];
            calleeSaved[i] = currentFunction.addRegister(reg.name + ".save");
            currentBB.addInst(new Move(currentBB,reg,calleeSaved[i]));
        }
////////////////////arguments//////////////
        //move argument from register///////
        ArrayList<Parameter> parameters = function.getParaList();
        for(int i = 0; i< Integer.min(8,parameters.size()); ++i){
            currentBB.addInst(new Move(currentBB,RegisterTable.argumentRegisters[i],toRiscRegister(parameters.get(i))));
        }

        ///////load argument from stack////////
        for(int i = 8;i < parameters.size(); ++i) {
            Parameter para = parameters.get(i);
            StackOffset stackOffset = new StackOffset(para.getName()+".para");
            stackFrame.addFormalPara(stackOffset);
            currentBB.addInst(new ILoad(currentBB,toRiscRegister(para),stackOffset, ILoad.LoadType.lw));
        }


        for(BasicBlock bb = function.getEntranceBB();bb != null; bb = bb.getNextBB()){
            visit(bb);
        }

    }
    @Override
    public void visit(BasicBlock bb) {
        currentBB = bb.getRiscBB();
        for(Instruction instruction = bb.getHead();instruction!= null; instruction = instruction.getNxt()){
            instruction.accept(this);
        }
    }


    private void visit(StaticVar var){
        Operand init = var.getInit();
        if(init instanceof ConstString){
            ConstStrings cs = stringMap.get(((ConstString) init).getValue());
            cs.setName(var.getName().replace(".",""));
            var.setRiscGV(cs);
            module.addGV(cs);
            module.addString(cs);
        }
        else {
            GlobalVar gv = new GlobalVar(var.getName().split("\\.")[0]);
            var.setRiscGV(gv);
            module.addGV(gv);
            if (init instanceof ConstInt) {
                Immidiate imm = new Immidiate(((ConstInt) init).getValue());
                gv.setVal(imm);
                gv.setSize(4);
            }
            else if(init instanceof ConstBool){
                int val = ((ConstBool) init).isValue() ? 1:0;
                Immidiate imm = new Immidiate(val);
                gv.setVal(imm);
                gv.setSize(1);
            }
            else if(init instanceof ConstNull){
                assert var.getType() instanceof PtrType;
                gv.setVal(new Immidiate(0));
                gv.setSize(8);
            }
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
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.xor, rvReg,toRiscRegister(lhs),toRiscRegister(rhs)));
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
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.and, rvReg,toRiscRegister(lhs),toRiscRegister(rhs)));
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
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.or, rvReg, toRiscRegister(lhs),toRiscRegister(rhs)));
                }
                break;
            case SAR:
                if(rhs instanceof ConstInt){
                    Immidiate  imm = toImm(((ConstInt) rhs).getValue());
                    RiscRegister reg = toRiscRegister(lhs);
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.srai,rvReg,reg,imm));
                }
                else {
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.sra, rvReg, toRiscRegister(lhs),toRiscRegister(rhs)));
                }
                break;
            case SAL:
                if(rhs instanceof ConstInt){
                    Immidiate  imm = toImm(((ConstInt) rhs).getValue());
                    RiscRegister reg = toRiscRegister(lhs);
                    currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.slli,rvReg,reg,imm));
                }
                else {
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.sll, rvReg, toRiscRegister(lhs),toRiscRegister(rhs)));
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
                currentBB.addInst(new Jal(currentBB,branchJump.getElseBlock().getRiscBB()));
            }
            else {
                currentBB.addInst(new UnaryBranch(currentBB,beqz,toRiscRegister(cond), branchJump.getElseBlock().getRiscBB()));
                currentBB.addInst(new Jal(currentBB,branchJump.getThenBlock().getRiscBB()));

            }

        }


    }

    private void icmpAndBranch(Icmp icmp){

            BinaryBranch.BranchOp op = null;
            switch (icmp.getOp()) {
                case EQUAL:
                    op = BinaryBranch.BranchOp.beq;
                    break;
                case NOTEQUAL:
                    op = BinaryBranch.BranchOp.bne;
                    break;
                case GREATER:
                    op = BinaryBranch.BranchOp.bgt;
                    break;
                case LEQ:
                    op = BinaryBranch.BranchOp.ble;
                    break;
                case LESS:
                    op = BinaryBranch.BranchOp.blt;
                    break;
                case GEQ:
                    op = BinaryBranch.BranchOp.bge;
                    break;
            }

            for (IRNode inst : icmp.getDest().getUsers()) {
                assert inst instanceof BranchJump;
                ((BranchJump) inst).setRiscInstruction(new BinaryBranch(currentBB, op, toRiscRegister(icmp.getLhs()), toRiscRegister(icmp.getRhs()),
                        ((BranchJump) inst).getThenBlock().getRiscBB()));

            }

    }

    @Override
    public void visit(Icmp icmp) {
        if(icmp.onlyInBranch()){//
           icmpAndBranch(icmp); //branch inst will handle it
        }
        else {
            if(icmp.getOp() == Icmp.CompareOp.EQUAL) {
                RiscRegister lhs = toRiscRegister(icmp.getLhs());
                RiscRegister rhs = toRiscRegister(icmp.getRhs());
                RiscRegister res = toRiscRegister(icmp.getDest());
                VirtualReg vreg = currentFunction.addRegister("tmp");
                currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.xor, vreg, lhs, rhs));
                currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.sltiu, res, vreg, new Immidiate(1)));
            }
            else if(icmp.getOp() == Icmp.CompareOp.NOTEQUAL) {
                RiscRegister lhs = toRiscRegister(icmp.getLhs());
                RiscRegister rhs = toRiscRegister(icmp.getRhs());
                RiscRegister res = toRiscRegister(icmp.getDest());
                VirtualReg tmp = currentFunction.addRegister("tmp");
                currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.xor,tmp, lhs, rhs));
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
                        currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.slt, res,lhs,rhs));
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
                        currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.slt,res, rhs,lhs));
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
                        currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.slt,res, lhs,rhs));
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
                        currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.slt,res, rhs,lhs));
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
        }
        else {
            Operand ptr = load.getDest();
            ILoad.LoadType op =  load.getType().getByteWidth() == 1? ILoad.LoadType.lb : ILoad.LoadType.lw;

            if (ptr instanceof StaticVar){
                VirtualReg tmp = currentFunction.addRegister("lui.high");
                GlobalVar globalVar = ((StaticVar) ptr).getRiscGV();
                currentBB.addInst(new Lui(currentBB,tmp,new GVAddress(RegisterTable.hi,globalVar)));
                currentBB.addInst(new ILoad(
                        currentBB,toRiscRegister(load.getRes()),
                        new AddrWithOffset(tmp, new GVAddress(RegisterTable.lo,
                        globalVar)),op));
            }
            else {
                if(load.getDest() instanceof ConstNull){
                    currentBB.addInst(new ILoad(currentBB,toRiscRegister(load.getRes()),
                             new AddrWithOffset(RegisterTable.zero,new Immidiate(0)),op));
                }
                else {
                    assert load.getDest() instanceof Parameter || load.getDest() instanceof Register;

                    if(getPtrMap.containsKey(load.getDest())){
                        AddrWithOffset addr = getPtrMap.get(load.getDest());
                        currentBB.addInst(new ILoad(
                                currentBB,toRiscRegister(load.getRes()),addr,op));
                    }
                    else {
                        currentBB.addInst(new ILoad(currentBB,toRiscRegister(load.getRes()),
                             new AddrWithOffset(toRiscRegister(load.getDest()), new Immidiate(0)),op ));
                    }
                }
            }
        }
    }


    @Override
    public void visit(Return retJump) {
        //////save return value/////////
        if(retJump.getReturnVal() != null){
            currentBB.addInst(new Move(currentBB,toRiscRegister(retJump.getReturnVal()),RegisterTable.a0));
        }

        /////////////recover callee saved registers//////////
        for(int i = 11; i >=0 ; i --){
            currentBB.addInst(new Move(currentBB,calleeSaved[i],RegisterTable.calleeSavedRegisters[i]));
        }

        ///////////load ra//////////////
        currentBB.addInst(new Move(currentBB,calleeSaved[12],RegisterTable.ra));
        currentFunction.setExitBB(currentBB);

        currentBB.addInst(new BackEnd.Instruction.Return(currentBB));
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
            GlobalVar globalVar = ((StaticVar) store.getDest()).getRiscGV();
            VirtualReg lui = currentFunction.addRegister("luihigh");
            currentBB.addInst(new Lui(currentBB, lui, new GVAddress(RegisterTable.hi, globalVar)));
            currentBB.addInst(new Stype(
                    currentBB, toRiscRegister(store.getValue()),
                    new AddrWithOffset(lui, new GVAddress(RegisterTable.lo, globalVar)), op));
        }
        else {
            if(store.getDest() instanceof ConstNull)
                currentBB.addInst(new Stype(currentBB,toRiscRegister(store.getValue()),
                       new AddrWithOffset(RegisterTable.zero, new Immidiate(0)), op));
            else {
                    currentBB.addInst(new Stype(
                            currentBB,toRiscRegister(store.getValue()),
                            new AddrWithOffset(toRiscRegister(store.getDest()),new Immidiate(0)),op));

            }
        }
    }

    @Override
    public void visit(CallFunction callFunction) {

        ArrayList<Operand> paras = callFunction.getParameters();
        ////////save parameters to register////////////
        for(int i = 0; i < 8 && i < paras.size();  ++i){
            PhysicalReg reg = RegisterTable.argumentRegisters[i];
            currentBB.addInst(new Move(currentBB,toRiscRegister(paras.get(i)),reg));
        }


        RiscFunction callee = callFunction.getFunction().getRiscFunction();

        StackFrame stackFrame = currentFunction.getStackFrame();

        if(stackFrame.getParas().containsKey(callee)){
            ArrayList<StackOffset> paralist = stackFrame.getParas().get(callee);
            for(int i = 8; i < paras.size(); i++){
                currentBB.addInst(new Stype(
                        currentBB,toRiscRegister(paras.get(i)),
                        paralist.get(i-8), Stype.BSize.sw));
            }
        }
        else {
            ArrayList<StackOffset> paralist = new ArrayList<>();
            for(int i = 8;i <  paras.size(); i++){
                StackOffset stackOffset = new StackOffset(".para"+i);
                paralist.add(stackOffset);
                currentBB.addInst(new Stype(currentBB,toRiscRegister(paras.get(i)),stackOffset,
                         Stype.BSize.sw));
            }
            stackFrame.getParas().put(callee,paralist);
        }

        currentBB.addInst(new CallFunc(
                currentBB,callee,paras.size()
        ));

        if(callFunction.getResult()!=null){
            currentBB.addInst(new Move(currentBB,RegisterTable.a0,toRiscRegister(callFunction.getResult())));
        }

    }

    private void getPtrAndLoadStore(GetPtr getPtr, Immidiate offset){
        for(IRNode inst: getPtr.getDest().getUsers()){
            assert inst instanceof Instruction;
            if(inst instanceof Load){
                ILoad.LoadType op = ((Load) inst).getType().getByteWidth() == 1? ILoad.LoadType.lb : ILoad.LoadType.lw;
                ((Load) inst).setRiscInst(new ILoad(currentBB,toRiscRegister(((Load) inst).getRes()),
                        new AddrWithOffset(toRiscRegister(getPtr.getPointer()),offset), op));
            }
            else{

                assert inst instanceof Store;
                Stype.BSize op = ((Store) inst).getValue().getType().getByteWidth() == 1?
                        Stype.BSize.sb: Stype.BSize.sw;

                ((Store) inst).setRiscInst(new Stype(currentBB,toRiscRegister(((Store) inst).getValue()),
                        new AddrWithOffset(toRiscRegister(getPtr.getDest()),offset),op));
            }
        }
    }
    @Override
    public void visit(GetPtr getPtr) {
         Register res = getPtr.getDest();
         Operand ptr = getPtr.getPointer();

         if(getPtr.getPointer() instanceof StaticVar){//string
             assert ((StaticVar) getPtr.getPointer()).getInit() instanceof ConstString;
             GlobalVar str =((StaticVar) getPtr.getPointer()).getRiscGV();
             assert str instanceof ConstStrings;
             currentBB.addInst(new LA(currentBB, toRiscRegister(res), str));

         }

         else if(getPtr.getIndex().size()  == 1){//array
             Operand ind = getPtr.getIndex().get(0);
             if(ind instanceof ConstInt){
                 int offset = ((ConstInt) ind).getValue() * 4;
                 Immidiate imm = toImm(offset);
                 if(imm != null){
                     currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.addi,toRiscRegister(res),toRiscRegister(ptr),imm));
                 }
                 else {
                      RiscRegister off = toRiscRegister(new ConstInt(offset, IntIRType.intType.i32));
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.add,toRiscRegister(res), toRiscRegister(ptr), off));
                 }

             } else {
                 RiscRegister rs1 = toRiscRegister(ind);
                 RiscRegister rs2 = currentFunction.addRegister("slli");
                 currentBB.addInst(new ImmOperation(currentBB, ImmOperation.IOp.slli, rs2,rs1, new Immidiate(2)));
                 currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.add,toRiscRegister(res),rs2,toRiscRegister(ptr)));

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
//                     if (getPtr.getDest().onlyUseForLoadStore()) {
//                         getPtrAndLoadStore(getPtr, imm);
//                     }
//                     else {
                         if(offset == 0){
                             currentBB.addInst(new Move(currentBB,rptr,toRiscRegister(res)));
                         }
                         else currentBB.addInst(
                                 new ImmOperation(currentBB, ImmOperation.IOp.addi,toRiscRegister(res),rptr,imm));
//                     }
                 }
                 else {//TODO can be optimized if ptr.getPtrType is pointer again
                    currentBB.addInst(new ArtheticOp(currentBB, ArtheticOp.ROp.add,toRiscRegister(res),rptr,
                            toRiscRegister(new ConstInt(offset, IntIRType.intType.i32))));
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
        currentBB.addInst(new Move(currentBB,toRiscRegister(bitCast.getSrc()),toRiscRegister(bitCast.getRes())));
    }

    @Override
    public void visit(MoveInst move) {
        currentBB.addInst(new Move(currentBB,toRiscRegister(move.getSrc()),toRiscRegister(move.getRes())));
    }

    private RiscRegister toRiscRegister(Operand irOperand){
        if(irOperand instanceof ConstInt){
            int val = ((ConstInt) irOperand).getValue();
            if(val == 0) return RegisterTable.zero;
            VirtualReg vreg = currentFunction.addRegister("immReg");
            currentBB.addInst(new Li(currentBB,vreg,new Immidiate(val)));
            return vreg;
        }
        else if(irOperand instanceof ConstBool){
            boolean val = ((ConstBool) irOperand).isValue();
            if(!val) return RegisterTable.zero;
            else {
                VirtualReg vreg = currentFunction.addRegister("immReg");
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
                ((Register) irOperand).setRiscRegister(reg);
                return reg;
            }
        }
        else if(irOperand instanceof Parameter){
            RiscRegister reg = ((Parameter) irOperand).getRiscRegister();
            if(reg != null) return reg;
            else {
                reg = currentFunction.addRegister(irOperand.getName());
                ((Parameter) irOperand).setRiscRegister(reg);
                return reg;
            }
        }
        return null;
    }

    public RiscModule getModule() {
        return module;
    }

    public void setModule(RiscModule module) {
        this.module = module;
    }

    public Module getIRModule() {
        return IRModule;
    }

    public void setIRModule(Module IRModule) {
        this.IRModule = IRModule;
    }

    public RiscFunction getCurrentFunction() {
        return currentFunction;
    }

    public void setCurrentFunction(RiscFunction currentFunction) {
        this.currentFunction = currentFunction;
    }

    public RiscBB getCurrentBB() {
        return currentBB;
    }

    public void setCurrentBB(RiscBB currentBB) {
        this.currentBB = currentBB;
    }

    public VirtualReg[] getCalleeSaved() {
        return calleeSaved;
    }

    public void setCalleeSaved(VirtualReg[] calleeSaved) {
        this.calleeSaved = calleeSaved;
    }
}
