package br.com.gold.utils;

public class TokenMatch {
	
	public static boolean integerLiteralConstant(String image) {
		return image.matches("\\d\\d*");
	}
	
	public static boolean realLiteralConstant(String image) {
		return image.matches("\\d\\d*\\.\\d\\d*");
	}
	
	public static boolean identifierLiteralConstant(String image) {
		return image.matches("\\p{Alpha}\\p{Alnum}*");
	}
	
	public static String replace(String line) {
		line = line.replaceAll("("
				+ "^\\p{Alpha}fx^\\p{Alpha}|^\\p{Alpha}integer^\\p{Alpha}|^\\p{Alpha}real^\\p{Alpha}|"
				+ "^\\p{Alpha}text^\\p{Alpha}|^\\p{Alpha}none^\\p{Alpha}|^\\p{Alpha}or^\\p{Alpha}|"
				+ "^\\p{Alpha}if^\\p{Alpha}|^\\p{Alpha}when^\\p{Alpha}|^\\p{Alpha}back^\\p{Alpha}|"
				+ "^\\p{Alpha}read^\\p{Alpha}|^\\p{Alpha}show^\\p{Alpha}|^\\p{Alpha}observe^\\p{Alpha}|"
				+ "^\\p{Alpha}equals^\\p{Alpha}|^\\p{Alpha}stop^\\p{Alpha}|^\\p{Alpha}base^\\p{Alpha}|"
				+ "^\\p{Alpha}end^\\p{Alpha}|"
				+ "\\(|\\)|\\{|\\}|\\,|\\;|\\:|"
				+ "\\+|\\-|\\*|\\/|\\%|\\=>|\\#|"
				+ "\\<=|\\>=|\\==|\\!=|\\<|\\>|"
				+ "\\+=|\\-=|\\*=|\\/=|\\\"|\\="
				+ ")", " $1 ");
		line = line.replace("  ", " ");
		
		return line;
	}
}
