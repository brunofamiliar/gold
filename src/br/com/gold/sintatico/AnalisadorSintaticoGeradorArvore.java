package br.com.gold.sintatico;

import java.util.ArrayList;

import br.com.l7p.lexico.TabelaSimbolos;
import br.com.l7p.lexico.Token;

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
		if (token != null && token.getClasse().equals("ID")) {
			TabelaSimbolos.addSimbolo(token, escopo);
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
		if (!token.getClasse().equals("$")) {
			erros.add("Erro: esperado um final de cadeia.");
		}
	}

	public Token lastToken() {
		return tokens.get(pToken-2);
	}

	//<ListDef> ::= <Def><ListDef> | 
	private No ListDef() {
		No no = new No(TipoNo.NO_LISTDEF);
		//if (!token.getImagem().equals(" ")) {
		if(token.getImagem().equals("def")) {	
			no.addFilho(Def());
			no.addFilho(ListDef());
		}
		return no;
	}

	//<Def> ::= ‘def’  id ‘(‘ <ListParam> ‘)’  ‘:’  <Tipo> ‘{‘ <ListComan> ‘}’
	private No Def() {
		No no = new No(TipoNo.NO_DEF);
		if (token.getImagem().equals("def")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getClasse().equals("ID")) {
				escopo = token.getImagem();
				no.addFilho(new No(token));
				leToken();
				if (token.getImagem().equals("(")) {
					no.addFilho(new No(token));
					leToken();
					no.addFilho(ListParam());
					if(token.getImagem().equals(")")) {
						no.addFilho(new No(token));
						leToken();
						if(token.getImagem().equals(":")) {
							no.addFilho(new No(token));
							leToken();
							no.addFilho(Tipo());
							if(token.getImagem().equals("{")) {
								no.addFilho(new No(token));
								leToken();
								no.addFilho(ListComan());
								if(token.getImagem().equals("}")) {
									no.addFilho(new No(token));
									leToken();
								}else {
									Token lastToken = lastToken();
									erros.add("Erro: esperado um '}'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
								}
							}else {
								Token lastToken = lastToken();
								erros.add("Erro: esperado um '{'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
							}
						}else {
							Token lastToken = lastToken();
							erros.add("Erro: esperado um ':'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
						}
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um '('. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'def'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
		}
		return no;
	}

	//<ListComan> ::=  | <Coman><ListComan>
	private No ListComan() {
		No no = new No(TipoNo.NO_LISTCOMAN);
		if(token.getClasse().equals("ID")
				|| token.getImagem().equals("se")
				//Arrumei aqui estava equanto ou invés de enquanto
				|| token.getImagem().equals("enquanto")
				|| token.getImagem().equals("retorna")
				|| token.getImagem().equals("leia")
				|| token.getImagem().equals("escreva")
				|| token.getImagem().equals("{")) {
			no.addFilho(Coman());
			no.addFilho(ListComan());
			//leToken();
		}
		return no;
	}

	//<Coman> ::= <Decl>
	//| <Atrib>
	//| <Se>
	//| <Laco>
	//| <Ret>
	//| <Entrada>
	//| <Saida>
	//| <Chamada> ‘;’
	//| ‘{’ <ListComan> ‘}’

	private No Coman() {
		No no = new No(TipoNo.NO_COMAN);
		if(token.getClasse().equals("ID")) {
			Token lookHead = lookHead();
			if (lookHead.getImagem().equals("=")) {
				no.addFilho(Atrib());
			}else if(lookHead.getImagem().equals("(")) {
				no.addFilho(Chamada());
				if(token.getImagem().equals(";")) {
					no.addFilho(new No(token));
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um ';'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
				}
			}else {
				no.addFilho(Decl());
			}
		}else if(token.getImagem().equals("se"))
			no.addFilho(Se());
		else if(token.getImagem().equals("enquanto"))
			no.addFilho(Laco());
		else if(token.getImagem().equals("retorna"))
			no.addFilho(Ret());
		else if(token.getImagem().equals("leia"))
			no.addFilho(Entrada());
		else if(token.getImagem().equals("escreva"))
			no.addFilho(Saida());
		else if(token.getImagem().equals("{")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(ListComan());
			if(token.getImagem().equals("}")) {
				no.addFilho(new No(token));
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um '}'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}
		else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'id' | 'retorna' | 'se' | 'equanto' | 'leia' | 'escreva' | '=' | '('. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
		}

		return no;
	}

	//<Saida> ::= ‘escreva’ ‘(‘ <Operando> ‘)’ ‘;’ 
	private No Saida() {
		No no = new No(TipoNo.NO_SAIDA);
		if(token.getImagem().equals("escreva")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getImagem().equals("(")) {
				no.addFilho(new No(token));
				leToken();
				no.addFilho(Operando());
				if(token.getImagem().equals(")")) {
					no.addFilho(new No(token));
					leToken();
					if(token.getImagem().equals(";")) {
						no.addFilho(new No(token));
						leToken();
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado um ';'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um '('. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'escreva'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
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
		if(token.getClasse().equals("ID")
				|| token.getClasse().equals("CLI")
				|| token.getClasse().equals("CLR")
				|| token.getClasse().equals("CLT")) {
			no.addFilho(new No(token));
			leToken();
		}
		return no;
	}

	//<Entrada> ::= ‘leia’ ‘(‘ id ‘)’ ‘;’ 
	private No Entrada() {
		No no = new No(TipoNo.NO_ENTRADA);
		if(token.getImagem().equals("leia")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getImagem().equals("(")) {
				no.addFilho(new No(token));
				leToken();
				//Estava ID modifiquei para CLT
				if(token.getClasse().equals("CLT")) {
					no.addFilho(new No(token));
					leToken();
					if(token.getImagem().equals(")")) {
						no.addFilho(new No(token));
						leToken();
						if(token.getImagem().equals(";")) {
							no.addFilho(new No(token));
							leToken();
						}else {
							Token lastToken = lastToken();
							erros.add("Erro: esperado um ';'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
						}
					}else {
						Token lastToken = lastToken();
						erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
					}
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um '('. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'leia'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
		}

		return no;
	}

	//<Ret> ::= ‘retorna’ <Fator> ‘;’
	private No Ret() {
		No no = new No(TipoNo.NO_RET);
		if(token.getImagem().equals("retorna")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Fator());
			if(token.getImagem().equals(";")) {
				no.addFilho(new No(token));
				leToken();
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'retorna'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
		}
		return no;
	}

	//<Fator> ::= <Operando> | <Chamada> | ‘(‘ <ExpArit> ‘)’
	private No Fator() {
		No no = new No(TipoNo.NO_FATOR);
		if(token.getClasse().equals("ID")) {
			Token lookHead = lookHead();
			if (lookHead.getImagem().equals("(")) {
				no.addFilho(Chamada());
			}else {
				no.addFilho(Operando());
			}
		}else if(token.getClasse().equals("CLI") ||
				token.getClasse().equals("CLT") ||
				token.getClasse().equals("CLR")) {
			no.addFilho(Operando());
		}else if(token.getImagem().equals("(")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(ExpArit());
			if(token.getImagem().equals(")")) {
				no.addFilho(new No(token));
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '('. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
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
		if(token.getImagem().equals("+") || token.getImagem().equals("-")) {
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
		if(token.getImagem().equals("+") || token.getImagem().equals("-"))  {
			no.addFilho(new No(token));
			leToken();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '+' ou '-'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
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

		if(token.getImagem().equals("*") || token.getImagem().equals("/")) {
			no.addFilho(Op2());
			no.addFilho(Termo());
		}

		return no;
	}

	//<Op2> ::= ‘* | ‘/‘
	private No Op2() {
		No no = new No(TipoNo.NO_OP2);
		if(token.getImagem().equals("*") || token.getImagem().equals("/"))  {
			no.addFilho(new No(token));
			leToken();
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '*' ou '/'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
		}
		return no;
	}

	//<Laco> ::= ‘enquanto’  ‘(‘  <ExpRel> ’)’ <Coman>
	private No Laco() {
		No no = new No(TipoNo.NO_LACO);
		//Arrumei aqui estava escrito equanto ou invés de enquanto
		if(token.getImagem().equals("enquanto")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getImagem().equals("(")) {
				no.addFilho(new No(token));
				leToken();
				no.addFilho(ExpRel());
				if(token.getImagem().equals(")")) {
					no.addFilho(new No(token));
					leToken();
					no.addFilho(Coman());
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um '(. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'enquanto'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
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
		if(token.getImagem().equals(">") ||
				token.getImagem().equals(">=") ||
				token.getImagem().equals("<") ||
				token.getImagem().equals("<=") ||
				token.getImagem().equals("==") ||
				token.getImagem().contentEquals("!=")) {
			no.addFilho(new No(token));
			leToken();
		}
		return no;
	}

	//<Se> ::= ‘se’ ‘(‘ <ExpRel> ‘)’ <Coman> <Senao>
	private No Se() {
		No no = new No(TipoNo.NO_SE);
		if(token.getImagem().equals("se")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getImagem().equals("(")) {
				no.addFilho(new No(token));
				leToken();
				no.addFilho(ExpRel());
				if(token.getImagem().equals(")")) {
					no.addFilho(new No(token));
					leToken();
					no.addFilho(Coman());
					no.addFilho(Senao());
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um '('. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}else {			
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'se'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
		}
		return no;
	}

	//<Senao> ::= ‘senao’ <Coman> | 
	private No Senao() {
		No no = new No(TipoNo.NO_SENAO);
		if(token.getImagem().equals("senao")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Coman());
		}
		return no;
	}
	
	//<Decl> ::= <ListId> ‘:’ <Tipo> ‘;’
	private No Decl() {
		No no = new No(TipoNo.NO_DECL);
		no.addFilho(ListId());
		raiz = no;
		if(token.getImagem().equals(":")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Tipo());
			if(token.getImagem().equals(";")) {
				no.addFilho(new No(token));
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um ';'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um ':'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
		}
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
		if(token.getImagem().equals(",")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getClasse().equals("ID")) {
				no.addFilho(new No(token));
				leToken();
				no.addFilho(ListId2());
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}
		return no;
	}

	//<Chamada> ::= id ‘(‘ <ListArg> ‘)’ ‘;’
	private No Chamada() {
		No no = new No(TipoNo.NO_CHAMADA);
		no.addFilho(new No(token));
		leToken();
		if(token.getImagem().equals("(")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(ListArg());
			if(token.getImagem().equals(")")) {
				no.addFilho(new No(token));
				leToken();
				if(token.getImagem().equals(";")) {
					no.addFilho(new No(token));
					leToken();
				}else {
					Token lastToken = lastToken();
					erros.add("Erro: esperado um ';'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());				
				}
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um ')'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '('. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
		}
		return no;
	}

	//<ListArg> ::=  | <Operando> <ListArg2>
	private No ListArg() {
		No no = new No(TipoNo.NO_LISTARG);
		if (token.getClasse().equals("ID")) {
			no.addFilho(Operando());
			no.addFilho(ListArg2());
		}
		return no;
	}

	//<ListArg2> ::=  | ‘,’ <Operando><ListArg2> 
	private No ListArg2() {
		No no = new No(TipoNo.NO_LISTARG2);
		if(token.getImagem().equals(",")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Operando());
			no.addFilho(ListArg2());
		}
		return no;
	}

	
	//<Atrib> ::= id ‘=‘ <ExpArit> ‘;’
	private No Atrib() {
		No no = new No(TipoNo.NO_ATRIB);
		no.addFilho(new No(token));
		leToken();
		if(token.getImagem().equals("=")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(ExpArit());
			if(token.getImagem().equals(";")) {
				no.addFilho(new No(token));
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um ';'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um '='. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
		}
		return no;
	}

	/* 
	 * <Tipo> ::= ‘inteiro’
		| ‘real’
		| ‘texto’
		| ‘nada’

	 */
	private No Tipo() {
		No no = new No(TipoNo.NO_TIPO);
		if(token.getClasse().equals("PR")){
			if(token.getImagem().equals("inteiro") ||
				token.getImagem().equals("real") ||
				token.getImagem().equals("texto") ||
				token.getImagem().equals("nada")) {
			
				no.addFilho(new No(token));
				leToken();
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado uma das seguintes palavras reservadas 'inteiro', 'real', 'texto' ou 'nada. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
			
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado uma 'palavra reservada'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
		}
		return no;
	}

	//<ListParam> ::= <Param><ListParam2> |
	private No ListParam() {
		No no = new No(TipoNo.NO_LISTPARAM);
		if(token.getClasse().equals("ID")) {
			no.addFilho(Param());
			no.addFilho(ListParam2());
		}
		return no;
	}
	
	//<ListParam2> ::=  ‘,’ <Param><ListParam2> | 
	private No ListParam2() {
		No no = new No(TipoNo.NO_LISTPARAM2);
		if(token.getImagem().equals(",")) {
			no.addFilho(new No(token));
			leToken();
			no.addFilho(Param());
			no.addFilho(ListParam2());
		}
		return no;
	}

	//<Param> ::= id ‘:’ <Tipo>
	private No Param() {
		No no = new No(TipoNo.NO_PARAM);
		if(token.getClasse().equals("ID")) {
			no.addFilho(new No(token));
			leToken();
			if(token.getImagem().equals(":")) {
				no.addFilho(new No(token));
				leToken();
				no.addFilho(Tipo());
			}else {
				Token lastToken = lastToken();
				erros.add("Erro: esperado um ':'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
			}
		}else {
			Token lastToken = lastToken();
			erros.add("Erro: esperado um 'identificador'. Linha: " + lastToken.getLinha() + "Coluna: " + lastToken.getColuna());
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
