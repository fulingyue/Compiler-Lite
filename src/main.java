import FrontEnd.AstNode.*;
import FrontEnd.*;


import FrontEnd.ErrorChecker.SemanticException;
import FrontEnd.Scope.Scope;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import FrontEnd.Antlr.*;
import util.ErrorHandler;
import util.Location;

public class main  {

    public static void main(String[] args)throws Exception {
        String path = "code/Compiler-2020-testcases/sema/basic-package/basic-11.mx";
        InputStream inputStream = new FileInputStream(path);
        try {
            compile(inputStream);
            System.exit(0);
        }
        catch (Error error){
            System.err.println(error.getMessage());
            System.exit(1);
        }
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
//            throw new RuntimeException();
        }


        /////////semantic/////////
        AstBuilder astBuilder = new AstBuilder();
        astBuilder.visit(tree);
        if(astBuilder.getError().getError_cnt() > 0){
//            throw new Exception();
            System.exit(astBuilder.getError().getError_cnt());
        }
        ProgramNode program = astBuilder.getProgram();
        ParentLinker parentLinker  = new ParentLinker();
        parentLinker.linkParent(program);



//        program.getInfo(0);//for debugging
//
        ScopeBuilder scopeBuilder = new ScopeBuilder();
        Scope topLevelScope = scopeBuilder.BuildScopeTree(program);
//        ScopePrinter scopePrinter = new ScopePrinter();
//        scopePrinter.printScopeTree(topLevelScope);
        typeDefChecker.checkTypeDef(program);

    }

}
