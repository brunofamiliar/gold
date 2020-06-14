package br.com.gold;

import java.io.BufferedReader;

import br.com.gold.lexion.LexicalAnalyzer;
import br.com.gold.lexion.WordClassifier;
import br.com.gold.utils.*;
import br.com.gold.sintatico.AnalisadorSintaticoGeradorArvore;

public class Main {
	public static void main(String[] args) {
		lexion();
		sintatico();
	}
	
	private static void lexion() {
		BufferedReader text = LoaderInput.load();
		WordClassifier.toRank(text);
		
		System.out.println("### ANALISADOR SINT�TICO ###");
		
		if(!LexicalAnalyzer.getErrors().isEmpty()) {
			System.out.println("####  Analisador L�xico com os seguintes erros: ####");
			LexicalAnalyzer.printErrors();
		}else {
			System.out.println("### An�lise L�xica conclu�da com sucesso ###");
			LexicalAnalyzer.printTokens();
			System.out.println(LexicalAnalyzer.getTokens());
		}
	}
	
	public static void sintatico() {
		System.out.println("### ANALISADOR SINT�TICO ###");
		AnalisadorSintaticoGeradorArvore sintatico = new AnalisadorSintaticoGeradorArvore(LexicalAnalyzer.getTokens());
		sintatico.analisar();
		if(!AnalisadorSintaticoGeradorArvore.getErros().isEmpty()) {
			System.out.println("### Analisador Sintatico com os seguinte erros:");
			System.out.println(AnalisadorSintaticoGeradorArvore.getErros());
		}else {
			System.out.println("### An�lise sint�tica conclu�da com sucesso ###");
			AnalisadorSintaticoGeradorArvore.mostrarArvore();
		}
	}
}
