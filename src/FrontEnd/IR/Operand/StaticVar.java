package FrontEnd.IR.Operand;

import BackEnd.Operands.GlobalVar;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IR.Type.PtrType;

public class StaticVar extends Operand{
    private  Operand init;
    private IRType actualType;

    private GlobalVar riscGV = null;

    public GlobalVar getRiscGV() {
        return riscGV;
    }

    public void setRiscGV(GlobalVar riscGV) {
        this.riscGV = riscGV;
    }

    public IRType getActualType() {
        return actualType;
    }

    public void setActualType(IRType actualType) {
        this.actualType = actualType;
    }

    public StaticVar(String name, IRType  type, Operand init) {
            super(name,new PtrType(type));
            this.init = init;

            actualType =type;

    }


    @Override
    public String print() {
        return "@" + name;
    }

    public String printDef() {
        StringBuilder stringBuilder = new StringBuilder(print() + " = ");
        assert init instanceof Constant;
        if(init instanceof ConstString){
            stringBuilder.append("private unnamed_addr constant ").append(actualType.print()).append(" ").append(init.print());
        } else {
            stringBuilder.append("global ").append(actualType.print()).append(" ").append(init.print());
        }
        return stringBuilder.toString();
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
