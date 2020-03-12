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
        stringClass.setScope(this);
        stringClass.setClassName("string");
        stringClass.addFunction(stringLengthFun());
        stringClass.addFunction(stringOrdFun());
        stringClass.addFunction(stringParseIntFun());
        stringClass.addFunction(stringSubstringFun());
        this.addClass("string", stringClass);
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
        addFunction("array_size",arraySizeFun());
        nameset.add("array_size");
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
        fun.setMethodName("string_length");
        fun.setReturnType(new PrimitiveTypeNode("int"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        fun.setFormalParameterList(paras);
        return fun;
    }
    private FunctionDefNode stringSubstringFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("string_substring");
        fun.setReturnType(new ClassTypeNode("string"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        paras.add(new VarDefNode(new PrimitiveTypeNode("int"),"left"));
        paras.add(new VarDefNode(new PrimitiveTypeNode("int"),"right"));
        fun.setFormalParameterList(paras);
        return fun;
    }

    private FunctionDefNode stringParseIntFun(){
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("string_parseInt");
        fun.setReturnType(new PrimitiveTypeNode("int"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        fun.setFormalParameterList(paras);
        return fun;
    }

    private FunctionDefNode stringOrdFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("string_ord");
        fun.setReturnType(new PrimitiveTypeNode("int"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        paras.add(new VarDefNode(new PrimitiveTypeNode("int"), "pos"));
        fun.setFormalParameterList(paras);
        return fun;

    }

    private FunctionDefNode arraySizeFun() {
        FunctionDefNode fun = new FunctionDefNode();
        fun.setMethodName("array_size");
        fun.setReturnType(new PrimitiveTypeNode("int"));
        LinkedList<VarDefNode> paras= new LinkedList<>();
        fun.setFormalParameterList(paras);
        return fun;
    }

}





















