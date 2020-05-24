package br.com.gold.lexion;

public class Token {
	private String image;
	private String classToken;
	private int index;
	private int line;
	private int column;
	
	public Token() {
		super();
	}
	
	public Token(String image, String classToken, int index, int line, int column) {
		this.image = image;
		this.classToken = classToken;
		this.index = index;
		this.line = line;
		this.column = column;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getClassToken() {
		return classToken;
	}

	public void setClassToken(String classToken) {
		this.classToken = classToken;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	
	@Override
	public String toString() {
		return image;
	}

}
