package br.com.gold.utils;

public interface Rule {
	boolean evaluate(Expression expression);
	Result getResult();
}
