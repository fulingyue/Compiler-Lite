package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Parameter;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IRVisitor;
import util.Pair;

import java.util.HashSet;
import java.util.Set;

public class Phi extends Instruction {
    private Set<Pair<Operand, BasicBlock>> branches;
    private Register res;

    public Phi(String name, BasicBlock bb, Set<Pair<Operand, BasicBlock>> branches, Register res) {
        super(name, bb);
        this.branches = branches;
        this.res = res;
    }

    @Override
    public void add() {
        for (Pair<Operand,BasicBlock> item: branches) {
            item.getKey().addUser(this);
            item.getValue().addUser(this);//addPhiUser
        }
        res.addDef(this);

    }

    @Override
    public void removeUsers() {
        for(Pair<Operand,BasicBlock> pair: branches) {
            pair.getValue().removeUser(this);
            pair.getKey().removeUser(this);
        }
    }

    @Override
    public void removeDefs() {
        res.removeDef(this);
    }


    public void removeIncomeBB(BasicBlock bb){
        for(Pair<Operand, BasicBlock> pair: branches){
            if(pair.getValue() == bb){
                branches.remove(pair);
            }
        }
    }
    public void addBr(Operand operand, BasicBlock bb) {
        branches.add(new Pair<>(operand,bb));
        operand.addUser(this);
        bb.addUser(this);
    }


    @Override
    public String print() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(res.print()).append(" = phi ").append(res.getType().print()).append(" ");
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

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        for(Pair<Operand,BasicBlock> item:branches) {
            if(item.getKey() == oldUser) {
                assert newUser instanceof Operand;
                item.getKey().removeUser(this);
                item.setKey((Operand)newUser);
                item.getKey().addUser(this);
            }
            else if(item.getValue()  ==  oldUser){
                assert newUser instanceof BasicBlock;
                item.getValue().removeUser(this);
                item.setValue((BasicBlock)newUser);
                item.getValue().addUser(this);
            }
        }
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
