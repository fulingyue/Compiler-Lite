package BackEnd.Instruction;

import BackEnd.Operands.RiscRegister;
import BackEnd.RegisterTable;
import BackEnd.RiscBB;
import BackEnd.RiscFunction;

public class CallFunc extends RiscInstruction {
    RiscFunction function;
    private int paraNum;

    public CallFunc(RiscBB parentBB, RiscFunction function, int paraNum) {
        super(parentBB);
        this.function = function;
        this.paraNum = paraNum;
    }
    @Override
    public void add(){
        for(int i = 0; i < paraNum  && i  < 8; ++i){
            addUse(RegisterTable.argumentRegisters[i]);
        }
        for (int i =0;i < 16; ++i){
            addDef(RegisterTable.callerSavedRegisters[i]);
        }
    }

    @Override
    public String print() {
        return "\tcall\t" + function.getName();
    }

    @Override
    public void replaceUse(RiscRegister old, RiscRegister newUse) {

    }

    @Override
    public void replaceDef(RiscRegister old, RiscRegister newDef) {

    }


    public RiscFunction getFunction() {
        return function;
    }

    public void setFunction(RiscFunction function) {
        this.function = function;
    }
}
