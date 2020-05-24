package br.com.gold.lexion;

import java.util.ArrayList;

public class Symbol {
	private String image;
	private String scope;
	private String type;
	
	private String nature;
	
	ArrayList<Token> params = new ArrayList<Token>();
	
	public Symbol(String image, String scope) {
		this.image = image;
		this.scope = scope;
	}

	@Override
	public String toString() {
		return "Symbol [image= " + image + ", scope= " + scope + ", type= " + type + ", nature= " + nature + ", params= "
				+ params + "]";
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public ArrayList<Token> getParams() {
		return params;
	}

	public void setParams(ArrayList<Token> params) {
		this.params = params;
	}
}
