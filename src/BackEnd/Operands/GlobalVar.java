package BackEnd.Operands;

public class GlobalVar extends RiscOperand {
    private RiscOperand val;
    private String name;


    public GlobalVar(RiscOperand val, String name) {
        this.val = val;
        this.name = name;
    }

    public GlobalVar(RiscOperand val) {
        this.val = val;
    }

    public GlobalVar(String name) {
        this.name = name;
    }
    @Override
    public int getSize() {
        return val.getSize();
    }

    @Override
    public String print() {
        return null;
    }

    public RiscOperand getVal() {
        return val;
    }

    public void setVal(RiscOperand val) {
        this.val = val;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
