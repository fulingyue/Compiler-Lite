package BackEnd.Operand;

public class Immidiate extends RiscOperand {
    private int value;

    public Immidiate(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    @Override
    public String print() {
        return String.valueOf(value);
    }
}
