package FrontEnd.IR.Operand;

import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.SymbolTable;
import FrontEnd.IR.Type.IRType;
import FrontEnd.IRVisitor;


public class VirtualReg extends Register{
//    static private SymbolTable symbolTable =  new SymbolTable();
    //save pair <String name, reg>
//    private PhysicalReg allocatedPR;


    public VirtualReg (String name, IRType type, Instruction parent)  {
        super(name,type,parent);
        this.setParent(parent);
    }

    public VirtualReg (String name, IRType type)  {
        super(name,type);
    }


    public String getOriName() {
        if (name.contains("."))
            return name.split("\\.")[0];
        else throw new RuntimeException();
    }

    @Override
    public String getName() {
//        return getOriName();
        return name;
    }

//    @Override
//    public RegVal copy() {
//
//    }

    @Override
    public String print() {
        return "%" + name;
    }

    /////setter and getter//////


}
