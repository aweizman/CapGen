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
import android.widget.TextView;
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

        //String imgURIString = imgURI.getPath();
		String terms[] = new String[2];

            main.setPhotoURI(Uri.parse(main.getPhotoPath()));
            imgURI = main.getPhotoURI();
			Log.d("From: ", imgURI.toString());
			//Log.d("looking at: ", imgURIString);
		try {
			terms = main.detectWebDetections(imgURI.toString(), terms);
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
		String[] quotes = {null, null};

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
			int i = 0;
			while (line != null) {
				if (line.contains(inWord1) || line.contains(inWord2)) {
					quotes[i] = line;
					Log.d("Quotes:", line);
				}
				line = reader.readLine();
				i++;
				if (i==2){
					break;
				}
			}

			reader.close();

			Log.d("Quote 0: ", quotes[0]);
			Log.d("Quote 1: ", quotes[1]);
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
		if(quotes[0] == null && quotes[1] == null){
			quotes[0] = "I like this.";
			quotes[1] = "Lorem ipsum";
		}
		Log.d("Quote 1: ", quotes[0]);
		Log.d("Quote 2: ", quotes[1]);
		((TextView)findViewById(R.id.textCaption)).setText(quotes[hold]);

	}
}