package br.com.gold.sintatico;

import java.util.ArrayList;

import br.com.gold.lexion.*;

public class AnalisadorSintatico {

	private Token token;
	private int pToken;
	private static ArrayList<Token> tokens = new ArrayList<Token>();
	private static ArrayList<String> erros = new ArrayList<String>();
	
	private String escopo = "";
	
	public AnalisadorSintatico(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}
	
	public void leToken() {
		if(token != null && token.getClassToken().equals("ID")){
			SymbolTable.addSymbol(token, escopo);
		}
		token = tokens.get(pToken);
		pToken++;
	}
	
	public Token lookHead() {
		return tokens.get(pToken);
	}
	
	private Token lastToken() {
		return tokens.get(pToken-2);
	}
	
	public void analisar() {
		pToken = 0;
		leToken();
		ListDef();
		if (!token.getClassToken().equals("$")) {
			erros.add("Erro: esperado um final de cadeia.");
		}
	}
	
	public static ArrayList<Token> getTokens(){
		return tokens;
	}
	
	public static ArrayList<String> getErros(){
		return erros;
	}
	
	public static void printTokens() {
		System.out.println("Impressão Tokens");
		for(Token token : tokens) {
			System.out.println(token);
		}
	}
	
	public static void printErroTokens() {
		System.out.println("Impressão Erros:");
		for(String erro : erros) {
			System.out.println(erro);
		}
	}
	
	
	// <ListDef> ::= <Def><ListDef> |
	private void ListDef() {
		if (token.getImage().equals("fx")) {
			Def();
			ListDef();
		}
		
	}
	
	// <Def> ::= ‘fx’  id ‘:‘ <ListParam> ‘=>’  <ListComan> ‘end’ ‘(‘ <Tipo> ‘)’
	private void Def() {
		if (token.getImage().equals("fx")) {
			leToken();
			if (token.getClassToken().equals("ID")) {
				escopo = token.getImage();
				leToken();
				if(token.getImage().equals(":")) {
					leToken();
					ListParam();
					if(token.getImage().equals("=>")) {
						leToken();
						ListComan();
						if(token.getImage().equals("end")) {
							leToken();
							if(token.getImage().equals("(")) {
								leToken();
								Tipo();
								if(token.getImage().equals(")")){
									leToken();
								}else {
									Token lastToken = lastToken();
									erros.add("Erro: esperado o ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
								}
							}else {
								Token lastToken = lastToken();
								erros.add("Erro: esperado o '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
							}
						}else {
							Token lastToken = lastToken();
							erros.add("Erro: esperado o 'end'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
						}
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado o '=>'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado o ':'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um ID. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}
	}

	



	/*
	 * <ListComan> ::=  | <Coman><ListComan>
	 * 
	 */
	private void ListComan() {
		if(token.getClassToken().equals("ID") || token.getImage().equals("if")
				|| token.getImage().equals("when") || token.getImage().equals("read")
				|| token.getImage().equals("show") || token.getImage().equals("back")
				|| token.getImage().equals("=>") || token.getImage().equals("observer")
				) {
		Coman();
		ListComan();
		}
	}

	/*
	 * <Coman> ::= <Decl>
                    	| <Atrib>
                    	| <Se>
                    	| <Laco>
						| <Obser>
                    	| <Ret>
                    	| <Entrada>
                    	| <Saida>
                    	| <Chamada>
                    	| ‘=>’ <ListComan> ‘end’

	 */
	
	private void Coman() {
		
		if (token.getClassToken().equals("ID")) {
			Token lookHead = lookHead();
			// Decl | Atrib | Chamada
			if (lookHead.getImage().equals("=")) {
				Atrib();				
			}else if(lookHead.getImage().equals("(")) {
				Chamada();
			}else {
				Decl();
			}
		}else if (token.getImage().equals("back")) {
			Ret();
		}else if (token.getImage().equals("if")) {
			Se();
		}else if (token.getImage().equals("when")) {
			Laco();
		}else if (token.getImage().equals("read")) {
			Entrada();
		}else if(token.getImage().equals("observe")) {
			Observe();
		}else if (token.getImage().equals("show")) {
			Saida();
		}else if (token.getImage().equals("=>")) {
			leToken();
			ListComan();
			if (token.getImage().equals("end")) {
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado o 'end'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}
		else {
			erros.add("Erro: esperado o 'id'|'back'|'if'|'when'|'read'|'show'. Linha: " + token.getLine() + "Coluna: " + token.getColumn());
		}
	}

	//<Obser> ::= ‘observer’ id ‘=>’ <Caso> ‘end’
	private void Observe() {
		if(token.getImage().equals("observe")) {
			leToken();
			if(token.getClassToken().equals("ID")) {
				leToken();
				if(token.getImage().equals("=>")) {
					Caso();
					leToken();
					if(token.getImage().equals("end")) {
						leToken();
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado o 'end'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado o '=>'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um identificador. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado o 'observe'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
	}

	// <Caso> ::= ‘equals’ <Operando><ListComan> ´break´ | <Caso2>
	private void Caso() {
		if(token.getImage().equals("equals")) {
			Operando();
			ListComan();
			if(token.getImage().equals("break")) {
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado o 'break'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Caso2();
		}
	}

	//<Caso2>::= <Caso> | <Base>
	private void Caso2() {
		if(token.getImage().equals("base")) {
			Base();
		}else {
			Caso();
		}
	}

	//<Base> ::= ‘base’ <Operando>
	private void Base() {
		Operando();
		leToken();
	}

	// <Saida> ::= ‘show’ ‘(‘ <Saida2> ‘)’
	private void Saida() {
		if(token.getImage().equals("show")) {
			leToken();
			if(token.getImage().equals("(")) {
				leToken();
				Saida2();
				if(token.getImage().equals(")")) {
					leToken();
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado o ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado o '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado a palavra reservada 'show'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		
	}

	//“ clt “, <Operando> | <Operando> 
	private void Saida2() {
		if(token.getImage().equals("\"")) {
			leToken();
			if(token.getClassToken().equals("ID")) {
				leToken();
				if(token.getImage().equals("\"")) {
					leToken();
					if(token.getImage().equals(",")) {
						leToken();
						Operando();
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um '\"': " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um identificador.e " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else if(token.getClassToken().equals("ID") ||
				token.getClassToken().equals("ILC") ||
				token.getClassToken().equals("RCL") ||
				token.getClassToken().equals("TCL")
				) {
			Operando();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um operando ou uma string. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
	}

	/*
	 * <Operando> ::= id 
			| cli
			| clr
			| clt
	 */
	private void Operando() {
		if(token.getClassToken().equals("ID")) {
			leToken();
		}else if(token.getClassToken().equals("ILC")) {
			leToken();
		}else if(token.getClassToken().equals("RCL")) {
			leToken();
		}else if(token.getClassToken().equals("TCL")) {
			leToken();
		}
		
	}

	//<Entrada> ::= ‘read’ ‘(‘ id ‘)’
	private void Entrada() {
		if(token.getImage().equals("read")) {
			leToken();
			if(token.getImage().equals("(")) {
				leToken();
				if(token.getClassToken().equals("CLT")) {
					leToken();
					if(token.getImage().equals(")")) {
						leToken();
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado o ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado uma 'string'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado o '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado uma palavra reservada 'read'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		
	}
	


	//<Laco> ::= ‘when’ <ExpRel> <Coman>
	private void Laco() {
		if(token.getImage().equals("when")) {
			leToken();
			ExpRel();
			Coman();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado a palavra reservada 'when'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		
	}

	//<ExpRel> ::= <ExpArit> <Op3> <ExprArit>
	private void ExpRel() {
		ExpArit();
		Op3();
		ExpArit();	
	}

	/*
	 *<Op3> ::= ‘>’
            	| ‘>=‘
            	| ‘<‘
            	| ‘<=‘
            	| ‘==‘
            	| ‘!=‘
	 */
	
	private void Op3() {
		if(token.getImage().equals(">"))
			leToken();
		else if(token.getImage().equals(">="))
			leToken();
		else if(token.getImage().equals("<"))
			leToken();
		else if(token.getImage().equals("<="))
			leToken();
		else if(token.getImage().equals("=="))
			leToken();
		else if(token.getImage().equals("!="))
			leToken();
		else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um dos operadores a seguir: '>' ou '>=' ou '<' ou '<=' ou '==' ou '!=' . Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
	}

	//<ExpArit> ::= <Termo><ExpArit2>
	private void ExpArit() {
		Termo();
		ExpArit2();
		
	}

	//<ExpArit2> ::=  | <Op1> <ExpArit>
	private void ExpArit2() {
		if(token.getClassToken().equals("AOP")) {
			Op1();
			ExpArit();
		}
		
	}

	//<Op1> ::= +
	//		  | -

	private void Op1() {
		if(token.getImage().equals("+")) {
			leToken();
		}else if(token.getImage().equals("-")) {
			leToken();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado o '+' ou '-'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
	}

	//<Termo> ::= <Fator><Termo2>
	private void Termo() {
		Fator();
		Termo2();
		
	}
	
	//<Termo2> ::= | <Op2> <Termo>
	private void Termo2() {
		if(token.getClassToken().equals("OPA")) {
			Op2();
			Termo();
		}
		
	}

	//<Op2> ::= ‘*’
	// | ‘/‘
	private void Op2() {
		if(token.getImage().equals("*") || token.getImage().equals("/")) {
			leToken();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado o '*' ou '/'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
	}

	//<Fator> ::= <Operando> | <Chamada> | ‘(‘ <ExpArit> ‘)’
	private void Fator() {
		if(token.getClassToken().equals("ID")) {
			Token lookHead = lookHead();
			if(lookHead.getImage().equals("(")) {
				Chamada();
			}else {
				Operando();
			}
		}else if(token.getClassToken().equals("CLI") || token.getClassToken().equals("CLR") || token.getClassToken().equals("CLT")){
			Operando();
		}else if(token.getImage().equals("(")) {
			leToken();
			ExpArit();
			if(token.getImage().equals(")")) {
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado o ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado o '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		
	}

	//<Se> ::= ‘if’ <ExpRel> <Coman> <Senao>
	private void Se() {
		if(token.getImage().equals("if")) {
			leToken();
			ExpRel();
			Coman();
			Senao();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado a palavra reservada 'if'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		
	}
	
	//<Senao> ::= ‘or’ <Coman> | 
	private void Senao() {
		if(token.getImage().equals("or")) {
			leToken();
			Coman();
		}
	}

	//<Ret> ::= ‘back’ <Fator>
	private void Ret() {
		if(token.getImage().equals("back")) {
			leToken();
			Fator();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado a palavra reservada 'back'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		
	}

	/* <Decl> ::= <Tipo> <ListId>
	 * 
	 */
	private void Decl() {
		Tipo();
		ListId();
	}

	//<ListId> ::= id <ListId2>
	private void ListId() {
		leToken();
		if(token.getClassToken().equals("ID")) {
			leToken();
			ListId2();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
	}
	
	

	//<ListId2> ::= ‘,’ id <ListId2> | 
	private void ListId2() {
		if(token.getImage().equals(",")) {
			leToken();
			if (token.getClassToken().equals("ID")) {
				leToken();
				ListId2();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}
		
	}

	//<Chamada> ::= id ‘(‘ <Arg> ‘)’ 
	private void Chamada() {
		if(token.getClassToken().equals("ID")) {
			leToken();
			if(token.getImage().equals("(")) {
				leToken();
				Arg();
				if(token.getImage().equals(")")) {
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado o ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado o '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado o 'identificador'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
	}
	
	//<Arg> ::= <ListArg> | <Anon>
	private void Arg() {
		if(token.getImage().equals("{")) {
			leToken();
			Anon();
		}else {
			leToken();
			ListArg();
		}
	}

	// <Anon> ::= ‘{‘ <ListParam> ‘=>’  <ListComan> ‘end’ ‘(‘ <Tipo> ‘)’ ‘}’
	private void Anon() {
		if(token.getImage().equals("{")) {
			leToken();
			ListParam();
			if(token.getImage().equals("=>")) {
				leToken();
				ListComan();
				if(token.getImage().equals("end")) {
					leToken();
					if(token.getImage().equals("(")) {
						leToken();
						Tipo();
						if(token.getImage().equals(")")) {
							leToken();
							if(token.getImage().equals("}")) {
								leToken();
							}else {
								Token lastToken = lastToken();
								erros.add("Erro: esperado o '}'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
							}
						}else {
							Token lastToken = lastToken();
							erros.add("Erro: esperado o ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
						}
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado o '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado o 'end'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado o '=>'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado o '{'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
	}

	//<ListArg> ::=  | <Operando> <ListArg2>
	private void ListArg() {
		if(token.getClassToken().equals("ID")) {
			Operando();
			ListArg2();
		}
		
	}

	//<ListArg2> ::=  | ‘,’ <Operando><ListArg2>  
	private void ListArg2() {
		if(token.getImage().equals(",")) {
			leToken();
			Operando();
			ListArg2();
		}
	}

	//<Atrib> ::= id ‘=‘ <ExpArit>
	private void Atrib() {
		if(token.getClassToken().equals("ID")) {
			leToken();
			if(token.getImage().equals("=")) {
				leToken();
				ExpArit();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado o '='. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado o 'identificador'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
	}

	/*
	 * <Tipo> ::= ‘integer’
            	| ‘real’
            	| ‘text’
            	| ‘none’
            	|  
	 */
	private void Tipo() {
		if(token.getClassToken().equals("RW")) {
			if(token.getClassToken().equals("integer"))
				leToken();
			else if(token.getImage().equals("real"))
				leToken();
			else if(token.getImage().equals("text"))
				leToken();
			else if(token.getImage().equals("none"))
				leToken();
			else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um dos tipos a seguir: 'integer', 'real', 'text' ou 'none'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado uma das palavras reservadas a seguir: 'integer', 'real', 'text' ou 'none " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
	}

	// <ListParam> ::= <Param><ListParam2> |
	private void ListParam() {
		if(token.getClassToken().equals("ID")) {
			Param();
			ListParam2();
		}
	}

	//<Param> ::= <Tipo> id
	private void Param() {
		Tipo();
		if(token.getClassToken().equals("ID")) {
			leToken();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}			
	}

	//<ListParam2> ::=  ‘,’ <Param><ListParam2> | 
	private void ListParam2() {
		if(token.getImage().equals(",")) {
			leToken();
			Param();
			ListParam2();
		}
		
	}


}
