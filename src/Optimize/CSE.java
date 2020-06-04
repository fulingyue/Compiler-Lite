package Optimize;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Instruction.BinaryOp;
import FrontEnd.IR.Instruction.Icmp;
import FrontEnd.IR.Instruction.Instruction;
import FrontEnd.IR.Module;
import FrontEnd.IR.Operand.Operand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class CSE extends Pass {

    public CSE(Module module) {
        super(module);
    }

    static class Expression{
        String op;
        String lhs, rhs;
        Expression(String op, String lhs, String rhs){
            this.op = op;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public boolean equals(Object o){
            if(this == o) return true;
            if(o == null || getClass()  != o.getClass()) return false;
            Expression expr = (Expression) o;
            return Objects.equals(op, expr.op) &&
                    Objects.equals(lhs, expr.lhs) &&
                    Objects.equals(rhs, expr.rhs);
        }

        @Override
        public int hashCode(){
            return Objects.hash(op,lhs,rhs);
        }
    }

    private HashSet<BasicBlock> visited = new HashSet<>();
    private Map<Expression, Operand> expressionMap = new HashMap<>();
    @Override
    public boolean run() {
        changed = false;
        for(IRFunction function: module.getFunctionMap().values()){
            visited = new HashSet<>();
            changed |= functionCSE(function);
        }
        return false;
    }

    private boolean functionCSE(IRFunction function){
        boolean ischanged = false;
        for(BasicBlock  bb:  function.getBlocks()){
            if(!visited.contains(bb)){
                expressionMap.clear();
                ischanged |= bbCSE(bb);
            }
        }
        return ischanged;
    }

    private boolean bbCSE(BasicBlock bb){
        boolean ischanged = false;
        for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNxt()){
            if(inst instanceof BinaryOp){
                Expression expr = new Expression(
                        ((BinaryOp) inst).getOp().toString(),
                        ((BinaryOp) inst).getLhs().toString(),
                        ((BinaryOp) inst).getRhs().toString());
                Operand dst = expressionMap.get(expr);
                if(dst != null){
                    ischanged = true;
                    ((BinaryOp) inst).getDest().replaceUser(dst);
                    inst.remove();
                }else {
                    expressionMap.put(expr,((BinaryOp) inst).getDest());
                    if(((BinaryOp)inst).isCommutative()){
                        Expression expr2 = new Expression(
                                ((BinaryOp) inst).getOp().toString(),
                                ((BinaryOp) inst).getRhs().toString(),
                                ((BinaryOp) inst).getLhs().toString());
                        expressionMap.put(expr2,((BinaryOp) inst).getDest());
                    }
                }
            }
            else if(inst instanceof Icmp){
                Expression cmpExpr = new Expression(
                        ((Icmp) inst).getOp().toString(),
                        ((Icmp) inst).getLhs().toString(),
                        ((Icmp) inst).getRhs().toString());
                Operand dst = expressionMap.get(cmpExpr);
                if(dst != null){
                    ischanged = true;
                    ((Icmp) inst).getDest().replaceUser(dst);
                    inst.remove();
                }
                else {
                    expressionMap.put(cmpExpr,((Icmp) inst).getDest());
                    if(((Icmp) inst).isCommutative()){
                        Expression cmpExpr2 = new Expression(
                                ((Icmp) inst).getOp().toString(),
                                ((Icmp) inst).getRhs().toString(),
                                ((Icmp) inst).getLhs().toString());
                        expressionMap.put(cmpExpr2,((Icmp) inst).getDest());
                    }
                    else if(((Icmp) inst).getCommutativeOp() != null){
                        Expression cmpExpr2 = new Expression(
                                ((Icmp) inst).getCommutativeOp().toString(),
                                ((Icmp) inst).getRhs().toString(),
                                ((Icmp) inst).getLhs().toString());
                        expressionMap.put(cmpExpr2,((Icmp) inst).getDest());
                    }
                }
            }
        }

        for(BasicBlock  successor: bb.getSuccessors()){
            if(successor != bb && successor.getPredecessorBB().size() == 1)
                ischanged |= bbCSE(successor);
        }

        for(Instruction  inst = bb.getHead(); inst != null; inst = inst.getNxt()){
            if(inst instanceof BinaryOp){
                Expression expr = new Expression(
                        ((BinaryOp) inst).getOp().toString(),
                        ((BinaryOp) inst).getLhs().toString(),
                        ((BinaryOp) inst).getRhs().toString());

                    expressionMap.remove(expr);
                    if(((BinaryOp)inst).isCommutative()){
                        Expression expr2 = new Expression(
                                ((BinaryOp) inst).getOp().toString(),
                                ((BinaryOp) inst).getRhs().toString(),
                                ((BinaryOp) inst).getLhs().toString());
                        expressionMap.remove(expr2);

                }
            }
            else if(inst instanceof Icmp){
                Expression cmpExpr = new Expression(
                        ((Icmp) inst).getOp().toString(),
                        ((Icmp) inst).getLhs().toString(),
                        ((Icmp) inst).getRhs().toString());
                expressionMap.remove(cmpExpr);


                    if(((Icmp) inst).isCommutative()){
                        Expression cmpExpr2 = new Expression(
                                ((Icmp) inst).getOp().toString(),
                                ((Icmp) inst).getRhs().toString(),
                                ((Icmp) inst).getLhs().toString());
                        expressionMap.remove(cmpExpr2);
                    }
                    else if(((Icmp) inst).getCommutativeOp() != null){
                        Expression cmpExpr2 = new Expression(
                                ((Icmp) inst).getCommutativeOp().toString(),
                                ((Icmp) inst).getRhs().toString(),
                                ((Icmp) inst).getLhs().toString());
                        expressionMap.remove(cmpExpr2);
                    }

            }
        }
        return ischanged;
    }
}
