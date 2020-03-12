// Generated from /Users/fulingyue/IdeaProjects/Java_test/src/FrontEnd/Antlr/Mx.g4 by ANTLR 4.8
package FrontEnd.Antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MxParser}.
 */
public interface MxListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MxParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MxParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MxParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#classDefinition}.
	 * @param ctx the parse tree
	 */
	void enterClassDefinition(MxParser.ClassDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#classDefinition}.
	 * @param ctx the parse tree
	 */
	void exitClassDefinition(MxParser.ClassDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#memberVariable}.
	 * @param ctx the parse tree
	 */
	void enterMemberVariable(MxParser.MemberVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#memberVariable}.
	 * @param ctx the parse tree
	 */
	void exitMemberVariable(MxParser.MemberVariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#constructionFunctionDefinition}.
	 * @param ctx the parse tree
	 */
	void enterConstructionFunctionDefinition(MxParser.ConstructionFunctionDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#constructionFunctionDefinition}.
	 * @param ctx the parse tree
	 */
	void exitConstructionFunctionDefinition(MxParser.ConstructionFunctionDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#functionDefinition}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDefinition(MxParser.FunctionDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#functionDefinition}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDefinition(MxParser.FunctionDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterList(MxParser.FormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterList(MxParser.FormalParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter(MxParser.FormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter(MxParser.FormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MxParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MxParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#statementList}.
	 * @param ctx the parse tree
	 */
	void enterStatementList(MxParser.StatementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#statementList}.
	 * @param ctx the parse tree
	 */
	void exitStatementList(MxParser.StatementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#variableDefinition}.
	 * @param ctx the parse tree
	 */
	void enterVariableDefinition(MxParser.VariableDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#variableDefinition}.
	 * @param ctx the parse tree
	 */
	void exitVariableDefinition(MxParser.VariableDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#variableType}.
	 * @param ctx the parse tree
	 */
	void enterVariableType(MxParser.VariableTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#variableType}.
	 * @param ctx the parse tree
	 */
	void exitVariableType(MxParser.VariableTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#baseType}.
	 * @param ctx the parse tree
	 */
	void enterBaseType(MxParser.BaseTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#baseType}.
	 * @param ctx the parse tree
	 */
	void exitBaseType(MxParser.BaseTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#variableDeclarExprs}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarExprs(MxParser.VariableDeclarExprsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#variableDeclarExprs}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarExprs(MxParser.VariableDeclarExprsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#variableDeclarExpr}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarExpr(MxParser.VariableDeclarExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#variableDeclarExpr}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarExpr(MxParser.VariableDeclarExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStat(MxParser.ExpressionStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStat(MxParser.ExpressionStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStat(MxParser.IfStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStat(MxParser.IfStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForStat(MxParser.ForStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForStat(MxParser.ForStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStat(MxParser.WhileStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStat(MxParser.WhileStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code returnStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStat(MxParser.ReturnStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStat(MxParser.ReturnStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStat(MxParser.BreakStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStat(MxParser.BreakStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continueStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStat(MxParser.ContinueStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continueStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStat(MxParser.ContinueStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code emptyStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterEmptyStat(MxParser.EmptyStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code emptyStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitEmptyStat(MxParser.EmptyStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code definitionStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterDefinitionStat(MxParser.DefinitionStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code definitionStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitDefinitionStat(MxParser.DefinitionStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blockStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStat(MxParser.BlockStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blockStat}
	 * labeled alternative in {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStat(MxParser.BlockStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpr(MxParser.NewExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpr(MxParser.NewExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterThisExpr(MxParser.ThisExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitThisExpr(MxParser.ThisExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(MxParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(MxParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code primaryExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(MxParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code primaryExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(MxParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code indexAccessExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIndexAccessExpr(MxParser.IndexAccessExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code indexAccessExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIndexAccessExpr(MxParser.IndexAccessExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionCallExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallExpr(MxParser.FunctionCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionCallExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallExpr(MxParser.FunctionCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code memberAccessExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemberAccessExpr(MxParser.MemberAccessExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code memberAccessExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemberAccessExpr(MxParser.MemberAccessExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpr(MxParser.BinaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpr(MxParser.BinaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parensExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterParensExpr(MxParser.ParensExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parensExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitParensExpr(MxParser.ParensExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code identifierExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierExpr(MxParser.IdentifierExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code identifierExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierExpr(MxParser.IdentifierExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code errorCreator}
	 * labeled alternative in {@link MxParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterErrorCreator(MxParser.ErrorCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code errorCreator}
	 * labeled alternative in {@link MxParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitErrorCreator(MxParser.ErrorCreatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code correctCreator}
	 * labeled alternative in {@link MxParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCorrectCreator(MxParser.CorrectCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code correctCreator}
	 * labeled alternative in {@link MxParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCorrectCreator(MxParser.CorrectCreatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#actualParameterList}.
	 * @param ctx the parse tree
	 */
	void enterActualParameterList(MxParser.ActualParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#actualParameterList}.
	 * @param ctx the parse tree
	 */
	void exitActualParameterList(MxParser.ActualParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(MxParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(MxParser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(MxParser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(MxParser.ClassTypeContext ctx);
}