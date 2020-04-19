package FrontEnd.IR.Type;


import FrontEnd.AstNode.VarDefNode;
import FrontEnd.IR.Operand.Operand;
import util.Aligner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ClassIRType extends IRType {
    private String  name;
    private ArrayList<IRType> memberList;
    private ArrayList<String> nameList;

    public ClassIRType(String name) {
        this.name = name;
        memberList = new ArrayList<>();
        nameList =  new ArrayList<>();

    }

    public ClassIRType(String  name, ArrayList<IRType> memberList) {
        this.name = name;
        this.memberList = memberList;
        this.byteWidth = 0;
        for(IRType item: memberList) {
            this.byteWidth += item.getByteWidth();
        }
    }

    @Override
    public int getByteWidth() {
        int max = 0;
        for(IRType item: memberList) {
            int size = item.getByteWidth();
            this.byteWidth = Aligner.align(byteWidth,size) + size;
            max = Math.max(max,size);
        }
        byteWidth  = Aligner.align(byteWidth,max);
        return byteWidth;
    }

    public void addMember(String name, IRType type) {
        memberList.add(type);
        nameList.add(name);
    }

    public int getByteIndex(String name)  {
        int index=  0;
        for(int i = 0;i < memberList.size(); ++i)  {
            if(nameList.get(i).equals(name)){break;}
            else {
                index += memberList.get(i).getByteWidth();
            }
        }
        return index;
    }

    public int getIndex(String name) {
        int i = 0;
        for(;i < memberList.size(); ++i) {
            if(nameList.get(i).equals(name))
                return i;
        }
        return -1;
    }

    public IRType getPtrType() {
        return new PtrType(this);
    }
    @Override
    public String print() {
        return ("%" + name);

    }

    public String printType() {
        StringBuilder stringBuilder = new StringBuilder(this.print());
        stringBuilder.append(" = type { ");
        if(memberList.size() == 0)
            stringBuilder.append(",  ");
        for (int i= 0; i < memberList.size(); i++) {
            stringBuilder.append(memberList.get(i).print()).append(", ");
        }
        stringBuilder.delete(stringBuilder.length()-2,stringBuilder.length());
        stringBuilder.append(" }");
        return stringBuilder.toString();
    }

    @Override
    public Operand getDefaultValue() {
        throw new RuntimeException();
    }
    ////////setter and getter////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<IRType> getMemberList() {
        return memberList;
    }

    public void setMemberList(ArrayList<IRType> memberList) {
        this.memberList = memberList;
    }
}
