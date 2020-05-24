package BackEnd.Operands;

public class GVAddress extends Immidiate{

    private PhysicalReg reg;
    private GlobalVar globalVar;


    public GVAddress(PhysicalReg reg, GlobalVar var) {
        super(0);
        this.reg = reg;
        this.globalVar = var;
    }

    @Override
    public String print(){
        return  reg.print() + "(" + globalVar.getName() + ")";
    }
}
