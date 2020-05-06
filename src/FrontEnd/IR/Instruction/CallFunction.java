package FrontEnd.IR.Instruction;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.IRNode;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Parameter;
import FrontEnd.IR.Operand.Register;
import FrontEnd.IR.Type.VoidType;
import FrontEnd.IRVisitor;
import Optimize.SCCP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

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
        if(result != null) {
            result.addDef(this);
            result.setDefInst(this);
        }
        function.addUser(this);

        for (Operand item: parameters)
            item.addUser(this);
    }

    @Override
    public void removeUsers() {

        function.removeUser(this);

        for (Operand item: parameters)
            item.removeUser(this);
    }


    @Override
    public void removeDefs() {
        if(result != null)
            result.removeDef(this);

    }


    @Override
    public String print() {
        StringBuilder stringBuilder = new StringBuilder();
        if(result!= null) {
            stringBuilder.append(result.print());
            stringBuilder.append(" = ");
        }

        stringBuilder.append("call ");
        stringBuilder.append(function.getFunctionType().getReturnType().print());
        stringBuilder.append(" @");
        stringBuilder.append(function.getName());
        stringBuilder.append("(");
        if(parameters.size() == 0)
            stringBuilder.append(", ");
        for(Operand item: parameters) {
            stringBuilder.append(item.getType().print());
            stringBuilder.append(" ");
            stringBuilder.append(item.print());
            stringBuilder.append(", ");
        }
        stringBuilder.delete(stringBuilder.length()-2,stringBuilder.length());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(IRNode oldUser, IRNode newUser) {
        if(function == oldUser) {
            assert newUser instanceof IRFunction;
//            function.removeUser(this);
            function = (IRFunction)newUser;
            function.addUser(this);
        }
        else {
            for(int i = 0;i < parameters.size();++i) {
                if(parameters.get(i) == oldUser) {
                    assert newUser instanceof Operand;
//                    parameters.get(i).removeUser(this);
                    parameters.set(i,(Operand)newUser);
                    parameters.get(i).addUser(this);
                }
            }
        }
    }

    @Override
    public void markLive(LinkedList<Instruction> workList, HashSet<Instruction> alive) {
        for(Operand para:parameters) {
            para.markLive(workList,alive);
        }
    }

    @Override
    public boolean replaceConst(SCCP sccp) {
        if(result == null)return false;
        SCCP.LatticeVal latticeVal = sccp.getStatus(result);
        if(latticeVal.getType() == SCCP.LatticeVal.LatticaValType.constant){
            result.replaceUser(latticeVal.getOperand());
            this.remove();
            return true;
        }
        return false;
    }


    public IRFunction getFunction() {
        return function;
    }

    public void setFunction(IRFunction function) {
        this.function = function;
    }

    public ArrayList<Operand> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<Operand> parameters) {
        this.parameters = parameters;
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
    }
}
