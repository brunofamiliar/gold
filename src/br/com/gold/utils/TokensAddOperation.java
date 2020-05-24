
package br.com.gold.utils;

import br.com.gold.lexion.*;
import br.com.gold.lexion.LexicalAnalyzer.Symbols;

import java.util.ArrayList;
import java.util.List;



public class TokensAddOperation implements Operation {
	private List<Token> tokens;
	int line, column;
	String image, classToken;
	
	public TokensAddOperation(List<Token> tokens, String image, String classToken, int line, int column) {
		this.tokens = tokens;
		this.classToken = classToken;
		this.image = image;
		this.line = line;
		this.column = column;
	}
	
	public void execute() {
		tokens.add(new Token(image, classToken, -1, line, column));
	}
	
	public Result getResult() {
		return new Result(true);
	}

}
