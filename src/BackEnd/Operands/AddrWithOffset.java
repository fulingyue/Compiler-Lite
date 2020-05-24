package BackEnd.Operands;

public class AddrWithOffset extends Address{
    private RiscRegister base;
    private Immidiate offset;

    public AddrWithOffset(RiscRegister base, Immidiate offset) {
        this.base = base;
        this.offset = offset;
    }

    public void setBase(RiscRegister base) {
        this.base = base;
    }

    public void setOffset(Immidiate offset) {
        this.offset = offset;
    }

    public RiscRegister getBase() {
        return base;
    }

    public Immidiate getOffset() {
        return offset;
    }

    @Override
    public String print() {
        return offset.print()+ "(" +base.print() + ")";
    }

    @Override
    public RiscRegister getUse() {
        return base;
    }
}
