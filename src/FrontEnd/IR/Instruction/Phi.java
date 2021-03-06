package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IRVisitor;
import Optimize.SCCP;
import util.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Phi extends Instruction {
    private Set<Pair<Operand, BasicBlock>> branches;
    private Register res;

    public Phi(String name, BasicBlock bb, Set<Pair<Operand, BasicBlock>> branches, Register res) {
        super(name, bb);
        this.branches = branches;
        this.res = res;
    }
    public Phi(BasicBlock bb, Set<Pair<Operand, BasicBlock>> branches, Register res) {
        super("phi", bb);
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
        res.setDefInst(this);

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
        HashSet<Pair<Operand, BasicBlock>> removeSet = new HashSet<>();
        for(Pair<Operand, BasicBlock> pair: branches){
            if(pair.getValue() == bb){
                removeSet.add(pair);
            }
        }
        branches.removeAll(removeSet);

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
//                item.getKey().removeUser(this);
                item.setKey((Operand)newUser);
                item.getKey().addUser(this);
            }
            else if(item.getValue()  ==  oldUser){
                assert newUser instanceof BasicBlock;
//                item.getValue().removeUser(this);
                item.setValue((BasicBlock)newUser);
                item.getValue().addUser(this);
            }
        }
    }

    @Override
    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {
        for(Pair<Operand,BasicBlock> iteme:branches){
            iteme.getKey().markLive(workList,alive);
        }
    }

    @Override
    public boolean replaceConst(SCCP sccp) {
        SCCP.LatticeVal latticeVal = sccp.getStatus(res);
        if(latticeVal.getType() == SCCP.LatticeVal.LatticaValType.constant){
            res.replaceUser(latticeVal.getOperand());
            this.remove();
            return true;
        }
        return false;
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
