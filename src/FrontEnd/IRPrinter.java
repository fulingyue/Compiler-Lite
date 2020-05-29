package FrontEnd;

import FrontEnd.IR.BasicBlock;
import FrontEnd.IR.IRFunction;
import FrontEnd.IR.Instruction.*;
import FrontEnd.IR.Module;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class IRPrinter implements IRVisitor {
    private String indent;
    private PrintWriter printWriter;
    private FileWriter fileWriter;

    public IRPrinter() throws Exception{
        indent = "    ";
        File file = new File("test/test.ll");
        if(!file.exists())
            file.createNewFile();
        fileWriter = new FileWriter("test/test.ll");
        printWriter = new PrintWriter(fileWriter);

    }
    private void println(String str) {
        printWriter.println(str);
    }
    public void print(Module root){
        println("; ModuleID = 'test.txt'");
        println("source_filename = \"test.txt\"");
        println("target datalayout = \"e-m:e-i64:64-f80:128-n8:16:32:64-S128\"");
//        println("target triple = \"x86_64-unknown-linux-gnu\"");
        println("target triple = \"x86_64-apple-macosx10.15.0\"");
        println("");
        visit(root);
        printWriter.flush();
    }

    @Override
    public void visit(Module root) {
        //////Class//////
        for(String name: root.getClassMap().keySet()) {
            println(root.getClassMap().get(name).printType());
        }
        println("");
        for(String name: root.getStaticVariableMap().keySet()) {
            println(root.getStaticVariableMap().get(name).printDef());
        }
        println("");
        println("; Function Attrs:");

        for(String name: root.getExternalFuncMap().keySet()) {
            IRFunction function = root.getExternalFuncMap().get(name);
            println(function.printDeclare());
            println("");
        }

        for (String name: root.getFunctionMap().keySet()) {
            root.getFunctionMap().get(name).accept(this);
            println("");
        }

    }

    @Override
    public void visit(IRFunction function) {
        println(function.printDef() + " {");

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
    public void visit(MoveInst move) {
        println(indent + move.print());
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




}



