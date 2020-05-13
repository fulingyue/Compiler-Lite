package FrontEnd.IR.Operand;

import BackEnd.Operands.GlobalVar;
import BackEnd.Operands.Immidiate;
import BackEnd.Operands.PhysicalReg;

public class Address extends Immidiate {
    private PhysicalReg  reg; // %hi or %lo
    private GlobalVar globalVar;

    public Address( PhysicalReg reg, GlobalVar globalVar) {
        super(0);
        this.reg = reg;
        this.globalVar = globalVar;
    }


}
