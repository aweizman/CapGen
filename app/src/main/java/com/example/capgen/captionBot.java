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


public class captionBot extends Activity {

	MainActivity main = new MainActivity();
	Button buttonBack, buttonGenerate;
	int setView = 1;
	ImageView imgView;

    Bitmap bm = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_caption);
		Log.d("img URI: ", main.getPhotoURI().toString());
		imgView = (ImageView) findViewById(R.id.imageView);
		Bundle bundle = getIntent().getExtras();


        //String photoURI = main.getPhotoURI();
        /*if (bundle.getString("MediaStore.EXTRA_OUTPUT")!=null){
			imgView.setImageURI(Uri.fromFile(new File(main.getPhotoPath())));
		}*/

        imgView.setImageURI(main.getPhotoURI());

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

		buttonGenerate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}

		});
	}
	public void main(String[] args) {
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