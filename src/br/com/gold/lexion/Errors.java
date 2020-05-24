package br.com.gold.lexion;

public class Errors {
	String image;
	int line;
	int column;
	
	public Errors( String image, int line, int column) {
		super();
		this.image = image;
		this.line = line;
		this.column = column;
	}
	
	@Override
	public String toString() {
		return "Errors [image= " + image + " line= " + line + " column = " + column + "]";
	}
}
