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


   /* private void dispatchTakePictureIntent() { //will create the intent to take a picture w/ camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/

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

    Bitmap thumbnail;
    public Bitmap getThumbnail() {
        return thumbnail;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void selectFromGallery() {
        Intent selectPhoto = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Uri selectedImage = selectPhoto.getData();
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
        int columnIndex;
        String picturePath = null;
        if( c != null && c.moveToFirst() ){
            columnIndex = c.getColumnIndex(filePath[0]);
            picturePath = c.getString(columnIndex);
            c.close();
        }
        if (picturePath != null) {
            thumbnail = (BitmapFactory.decodeFile(picturePath));
        }
        startActivity(new Intent(MainActivity.this, captionBot.class));
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }


    String[] terms = new String[2];
    public String[] getTerms() {
        return terms;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();


            /*if(extras != null){
                setPic();
            }*/

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    //terms = detectWebDetections(photoURI.toString(), terms);
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


    /*static void authExplicit(String jsonPath) throws IOException {
        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("C:\Users\AWeizman\Downloads\My First Project-d16a6569c1c7.json"))
        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        Context.Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        System.out.println("Buckets:");
        Page<NetworkStats.Bucket> buckets = storage.list();
        for (NetworkStats.Bucket bucket : buckets.iterateAll()) {
            System.out.println(bucket.toString());
        }
    }*/
/*    public void test(String filePath, PrintStream out) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.WEB_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

// Instantiates a client
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            // The path to the image file to annotate
            String fileName = currentPhotoPath;

            // Reads the image file into memory
            Path path = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                path = Paths.get(fileName);
            }
            byte[] data = new byte[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                data = Files.readAllBytes(path);
            }

            // Performs label detection on the image file (maybe
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }
                WebDetection annotation = res.getWebDetection();
                out.println("Entity:Id:Score");
                out.println("===============");
                for (WebDetection.WebEntity entity : annotation.getWebEntitiesList()) {
                    out.println(entity.getDescription() + " : " + entity.getEntityId() + " : "
                            + entity.getScore());
                }
                for (WebDetection.WebLabel label : annotation.getBestGuessLabelsList()) {
                    out.format("\nBest guess label: %s", label.getLabel());
                }
                out.println("\nPages with matching images: Score\n==");
                for (WebDetection.WebPage page : annotation.getPagesWithMatchingImagesList()) {
                    out.println(page.getUrl() + " : " + page.getScore());
                }
                out.println("\nPages with partially matching images: Score\n==");
                    out.println(image.getUrl() + " : " + image.getScore());
                }
                out.println("\nPages with fully matching images: Score\n==");
                for (WebDetection.WebImage image : annotation.getFullMatchingImagesList()) {
                    out.println(image.getUrl() + " : " + image.getScore());
                }
                out.println("\nPages with visually similar images: Score\n==");
                for (WebDetection.WebImage image : annotation.getVisuallySimilarImagesList()) {
                    out.println(image.getUrl() + " : " + image.getScore());
                }
            }

            }
        }

        */
    }

