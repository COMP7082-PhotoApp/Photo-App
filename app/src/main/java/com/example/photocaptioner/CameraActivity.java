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
// CameraActivity - Takes photo when button pressed, and adds a timestamp automatically

public class CameraActivity extends AppCompatActivity {

    String currentPhotoPath = null;
    ExifInterface exif;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }

    public void takePhoto(View v) throws IOException {

        dispatchTakePictureIntent();

    }

    private File newImageFile() throws IOException{

        //Check to see if "Download" folder does not exist

        File localDirectory = getFilesDir();
        File downloadFolder = new File(localDirectory, "Download");

        if(!downloadFolder.exists()) {
            downloadFolder.mkdir();
        }


        Log.i("Message", "attempt to make new image file");

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                downloadFolder /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        Log.i("Photo Path", currentPhotoPath);

        showTimestamp(currentPhotoPath);

        return image;

    }

    private void showTimestamp(String selectedPhoto){
        try {
            ExifInterface e = new ExifInterface(selectedPhoto);
            Toast toast = Toast.makeText(CameraActivity.this, "CAPTION: " + e.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION) +
                    "\n DATE: " + e.getAttribute(ExifInterface.TAG_DATETIME), Toast.LENGTH_LONG);
            TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
            textView.setTextColor(Color.RED);
            toast.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Example from Android tutorial
    private void dispatchTakePictureIntent() throws IOException {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File newPhoto = newImageFile();

            if (newPhoto != null) {

                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.photocaptioner.fileprovider",
                        newPhoto);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }

        }

    }

}
