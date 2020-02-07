package com.example.capgen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.usage.NetworkStats;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.api.client.util.Lists;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.WebDetection;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import io.grpc.Context;

import static androidx.core.content.FileProvider.getUriForFile;




public class MainActivity extends AppCompatActivity {
    //captionBot cBot = new captionBot();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    Button buttonCamera, buttonGallery;

    public String currentPhotoPath;

    private File createImageFile() throws IOException { //creates image file to save photo into storage
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    final int REQUEST_TAKE_PHOTO = 1;

    public static Uri photoURI;
    public static String photoPath;
    String encodedURI;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            photoPath = photoFile.getAbsolutePath();
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                //encodedURI = photoURI.getEncodedAuthority();
                //Log.d("Encoded URI: ", encodedURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public String getPhotoPath () {
        return photoPath;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void selectFromGallery() {

    }


    String[] terms = new String[2];
    public String[] getTerms() {
        return terms;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    startActivity(new Intent(MainActivity.this, captionBot.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Uri getPhotoURI() {
        return photoURI;
    }
    public void setPhotoURI(Uri foo) {
        photoURI = foo;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonGallery = (Button) findViewById(R.id.buttonGallery);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        buttonGallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) { selectFromGallery();}
        });
    }
//Vision Web Detection API
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    /**
     * Finds references to the specified image on the web.
     *
     * @param filePath The path to the local file used for web annotation detection.
     * @param out A {@link PrintStream} to write the results to.
     * @throws Exception on errors while closing the client.
     * @throws IOException on Input/Output errors.
     */


    //filePath = currentPhotoPath when called, output gets array which calls code to find quotes in DB
    public String[] detectWebDetections(String filePath, String[] out) throws IOException  {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        FileInputStream inputStream = new FileInputStream(filePath);
        ByteString imgBytes = ByteString.readFrom(inputStream);

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.WEB_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    out[0] = ("Error: " + res.getError().getMessage());
                    return out;
                }

                // Search the web for usages of the image. You could use these signals later
                // for user input moderation or linking external references.
                // For a full list of available annotations, see http://g.co/cloud/vision/docs
                WebDetection annotation = res.getWebDetection();
                int i = 0;
                for (WebDetection.WebEntity entity : annotation.getWebEntitiesList()) {
                    out[i] = (entity.getDescription() + " : " + entity.getEntityId() + " : "
                            + entity.getScore());
                    i++;
                    if (i == 2){
                        break;
                    }
                }

            }
        }
        return out;
    }
    }

