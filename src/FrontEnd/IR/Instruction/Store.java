package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.StaticVar;
import FrontEnd.IR.Type.PtrType;
import FrontEnd.IRVisitor;

public class Store extends Instruction{
    private Operand value;
    private Operand dest;

    public Store(String name, BasicBlock bb, Operand value, Operand dest) {
        super(name, bb);
        this.value = value;
        this.dest = dest;
    }


    @Override
    public void add() {
        value.addUser(this);
        dest.addDef(this);
    }

    @Override
    public String print() {
//        if(dest instanceof StaticVar) {
//            return "store " + dest.getType().print() + " " + value.print() +
//                    ", " + (new PtrType(dest.getType())).print() + " " + dest.print();
//        }
        if(dest == null) {
            System.out.print("fasfas");
        }
//        return name + " " + ((PtrType)(dest.getType())).getPointerType().print() + " " + value.print() +
//                ", " + dest.getType().print() + " " + dest.print();
        return "store " + ((PtrType)(dest.getType())).getPointerType().print() + " " + value.print() +
                ", " + dest.getType().print() + " " + dest.print();

    }

    @Override
    public void removeUsers() {
        value.removeUser(this);
    }


    @Override
    public void removeDefs() {
        dest.removeDef(this);
    }

    @Override
    public void accept(IRVisitor vistor) {
        vistor.visit(this);
    }

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        if(value == oldUser) {
            assert newUser  instanceof Operand;
            value = (Operand)newUser;
            value.addUser(this);
        }
        if(dest == oldUser) {
            assert newUser instanceof Operand;
            dest  = (Operand)newUser;
            dest.addUser(this);
        }
    }

    public Operand getValue() {
        return value;
    }

    public void setValue(Operand value) {
        this.value = value;
    }

    public Operand getDest() {
        return dest;
    }

    public void setDest(Operand dest) {
        this.dest = dest;
    }
}
