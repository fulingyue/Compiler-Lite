package FrontEnd.Scope;

import FrontEnd.AstNode.*;
import FrontEnd.ErrorChecker.SemanticException;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.LinkedList;

import util.Defines;

public class Scope {
    private AstNode defNode;
    private HashMap<String, VarDefNode>  variables;
    private HashMap<String, FunctionDefNode> functions;
    protected Scope parent;
    private HashMap<String,Integer> offsets;//offsets and name set
    private LinkedList<Scope> childrenList;

    protected HashSet<String> nameset;
    private Integer curOffset;


    public Scope() {
        defNode = null;
        this.variables = new LinkedHashMap<String, VarDefNode>();
        this.functions = new LinkedHashMap<String,FunctionDefNode>();
        this.parent = null;
        this.offsets = new  HashMap<>();
        this.childrenList = new LinkedList<>();
        nameset = new HashSet<>();
        curOffset = 0;
    }
    public Scope(Scope parent) {
        defNode = null;
        this.variables = new LinkedHashMap<String, VarDefNode>();
        this.functions = new LinkedHashMap<String, FunctionDefNode>();
        this.parent = parent;
        this.offsets = new  HashMap<>();
        curOffset = 0;
        nameset = new HashSet<>();
    }


    public void addVariables(String name, VarDefNode variableDef) throws SemanticException{
        if(nameset.contains(name))
            throw new SemanticException("duplicated variable name");
        else {
            variables.put(name, variableDef);
            if (name != "this") {
                offsets.put(name, curOffset);
                nameset.add(name);
                curOffset += Defines.REG_SIZE;
            }
        }
    }

    public ClassDefNode getClass(String name) {
        return this.parent.getClass(name);
    }
    public VarDefNode getVariableInScope(String name) {
        return variables.get(name);
    }

    public VarDefNode getVariable(String name) {
        VarDefNode ret = variables.get(name);
        if (ret != null) {
            return ret;
        } else {
            if (parent != null) {
                return parent.getVariable(name);
            }
            else return null;
        }
    }

    public void addFunction(String name, FunctionDefNode functionEntity)  throws SemanticException{
        if(nameset.contains(name))
            throw new SemanticException(functionEntity.getLocation(), "duplicated function name");
        else {
            functions.put(name, functionEntity);
            nameset.add(name);
        }
    }

    public FunctionDefNode getFunctionInScope(String name) {
        return functions.get(name);
    }

    public FunctionDefNode getFunction(String name) {
        FunctionDefNode ret = functions.get(name);
        if (ret != null) return ret;
        else{
            if (parent != null) return parent.getFunction(name);
            else return null;
        }

    }


    public int getOffset(String name) {
        return offsets.get(name);
    }

    //just check the nodes' existence
    public boolean contains(String name) {
        if (nameset.contains(name)) return true;
        else if(parent != null){
            return parent.contains(name);
        }
        else return false;
    }

    //get the specific def node
    public AstNode get(String name) throws SemanticException{
        FunctionDefNode functionEntity = functions.get(name);
        VarDefNode variableEntity = variables.get(name);
        if(functionEntity != null) return functionEntity;
        if(variableEntity != null) return variableEntity;
        if(parent!= null) return parent.get(name);
        throw new SemanticException("no such entity named \"" + name + "\"" );
    }


    public void printScope(int tab) {
        if(defNode instanceof FunctionDefNode) {
            util.print_tool.printDashAndStr(tab, ((FunctionDefNode) defNode).getMethodName() + "Scope");
        }
        else if(defNode instanceof ClassDefNode) {
            util.print_tool.printDashAndStr(tab,((ClassDefNode) defNode).getClassName() + "Scope");
        }
        else if(defNode instanceof BlockNode) {
            util.print_tool.printDashAndStr(tab,"blockScope");
        }
        else if (defNode instanceof ForStaNode) {
            util.print_tool.printDashAndStr(tab,"forScope");
        }
        else if(defNode instanceof WhileStaNode) {
            util.print_tool.printDashAndStr(tab,"whileScope");
        }
        else if(defNode instanceof IfStaNode) {
            util.print_tool.printDashAndStr(tab,"ifScope");
        }
        else {
            util.print_tool.printDashAndStr(tab,"unknownScope");
        }

        for (Scope item: childrenList) {
            item.printScope(tab + 1);
        }
    }

    public AstNode getDefNode() {
        return defNode;
    }

    public void setDefNode(AstNode defNode) {
        this.defNode = defNode;
    }

    public HashMap<String, VarDefNode> getVariables() {
        return variables;
    }

    public void setVariables(HashMap<String, VarDefNode> variables) {
        this.variables = variables;
    }

    public HashMap<String, FunctionDefNode> getFunctions() {
        return functions;
    }

    public void setFunctions(HashMap<String, FunctionDefNode> functions) {
        this.functions = functions;
    }

    public Scope getParent() {
        return parent;
    }

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    public HashMap<String, Integer> getOffsets() {
        return offsets;
    }

    public void setOffsets(HashMap<String, Integer> offsets) {
        this.offsets = offsets;
    }

    public LinkedList<Scope> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(LinkedList<Scope> childrenList) {
        this.childrenList = childrenList;
    }

    public HashSet<String> getNameset() {
        return nameset;
    }

    public void setNameset(HashSet<String> nameset) {
        this.nameset = nameset;
    }

    public Integer getCurOffset() {
        return curOffset;
    }

    public void setCurOffset(Integer curOffset) {
        this.curOffset = curOffset;
    }
}



























