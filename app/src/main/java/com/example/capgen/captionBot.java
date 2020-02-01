package com.example.capgen;// package com.journaldev.readfileslinebyline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.*;

public class captionBot{
	public static void main(String[] input) {
		// File file = new File ("Desktop/google-cloud-sdk/quotes.txt");
		String[] inWord = input;
		Random lineMaker = new Random();
		BufferedReader reader;
		Vector quotes = new Vector();
		// List<String> lines = Files.readAllLines(Paths.get("quotes.txt"));
		// while(lines.hasMoreElements()){
		// 	line = lines.nextElement();
		// 	if (line.contains(inWord)){
		// 		quotes.addElement(line);
			// 		}
			// }


			// BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader("quotes.txt"));
				String line = reader.readLine();
				while (line != null) {
					for (int i = 0; i < inWord.length; i++){
						if (line.contains(inWord[i])){
							quotes.add(line);
						}
						line = reader.readLine();
					}
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
		// Scanner myScan = new Scanner(file);

		int hold = lineMaker.nextInt(quotes.size());
		System.out.print(quotes.get(hold)); //FIXME: send as output to xml file


	}
}