import FrontEnd.AstNode.*;
import FrontEnd.*;


import FrontEnd.ErrorChecker.SemanticException;
import FrontEnd.Scope.Scope;
import javafx.util.Pair;
import org.antlr.v4.runtime.ANTLRInputStream;
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
        String path = "code/basic-62.mx";
        InputStream inputStream = new FileInputStream(path);
        try {
            compile(inputStream);
        }
        catch (Error error){
            System.err.println(error.getMessage());
            System.exit(1);
        }
    }


    private static void compile(InputStream input) throws Exception {

        ANTLRInputStream antlrInputStream =  new ANTLRInputStream(input);
        MxLexer lexer = new MxLexer(antlrInputStream);
        ErrorHandler errorHandler = new ErrorHandler();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MxParser parser = new MxParser(tokens);
        TypeDefChecker typeDefChecker = new TypeDefChecker();
        //////init/////
        lexer.removeErrorListeners();
        lexer.addErrorListener(new MxErrorListener(errorHandler));
        parser.removeErrorListeners();
        parser.addErrorListener(new MxErrorListener(errorHandler));
        if(errorHandler.getError_cnt() > 0){
            throw new RuntimeException();
        }
        ////run/////////
        ParseTree tree = parser.program();
        AstBuilder astBuilder = new AstBuilder();
        astBuilder.visit(tree);
        if(astBuilder.getError().getError_cnt() > 0){
            throw new Exception();
        }
        ProgramNode program = astBuilder.getProgram();

        program.getInfo(0);//for debugging
//
        ScopeBuilder scopeBuilder = new ScopeBuilder();
        Scope topLevelScope = scopeBuilder.BuildScopeTree(program);
        ScopePrinter scopePrinter = new ScopePrinter();
        scopePrinter.printScopeTree(topLevelScope);
        typeDefChecker.checkTypeDef(program);

    }

}
