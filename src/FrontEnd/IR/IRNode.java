package FrontEnd.IR;

import FrontEnd.IR.Operand.Operand;
import FrontEnd.IRPrinter;
import FrontEnd.IRVisitor;

import java.util.LinkedList;

public abstract class IRNode {
    protected String name;
    protected LinkedList<IRNode> Usages = new LinkedList<>();

    public IRNode() {
        name = null;
    }
    public IRNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<IRNode> getUsages() {
        return Usages;
    }

    public void setUsages(LinkedList<IRNode> usages) {
        Usages = usages;
    }

    public  void accept(IRVisitor irVisitor){};
}
