package BackEnd.Operands;

public class GlobalVar extends RiscOperand {
    private RiscOperand init;
    private String name;
    private int size;


    public GlobalVar(RiscOperand val, String name) {
        this.init = val;
        this.name = name;
    }

    public GlobalVar(RiscOperand val) {
        this.init = val;
    }

    public GlobalVar(String name) {
        this.name = name;
    }


    public RiscOperand getInit() {
        return init;
    }

    public void setInit(RiscOperand init) {
        this.init = init;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String print() {
        if (init != null)
            return  "\t.word   " + init.print() + "\n";
        else
            return  "\t.word   0\n";
    }

    public RiscOperand getVal() {
        return init;
    }

    public void setVal(RiscOperand val) {
        this.init = val;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
