package FrontEnd.IR.Operand;

import FrontEnd.IR.Type.IRType;

public abstract class Constant extends Operand {
    public Constant(String name,IRType type)  {
        super(name,type);
    }
    public Constant() {
        super();
    }
}
