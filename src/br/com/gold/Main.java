package br.com.gold;

import java.io.BufferedReader;

import br.com.gold.lexion.LexicalAnalyzer;
import br.com.gold.lexion.WordClassifier;
import br.com.gold.utils.*;

public class Main {
	public static void main(String[] args) {
		BufferedReader text = LoaderInput.load();
		WordClassifier.toRank(text);
		
		if(!LexicalAnalyzer.getErrors().isEmpty()) {
			System.out.println("####  Analisador Léxico com os seguintes erros: ####");
			LexicalAnalyzer.printErrors();
		}else {
			System.out.println("### Análise Léxica concluída com sucesso ###");
			LexicalAnalyzer.printTokens();
			System.out.println(LexicalAnalyzer.getTokens());
		}
	}
}
