package FrontEnd.IR.Operand;

import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;

public class ConstString extends Constant{
    private String value;
//    private int size;
    public ConstString(String name, IRType type, String string) {
        super(name,type);
        this.value = string;
    }
    @Override
    public String print() {
        String str = value;
        str = str.replace("\\", "\\5C");
        str = str.replace("\n", "\\0A");
        str = str.replace("\"", "\\22");
        str = str.replace("\0", "\\00");

        return "c\"" + str + "\"";
    }


    /////getter and setter////////

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
