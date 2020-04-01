package FrontEnd.IR.Operand;

import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;

public class StaticVar extends Operand{
    private  Operand init;

    public StaticVar(String name, IRType  type, Operand init) {
            super(name,type);
            this.init = init;
    }


    @Override
    public String print() {
        return "@" + name;
    }

    /////getter and setter////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Operand getInit() {
        return init;
    }

    public void setInit(Operand init) {
        this.init = init;
    }

}
