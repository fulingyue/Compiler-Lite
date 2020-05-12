package FrontEnd;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Module;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Operand.*;

public interface IRVisitor {
    void visit(BasicBlock bb);
    void visit(Module root);
    void visit(IRFunction function);

    void visit(AllocateInst allocateInst);
    void visit(BinaryOp inst);
    void visit(BranchJump branchJump);
    void visit(Icmp icmp);
    void visit(Load load);
    void visit(Return retJump);
    void visit(Store store);
    void visit(CallFunction callFunction);
    void visit(GetPtr getPtr);
    void visit(Phi phi);
    void visit (BitCast bitCast);
    void visit(MoveInst move);

//    void visit(VirtualReg  reg);
//    void visit(ConstInt constInt);
//    void visit(StaticString string);
//    void visit(StaticVar var);
//    void visit(ConstBool constBool);
//    void visit(Parameter parameter);
//    void visit(ConstNull constNull);
}
