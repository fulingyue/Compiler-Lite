package BackEnd;

import BackEnd.Instruction.*;
import BackEnd.Operands.ConstStrings;
import BackEnd.Operands.GlobalVar;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;

public class Codegen {
    private File outputFile;
    private OutputStream os;
    private PrintWriter writer;
    private String indent;

    private int functionCnt;
    private boolean printRealASM;

    public Codegen(){
    }

    public void run(RiscModule module){
        for(RiscFunction function : module.getFunctionList()){
            visit(function);
        }

        for(GlobalVar globalVar: module.getGlobalVars()){
            visit(globalVar);
        }

        for(ConstStrings string: module.getConstStrings()){
            visit(string);
        }
    }

    public void visit(RiscFunction function){
        System.out.print(function.getName() + ":\n");
        for(RiscBB bb = function.getEntranceBB(); bb != null; bb = bb.getNext()){
            visit(bb);
        }
    }

    public void visit(GlobalVar globalVar){
        System.out.println("globalVar:" + globalVar.getName());
    }

    public void visit(RiscBB bb){
        System.out.println(bb.getLabel());
        for(RiscInstruction instruction = bb.getHead();instruction != null;instruction= instruction.getNext()){
            visit(instruction);
        }
    }


    public void visit(ConstStrings string){
        System.out.println("string:"+ string.getStr());
    }

    public void visit(RiscInstruction inst){
        if(inst instanceof ArtheticOp) visit((ArtheticOp)inst);
        else if(inst instanceof BinaryBranch) visit((BinaryBranch)inst);
        else if(inst instanceof CallFunc) visit((CallFunc)inst);
        else if(inst instanceof ILoad) visit((ILoad) inst);
        else if(inst instanceof ImmOperation) visit((ImmOperation)inst);
        else if(inst instanceof Jal) visit((Jal)inst);
        else if(inst instanceof Jalr) visit((Jalr)inst);
        else if(inst instanceof Li) visit((Li)inst);
        else if(inst instanceof Lui) visit((Lui)inst);
        else if(inst instanceof Move) visit((Move)inst);
        else if(inst instanceof Stype) visit((Stype)inst);
        else throw new RuntimeException("no Inst!");
    }

    public void visit(ArtheticOp inst){
        System.out.println(inst.getRd().print() + "\t="  +
                inst.getOp().toString() + "\t" + inst.getRs1().print()
                        + "\t" +  inst.getRs2().print());
    }

    public void visit(BinaryBranch inst){
        System.out.println(
                inst.getOp().toString() +  "\t" + inst.getRs().print() +
                        "\t" + inst.getRt().print() + "\t" +  "to" +
                        inst.getTarget().getLabel()
        );
    }

    public void visit(CallFunc inst){
        System.out.print(
                "call:\t" + inst.getFunction().getName()
        );

    }

    public void visit(ILoad load){
        System.out.println(
                "load" +  load.gettarget().print() + load.getOffset().getValue()+
                        "to\t" + load.getRd().print()
        );
    }


    public  void visit(ImmOperation immOperation){
        System.out.println(
                immOperation.getRd().print() + "\t=" +
                        immOperation.getOp().toString() + "\t" +
                        immOperation.getRs().print() + "\t" +
                                immOperation.getImm().print()

        );
    }

    public void visit(Jal jal){
        System.out.println(
                "jump to \t" + jal.getTarget().getLabel()
        );
    }

    public void visit(Jalr jalr){
        System.out.println(
                "jump to the address: \t" + jalr.getRs().print()
        );
    }

    public void visit(Li li){
        System.out.println(
                "li:" + li.getRd().print() + "\t" + li.getImm().print()
        );
    }

    public void visit(Lui lui){
        System.out.println(
                "lui:" + lui.getRd().print() + "\t" + lui.getImm().print()
        );
    }

    public void visit(Move mv){
        System.out.println(
                "mv \t" + mv.getRs().print() + "to\t" + mv.getRd().print()
        );
    }

    public void visit(Stype store){
        System.out.println(
                "store " + store.getOp().toString() +
                         store.getDest() + "\t+" +
                        store.getDest().print() + "to \t" +
                        store.getRd().print()
        );
    }


}
