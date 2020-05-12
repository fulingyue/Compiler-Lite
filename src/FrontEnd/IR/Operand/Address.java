package FrontEnd.IR.Operand;

import BackEnd.Operand.GlobalVar;
import BackEnd.Operand.Immidiate;
import BackEnd.Operand.PhysicalReg;

public class Address extends Immidiate {
    private PhysicalReg  reg; // %hi or %lo
    private GlobalVar globalVar;

    public Address( PhysicalReg reg, GlobalVar globalVar) {
        super(0);
        this.reg = reg;
        this.globalVar = globalVar;
    }


}
