package FrontEnd;
import FrontEnd.AstNode.*;
import FrontEnd.Scope.*;
import FrontEnd.ErrorChecker.SemanticException;

import java.util.LinkedList;


public class ScopeBuilder extends AstVisitor{
    LinkedList<Scope> scopeStack;//this is the builder's utility
    //exception handler

    public ScopeBuilder() {
        this.scopeStack = new LinkedList<Scope>();
    }

    public Scope BuildScopeTree(ProgramNode program) throws Exception{
        visit(program);
        if(scopeStack.size()!= 1) {
            throw new SemanticException("Scope building failed.");
        }
        return scopeStack.getFirst();
    }


    @Override
     public void visit(ProgramNode node) throws Exception {
        GlobalScope toplevelScope = new GlobalScope();
        toplevelScope.setDefNode(node);
        node.setScope(toplevelScope);
        scopeStack.addLast(toplevelScope);

        ////////for check the namespace/////////
        for  (ClassDefNode item: node.getClassDefList()){
            registerClass(item);
        }
        for (FunctionDefNode item: node.getFunctionDefList()){
            registerFunction(item);
        }
        //forward references are not supported
//        for (AstNode item:node.getChildenList()) {
//            if(item instanceof ClassDefNode)
//                registerClass((ClassDefNode)item);
//            if(item instanceof FunctionDefNode)
//                registerFunction((FunctionDefNode)item);
//        }


        /////////////for traverse and add leave scopes(recursion step)///////////
//        for (VarDefNode item: node.getVarDefList())
//            visit(item);
//        for (ClassDefNode item: node.getClassDefList())
//            visit(item);
//        for (FunctionDefNode item: node.getFunctionDefList())
//            visit(item);
        super.visit(node);

    }

    @Override
     public void visit(ClassDefNode node) throws Exception{
//        registerClass(node);
        ////init a new scope //////
        Scope scope = new Scope();
        scope.setDefNode(node);
        node.setScope(scope);
        //////enter the scope and check names///////
        pushScope(scope);
//        for (FunctionDefNode item: node.getFunctionDefList()) {
//            registerFunction(item);
//        }
//        for (FunctionDefNode item: node.getConstructionDefList()) {
//            registerFunction(item);
//        }
        /////traverse the scope sons///////
        super.visit(node);

        ////quit the scope/////
        popScope();
    }

    @Override
     public void visit(FunctionDefNode node) throws Exception {
//        registerFunction(node);
        ///////init a new scope/////////
        Scope scope = new Scope();
        scope.setDefNode(node);
        node.setScope(scope);
        ///////enter the scope and complete the name set///////
        pushScope(scope);
        for (VarDefNode item: node.getFormalParameterList()) {
            visit(item);
        }
        ///////the function block is a new scope///////
        visit(node.getBlock());
        ///////exit the scope/////
        popScope();
    }

    /////////////////the following node types all introduce a new scope///////////
    @Override
     public void visit(BlockNode node) throws Exception{
        Scope scope = new Scope();
        scope.setDefNode(node);
        pushScope(scope);
        node.setScope(scope);
        for (AstNode item: node.getChildList()) {
            if(item instanceof VarDefListNode) {
                visit((VarDefListNode) item);
            }else if(item instanceof ForStaNode){
                visit((ForStaNode)item);
            } else if(item instanceof WhileStaNode) {
                visit((WhileStaNode)item);
            }else if(item instanceof IfStaNode) {
                visit((IfStaNode)item);
            } else if(item instanceof BlockNode) {
                visit((BlockNode) item);
            } else {
                visit(item);
            }
        }
        popScope();
    }

    @Override
     public void visit(ForStaNode node) throws Exception{
        Scope scope = new Scope();
        scope.setDefNode(node);
        pushScope(scope);
        node.setScope(curScope());
        super.visit(node);
        popScope();
    }

    @Override
     public void visit(WhileStaNode node) throws Exception {
//        Scope scope = new Scope();
//        scope.setDefNode(node);
//        pushScope(scope);
        node.setScope(curScope());
        super.visit(node);
//        popScope();
    }

    @Override
     public void visit(IfStaNode node) throws Exception {
//        Scope scope = new Scope();
//        scope.setDefNode(node);
//        pushScope(scope);
        node.setScope(curScope());
        super.visit(node);
//        popScope();
    }
///////////variable register//////////////
    @Override
     public void visit(VarDefListNode node) throws Exception {
        node.setScope(curScope());
        for(VarDefNode item: node.getVarDefNodeList())
            visit((VarDefNode)item);
    }

    @Override
     public void visit(VarDefNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
        if(curScope().getClass(node.getVarName()) != null){
            throw new SemanticException( node.getLocation(), "duplicated name with class");
        }
        else {
            curScope().addVariables(node.getVarName(), node);
        }
    }

    //////////////some node need to verify its scope //////////////

    @Override
     public void visit(StatementNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(ExprStaNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(PrimaryExprNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(ReferenceNode node) throws Exception {
        node.setScope(curScope());
        if (!(curScope().contains(node.getReferenceName()))) {
            throw new SemanticException(node.getLocation(), "reference " + node.getReferenceName() +" is not contained~");
        }
        else {
            AstNode defNode = curScope().get(node.getReferenceName());
            node.setDefinitionNode(defNode);
            if (defNode instanceof ClassDefNode) {
                node.setReferenceType(ReferenceNode.ReferenceType.CLASS);
            }
            else if (defNode instanceof FunctionDefNode){
                node.setReferenceType(ReferenceNode.ReferenceType.METHOD);
            }
            else if (defNode instanceof VarDefNode){
                node.setReferenceType(ReferenceNode.ReferenceType.VARIABLE);
            }
            else {
                throw new SemanticException(node.getLocation(), "reference type cannot be used.");
            }
        }
    }

    @Override
     public void visit(VariableTypeNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(UnaryOpNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(BinaryOpNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }


    @Override
     public void visit(IndexAccessNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(MemberAccessNode node) throws Exception {
        node.setScope(curScope());
        visit(node.getCaller());
        try{
            visit(node.getMember());
        } catch (SemanticException exception) {}
    }


    @Override
     public void visit(FunctionCallNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(NewExprNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }


    @Override
     public void visit(PrimitiveTypeNode node) throws Exception{
        node.setScope(curScope());
        super.visit(node);
    }
    @Override
     public void visit(ClassTypeNode node) throws Exception {
        node.setScope(curScope());
        ClassDefNode classDef = curScope().getClass(node.getReferenceClassName());
        if(classDef == null)
            throw new SemanticException(node.getLocation(),"This class does not exist.");
        node.setReferenceClass(classDef);
    }

    @Override
     public void visit(ArrayTypeNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(NullNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(BoolNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(IntNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(StringNode node) throws Exception {
        node.setScope(curScope());
        super.visit(node);
    }

    @Override
     public void visit(ThisExprNode node) throws Exception{
        node.setScope(curScope());
        super.visit(node);
    }




    ///////////// pivate utility  functions/////////////
    private Scope curScope() {
        return scopeStack.getLast();
    }

    private void pushScope(Scope scope) throws SemanticException {
        scope.setParent(curScope());
        scope.getParent().getChildrenList().add(scope);
        scopeStack.addLast(scope);
    }

    private void popScope() {
        scopeStack.removeLast();
    }


    private void registerClass(ClassDefNode node) throws SemanticException {
        Scope topScope = curScope();
        if( ! (topScope instanceof GlobalScope))
            throw new SemanticException(node.getLocation(), "class declaration cannot be put here.");
        if (topScope.contains(node.getClassName()))
            throw new SemanticException(node.getLocation(), "duplicated class name");
        else {
            node.setScope(topScope);
            ((GlobalScope) topScope).addClass(node.getClassName(), node);
        }

    }


    private void registerFunction(FunctionDefNode node) throws SemanticException{
        if (curScope().contains(node.getMethodName())) {
            throw new SemanticException(node.getLocation(),"Duplicated FunctionDeclaration");
        }
        else {
            node.setScope(curScope());

            if(curScope() instanceof GlobalScope){

            }
            else if (curScope().getDefNode() instanceof ClassDefNode){
                node.setMethodName( ((ClassDefNode)curScope().getDefNode()).getClassName() + '_' + node.getMethodName());
            }
            else
            {
                throw new SemanticException("function declaration cannot be put here.");
            }
            curScope().addFunction(node.getMethodName(), node);
        }
    }





    ////////* "resolve" functions described VariableTypeNode as types in scope *///////
//    private Type resolveType(VariableTypeNode typeNode) throws Exception{
//        if(typeNode instanceof PrimitiveTypeNode){
//            return new Type(typeNode.getType());
//        }
//        else if(typeNode instanceof ArrayTypeNode) {
//            return new ArrayTypeNode(resolveType(((ArrayTypeNode)typeNode).innerTypeNode));
//        }
//        else if (typeNode instanceof ClassTypeNode) {
//            return resolveClassType((ClassTypeNode)typeNode);
//        }
//        else {
//            return null;
//        }
//    }
//
//    private Type resolveClassType(ClassTypeNode classTypeNode) throws Exception {
//        if (!(curScope() instanceof GlobalScope)) {
//            throw new SemanticException(classTypeNode.getLocation(), "class declaration cannot be here");
//        }
//        else{
//            ClassEntity classEntity = ((GlobalScope)curScope()).getClassEntity(classTypeNode.getType());
//        if (classEntity == null)
//            return null;
//        else {
//            ClassTypeNode classType = new ClassTypeNode(classTypeNode.getType());
//            classType.classEntity = classEntity;
//            return classType;
//        }
//    }
//    }



}
