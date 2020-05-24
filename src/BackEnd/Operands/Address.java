package BackEnd.Operands;

public abstract class Address extends RiscOperand{
    public abstract  String print();
    public abstract RiscRegister getUse();
}
