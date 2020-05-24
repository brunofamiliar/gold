package br.com.gold.sintatico;

import java.util.ArrayList;
import java.util.List;

import br.com.gold.lexion.Token;

public class No {

	private No pai;
	private List<No> filhos = new ArrayList<No>();
	private TipoNo tipo;
	private Token token;
	
	public No(TipoNo tipo) {
		this.tipo = tipo;
	}
	
	public No(Token token) {
		this.token = token;

		tipo = TipoNo.NO_TOKEN;
	}
	
	public void addFilho(No filho) {
		if(filho != null) {
			filhos.add(filho);
			filho.setPai(this);
		}
	}


	public No getFilho(int pos) {
		return filhos.get(pos);
	}
	
	private void setPai(No pai) {
		this.pai = pai;
		
	}

	public List<No> getFilhos() {
		return filhos;
	}

	public void setFilhos(List<No> filhos) {
		this.filhos = filhos;
	}

	public TipoNo getTipo() {
		return tipo;
	}

	public void setTipo(TipoNo tipo) {
		this.tipo = tipo;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public No getPai() {
		return pai;
	}

	@Override
	public String toString() {
		if (tipo == TipoNo.NO_TOKEN) {
			return String.valueOf(token);
		}else {
			return tipo.toString();
		}
	}
	
	

}
