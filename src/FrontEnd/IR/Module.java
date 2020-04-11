package FrontEnd.IR;

import FrontEnd.AstNode.ProgramNode;
import FrontEnd.IR.Operand.ConstString;
import FrontEnd.IR.Operand.Operand;
import FrontEnd.IR.Operand.Parameter;
import FrontEnd.IR.Operand.StaticVar;
import FrontEnd.IR.Type.*;
import FrontEnd.IRVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Module {
    private Map<String, IRFunction> functionMap = new LinkedHashMap<>();
    private Map<String, IRFunction> externalFuncMap =  new LinkedHashMap<>();
    private Map<String, StaticVar>  staticVariableMap = new LinkedHashMap<>();
    private Map<String, StaticVar> staticStrings = new LinkedHashMap<>();
    private Map<String, ClassIRType>  classMap = new LinkedHashMap<>();
    private SymbolTable symbolTable  =  new SymbolTable();
    private String name;
    IRTypeTable typeTable;

    public Module(ProgramNode programNode) {
        typeTable = new IRTypeTable(this,programNode);

    }

    public IRFunction getFunction(String name)  {
        if(functionMap.get(name)  != null)
            return functionMap.get(name);

        return externalFuncMap.get(name);
    }


    public void  addFunction(IRFunction function) {
        functionMap.put(function.getName(), function);
    }

    public void addFunctionExternal(IRFunction function) {
        externalFuncMap.put(function.getName(),function);
    }

    public void addStaticVar(StaticVar var) {
        staticVariableMap.put(var.getName(),var);
    }


    //////////private util funcs//////////
    public void initBuildin() {
        IRType retType;
        ArrayList<Parameter> parameters;


        retType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction print = new IRFunction("print",this,parameters,retType,true);
        addFunctionExternal(print);

        retType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction println = new IRFunction("println",this,parameters,retType,true);
        addFunctionExternal(println);

        retType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter("i",new IntIRType(IntIRType.intType.i32)));
        IRFunction printInt = new IRFunction("printInt",this,parameters,retType,true);
        addFunctionExternal(printInt);

        retType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter("i",new IntIRType(IntIRType.intType.i32)));
        IRFunction printlnInt = new IRFunction("printlnInt",this,parameters,retType,true);
        addFunctionExternal(printlnInt);

        retType = new PtrType(new IntIRType(IntIRType.intType.i8));
        parameters = new ArrayList<>();
        IRFunction getString = new IRFunction("getString",this,parameters,retType,true);
        addFunctionExternal(getString);

        retType  = new IntIRType(IntIRType.intType.i32);
        parameters = new ArrayList<>();
        IRFunction getInt = new IRFunction("getInt",this, parameters,retType,true);
        addFunctionExternal(getInt);

        retType = new PtrType(new IntIRType(IntIRType.intType.i8));
        parameters = new ArrayList<>();
        parameters.add(new Parameter("i",new IntIRType(IntIRType.intType.i32)));
        IRFunction toString = new IRFunction("toString",this,parameters,retType,true);
        addFunctionExternal(toString);

        retType = new PtrType(new IntIRType(IntIRType.intType.i8));
        parameters = new ArrayList<>();
        parameters.add(new Parameter("size",new IntIRType(IntIRType.intType.i32)));
        IRFunction malloc = new IRFunction("malloc",this,parameters,retType,true);
        addFunctionExternal(malloc);

        retType = new PtrType(new IntIRType(IntIRType.intType.i8));
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str1",new PtrType(new IntIRType(IntIRType.intType.i8))));
        parameters.add(new Parameter("str2",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction stringAdd = new IRFunction("__string_link",this,parameters,retType,true);
        addFunctionExternal(stringAdd);

        retType = new IntIRType(IntIRType.intType.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str1",new PtrType(new IntIRType(IntIRType.intType.i8))));
        parameters.add(new Parameter("str2",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction string_ge = new IRFunction("__string_ge",this,parameters,retType,true);
        addFunctionExternal(string_ge);

        retType = new IntIRType(IntIRType.intType.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str1",new PtrType(new IntIRType(IntIRType.intType.i8))));
        parameters.add(new Parameter("str2",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction string_eq = new IRFunction("__string_equal",this,parameters,retType,true);
        addFunctionExternal(string_eq);

        retType = new IntIRType(IntIRType.intType.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str1",new PtrType(new IntIRType(IntIRType.intType.i8))));
        parameters.add(new Parameter("str2",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction string_noteq = new IRFunction("__string_notequal",this,parameters,retType,true);
        addFunctionExternal(string_noteq);

        retType = new IntIRType(IntIRType.intType.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str1",new PtrType(new IntIRType(IntIRType.intType.i8))));
        parameters.add(new Parameter("str2",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction string_le = new IRFunction("__string_le",this,parameters,retType,true);
        addFunctionExternal(string_le);

        retType = new IntIRType(IntIRType.intType.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str1",new PtrType(new IntIRType(IntIRType.intType.i8))));
        parameters.add(new Parameter("str2",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction string_lt = new IRFunction("__string_lt",this,parameters,retType,true);
        addFunctionExternal(string_lt);

        retType = new IntIRType(IntIRType.intType.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str1",new PtrType(new IntIRType(IntIRType.intType.i8))));
        parameters.add(new Parameter("str2",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction string_gt = new IRFunction("__string_gt",this,parameters,retType,true);
        addFunctionExternal(string_gt);

        retType = retType = new IntIRType(IntIRType.intType.i32);
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction string_length = new IRFunction("__string_length",this,parameters,retType,true);
        addFunctionExternal(string_length);

        retType = retType = new IntIRType(IntIRType.intType.i32);
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str",new PtrType(new IntIRType(IntIRType.intType.i8))));
        IRFunction string_parseInt = new IRFunction("__string_parseInt",this,parameters,retType,true);
        addFunctionExternal(string_parseInt);

        retType = new PtrType(new IntIRType(IntIRType.intType.i8));
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str",new PtrType(new IntIRType(IntIRType.intType.i8))));
        parameters.add(new Parameter("lft", new IntIRType(IntIRType.intType.i32)));
        parameters.add(new Parameter("right", new IntIRType(IntIRType.intType.i32)));
        IRFunction string_substring = new IRFunction("__string_substring",this,parameters,retType,true);
        addFunctionExternal(string_substring);

        retType = new IntIRType(IntIRType.intType.i32);
        parameters = new ArrayList<>();
        parameters.add(new Parameter("str",new PtrType(new IntIRType(IntIRType.intType.i8))));
        parameters.add(new Parameter("pos", new IntIRType(IntIRType.intType.i32)));
        IRFunction string_ord = new IRFunction("__string_ord",this,parameters,retType,true);
        addFunctionExternal(string_ord);

        //arraysize can be solved by instructio
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

    public void setStaticStrings(Map<String, StaticVar> staticStrings) {
        this.staticStrings = staticStrings;
    }

    public IRTypeTable getTypeTable() {
        return typeTable;
    }

    public void setTypeTable(IRTypeTable typeTable) {
        this.typeTable = typeTable;
    }

    public StaticVar addString(String string) {

        string = string + "\0";
        if(staticStrings.containsKey(string))
            return staticStrings.get(string);
        else {
            int cnt = staticStrings.size();
            String name = "_str." + cnt;
            IRType type = new ArrayIRType(string.length(),new IntIRType(IntIRType.intType.i8));
            StaticVar var = new StaticVar(name,type, new ConstString(name,type,string));
            staticStrings.put(string,var);
            staticVariableMap.put(name,var);
            return var;
        }
    }
}
