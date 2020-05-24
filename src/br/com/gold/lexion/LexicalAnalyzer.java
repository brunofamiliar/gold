package br.com.gold.lexion;

import java.util.ArrayList;
import java.util.List;

import br.com.gold.utils.*;

public class LexicalAnalyzer {
	private static final List<Token> tokens = new ArrayList<Token>();
	private static final List<Errors> errors = new ArrayList<Errors>();
	
	public static enum Symbols {
		RW, DE, AOP, COP, AO, ILC, RCL, TCL, ID, $
	}
	
	public static void analyze(String image, int line, int column) {
		List<Expression> expressions = new ArrayList<>(
			List.of(
					new Expression(ReservedWord.isReservedWord(image)),
					new Expression(ReservedWord.isDelimiter(image)),
					new Expression(ReservedWord.isArithmeticOperator(image)),
					new Expression(ReservedWord.isComparativeOperator(image)),
					new Expression(ReservedWord.isAssignmentOperator(image)),
					new Expression(TokenMatch.integerLiteralConstant(image)),
					new Expression(TokenMatch.realLiteralConstant(image)),
					new Expression(TokenMatch.identifierLiteralConstant(image))
					)
		);
		
		List<Operation> operations = new ArrayList<>(
			List.of(
					new TokensAddOperation(tokens, image, Symbols.RW.toString(), line, column),
					new TokensAddOperation(tokens, image, Symbols.DE.toString(), line, column),
					new TokensAddOperation(tokens, image, Symbols.AOP.toString(), line, column),
					new TokensAddOperation(tokens, image, Symbols.COP.toString(), line, column),
					new TokensAddOperation(tokens, image, Symbols.AO.toString(), line, column),
					new TokensAddOperation(tokens, image, Symbols.ILC.toString(), line, column),
					new TokensAddOperation(tokens, image, Symbols.RCL.toString(), line, column),
					new TokensAddOperation(tokens, image, Symbols.ID.toString(), line, column)
					)
			);
		
		RuleEngine engine =  new RuleEngine
							.RuleEngineBuilder()
							.withExpressionsAndOperations(expressions, operations)
							.defaultOperation(new ErrorsAddOperation(errors, image, line, column))
							.build();
		
		engine.process().action();
		
	}
	
	public static void addCLT(String image, int line, int column) {
		tokens.add(new Token(image, Symbols.TCL.toString(), -1, line, column));
	}
	
	public static void EndOfChain() {
		tokens.add(new Token("$", Symbols.$.toString(), -1, -1, -1));
	}
	
	public static void printTokens() {
		System.out.println("Tokens");
		
		for(Token tok: tokens) {
			System.out.println(tok.getClassToken()+ ": " + tok);
		}
	}
	
	public static void printErrors() {
		System.out.println("Errors");
		
		for(Errors error: errors) {
			System.out.println(error);
		}
	}
	
	public static ArrayList<Token> getTokens(){
		return (ArrayList<Token>) tokens;
	}
	
	public static ArrayList<Errors> getErrors(){
		return (ArrayList<Errors>) errors;
	}


}
