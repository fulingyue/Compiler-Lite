package BackEnd.Operands;

public class StackOffset extends RiscOperand {
    private String name;
    private int offset;


    public StackOffset(String name) {
        this.name = name;
        this.offset = -1;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String print() {
        return name + "(sp)";
    }
}
