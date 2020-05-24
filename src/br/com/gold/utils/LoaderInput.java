package br.com.gold.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;

public class LoaderInput {
	
	public static BufferedReader load() {
		
		try {
			
			FileReader file = new FileReader(new File("src/br/com/gold/input.txt").getCanonicalPath());
			return  new BufferedReader(file);
						
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
