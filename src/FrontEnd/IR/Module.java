package FrontEnd.IR;

import FrontEnd.AstNode.ProgramNode;
import FrontEnd.IR.Operand.StaticString;
import FrontEnd.IR.Operand.StaticVar;
import FrontEnd.IR.Type.ClassIRType;
import FrontEnd.IR.Type.IRTypeTable;
import FrontEnd.IRVisitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Module {
    private Map<String, IRFunction> functionMap = new LinkedHashMap<>();
    private Map<String, IRFunction> externalFuncMap =  new LinkedHashMap<>();
    private Map<String, StaticVar>  staticVariableMap = new LinkedHashMap<>();
    private Map<String, StaticString> staticStrings = new LinkedHashMap<>();
    private Map<String, ClassIRType>  classMap = new LinkedHashMap<>();
    private SymbolTable symbolTable  =  new SymbolTable();
    private String name;
    IRTypeTable typeTable;

    public Module(ProgramNode programNode) {
        typeTable = new IRTypeTable(this,programNode);
        initBuildInFunc();

    }

    public IRFunction getFunction(String name)  {
        if(functionMap.get(name)  != null)
            return functionMap.get(name);

        return externalFuncMap.get(name);
    }


    public void  addFunction(IRFunction function) {
        functionMap.put(function.getName(), function);
    }



    public void addStaticVar(StaticVar var) {
        staticVariableMap.put(var.getName(),var);
    }

    public void addStaticString(StaticString staticString) {
        staticStrings.put(staticString.getName(), staticString);
    }

    //////////private util funcs//////////
    private void initBuildInFunc() {
//        IRFunction print =   new IRFunction(IRFunction.FunctionType.Library, "print",  true);
//        addFunction(print);
//        IRFunction println = new IRFunction(IRFunction.FunctionType.Library, "println", true);
//        addFunction(println);
//        IRFunction getString = new IRFunction(IRFunction.FunctionType.Library, "getString", true);
//        addFunction(getString);
//        IRFunction getInt = new IRFunction(IRFunction.FunctionType.Library, "getInt", true);

//        IRFunction toString = new Function(FuncType.Library, "toString", true, true);
//        IRFunction string_length = new Function(FuncType.Library, "string_length", true, true);
//        IRFunction string_substring = new Function(FuncType.Library, "string_substring", true, true);
//        IRFunction string_parseInt = new Function(FuncType.Library, "string_parseInt", true, true);
//        IRFunction string_ord = new Function(FuncType.Library, "string_ord", true, true);
//        IRFunction string_concat = new Function(FuncType.Library, "string_concat", true, true);
//        IRFunction string_compare = new Function(FuncType.Library, "string_compare", true, true);
//        IRFunction malloc = new Function(FuncType.External, "malloc", false, true);
//        IRFunction global_init = new Function(FuncType.UserDefined, "global_init", true, true);
    }








    public void accept(IRVisitor vistor) {
        vistor.visit(this);
    }
    /////////getter and setter///////

    public Map<String, IRFunction> getFunctionMap() {
        return functionMap;
    }

    public void setFunctionMap(Map<String, IRFunction> functionMap) {
        this.functionMap = functionMap;
    }

    public Map<String, IRFunction> getExternalFuncMap() {
        return externalFuncMap;
    }

    public void setExternalFuncMap(Map<String, IRFunction> externalFuncMap) {
        this.externalFuncMap = externalFuncMap;
    }

    public Map<String, StaticVar> getStaticVariableMap() {
        return staticVariableMap;
    }

    public void setStaticVariableMap(Map<String, StaticVar> staticVariableMap) {
        this.staticVariableMap = staticVariableMap;
    }

    public void setStaticStrings(Map<String, StaticString> staticStrings) {
        this.staticStrings = staticStrings;
    }

    public Map<String, ClassIRType> getClassMap() {
        return classMap;
    }

    public void setClassMap(Map<String, ClassIRType> classMap) {
        this.classMap = classMap;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, StaticString> getStaticStrings() {
        return staticStrings;
    }

    public IRTypeTable getTypeTable() {
        return typeTable;
    }

    public void setTypeTable(IRTypeTable typeTable) {
        this.typeTable = typeTable;
    }
}
