package FrontEnd.IR;

import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IRVisitor;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class IRNode {
    protected String name;
    protected ArrayList<IRNode> Users = new ArrayList<>();
    protected HashSet<Instruction> defs = new HashSet<>();

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

    public ArrayList<IRNode> getUsers() {
        return Users;
    }

    public void setUsers(ArrayList<IRNode> users) {
        Users = users;
    }

    public void addUser(IRNode user) {
        Users.add(user);
    }

    public void accept(IRVisitor irVisitor){};

    public void addDef(Instruction node) {
        defs.add(node);
    }

    public void removeDef(IRNode node) {
        defs.remove(node);
    }
    public void removeUser(IRNode node) {
        Users.remove(node);
    }

    public void replaceUser(IRNode newUser){
        assert (this instanceof Operand) ||
                (this instanceof BasicBlock) ||
                (this instanceof IRFunction);
        for(int i = 0;i < Users.size(); ++i){
            IRNode node = Users.get(i);
            assert node instanceof Instruction;
            ((Instruction) node).replaceUse(this,newUser);
        }

        for(IRNode item:defs){
            ((Instruction)item).replaceUse(this,newUser);
        }
    }

    public HashSet<Instruction> getDefs() {
        return defs;
    }

    public void setDefs(HashSet<Instruction> defs) {
        this.defs = defs;
    }
}
