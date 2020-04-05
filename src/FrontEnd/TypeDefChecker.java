package FrontEnd;

import FrontEnd.AstNode.*;
import FrontEnd.ErrorChecker.SemanticException;
import FrontEnd.Scope.GlobalScope;
import FrontEnd.Scope.Scope;
import java.util.*;

import static FrontEnd.AstNode.PrimitiveTypeNode.PrimitiveTypeKeyword.*;

//////check void type and cycling definition/////////


public class TypeDefChecker extends AstVisitor {
    private HashMap<String, Integer> indgreeMap;
    private HashMap<String, LinkedList<String>> edgeMap;
    private Scope topLevelScope;


    public TypeDefChecker() {
        indgreeMap = new HashMap<>();
        edgeMap = new HashMap<String, LinkedList<String>>();
    }

    public void checkTypeDef(ProgramNode node) throws Exception {
        topLevelScope = node.getScope();
        if (!(topLevelScope instanceof GlobalScope))
            throw new SemanticException(node.getLocation(), "scope must be the global scope");
        visit(node);
//        check();
    }

    @Override
    public void visit(ProgramNode node) throws Exception {
        FunctionDefNode main = topLevelScope.getFunctions().get("main");
        ///////////main function checker///////////
        if (main == null)
            throw new SemanticException("no main function~~");
        else if (!main.getReturnType().equalTo(new PrimitiveTypeNode("int")))
            throw new SemanticException("return type must be int");
        else if (!main.getFormalParameterList().isEmpty())
            throw new SemanticException("main function cannot have parameters");

        for (ClassDefNode item : node.getClassDefList()) {
            indgreeMap.put(item.getClassName(), 0);
            edgeMap.put(item.getClassName(), new LinkedList<String>());
        }
        super.visit(node);
    }

    @Override
    public void visit(ClassDefNode node) throws Exception {
        super.visit(node);
        for (VarDefListNode item : node.getMemberList()) {
            VariableTypeNode varType = item.getType();
            while (varType instanceof ArrayTypeNode) {
                varType = ((ArrayTypeNode) varType).getInnerTypeNode();
            }
            if (varType instanceof ClassTypeNode) {
                String name = ((ClassTypeNode) varType).getReferenceClassName();
//                indgreeMap.put(name, indgreeMap.get(name) + 1);
//                edgeMap.get(node.getClassName()).add(name);
            }
        }
    }


    @Override
    public void visit(FunctionDefNode node) throws Exception {
        if (isVoidArray(node.getReturnType()))
            throw new SemanticException(node.getLocation(), "return an array of void");
        else {
            super.visit(node);
            for (VarDefNode item : node.getFormalParameterList()) {
                if (item.getVarType().equalPrimitive(PrimitiveTypeNode.PrimitiveTypeKeyword.VOID))
                    throw new SemanticException(item.getLocation(), "parameter cannot be of void type");
            }
            if (!node.getReturnType().equalPrimitive(VOID) && !node.hasReturnSta()
                    && !node.getMethodName().equals("main"))
                throw new SemanticException(node.getLocation(),
                        "function " + node.getMethodName() + " does not return???");

        }
        super.visit(node);
    }

    @Override
    public void visit(VarDefNode node) throws Exception {
        if (isVoid(node.getVarType()) || isVoidArray(node.getVarType()))
            throw new SemanticException(node.getLocation(), "define a void type variable");
        super.visit(node);
        if (node.getInitVal() == null) return;
        VariableTypeNode variableType = node.getVarType();
        VariableTypeNode initValType = node.getInitVal().getExprType();
        if (variableType.getType().equals("string") && initValType.getType().equals("null"))
            throw new SemanticException(node.getLocation(), "string cannot be initialized to null");
        else if (variableType instanceof ArrayTypeNode && initValType instanceof ArrayTypeNode) {
            if (!((ArrayTypeNode) variableType).equalType((ArrayTypeNode) initValType))
                throw new SemanticException(node.getLocation(), "array type is not matched");
        } else if (!variableType.equalTo(initValType)) {
            throw new SemanticException(node.getLocation(), "initial value is not matching to the assignment.");
        }
    }

    @Override
    public void visit(NewExprNode node) throws Exception {
        if (isVoidArray(node.getVariableType()) || isVoid(node.getVariableType()))
            throw new SemanticException(node.getLocation(), "new a void type variable");
        super.visit(node);
        node.setExprType(node.getVariableType());
    }


    @Override
    public void visit(ReferenceNode node) {
        if (node.getReferenceType() == ReferenceNode.ReferenceType.VARIABLE) {
            node.setExprType(((VarDefNode) node.getDefinitionNode()).getVarType());
            node.setLeft_val(true);
        }
        if (node.getReferenceType() == ReferenceNode.ReferenceType.CLASS) {
            node.setExprType(new ClassTypeNode(node.getReferenceName()));
        }
        if (node.getReferenceType() == ReferenceNode.ReferenceType.METHOD) {
            node.setExprType(((FunctionDefNode) node.getDefinitionNode()).getReturnType());
        }
    }

    @Override
    public void visit(ThisExprNode node) {
        AstNode tmp = node;
        while (!(tmp instanceof ClassDefNode)) {
            if (tmp.getParent() == null)
                throw new SemanticException(node.getLocation(), "this reference is wrong");
            tmp = tmp.getParent();
        }
        node.setExprType(new ClassTypeNode(((ClassDefNode) tmp).getClassName()));

    }

    @Override
    public void visit(UnaryOpNode node) throws Exception {
        super.visit(node);
        switch (node.getOp()) {
            case PREFIX_DEX:
            case PREFIX_INC:
                if (!node.getInnerExpr().isLeft_val())
                    throw new SemanticException(node.getLocation(), "++x and --x should operate on left value");

                if (!node.getInnerExpr().getExprType().equalPrimitive(INT))
                    throw new SemanticException(node.getLocation(), "++x and --x should operate on int");
                node.setLeft_val(true);
                break;
            case POSTFIX_DEC:
            case POSTFIX_INC:
                if (!node.getInnerExpr().isLeft_val())
                    throw new SemanticException(node.getLocation(), "x++ and x-- should operate on left value");
                if (!node.getInnerExpr().getExprType().equalPrimitive(INT))
                    throw new SemanticException(node.getLocation(), "x++ and x-- should operate on int");
                break;
        }
        node.setExprType(node.getInnerExpr().getExprType());
    }


    @Override
    public void visit(BinaryOpNode node) throws Exception {
        super.visit(node);
        ExprStaNode lhs = node.getLhs();
        ExprStaNode rhs = node.getRhs();
        if (lhs == null)
            throw new SemanticException(node.getLocation(), "lhs expr is null.");
        if (rhs == null)
            throw new SemanticException(node.getLocation(), "rhs expr is null");
        if (lhs.getExprType() == null) {
            lhs.setExprType(rhs.getExprType());
        }
        if (rhs.getExprType() == null) {
            if (lhs.getExprType() == null) {
                lhs.setExprType(new PrimitiveTypeNode("int"));
            }
            rhs.setExprType(lhs.getExprType());
        }

        switch (node.getOp()) {
            case ADD:
                if (lhs.getExprType() instanceof ClassTypeNode &&
                        rhs.getExprType() instanceof ClassTypeNode) {
                    if (!(lhs.getExprType().equalTo(new ClassTypeNode("string"))) &&
                            !(rhs.getExprType().equalTo(new ClassTypeNode("string"))))
                        throw new SemanticException(node.getLocation(), "only class string can add");
                } else if (!(lhs.getExprType().equalPrimitive(INT)) &&
                        !(rhs.getExprType().equalPrimitive(INT))) {
                    throw new SemanticException(node.getLocation(), "only int can add");
                }
                node.setExprType(lhs.getExprType());
                break;
            case OR:
            case AND:
            case XOR:
            case DIV:
            case MOD:
            case MUL:
            case SUB:
            case LSHIFT:
            case RSHIFT:
                // ^ & | / % * - << >>
                if (!(lhs.getExprType().equalPrimitive(INT)) &&
                        !(rhs.getExprType().equalPrimitive(INT))) {
                    throw new SemanticException(node.getLocation(), "only int can do this operation.");
                }
                node.setExprType(lhs.getExprType());
                break;
            case ASSIGN: // =
                if (!lhs.isLeft_val())
                    throw new SemanticException(lhs.getLocation(), "lhs must be left value");
                if (!lhs.getExprType().equalTo(rhs.getExprType()))
                    throw new SemanticException(node.getLocation(), "lhs and rhs must have same type");
                node.setExprType(lhs.getExprType());
                break;
            case EQUAL:
            case NOTEQUAL:// == !=
                if (!lhs.getExprType().equalTo(rhs.getExprType())
                        && !lhs.getExprType().equalPrimitive(NULL)
                        && !rhs.getExprType().equalPrimitive(NULL))
                    throw new SemanticException(node.getLocation(), "lhs and rhs must be same type");
                node.setExprType(new PrimitiveTypeNode("bool"));
                break;
            case LAND:
            case LOR:// && ||
                if ((!lhs.getExprType().equalPrimitive(BOOL) && !lhs.getExprType().equalPrimitive(INT)) ||
                        (!rhs.getExprType().equalPrimitive(BOOL) && !rhs.getExprType().equalPrimitive(INT)))
                    throw new SemanticException(node.getLocation(), "lhs and rhs must be bool or int");
                node.setExprType(new PrimitiveTypeNode("bool"));
                break;
            case GE:
            case GT:
            case LE:
            case LT: //LT : '<'; GT : '>'; LE : '<='; GE : '>=';
                if (!lhs.getExprType().equalTo(rhs.getExprType()))
                    throw new SemanticException(node.getLocation(), "operation should operate on same type");
                if (!(lhs.getExprType().equalPrimitive(INT)) &&
                        !(lhs.getExprType().equalPrimitive(BOOL)) &&
                        !(lhs.getExprType().equalTo(new ClassTypeNode("string"))))
                    throw new SemanticException(node.getLocation(), "this type does not support this operator");
                node.setExprType(new PrimitiveTypeNode("bool"));
                break;
            default:
                throw new SemanticException(node.getLocation(), "this binary operator does not exist");
        }

    }

    @Override
    public void visit(ForStaNode node) throws Exception {
        super.visit(node);
        if (node.getCondition() == null) return;
        if (!node.getCondition().getExprType().equalPrimitive(BOOL))
            throw new SemanticException(node.getCondition().getLocation(), "condition of for statement should be a boolean expr");
    }

    @Override
    public void visit(WhileStaNode node) throws Exception {
        super.visit(node);
        if (node.getCondition() == null) return;
        if (!node.getCondition().getExprType().equalPrimitive(BOOL))
            throw new SemanticException(node.getCondition().getLocation(), "condition of while statement should be a boolean expr");
    }


    @Override
    public void visit(IndexAccessNode node) throws Exception {
        super.visit(node);
        if (!(node.getCaller().getExprType() instanceof ArrayTypeNode))
            throw new SemanticException(node.getLocation(), "the index access only on array type");
        if (!(node.getIndex().getExprType().equalPrimitive(INT)))
            throw new SemanticException(node.getIndex().getLocation(), "index must be int");
        node.setExprType(((ArrayTypeNode) node.getCaller().getExprType()).getInnerTypeNode());//to be check...
        node.setLeft_val(true);
    }

    @Override
    public void visit(FunctionCallNode node) throws Exception {
        super.visit(node);
        ///////check caller///////
        if (!(node.getCaller() instanceof ReferenceNode))
            throw new SemanticException(node.getLocation(), "caller must be reference type");
        if (node.getCaller().getReferenceType() != ReferenceNode.ReferenceType.METHOD)
            throw new SemanticException(node.getCaller().getLocation(), "function call reference must be method type");

        String name = node.getCaller().getReferenceName();
        FunctionDefNode def;
        AstNode parent = node.getParent();
        while (parent.getScope() == null || parent.getScope().getFunctionInScope(name) == null) {
            if (parent.getParent() == null)
                throw new SemanticException(node.getCaller().getLocation(), "no such method");
            parent = parent.getParent();
        }
        def = parent.getScope().getFunctionInScope(name);

        ////check parameters/////
        if (def.getFormalParameterList().size() != node.getActualParameterList().size())
            throw new SemanticException(node.getLocation(), "actual parameters' number is wrong");
        for (int i = 0; i < node.getActualParameterList().size(); ++i) {
            VarDefNode formalDef = def.getFormalParameterList().get(i);
            ExprStaNode actualDef = node.getActualParameterList().get(i);
            if (!formalDef.getVarType().equalTo(actualDef.getExprType()))
                throw new SemanticException(actualDef.getLocation(), "parameter type error");
        }

        node.setExprType(def.getReturnType());
    }

    @Override
    public void visit(ReturnStaNode node) throws Exception {
        super.visit(node);
        AstNode parent = node;
        while (!(parent instanceof FunctionDefNode)) {
            parent = parent.getParent();
        }
        if (node.getReturnVal() == null) {
            if (!((FunctionDefNode) parent).getReturnType().equalPrimitive(VOID))
                throw new SemanticException(node.getLocation(), "return val shouldn't be null");
            ((FunctionDefNode) parent).setReturnSta(true);
        } else {
            if (!node.getReturnVal().getExprType().equalTo(((FunctionDefNode) parent).getReturnType())) {
                throw new SemanticException(node.getLocation(), "return type is not fit to function def");
            }
            ((FunctionDefNode) parent).setReturnSta(true);
        }
    }

    @Override
    public void visit(ArrayTypeNode node) throws Exception {
        super.visit(node);
        if (node.getSize() != null)
            if (!node.getSize().getExprType().equalPrimitive(INT))
                throw new SemanticException(node.getSize().getLocation(), "size should an int");
    }

    @Override
    public void visit(BoolNode node) throws Exception {
        super.visit(node);
        node.setExprType(new PrimitiveTypeNode("bool"));
    }

    @Override
    public void visit(NullNode node) throws Exception {
        super.visit(node);
        node.setExprType(new PrimitiveTypeNode("null"));
    }

    @Override
    public void visit(IntNode node) throws Exception {
        super.visit(node);
        node.setExprType(new PrimitiveTypeNode("int"));
    }

    @Override
    public void visit(StringNode node) throws Exception {
        super.visit(node);
        node.setExprType(new ClassTypeNode("string"));
    }
    //////private utilities///////

    /////////dfs check cycling definition////////
    private void check() throws Exception {
        LinkedList<String> queue = new LinkedList<>();
        int cnt = 0;
        for (String name : indgreeMap.keySet()) {
            if (indgreeMap.get(name).equals(0)) {
                queue.add(name);
                ++cnt;
            }
        }
        while (!queue.isEmpty()) {
            String name = queue.getLast();
            queue.removeLast();
            for (String base : edgeMap.get(name)) {
                int t = indgreeMap.get(name) - 1;
                indgreeMap.remove(base);
                if (t == 0) {
                    queue.add(base);
                    ++cnt;
                } else indgreeMap.put(base, t);
            }
        }
        if (cnt < edgeMap.size())
            throw new SemanticException("cycling class definition~~");
    }

    private boolean isVoid(VariableTypeNode node) throws Exception {
        if (node instanceof PrimitiveTypeNode) {
            if (node.equalTo(new PrimitiveTypeNode("void")))
                return true;
        }
        return false;
    }

    private boolean isVoidArray(VariableTypeNode node) throws Exception {
        if (node instanceof ArrayTypeNode) {
            if (((ArrayTypeNode) node).getInnerTypeNode() instanceof ArrayTypeNode) {
                return isVoidArray(((ArrayTypeNode) node).getInnerTypeNode());
            } else if (isVoid(((ArrayTypeNode) node).getInnerTypeNode())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void visit(IfStaNode node) throws Exception {
        super.visit(node);
        if (node.getCondition() == null) return;
        if (!node.getCondition().getExprType().equalPrimitive(BOOL))
            throw new SemanticException(node.getCondition().getLocation(), "condition of if statement should be a boolean expr");
    }

    @Override
    public void visit(MemberAccessNode node) throws Exception {
        try {
            super.visit(node);
        } catch (SemanticException exception) {
        }
        //for we have not define reference type and so on, thus the semantic check here is just for try
        //thus we can finish building some expressions in this try
        ClassDefNode classDefNode;
        if(!(node.getCaller().getExprType() instanceof ClassTypeNode))
            if(node.getCaller().getExprType() instanceof ArrayTypeNode)
                classDefNode = topLevelScope.getClass("_array");
            else throw new SemanticException(node.getCaller().getLocation(), "caller must be class type");
        else {
            classDefNode = topLevelScope.getClass(((ClassTypeNode) node.getCaller().getExprType()).getReferenceClassName());
        }
        if(node.getMember() instanceof ReferenceNode) {
            ReferenceNode member = (ReferenceNode)node.getMember();
            member.setReferenceType(ReferenceNode.ReferenceType.VARIABLE);
            VarDefNode memberDef =
                    classDefNode.getScope().getVariable(member.getReferenceName());
            if(memberDef == null)
                throw new SemanticException(member.getLocation(),
                        "class " + classDefNode.getClassName() + " does not has such member");
            member.setDefinitionNode(memberDef);
            node.setExprType(memberDef.getVarType());
            node.setLeft_val(true);
        }
        else if (node.getMember() instanceof FunctionCallNode) {
            ReferenceNode member = ((FunctionCallNode)node.getMember()).getCaller();
            member.setReferenceType(ReferenceNode.ReferenceType.METHOD);
            if(node.getCaller().getExprType() instanceof ArrayTypeNode) {
                if(member.getReferenceName().equals("size")) {
                    node.setExprType(new PrimitiveTypeNode("int"));
                    member.setDefinitionNode(new FunctionDefNode());
                    member.setReferenceName("_array_size");
                    return;
                }
            }
            FunctionDefNode memberDef =
                    classDefNode.getScope().getFunctionInScope(member.getReferenceName());
            if(memberDef == null)
                throw new SemanticException(member.getLocation(),
                        "class "+ classDefNode.getClassName() + " does not have such function");
            member.setDefinitionNode(memberDef);
            node.setExprType(memberDef.getReturnType());
        }
        else
            throw new SemanticException(node.getLocation(),"member access failed");




        ////check caller
//        visit(node.getCaller());
//        visit(node.getMember());
//        if (!(node.getCaller().getExprType() instanceof ClassTypeNode))
//            if (node.getCaller().getExprType() instanceof ArrayTypeNode) {
//                if (node.getMember() instanceof ReferenceNode) {
//                    throw new SemanticException(node.getMember().getLocation(), "array does not have a member");
//                } else {
//                    if (!(node.getMember() instanceof FunctionCallNode))
//                        throw new SemanticException(node.getMember().getLocation(), "no such stupid member!!");
//                    if (((FunctionCallNode) node.getMember()).getCaller().getReferenceName().equals("size")) {
//                        defNode = null;
//                        node.setExprType(new PrimitiveTypeNode("int"));
//                    } else {
//                        throw new SemanticException(node.getMember().getLocation(), "array do not have such member");
//                    }
//                }
//            } else {
//                throw new SemanticException(node.getCaller().getLocation(), "caller must be class");
//            }
//        else {//reference
//            defNode = ((GlobalScope) topLevelScope).getClasses()
//                    .get(((ClassTypeNode) node.getCaller().getExprType()).getReferenceClassName());
//            if (defNode == null) throw new SemanticException(node.getCaller().getLocation(), "class does not exist");
//
//            //////check member
//            if (node.getMember() instanceof ReferenceNode) {
//                ReferenceNode member = (ReferenceNode) node.getMember();
//                member.setReferenceType(ReferenceNode.ReferenceType.VARIABLE);
//                VarDefNode memberDef = (defNode.getScope().getVariables().get(member.getReferenceName()));
//                if (memberDef == null)
//                    throw new SemanticException(member.getLocation(), "class" + defNode.getClassName() + "does not have such member");
//                else member.setDefinitionNode(memberDef);
//                node.setExprType(memberDef.getVarType());
//                node.setLeft_val(true);
//            } else if (node.getMember() instanceof FunctionCallNode) {
//                ReferenceNode method = ((FunctionCallNode) node.getMember()).getCaller();
//                method.setReferenceType(ReferenceNode.ReferenceType.METHOD);
//                if (node.getCaller().getExprType() instanceof ArrayTypeNode) {
//                    if (method.getReferenceName().equals("size")) {
//                        node.setExprType(new PrimitiveTypeNode("int"));
//                        method.setDefinitionNode(new FunctionDefNode());
//                        method.setReferenceName("array_size");
//                    }
//                } else {
//                    FunctionDefNode functionDef = null;
////                    String name = defNode.getClassName() + "_" + method.getReferenceName();
//                    String name = method.getReferenceName();
//                    for (FunctionDefNode item : defNode.getFunctionDefList()) {
//                        if (item.getMethodName().equals(name))
//                            functionDef = item;
//                    }
//                    if (functionDef == null)
//                        throw new SemanticException(method.getLocation(), "class " + defNode.getClassName() +
//                                " does not have " + method.getReferenceName() + " method");
//                    method.setDefinitionNode(functionDef);
//                    node.setExprType(functionDef.getReturnType());
//                }
//
//            } else throw new SemanticException(node.getMember().getLocation(), "class access must be method or member");
//        }
//        node.setClassDefNode(defNode);
    }
}

















