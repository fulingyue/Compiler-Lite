package FrontEnd.Scope;

import FrontEnd.AstNode.*;
import FrontEnd.ErrorChecker.SemanticException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class GlobalScope extends Scope {
    private HashMap<String, ClassDefNode> classes;


    public GlobalScope() throws SemanticException{
        classes = new HashMap<String, ClassDefNode>();
        initBuildinFunction();
//        initStrBuildinFunction();
        initStrClass();
        initArrayBuildinFunction();

        parent = null;
    }
    @Override
    public ClassDefNode getClass(String name) {
        if(!classes.containsKey(name)) return null;
        return classes.get(name);
    }


    public HashMap<String, ClassDefNode> getClasses() {
        return classes;
    }
    public void addClass(String name, ClassDefNode classEntity) throws SemanticException{
        if (nameset.contains(name))
            throw new SemanticException(classEntity.getLocation(), "Duplicated class definition");
        else {
            classes.put(name, classEntity);
            nameset.add(name);
        }
    }

    private void initStrClass() {
        ClassDefNode stringClass = new ClassDefNode();
        Scope newScope = new Scope();
        stringClass.setClassName("string");
        stringClass.addFunction(stringLengthFun());
        newScope.addFunction("length",stringLengthFun());
        stringClass.addFunction(stringOrdFun());
        newScope.addFunction("ord",stringOrdFun());
        stringClass.addFunction(stringParseIntFun());
        newScope.addFunction("parseInt",stringParseIntFun());
        stringClass.addFunction(stringSubstringFun());
        newScope.addFunction("substring",stringSubstringFun());
        this.addClass("string", stringClass);
        stringClass.setScope(newScope);
        newScope.setParent(this);
        newScope.setDefNode(stringClass);
    }


    private void initBuildinFunction(){
        addFunction("print", globalPrintFun());
        nameset.add("print");
        addFunction("println",globalPrintlnFun());
        nameset.add("println");
        addFunction("printInt",globalPrintIntFun());
        nameset.add("printInt");
        addFunction("printlnInt",globalPrintlnIntFun());
        nameset.add("printlnInt");
        addFunction("getString",globalGetStringFun());
        nameset.add("getString");
        addFunction("getInt",globalGetIntFun());
        nameset.add("getInt");
        addFunction("toString", globalToStringFun());
        nameset.add("toString");

    }
//    List void initStrBuildinFunction(){
//        addFunction("string_length", stringLengthFun());
//        nameset.add("string_length");
//        addFunction("string_substring",stringSubstringFun());
//        nameset.add("string_substring");
//        addFunction("string_parseInt",stringParseIntFun());
//        nameset.add("string_parseInt");
//        addFunction("string_ord",stringOrdFun());
//        nameset.add("string_ord");
//    }

    private void initArrayBuildinFunction(){
        ClassDefNode arrayClass = new ClassDefNode();
        arrayClass.setClassName("_array");
        Scope newScope = new Scope();
        arrayClass.setScope(newScope);
        newScope.setParent(this);
        newScope.addFunction("_array_size",arraySizeFun());
        arrayClass.addFunction(arraySizeFun());
        this.addClass("_array",arrayClass);
        newScope.setDefNode(arrayClass);
    }

    private FunctionDefNode globalPrintFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("print");
        fun.setReturnType(new PrimitiveTypeNode("void"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        paras.add(new VarDefNode(new ClassTypeNode("string"), "str"));
        fun.setFormalParameterList(paras);
        return fun;
    }

    private FunctionDefNode globalPrintlnFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("println");
        fun.setReturnType(new PrimitiveTypeNode("void"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        paras.add(new VarDefNode(new ClassTypeNode("string"), "str"));
        fun.setFormalParameterList(paras);
        return fun;
    }

    private  FunctionDefNode globalPrintIntFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("printInt");
        fun.setReturnType(new PrimitiveTypeNode("void"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        paras.add(new VarDefNode(new PrimitiveTypeNode("int"), "n"));
        fun.setFormalParameterList(paras);
        return fun;
    }


    private FunctionDefNode globalPrintlnIntFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("printlnInt");
        fun.setReturnType(new PrimitiveTypeNode("void"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        paras.add(new VarDefNode(new PrimitiveTypeNode("int"), "n"));
        fun.setFormalParameterList(paras);
        return fun;
    }

    private FunctionDefNode globalGetStringFun(){
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("getString");
        fun.setReturnType(new ClassTypeNode("string"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        fun.setFormalParameterList(paras);
        return fun;
    }

    private FunctionDefNode globalGetIntFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("getInt");
        fun.setReturnType(new PrimitiveTypeNode("int"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        fun.setFormalParameterList(paras);
        return fun;
    }
    private FunctionDefNode globalToStringFun() {

        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("toString");
        fun.setReturnType(new ClassTypeNode("string"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        paras.add(new VarDefNode(new PrimitiveTypeNode("int"), "n"));
        fun.setFormalParameterList(paras);
        return fun;
    }

    private FunctionDefNode stringLengthFun(){
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("length");
        fun.setReturnType(new PrimitiveTypeNode("int"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        fun.setFormalParameterList(paras);
        return fun;
    }
    private FunctionDefNode stringSubstringFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("substring");
        fun.setReturnType(new ClassTypeNode("string"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        paras.add(new VarDefNode(new PrimitiveTypeNode("int"),"left"));
        paras.add(new VarDefNode(new PrimitiveTypeNode("int"),"right"));
        fun.setFormalParameterList(paras);
        return fun;
    }

    private FunctionDefNode stringParseIntFun(){
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("parseInt");
        fun.setReturnType(new PrimitiveTypeNode("int"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        fun.setFormalParameterList(paras);
        return fun;
    }

    private FunctionDefNode stringOrdFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("ord");
        fun.setReturnType(new PrimitiveTypeNode("int"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        paras.add(new VarDefNode(new PrimitiveTypeNode("int"), "pos"));
        fun.setFormalParameterList(paras);
        return fun;

    }

    private FunctionDefNode arraySizeFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("_array_size");
        fun.setReturnType(new PrimitiveTypeNode("int"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        fun.setFormalParameterList(paras);
        return fun;
    }

}





















