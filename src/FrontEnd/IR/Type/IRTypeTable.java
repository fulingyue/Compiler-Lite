package FrontEnd.IR.Type;

import FrontEnd.AstNode.*;
import FrontEnd.IR.Module;

import java.util.ArrayList;
import java.util.HashMap;

public class IRTypeTable {
    private Module module;
    private HashMap<String, IRType> typeTable = new HashMap<>();

    public IRTypeTable(Module module,ProgramNode programNode) {
        this.module = module;

        typeTable.put("INT",new IntIRType(IntIRType.intType.i32));
        typeTable.put("BOOL",new IntIRType(IntIRType.intType.i1));
        typeTable.put("string",new PtrType(new IntIRType(IntIRType.intType.i8)));
        typeTable.put("VOID", new VoidType());
        for(ClassDefNode item: programNode.getClassDefList()) {
            if(item.getClassName().equals("string")) continue;
            ClassIRType irType = new ClassIRType(item.getClassName());

            module.getClassMap().put(item.getClassName(), irType);
        }

        for(ClassDefNode node: programNode.getClassDefList()) {
            IRType irType;
            assert typeTable.get(node.getClassName()) != null;

            irType = typeTable.get(node.getClassName());

            assert irType instanceof ClassIRType;

//            ArrayList<IRType> memberList =  new ArrayList<>();
            ArrayList<VarDefNode> memberList = new ArrayList<>();
            for(VarDefListNode item: node.getMemberList()){
                VariableTypeNode type = item.getType();
                IRType transport = transport(type);
                for(VarDefNode member: item.getVarDefNodeList()) {
//                    memberList.add(transport(member.getVarType()));

                    ((ClassIRType) irType).addMember(member.getVarName(),transport);
                }
            }

        }
    }
    public IRType get(String name) {
        return typeTable.get(name);
    }

    public IRType transport(VariableTypeNode astType) {
        if(typeTable.get(astType.getType()) != null)
            return typeTable.get(astType.getType());

        else return null;

    }
}
