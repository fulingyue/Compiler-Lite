package FrontEnd;

import FrontEnd.AstNode.FunctionCallNode;
import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Module;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class IRPrinter implements IRVisitor {
    private String indent;
    private OutputStream os;
    private PrintWriter writer;

    public IRPrinter() {
        try {
            os = new FileOutputStream("test.ll");
            writer = new PrintWriter(os);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        indent = "    ";
    }

    @Override
    public void visit(Module root) {
        // ------ HEAD ------
        println("; ModuleID = 'code.txt'");
        println("source_filename = \"code.txt\"");
        println("target datalayout = \"e-m:e-i64:64-f80:128-n8:16:32:64-S128\"");
        println("target triple = \"x86_64-pc-linux-gnu\"");
        println("");

        for (String name: root.getFunctionMap().keySet()) {
            root.getFunctionMap().get(name).accept(this);
            println("");
        }

    }

    @Override
    public void visit(IRFunction function) {
//        println(function.declareToString().replace("declare", "define") + " {");

        BasicBlock ptr = function.getEntranceBB();
        while (ptr != null) {
            ptr.accept(this); // visit BasicBlock
            if (ptr.getNextBB() != null)
                println("");
            ptr = ptr.getNextBB();
        }

        println("}");
    }

    @Override
    public void visit(BasicBlock block) {
        println(block.getName() + ":");
//        if (block.hasPredecessor()) {
//            print(" ".repeat(50 - (block.getName().length() + 1)));
//            print("; preds = ");
//            int size = block.getPredecessors().size();
//            int cnt = 0;
//            for (BasicBlock predecessor : block.getPredecessors()) {
//                print(predecessor.toString());
//                if (++cnt != size)
//                    print(", ");
//            }
//        }
//        println("");

        Instruction ptr = block.getHead();
        while (ptr != null) {
            ptr.accept(this); // visit IRInstruction
            ptr = ptr.getNxt();
        }
    }

    @Override
    public void visit(Return inst) {
        println(indent + inst.print());
    }

    @Override
    public void visit(BranchJump inst) {
        println(indent + inst.print());
    }

    @Override
    public void visit(BinaryOp inst) {
        println(indent + inst.print());
    }

    @Override
    public void visit(AllocateInst inst) {
        println(indent + inst.print());
    }

    @Override
    public void visit(Load inst) {
        println(indent + inst.print());
    }

    @Override
    public void visit(Store inst) {
        println(indent + inst.print());
    }

    @Override
    public void visit(GetPtr inst) {
        println(indent + inst.print());
    }

    @Override
    public void visit(BitCast inst) {
        println(indent + inst.print());
    }

    @Override
    public void visit(Icmp inst) {
        println(indent + inst.print());
    }

    @Override
    public void visit(Phi inst) {
        println(indent + inst.print());
    }

    @Override
    public void visit(CallFunction inst) {
        println(indent + inst.print());
    }



    public OutputStream getOs() {
        return os;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    private void print(String string) {
       System.out.print(string);
    }

    private void println(String string) {
        System.out.println(string);
    }

}



