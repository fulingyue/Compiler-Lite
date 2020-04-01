package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IRVisitor;
import javafx.scene.effect.InnerShadow;

import java.util.ArrayList;

public class CallFunction extends Instruction{
    private IRFunction function;
    private ArrayList<Operand> parameters;
    private Register result;

    public CallFunction(String name, BasicBlock bb, IRFunction function, ArrayList<Operand> parameters, Register result) {
        super(name,bb);
        this.function = function;
        this.parameters = parameters;
        this.result = result;

        assert parameters.size() == function.getParaList().size();
    }

    @Override
    public void add() {
        if(result != null)
            Usages.add(result);

        Usages.add(function);
        for (Operand item: parameters)
            Usages.add(item);
    }


    @Override
    public String print() {
        StringBuilder stringBuilder = new StringBuilder();
        if(result != null) {
            stringBuilder.append(result.print());
            stringBuilder.append(" = ");
        }

        stringBuilder.append("call ");
        stringBuilder.append(function.getFunctionType().getReturnType().print());
        stringBuilder.append(" @");
        stringBuilder.append(function.getName());
        stringBuilder.append("(");
        for(Operand item: parameters) {
            stringBuilder.append(item.getType());
            stringBuilder.append(" ");
            stringBuilder.append(item.print());
            stringBuilder.append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }



}
