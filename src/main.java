import BackEnd.*;
import FrontEnd.Antlr.MxErrorListener;
import FrontEnd.Antlr.MxLexer;
import FrontEnd.Antlr.MxParser;
import FrontEnd.*;
import FrontEnd.AstNode.ProgramNode;
import FrontEnd.IR.Module;
import FrontEnd.Scope.Scope;
import Optimize.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import util.ErrorHandler;

import java.io.InputStream;

public class main  {

    public static void main(String[] args)throws Exception {
//        String path ="/Users/fulingyue/Desktop/Compiler-Lite/test/sourceCode1.mx";
//        InputStream inputStream = new FileInputStream(path);
        InputStream inputStream = System.in;
        compile(inputStream, args);
        System.exit(0);

    }


    private static void compile(InputStream input,String[] args) throws Exception {
        ErrorHandler errorHandler = new ErrorHandler();
        CharStream antlrInputStream =  CharStreams.fromStream(input);

        TypeDefChecker typeDefChecker = new TypeDefChecker();
        //////init/////
        MxLexer lexer = new MxLexer(antlrInputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new MxErrorListener(errorHandler));
        CommonTokenStream tokens = new CommonTokenStream(lexer);


        MxParser parser = new MxParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new MxErrorListener(errorHandler));
        ParseTree tree = parser.program();

        if(errorHandler.getError_cnt() > 0){
            System.exit(errorHandler.getError_cnt());
        }


        /////////semantic/////////
        AstBuilder astBuilder = new AstBuilder();
        astBuilder.visit(tree);

        if(astBuilder.getError().getError_cnt() > 0){

            System.exit(astBuilder.getError().getError_cnt());
        }

        ProgramNode program = astBuilder.getProgram();

        ParentLinker parentLinker  = new ParentLinker();

        parentLinker.linkParent(program);


        ScopeBuilder scopeBuilder = new ScopeBuilder();
        Scope topLevelScope = scopeBuilder.BuildScopeTree(program);
//        ScopePrinter scopePrinter = new ScopePrinter();
//        scopePrinter.printScopeTree(topLevelScope);
        typeDefChecker.checkTypeDef(program);


        if(args[0].equals("0")){
            System.exit(errorHandler.getError_cnt());
        }
        ///////IR////////
        IRBuilder irBuilder =  new IRBuilder();
        irBuilder.build(program);
        Module module = irBuilder.getProgram();


        IRPrinter irPrinter  = new IRPrinter();
//        irPrinter.print(module);

        CFGSimplifier cfgSimplifier = new CFGSimplifier(module);
        cfgSimplifier.run();
        DominatorTree dominatorTree = new DominatorTree(module);
        dominatorTree.run();
//        dominatorTree.print();
        SSAConstructor ssaConstructor = new SSAConstructor(module);
        ssaConstructor.run();
        irPrinter.print(module);
        DeadCodeElim DCE = new DeadCodeElim(module);
        DCE.run();
        SCCP sccp = new SCCP(module);
        sccp.run();

        cfgSimplifier.run();

//        IRPrinter irPrinter  = new IRPrinter();
//        irPrinter.print(module);

        SSADestructor ssaDestructor = new SSADestructor(module);
        ssaDestructor.run();
//

        RegisterTable registerTable = new RegisterTable();

        InstructionSelection instructionSelector = new InstructionSelection(module);
        instructionSelector.run();
        RiscModule riscModule = instructionSelector.getModule();
        RegisterAlloca registerAlloca = new RegisterAlloca(riscModule);
        registerAlloca.run();
//        AllSpilledAlooca allSpilledAlooca = new AllSpilledAlooca(riscModule);
//        allSpilledAlooca.run();

        Codegen codegen = new Codegen("output.s");
//        Codegen codegen = new Codegen("test/test.s");
        codegen.run(riscModule);

    }

}
