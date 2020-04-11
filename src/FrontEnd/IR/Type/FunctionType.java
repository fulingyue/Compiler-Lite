package FrontEnd.IR.Type;

import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Parameter;

import java.util.ArrayList;

public class FunctionType extends IRType {
    private ArrayList<IRType> paraType = new ArrayList<>();
    private IRType returnType;

    public FunctionType(IRType returnType, ArrayList<Parameter> para)
    {
        for(int i = 0; i < para.size();++i)
        {
            paraType.add(para.get(i).getType());
        }
        this.returnType = returnType;
        byteWidth  = 0;
    }
    @Override
    public Operand getDefaultValue() {
        throw new RuntimeException();
    }
    @Override
    public String print() {
        StringBuilder string = new StringBuilder();
        string.append("FunctionType: ").append(returnType.print()).append(" (");
        for (int i = 0; i < paraType.size(); i++) {
            string.append(paraType.get(i).print());
            if (i != paraType.size() - 1)
                string.append(", ");
        }
        string.append(")\n");
        return string.toString();
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
