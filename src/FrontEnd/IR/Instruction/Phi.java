package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IRVisitor;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

public class Phi extends Instruction {
    private Set<Pair<Operand, BasicBlock>> branches = new HashSet<>();
    private Register res;

    public Phi(String name, BasicBlock bb, Set<Pair<Operand, BasicBlock>> branches, Register res) {
        super(name, bb);
        this.branches = branches;
        this.res = res;
    }

    @Override
    public void add() {
        for (Pair<Operand,BasicBlock> item: branches) {
            Usages.add(item.getValue());
            Usages.add(item.getKey());
        }
        res.setParent(this);
    }

    public void addBr(Operand operand, BasicBlock bb) {
        branches.add(new Pair<>(operand,bb));
        //usage?
    }

    @Override
    public String print() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(res.print()).append(" = phi").append(res.getType().print()).append(" ");
        for (Pair<Operand,BasicBlock> pair: branches) {
            stringBuilder.append("[ ").append(pair.getKey().print()).append(", ").append(pair.getValue().print()).append(" ]");
            stringBuilder.append(", ");
        }
        stringBuilder.delete(stringBuilder.length()-2,stringBuilder.length());
        return stringBuilder.toString();
    }



    @Override
    public void accept(IRVisitor visitor){
        visitor.visit(this);
    }
    ////getter and setter/////

    public Set<Pair<Operand, BasicBlock>> getBranches() {
        return branches;
    }

    public void setBranches(Set<Pair<Operand, BasicBlock>> branches) {
        this.branches = branches;
    }

    public Register getRes() {
        return res;
    }

    public void setRes(Register res) {
        this.res = res;
    }
}
