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
import util.Location;

public class main  {

    public static void main(String[] args)throws Exception {
        String path = "code/basic-38.mx";
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
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MxParser parser = new MxParser(tokens);
        TypeDefChecker typeDefChecker = new TypeDefChecker();

        ParseTree tree = parser.program();
        AstBuilder astBuilder = new AstBuilder();
        astBuilder.visit(tree);
        LinkedList<Pair<Location,String>> error = astBuilder.getError();
        if(error.size() != 0)
            throw new SemanticException(error.get(0).getKey(),error.get(0).getValue());
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
