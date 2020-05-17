import BackEnd.Codegen;
import BackEnd.InstructionSelection;
import BackEnd.RiscModule;
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

import java.io.FileInputStream;
import java.io.InputStream;

public class main  {

    public static void main(String[] args)throws Exception {
        String path ="test/sourceCode1.mx";
        InputStream inputStream = new FileInputStream(path);
//        InputStream inputStream = System.in;
        compile(inputStream);
        System.exit(0);

    }


    private static void compile(InputStream input) throws Exception {
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
//        program.getInfo(0);//for debugging

        ScopeBuilder scopeBuilder = new ScopeBuilder();
        Scope topLevelScope = scopeBuilder.BuildScopeTree(program);
//        ScopePrinter scopePrinter = new ScopePrinter();
//        scopePrinter.printScopeTree(topLevelScope);
        typeDefChecker.checkTypeDef(program);

        ///////IR////////
        IRBuilder irBuilder =  new IRBuilder();
        irBuilder.build(program);
        Module module = irBuilder.getProgram();


//        IRPrinter irPrinter  = new IRPrinter();
//        irPrinter.print(module);


        DominatorTree dominatorTree = new DominatorTree(module);
        dominatorTree.run();
//        dominatorTree.print();
        SSAConstructor ssaConstructor = new SSAConstructor(module);
        ssaConstructor.run();
        DeadCodeElim DCE = new DeadCodeElim(module);
        DCE.run();
        SCCP sccp = new SCCP(module);
        sccp.run();
        CFGSimplifier cfgSimplifier = new CFGSimplifier(module);
        cfgSimplifier.run();

        SSADestructor ssaDestructor = new SSADestructor(module);
        ssaDestructor.run();

        IRPrinter irPrinter  = new IRPrinter();
        irPrinter.print(module);


        InstructionSelection instructionSelector = new InstructionSelection(module);
        instructionSelector.run();
        RiscModule riscModule = instructionSelector.getModule();

        Codegen codegen = new Codegen();
        codegen.run(riscModule);

    }

}
