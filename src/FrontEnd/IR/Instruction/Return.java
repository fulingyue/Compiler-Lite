package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Type.VoidType;
import FrontEnd.IRVisitor;
import Optimize.SCCP;

import java.util.HashSet;
import java.util.LinkedList;

public class Return extends Instruction {
    private Operand returnVal;
    private IRType type;

    public Return(String name, BasicBlock bb,IRType type, Operand returnVal) {
        super(name, bb);
        this.returnVal  = returnVal;
        this.type = type;
    }

    @Override
    public void add()  {
        if(returnVal != null) {
            returnVal.addUser(this);
        }
    }

    @Override
    public String print() {
        if(!(type instanceof VoidType)) {
            return "ret " + type.print() + " " + returnVal.print();
        }
        else {
            return "ret void";
        }
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        if(returnVal == oldUser && returnVal != null) {
            assert newUser instanceof Operand;
            returnVal.removeUser(this);
            returnVal = (Operand)newUser;
            returnVal.addUser(this);
        }
    }

    @Override
    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {
        if(returnVal!=null)
            returnVal.markLive(workList,alive);
    }

    @Override
    public boolean replaceConst(SCCP sccp) {
        return false;
    }

    @Override
    public void removeDefs() {

    }

    @Override
    public void removeUsers() {
        if(returnVal != null)
            returnVal.removeUser(this);
    }


    /////getter and setter////////

    public Operand getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(Operand returnVal) {
        this.returnVal = returnVal;
    }

    public IRType getType() {
        return type;
    }

    public void setType(IRType type) {
        this.type = type;
    }
}
