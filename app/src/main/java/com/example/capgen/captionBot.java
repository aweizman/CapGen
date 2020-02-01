package com.example.capgen;// package com.journaldev.readfileslinebyline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class captionBot{
	public static void main(String[] input) {
		// File file = new File ("Desktop/google-cloud-sdk/quotes.txt");
		String[] inWord = input;
		Random lineMaker = new Random();
		BufferedReader reader;
		Vector quotes = new Vector();
		boolean quoteFound = false;
		Date now = new Date();
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
		String[] defaultQuotes = {"They said get the " + inWord[0] + " so I got it", inWord[0] + " no " + inWord[1], "We need more " + inWord[0], inWord[0] + " on a  " + simpleDateformat.format(now)};
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
							quoteFound = true;
							quotes.add(line);
						}
						line = reader.readLine();
					}
				}
				if (!quoteFound) {
					int randomNum = 0;
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
						randomNum = ThreadLocalRandom.current().nextInt(0, 4);
					}
					System.out.print(defaultQuotes[randomNum]);
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