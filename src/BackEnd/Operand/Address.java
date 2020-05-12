package BackEnd.Operand;

public class Address extends Immidiate{

    private PhysicalReg reg;
    private GlobalVar globalVar;

    public Address(PhysicalReg reg, GlobalVar var) {
        super(0);
        this.reg = reg;
        this.globalVar = var;
    }
}
