package com.example.photocaptioner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

// William Bradshaw
// CameraActivity - Launches Android's built-in camera to take a photo when button pressed.

public class CameraActivity extends AppCompatActivity {

    String currentPhotoPath = null;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }

    /** onClick function for button to take Photo */
    public void takePhoto(View v) throws IOException {

        dispatchTakePictureIntent();

    }

    /** Creates files/Download if it does not exist, then creates jpg file for new photo */
    private File newImageFile() throws IOException{

        //Create file path to Download folder to check if it exists and later generate a URI

        File localDirectory = getFilesDir();
        File downloadFolder = new File(localDirectory, "Download");

        //Create the Download folder if it does not exist
        if(!downloadFolder.exists()) {
            downloadFolder.mkdir();
        }


        //Log.i("Message", "attempt to make new image file");

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Prepares the final path for the output photo file
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                downloadFolder /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        //Log.i("Photo Path", currentPhotoPath);

        return image;

    }

    /** Launches Android's built-in camera activity */
    private void dispatchTakePictureIntent() throws IOException {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if Android camera can be successfully accessed by the app
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Creates an image file to store the new photo
            File newPhoto = newImageFile();

            // Check if creation of a new photo file was successful
            if (newPhoto != null) {

                // Gets a URI for the file based on authority of app's own fileprovider
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.photocaptioner.fileprovider",
                        newPhoto);

                // Creates takePictureIntent with two extra parameters
                // MediaStore.EXTRA_OUTPUT indicates the the following photoURI will store the photo

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Starts the Android Camera activity
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }

        }

    }

}
