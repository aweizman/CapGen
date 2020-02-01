// package com.journaldev.readfileslinebyline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.*;

public class captionBot{
	public static void main(String[] args) {
		// File file = new File ("Desktop/google-cloud-sdk/quotes.txt");
		String inWord = "glass";
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
					if (line.contains(inWord)){
						quotes.add(line);
					}
					line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
		// Scanner myScan = new Scanner(file);

		int hold = lineMaker.nextInt(quotes.size());
		System.out.print(quotes.get(hold));


	}
}