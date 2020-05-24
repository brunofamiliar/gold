package br.com.gold.lexion;

import java.util.Arrays;
import java.util.List;

public class ReservedWord {
	private static final List<String> rw = initReservedWord();
	private static final List<String> de = initDelimiter();
	private static final List<String> aop = initArithmeticOperator();
	private static final List<String> cop = initComparativeOperator();
	private static final List<String> ao = initAssignmentOperator();
	
	private static List<String> initReservedWord() {
		return Arrays.asList("fx",
							 "integer",
							 "real",
							 "text",
							 "none",
							 "if",
							 "or",
							 "when",
							 "back",
							 "read",
							 "show",
							 "observe",
							 "equals",
							 "base",
							 "stop"
							);
	}
	
	private static List<String> initDelimiter(){
		return Arrays.asList("(", ")", "=>", "end", ",", ":", "\"");
	}
	
	private static List<String> initArithmeticOperator(){
		return Arrays.asList("+", "-", "*", "/", "%");
	}
	
	private static List<String> initComparativeOperator(){
		return Arrays.asList("<=", ">=","<",">", "==", "!=");
	}
	
	private static List<String> initAssignmentOperator(){
		return Arrays.asList("+=", "*=", "/=", "=");
	}
	
	public static boolean isReservedWord(String image){
		return rw.contains(image);
	}
	
	public static boolean isDelimiter(String image){
		return de.contains(image);
	}
	
	public static boolean isArithmeticOperator(String image){
		return aop.contains(image);
	}
	
	public static boolean isComparativeOperator(String image){
		return cop.contains(image);
	}
	
	public static boolean isAssignmentOperator(String image){
		return ao.contains(image);
	}
	
	public static List<String> getPr() {
		return rw;
	}

	public static List<String> getDe() {
		return de;
	}

	public static List<String> getOpa() {
		return aop;
	}

	public static List<String> getOpc() {
		return cop;
	}

	public static List<String> getOa() {
		return ao;
	}
}
