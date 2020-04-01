package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IRVisitor;

public class BranchJump extends Instruction {
    private Operand condition;
    private BasicBlock  thenBlock, elseBlock;

    public BranchJump(String name, BasicBlock bb, Operand condition, BasicBlock thenBB, BasicBlock elseBB) {
        super(name,bb);
        this.condition = condition;
        this.thenBlock  = thenBB;
        this.elseBlock = elseBB;
        assert condition == null;
    }

//if  no conditionn  then thenbb
    @Override
    public void add() {
        if(condition != null) {
            this.Usages.add(condition);
            this.Usages.add(elseBlock);
        }
        this.Usages.add(elseBlock);
    }

    @Override
    public String print() {
        if(condition != null)
            return "br i1 " + condition.print() + ", label " + thenBlock.print() + ", label " +elseBlock.print();
        return "br label " + thenBlock.print();
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    /////getter and setter////////

    public Operand getCondition() {
        return condition;
    }

    public void setCondition(Operand condition) {
        this.condition = condition;
    }

    public BasicBlock getThenBlock() {
        return thenBlock;
    }

    public void setThenBlock(BasicBlock thenBlock) {
        this.thenBlock = thenBlock;
    }

    public BasicBlock getElseBlock() {
        return elseBlock;
    }

    public void setElseBlock(BasicBlock elseBlock) {
        this.elseBlock = elseBlock;
    }
}
