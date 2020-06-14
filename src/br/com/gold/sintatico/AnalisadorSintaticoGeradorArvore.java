package br.com.gold.sintatico;

import java.util.ArrayList;

import br.com.gold.lexion.SymbolTable;
import br.com.gold.lexion.Token;

public class AnalisadorSintaticoGeradorArvore {

	private Token token;
	private int pToken;
	private static ArrayList<Token> tokens = new ArrayList<Token>();
	private static ArrayList<String> erros = new ArrayList<String>();
	private String escopo = "";
	private static No raiz;

	@SuppressWarnings("static-access")
	public AnalisadorSintaticoGeradorArvore(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	public void leToken() {
		if (token != null && token.getClassToken().equals("ID")) {
			SymbolTable.addSymbol(token, escopo);
		}
		
		if(pToken < tokens.size()) {
			token = tokens.get(pToken);
			pToken++;
	
		}
	}

	public Token lookHead() {
		return tokens.get(pToken);
	}

	public void analisar() {
		pToken = 0;
		leToken();
		raiz = ListDef();
		if (!token.getClassToken().equals("$")) {
			erros.add("Erro: esperado um final de cadeia.");
		}
	}

	public Token lastToken() {
		return tokens.get(pToken-2);
	}

	//<ListDef> ::= <Def><ListDef> | 
	private No ListDef() {
		No no = new No(TipoNo.NO_LISTDEF);
		if(token.getImage().equals("fx")) {	
			no.addFilho(Def());
			no.addFilho(ListDef());
		}
		return no;
	}

	//<Def> ::= ‘fx’  id ‘:‘ <ListParam> ‘=>’  <ListComan> ‘end’ ‘(‘ <Tipo> ‘)’
	private No Def() {
		No no = new No(TipoNo.NO_DEF);
		if (token.getImage().equals("fx")) {
			no.addFilho(new No(token));	
			leToken();
			if(token.getClassToken().equals("ID")) {
				escopo = token.getImage();
				no.addFilho(new No(token));
				leToken();
				if (token.getImage().equals(":")) {
					no.addFilho(new No(token));
					leToken();
					no.addFilho(ListParam());
					if(token.getImage().equals("=>")) {
						no.addFilho(new No(token));
						leToken();
						no.addFilho(ListComan());
						if(token.getImage().equals("end")) {
							no.addFilho(new No(token));
							leToken();
							if(token.getImage().equals("(")) {
								no.addFilho(new No(token));
								leToken();
								no.addFilho(Tipo());
								if(token.getImage().equals(")")) {
									no.addFilho(new No(token));
									leToken();
								}else {
									Token lastToken = lastToken();
									erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
								}
							}else {
								Token lastToken = lastToken();
								erros.add("Erro: esperado um '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
							}	
						}else {
							Token lastToken = lastToken();
							erros.add("Erro: esperado um 'end'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
						}
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado um '=>'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um ':'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'fx'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}

	//<ListComan> ::=  | <Coman><ListComan>
	private No ListComan() {
		No no = new No(TipoNo.NO_LISTCOMAN);
		if(token.getClassToken().equals("ID")
				|| token.getImage().equals("if")
				|| token.getImage().equals("when")
				|| token.getImage().equals("back")
				|| token.getImage().equals("observe")
				|| token.getImage().equals("read")
				|| token.getImage().equals("show")
				|| token.getImage().equals("=>")) {
			no.addFilho(Coman());
			no.addFilho(ListComan());
		}
		return no;
	}

	/*
	 * <Coman> ::= <Decl> | <Atrib> | <Se> | <Laco> | <Obser> | <Ret> | <Entrada> |
	 * <Saida> | <Chamada> | ‘=>’ <ListComan> ‘end'
	 */

	private No Coman() {
		No no = new No(TipoNo.NO_COMAN);
		if(token.getClassToken().equals("ID")) {
			Token lookHead = lookHead();
			if (lookHead.getImage().equals("=")) {
				no.addFilho(Atrib());
			}else if(lookHead.getImage().equals("=>")) {
				no.addFilho(Chamada());
			}else {
				no.addFilho(Decl());
			}
		}else if(token.getImage().equals("if"))
			no.addFilho(Se());
		else if(token.getImage().equals("when"))
			no.addFilho(Laco());
		else if(token.getImage().equals("observe"))
			no.addFilho(Observe());
		else if(token.getImage().equals("back"))
			no.addFilho(Ret());
		else if(token.getImage().equals("read"))
			no.addFilho(Entrada());
		else if(token.getImage().equals("show"))
			no.addFilho(Saida());
		else if(token.getImage().equals("=>")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(ListComan());
			if(token.getImage().equals("end")) {
				no.addFilho(new No(token));
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um 'end'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}
		else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'id' | 'back' | 'if' | 'when' | 'read' | 'show' | '=' | '=>'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}

		return no;
	}
	
	
	//<Obser> ::= ‘observer’ id ‘=>’ <Caso> ‘end’
	private No Observe() {
		No no = new No(TipoNo.NO_OBSERVE);
		if(token.getImage().equals("observe")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getClassToken().equals("ID")) {
				no.addFilho(new No(token));
				leToken();
				if(token.getImage().equals("=>")) {
					no.addFilho(new No(token));
					leToken();
					no.addFilho(Caso());
					if(token.getImage().equals("end")) {
						no.addFilho(new No(token));
						leToken();
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado um 'end'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um '=>'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'observe'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		
		return no;
	}
	
	//<Caso> ::= ‘equals’ <Operando> <ListComan> ´stop´<Base>
	private No Caso() {
		No no = new No(TipoNo.NO_CASO);
		
		if(token.getImage().equals("equals")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Operando());
			no.addFilho(ListComan());
			if(token.getImage().equals("stop")) {
				no.addFilho(new No(token));
				leToken();
				no.addFilho(Base());
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um 'stop'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'equals'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		
		return no;
	}
	
	//<Base> ::= ‘base’ <Operando>
	private No Base() {
		No no = new No(TipoNo.NO_BASE);
		
		if(token.getImage().equals("base")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Operando());
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'base'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		
		return no;
	}

	//<Saida> ::= ‘show’ ‘(‘ <Saida2> ‘)’ 
	private No Saida() {
		No no = new No(TipoNo.NO_SAIDA);
		if(token.getImage().equals("show")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getImage().equals("(")) {
				no.addFilho(new No(token));
				leToken();
				no.addFilho(Saida2());
				if(token.getImage().equals(")")) {
					no.addFilho(new No(token));
					leToken();
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'show'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}
	
	//<Saida2> ::= “ clt “, <Operando> | <Operando> 
	private No Saida2() {
		No no = new No(TipoNo.NO_SAIDA2);

		if(token.getClassToken().contentEquals("TCL")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getImage().equals(",")) {
				no.addFilho(new No(token));
				leToken();
				no.addFilho(Operando());
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um ','. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			no.addFilho(Operando());
		}
		
		return no;
		
	}
	/* 
	 * <Operando> ::= id 
			| cli
			| clr
			| clt

	 */

	private No Operando() {
		No no = new No(TipoNo.NO_OPERANDO);
		if(token.getClassToken().equals("ID")
				|| token.getClassToken().equals("ILC")
				|| token.getClassToken().equals("RCL")
				|| token.getClassToken().equals("TCL")) {
			no.addFilho(new No(token));
			leToken();
		}
		return no;
	}

	//<Entrada> ::= ‘read’ ‘(‘ id ‘)’
	private No Entrada() {
		No no = new No(TipoNo.NO_ENTRADA);
		if(token.getImage().equals("read")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getImage().equals("(")) {
				no.addFilho(new No(token));
				leToken();
				//Estava ID modifiquei para CLT
				if(token.getClassToken().equals("ID")) {
					no.addFilho(new No(token));
					leToken();
					if(token.getImage().equals(")")) {
						no.addFilho(new No(token));
						leToken();
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'leia'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}

		return no;
	}

	//<Ret> ::= <Ret> ::= ‘back’ <Fator>
	private No Ret() {
		No no = new No(TipoNo.NO_RET);
		if(token.getImage().equals("back")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Fator());
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'back'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}

	//<Fator> ::= <Operando> | <Chamada> | ‘(‘ <ExpArit> ‘)’
	private No Fator() {
		No no = new No(TipoNo.NO_FATOR);
		if(token.getClassToken().equals("ID")) {
			no.addFilho(Operando());
		}else if(token.getClassToken().equals("ILC") ||
				token.getClassToken().equals("TCL") ||
				token.getClassToken().equals("RCL")) {
			no.addFilho(Operando());
		}else if(token.getImage().equals("=>")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(ExpArit());
			if(token.getImage().equals("end")) {
				no.addFilho(new No(token));
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um 'end'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '=>'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}

	//<ExpArit> ::= <Termo><ExpArit2>
	private No ExpArit() {
		No no = new No(TipoNo.NO_EXPARIT);
		no.addFilho(Termo());
		no.addFilho(ExpArit2());
		return no;
	}

	//<ExpArit2> ::=  | <Op1> <ExpArit>
	private No ExpArit2() {
		No no = new No(TipoNo.NO_EXPARIT2);
		if(token.getImage().equals("+") || token.getImage().equals("-")) {
			no.addFilho(Op1());
			no.addFilho(ExpArit());
		}

		return no;
	}

	/* 
	 * <Op1> ::= ‘+’
            	| ‘-‘
	 */
	private No Op1() {
		No no = new No(TipoNo.NO_OP1);
		if(token.getImage().equals("+") || token.getImage().equals("-"))  {
			no.addFilho(new No(token));
			leToken();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '+' ou '-'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}

	//<Termo> ::= <Fator><Termo2>
	private No Termo() {
		No no = new No(TipoNo.NO_TERMO);
		no.addFilho(Fator());
		no.addFilho(Termo2());
		return no;
	}

	//<Termo2> ::= | <Op2> <Termo>
	private No Termo2() {
		No no = new No(TipoNo.NO_TERMO2);
		
		if(token.getImage().equals("*") || token.getImage().equals("/")) {
			no.addFilho(Op2());
			no.addFilho(Termo());
		}

		return no;
	}

	//<Op2> ::= ‘*’
	// | ‘/‘
	private No Op2() {
		No no = new No(TipoNo.NO_OP2);
		if(token.getImage().equals("*") || token.getImage().equals("/"))  {
			no.addFilho(new No(token));
			leToken();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '*' ou '/'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}

	//<Laco> ::= ‘when’ <ExpRel> <Coman>
	private No Laco() {
		No no = new No(TipoNo.NO_LACO);
		if(token.getImage().equals("when")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(ExpRel());
			no.addFilho(Coman());
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'when'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}

	//<ExpRel> ::= <ExpArit> <Op3> <ExprArit>
	private No ExpRel() {
		No no = new No(TipoNo.NO_EXPREL);
		no.addFilho(ExpArit());
		no.addFilho(Op3());
		no.addFilho(ExpArit());

		return no;
	}

	/*
	 * <Op3> ::= ‘>’
            	| ‘>=‘
            	| ‘<‘
            	| ‘<=‘
            	| ‘==‘
            	| ‘!=‘
	 */
	private No Op3() {
		No no = new No(TipoNo.NO_OP3);
		if(token.getImage().equals(">") ||
				token.getImage().equals(">=") ||
				token.getImage().equals("<") ||
				token.getImage().equals("<=") ||
				token.getImage().equals("==") ||
				token.getImage().contentEquals("!=")) {
			no.addFilho(new No(token));
			leToken();
		}
		return no;
	}

	//<Se> ::= ‘if’ <ExpRel> <Coman> <Senao>
	private No Se() {
		No no = new No(TipoNo.NO_SE);
		if(token.getImage().equals("if")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(ExpRel());
			no.addFilho(Coman());
			no.addFilho(Senao());
		}else {			
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'if'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}

	//<Senao> ::= ‘or’ <Se> | 'or' <Coman> | 
	private No Senao() {
		No no = new No(TipoNo.NO_SENAO);
		if(token.getImage().equals("or")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getImage().equals("if")) {
				no.addFilho(Se());
			}else {
				no.addFilho(Coman());
			}
		}
		
		return no;
	}
	
	//<Decl> ::= <Tipo> <ListId>
	private No Decl() {
		No no = new No(TipoNo.NO_DECL);
			no.addFilho(Tipo());
			no.addFilho(ListId());
			raiz = no;
		return no;
	}

	//<ListId> ::= id <ListId2>
	private No ListId() {
		No no = new No(TipoNo.NO_LISTID);
		no.addFilho(new No(token));
		leToken();
		no.addFilho(ListId2());
		return no;
	}

	//<ListId2> ::= ‘,’ id <ListId2> | 
	private No ListId2() {
		No no = new No(TipoNo.NO_LISTID2);
		if(token.getImage().equals(",")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getClassToken().equals("ID")) {
				no.addFilho(new No(token));
				leToken();
				no.addFilho(ListId2());
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}
		return no;
	}

	//<Chamada> ::= id ‘(‘ <Arg> ‘)’ 
	private No Chamada() {
		No no = new No(TipoNo.NO_CHAMADA);
		no.addFilho(new No(token));
		leToken();
		if(token.getImage().equals("(")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Arg());
			if(token.getImage().equals(")")) {
				no.addFilho(new No(token));
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}
	
	//<Arg> ::= <ListArg> | <Anon>
	private No Arg() {
		No no = new No(TipoNo.NO_ARG);
		if(token.getImage().equals("{")) {
			no.addFilho(Anon());
		}else {
			no.addFilho(ListArg());
		}
		
		return no;
	}
	
	// <Anon> ::= ‘{‘ <ListParam> ‘=>’  <ListComan> ‘end’ ‘(‘ <Tipo> ‘)’ ‘}’
	private No Anon() {
		No no = new No(TipoNo.NO_ANON);
		no.addFilho(new No(token));
		no.addFilho(ListParam());
		
		if(token.getImage().equals("=>")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(ListComan());
			if(token.getImage().equals("end")) {
				no.addFilho(new No(token));
				leToken();
				if(token.getImage().equals("(")) {
					no.addFilho(new No(token));
					leToken();
					no.addFilho(Tipo());
					if(token.getImage().equals(")")) {
						no.addFilho(new No(token));
						leToken();
						if(token.getImage().equals("}")) {
							no.addFilho(new No(token));
							leToken();
						}else {
							Token lastToken = lastToken();
							erros.add("Erro: esperado um '}'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
						}
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um '('. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um 'end'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '=>'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		
		return no;
	}

	//<ListArg> ::=  | <Operando> <ListArg2>
	private No ListArg() {
		No no = new No(TipoNo.NO_LISTARG);
		if (token.getClassToken().equals("ID")) {
			no.addFilho(Operando());
			no.addFilho(ListArg2());
		}
		return no;
	}

	//<ListArg2> ::=  | ‘,’ <Operando><ListArg2>  
	private No ListArg2() {
		No no = new No(TipoNo.NO_LISTARG2);
		if(token.getImage().equals(",")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Operando());
			no.addFilho(ListArg2());
		}
		return no;
	}

	
	//<Atrib> ::= id ‘=‘ <ExpArit>
	private No Atrib() {
		No no = new No(TipoNo.NO_ATRIB);
		no.addFilho(new No(token));
		leToken();
		if(token.getImage().equals("=")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(ExpArit());
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '='. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}

	/* 
	 *<Tipo> ::= ‘integer’
            	| ‘real’
            	| ‘text’
            	| ‘none’
            	| 
	 */
	private No Tipo() {
		No no = new No(TipoNo.NO_TIPO);
		if(token.getClassToken().equals("RW")){
			if(token.getImage().equals("integer") ||
				token.getImage().equals("real") ||
				token.getImage().equals("text") ||
				token.getImage().equals("none")) {
			
				no.addFilho(new No(token));
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado uma das seguintes palavras reservadas 'integer', 'real', 'text' ou 'none'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
			}
			
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado uma 'palavra reservada'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}

	//<ListParam> ::= <Param><ListParam2> |
	private No ListParam() {
		No no = new No(TipoNo.NO_LISTPARAM);
		Token lookHead = lookHead();
		
		if(lookHead.getClassToken().equals("ID")) {
			no.addFilho(Param());
			no.addFilho(ListParam2());
		}
		return no;
	}
	
	//<ListParam2> ::=  ‘,’ <Param><ListParam2> | 
	private No ListParam2() {
		No no = new No(TipoNo.NO_LISTPARAM2);
		if(token.getImage().equals(",")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Param());
			no.addFilho(ListParam2());
		}
		return no;
	}

	//<Param> ::= <Tipo> id
	private No Param() {
		No no = new No(TipoNo.NO_PARAM);
		no.addFilho(Tipo());
		
		if(token.getClassToken().equals("ID")) {
			no.addFilho(new No(token));
			leToken();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLine() + "Coluna: " + lastToken.getColumn());
		}
		return no;
	}

	public static ArrayList<Token> getTokens() {
		return tokens;
	}

	public static ArrayList<String> getErros() {
		return erros;
	}
	
	
	public static No getRaiz() {
		return raiz;
	}

	public static void printTokens() {
		System.out.println("Tokens:");
		for(Token token: tokens) {
			System.out.println(token);
		}
	}
	
	public static void printErros() {
		System.out.println("ERROS:");
		for(String erro : erros) {
			System.out.println(erro);
		}
	}

	public static void mostraNo(No no, String esp) {
		System.out.println(esp + no);
		for(No noFilho : no.getFilhos()) {
			mostraNo(noFilho, esp + "  ");
		}
	}
	
	public static void mostrarArvore() {
		mostraNo(raiz, "  ");
	}
	
	
}
