package br.com.gold.lexion;

import java.util.ArrayList;

public class SymbolTable {
	private static ArrayList<Symbol> symbols = new ArrayList<Symbol>();
	
	public static void addSymbol(Token token, String scope) {
		Symbol simbolFound = findSymbol(token.getImage(), scope);
		
		if(simbolFound != null)
			token.setIndex(symbols.indexOf(simbolFound));
		else {
			simbolFound = new Symbol(token.getImage(), scope);
			symbols.add(simbolFound);
			token.setIndex(symbols.indexOf(simbolFound));
		}
	}
	
	public static Symbol findSymbol(String image, String scope) {
		for(Symbol symbol: symbols) {
			if(symbol.getImage().equals(image) && symbol.getScope().equals(scope)) {
				return symbol;
			}
		}
		
		return null;
	}
	
	public static String getType(Token id) {
		
		if(id.getClassToken().equals("ID"))
			return symbols.get(id.getIndex()).getType();
		else if (id.getClassToken().equals("ICL"))
			return "integer";
		else if (id.getClassToken().equals("RCL"))
			return "real";
		else if (id.getClassToken().equals("TCL"))
			return "text";
		
		return null;
	}
	
	public static void setParam(Token id) {
		String scope = symbols.get(id.getIndex()).getScope();
		findSymbol(scope,scope).getParams().add(id);
	}
	
	public static void setType(Token token, String type) {
		symbols.get(token.getIndex()).setType(type);
	}
	
	public static void setTypeDef(Token token, String type) {
		symbols.get(token.getIndex()).setNature("param");
	}
	
	public static ArrayList<Token> getParamDef(Token token){
		return symbols.get(token.getIndex()).getParams();
	}
	
	public static void printSymbolsTable() {
		for(Symbol symbol: symbols) {
			System.out.println(symbol);
		}
	}
}
