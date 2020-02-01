package com.example.capgen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;


   /* private void dispatchTakePictureIntent() { //will create the intent to take a picture w/ camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/

    String currentPhotoPath;

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

    static final int REQUEST_TAKE_PHOTO = 1;

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
                //FIXME: make an actual response
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
//Vision Web Detection API
    // FIXME: make sure file read is file that was just added from pic. Call fxn from onActivityResult?
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    /**
     * Finds references to the specified image on the web.
     *
     * @param filePath The path to the local file used for web annotation detection.
     * @param out A {@link PrintStream} to write the results to.
     * @throws Exception on errors while closing the client.
     * @throws IOException on Input/Output errors.
     */
    //filePath currentPhotoPath
    public static void detectWebDetections(String filePath, PrintStream out) throws Exception,
            IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

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
                    out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }

                // Search the web for usages of the image. You could use these signals later
                // for user input moderation or linking external references.
                // For a full list of available annotations, see http://g.co/cloud/vision/docs
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
                for (WebDetection.WebImage image : annotation.getPartialMatchingImagesList()) {
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

