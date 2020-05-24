package br.com.gold.utils;

import java.util.List;

import br.com.gold.lexion.Errors;

public class ErrorsAddOperation implements Operation {
	private List<Errors> errors;
	String image;
	int line, column;
	
	public ErrorsAddOperation(List<Errors> errors, String image, int line, int column) {
		this.errors = errors;
		this.line = line;
		this.column = column;
		this.image = image;
	}
	
	public void execute() {
		errors.add(new Errors(image, line, column));
	}
	
	public Result getResult() {
		return new Result(true);
	}
}
