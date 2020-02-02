// package com.journaldev.readfileslinebyline;
package com.example.capgen;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Random;
import java.util.*;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;


public class captionBot extends Activity {

	MainActivity main = new MainActivity();
	Button buttonBack, buttonGenerate;
	int setView = 1;
	ImageView imgView;
	Uri imgURI;

	public Uri getImgURI(){
		return imgURI;
	}

    Bitmap bm = null;
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_caption);


		imgView = (ImageView) findViewById(R.id.imageView);
		Bundle bundle = getIntent().getExtras();


        //String photoURI = main.getPhotoURI();
        /*if (bundle.getString("MediaStore.EXTRA_OUTPUT")!=null){
			imgView.setImageURI(Uri.fromFile(new File(main.getPhotoPath())));
		}*/

        imgView.setImageURI(main.getPhotoURI());
        imgURI = main.getPhotoURI();
        String imgURIString = imgURI.getPath();
		String terms[] = new String[2];


			Log.d("From: ", imgURI.toString());
			Log.d("looking at: ", imgURIString);
		try {
			terms = main.detectWebDetections(imgURIString, terms);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//main(terms);

        //Drawable drawable  = getResources().getDrawable(R.drawable.trevor);
		//imgView.setImageDrawable(drawable);
            //bm = BitmapFactory();
            //drawable  = getResources().getDrawable(R.drawable.trevor);
            //imgView.setImageDrawable(drawable);




		buttonBack = (Button) findViewById(R.id.buttonBack);
		buttonGenerate = (Button) findViewById(R.id.buttonGenerate);

		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(captionBot.this, MainActivity.class));
			}
		});

		String[] newerTerms = terms;
		buttonGenerate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				main(newerTerms);
			}

		});
	}
	public void main(String[] args) {
		// File file = new File ("Desktop/google-cloud-sdk/quotes.txt");
		String inWord1 = args[0];
		String inWord2 = args[1];
		//Random lineMaker = new Random();
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
				if (line.contains(inWord1) || line.contains(inWord2)) {
					quotes.add(line);
					Log.d("Quotes:", line);
				}
				line = reader.readLine();
			}

			reader.close();

			Log.d("Quote 0: ", quotes.get(0).toString());
			Log.d("Quote 1: ", quotes.get(1).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner myScan = new Scanner(file);
		LocalTime decider = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			decider = LocalTime.now();
		}
		DateTimeFormatter myFormatObj = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			myFormatObj = DateTimeFormatter.ofPattern("HHmmss");
		}
		String deciderString = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			deciderString = decider.format(myFormatObj);
		}
		Log.d("Decider: ", deciderString);

		int hold = Integer.parseInt(deciderString) % 2;
		Log.d("Quote: ", quotes.get(hold).toString());


	}
}