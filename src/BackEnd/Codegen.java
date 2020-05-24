package BackEnd;

import BackEnd.Instruction.*;
import BackEnd.Operands.ConstStrings;
import BackEnd.Operands.GlobalVar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import static util.print_tool.repeatString;

public class Codegen {
    private File outputFile;
    private OutputStream os;
    private PrintWriter writer;
    private String indent;

    private int functionCnt;

    public Codegen(String filename){

        try {
            outputFile = new File(filename);
            os = new FileOutputStream(filename);
            writer = new PrintWriter(os);
        }
        catch (Exception ex){
            throw new RuntimeException("file open failed");
        }

        indent = "    ";

    }
    private void print(String string) {

//        System.out.print(string);
        if (os != null)
            writer.print(string);
    }

    private void println(String string) {
//        System.out.println(string);
        if (os != null)
            writer.println(string);
    }


    public void run(RiscModule module){

        println(indent + ".text");
        println("");

        functionCnt = 0;
        for(RiscFunction function : module.getFunctionList()){
            visit(function);
        }
        println("");

        println(indent + ".section\t.sdata,\"aw\",@progbits");
        for(GlobalVar globalVar: module.getGlobalVars()){
            if(globalVar instanceof ConstStrings) continue;
            visit(globalVar);
        }

        for(ConstStrings string: module.getConstStrings()){
            visit(string);
        }

        try {
            writer.close();
            os.close();
        }
        catch (Exception exc){
            throw new RuntimeException("exc in codegen");
        }
    }


    public void visit(RiscFunction function){
        print(indent + ".globl" + indent + function.getName());
        print(repeatString(" ",Integer.max(1, 24 - function.getName().length())));
        println("# -- Begin function " + function.getName());
        println(indent + ".p2align" + indent + "2");

        print(function.getName() + ":" + repeatString(" ",Integer.max(1, 31 - function.getName().length())));
        println("# @" + function.getName());

        for (RiscBB bb:function.getBlocks())
            visit(bb);

        println(repeatString(" ",40) + "# -- End function");
        println("");
        println("");

        functionCnt++;
    }

    public void visit(GlobalVar globalVar){
        println(indent + ".globl" + indent + globalVar.getName());
        println(indent + ".p2align" + indent + "2");

        println(globalVar.getName() + ":");
        println(globalVar.print());
        println("");
    }

    public void visit(RiscBB bb){
        String name = bb.getLabel();
        println(name + ":" + repeatString(" ",40 - 1 - name.length()) + "# " + bb.getName());
        for(RiscInstruction instruction = bb.getHead();instruction != null;instruction= instruction.getNext()){
            println(instruction.print());
        }
    }


    public void visit(ConstStrings string){
        println(string.getName() + ":");
        println(string.print());
        println("");
    }
//
//    public void visit(RiscInstruction inst){
//        if(inst instanceof ArtheticOp) visit((ArtheticOp)inst);
//        else if(inst instanceof BinaryBranch) visit((BinaryBranch)inst);
//        else if(inst instanceof CallFunc) visit((CallFunc)inst);
//        else if(inst instanceof ILoad) visit((ILoad) inst);
//        else if(inst instanceof ImmOperation) visit((ImmOperation)inst);
//        else if(inst instanceof Jal) visit((Jal)inst);
//        else if(inst instanceof Jalr) visit((Jalr)inst);
//        else if(inst instanceof Li) visit((Li)inst);
//        else if(inst instanceof Lui) visit((Lui)inst);
//        else if(inst instanceof Move) visit((Move)inst);
//        else if(inst instanceof Stype) visit((Stype)inst);
//        else throw new RuntimeException("no Inst!");
//    }
//
//    public void visit(ArtheticOp inst){
//        System.out.println(inst.getRd().print() + "\t="  +
//                inst.getOp().toString() + "\t" + inst.getRs1().print()
//                        + "\t" +  inst.getRs2().print());
//    }
//
//    public void visit(BinaryBranch inst){
////        System.out.println(
////                inst.getOp().toString() +  "\t" + inst.getRs().print() +
////                        "\t" + inst.getRt().print() + "\t" +  "to" +
////                        inst.getTarget().getLabel()
////        );
//    }
//
//    public void visit(CallFunc inst){
//        System.out.print(
//                "call:\t" + inst.getFunction().getName()
//        );
//
//    }
//
//    public void visit(ILoad load){
//        System.out.println(
//                "load" +  load.gettarget().print() + load.getOffset().getValue()+
//                        "to\t" + load.getRd().print()
//        );
//    }
//
//
//    public  void visit(ImmOperation immOperation){
//        System.out.println(
//                immOperation.getRd().print() + "\t=" +
//                        immOperation.getOp().toString() + "\t" +
//                        immOperation.getRs().print() + "\t" +
//                                immOperation.getImm().print()
//
//        );
//    }
//
//    public void visit(Jal jal){
//        System.out.println(
//                "jump to \t" + jal.getTarget().getLabel()
//        );
//    }
//
//    public void visit(Jalr jalr){
//        System.out.println(
//                "jump to the address: \t" + jalr.getRs().print()
//        );
//    }
//
//    public void visit(Li li){
//        System.out.println(
//                "li:" + li.getRd().print() + "\t" + li.getImm().print()
//        );
//    }
//
//    public void visit(Lui lui){
//        System.out.println(
//                "lui:" + lui.getRd().print() + "\t" + lui.getImm().print()
//        );
//    }
//
//    public void visit(Move mv){
//        System.out.println(
//                "mv \t" + mv.getRs().print() + "to\t" + mv.getRd().print()
//        );
//    }
//
//    public void visit(Stype store){
////        System.out.println(
////                "store " + store.getOp().toString() +
////                         store.getDest() + "\t+" +
////                        store.getDest().print() + "to \t" +
////                        store.getRd().print()
////        );
//    }


}
