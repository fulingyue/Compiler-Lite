package FrontEnd.IR.Type;

import FrontEnd.IR.Operand.Operand;

import java.util.ArrayList;

public class FunctionType extends IRType {
    private ArrayList<IRType> paraType;
    private IRType returnType;

    public FunctionType(IRType returnType, ArrayList<IRType> paraType)
    {
        this.paraType =  paraType;
        this.returnType = returnType;
        byteWidth  = 0;
    }
    @Override
    public Operand getDefaultValue() {
        throw new RuntimeException();
    }
    @Override
    public String print() {
        StringBuilder builder = new StringBuilder();
        //TODO
        return builder.toString();
    }

    public ArrayList<IRType> getParaType() {
        return paraType;
    }

    public void setParaType(ArrayList<IRType> paraType) {
        this.paraType = paraType;
    }

    public IRType getReturnType() {
        return returnType;
    }

    public void setReturnType(IRType returnType) {
        this.returnType = returnType;
    }
}
