package br.com.gold;

import java.io.BufferedReader;

import br.com.gold.lexion.LexicalAnalyzer;
import br.com.gold.lexion.WordClassifier;
import br.com.gold.utils.*;
import br.com.gold.sintatico.AnalisadorSintaticoGeradorArvore;
import br.com.gold.semantico.AnalisadorSemantico;

public class Main {
	public static void main(String[] args) {
		lexion();
		sintatico();
		semantico();
	}
	
	private static void lexion() {
		BufferedReader text = LoaderInput.load();
		WordClassifier.toRank(text);
		
		System.out.println("### ANALISADOR SINTÁTICO ###");
		
		if(!LexicalAnalyzer.getErrors().isEmpty()) {
			System.out.println("####  Analisador Léxico com os seguintes erros: ####");
			LexicalAnalyzer.printErrors();
		}else {
			System.out.println("### Análise Léxica concluída com sucesso ###");
			LexicalAnalyzer.printTokens();
			System.out.println(LexicalAnalyzer.getTokens());
		}
	}
	
	public static void sintatico() {
		System.out.println("### ANALISADOR SINTÁTICO ###");
		AnalisadorSintaticoGeradorArvore sintatico = new AnalisadorSintaticoGeradorArvore(LexicalAnalyzer.getTokens());
		sintatico.analisar();
		if(!AnalisadorSintaticoGeradorArvore.getErros().isEmpty()) {
			System.out.println("### Analisador Sintatico com os seguinte erros:");
			System.out.println(AnalisadorSintaticoGeradorArvore.getErros());
		}else {
			System.out.println("### Análise sintática concluída com sucesso ###");
			AnalisadorSintaticoGeradorArvore.mostrarArvore();
		}
	}
	
	public static void semantico() {
		System.out.println("### ANALISADOR SEMÂNTICO ###");
		AnalisadorSemantico semantico = new AnalisadorSemantico(AnalisadorSintaticoGeradorArvore.getRaiz());
		semantico.analisar();
		if(!AnalisadorSemantico.getErros().isEmpty()) {
			System.out.println("### Analisador Semântico com os seguinte erros:");
			System.out.println(AnalisadorSemantico.getErros());
		}else {
			System.out.println("### Análise semântica concluída com sucesso ###");
			AnalisadorSemantico.getRaiz();
		}
	}
}
