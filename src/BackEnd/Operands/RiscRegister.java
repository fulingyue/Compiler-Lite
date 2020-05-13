package BackEnd.Operands;


import BackEnd.Instruction.RiscInstruction;

import java.util.HashSet;

public class RiscRegister extends RiscOperand {
    public String name;
    private HashSet<RiscInstruction> use, def;

    public void addUse(RiscInstruction instruction){
        use.add(instruction);
    }

    public void addDef(RiscInstruction instruction){
        def.add(instruction);
    }
    public RiscRegister(String name) {
        this.name = name;
    }

    public HashSet<RiscInstruction> getUse() {
        return use;
    }

    public void setUse(HashSet<RiscInstruction> use) {
        this.use = use;
    }

    public HashSet<RiscInstruction> getDef() {
        return def;
    }

    public void setDef(HashSet<RiscInstruction> def) {
        this.def = def;
    }


    @Override
    public String print() {
        return null;
    }
}
