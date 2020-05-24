package br.com.gold.lexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import br.com.gold.utils.TokenMatch;

public class WordClassifier {

	public static int variablePrevious;
	
	public static ArrayList<Token> toRank(BufferedReader text){
		
		String line = null;
	
		try {
			int lineNumber = -1;
			
			while((line = text.readLine())!= null) {
				
				line = TokenMatch.replace(line);

				String aux = line;
				int column = 0;
				variablePrevious = 0;
				
				StringTokenizer st = new StringTokenizer(line);
				
				while (st.hasMoreTokens()) {
					String image = st.nextToken();
					
					if(image.startsWith("/") && aux.substring(variablePrevious + 1, variablePrevious + 2).equals("/")) {
						break;
					}
					
					
					if(!image.startsWith("\"")){
						column = columnPosition(line, image);
						line = line.substring(line.indexOf(image) + image.length());
						LexicalAnalyzer.analyze(image, lineNumber, column);
						
					}else{
						column = columnPosition(line, image) + 1 - image.length();
						line = line.substring(line.indexOf(image) + image.length());
						
						if(!image.endsWith("\"") || image.length() == 1){
							int init = variablePrevious - image.length() + 1;
							image = st.nextToken();
							column = columnPosition(line, image) + 1 - image.length();
							line = line.substring(line.indexOf(image) + image.length());
							
							while(!image.endsWith("\"")) {
								image = st.nextToken();
								column = columnPosition(line, image) + 1 - image.length();
								line = line.substring(line.indexOf(image) + image.length());
							}
							
							LexicalAnalyzer.addCLT(aux.substring(init, variablePrevious-1), lineNumber , column);
						}else {
							LexicalAnalyzer.addCLT(aux.substring(column, variablePrevious-1), lineNumber , column);
						}
						
					}
					
				}
				lineNumber++;
				
				
			}
			
			
			LexicalAnalyzer.EndOfChain();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public static int columnPosition(String line, String token) {
		variablePrevious += line.indexOf(token) + token.length();
		return variablePrevious;
	}
	
}
