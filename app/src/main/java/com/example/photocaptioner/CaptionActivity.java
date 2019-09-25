package com.example.photocaptioner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class CaptionActivity extends AppCompatActivity {
    String currentPhotoPath = null;
    ExifInterface exif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);

        Intent intent = getIntent();
        currentPhotoPath = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        EditText capText = findViewById(R.id.capView);
        ImageView img = findViewById(R.id.bitmapView);
        if(currentPhotoPath != null){
            try {
                exif = new ExifInterface(currentPhotoPath);
                Bitmap myBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                img.setImageBitmap(myBitmap);
                String caption = exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
                if(caption != null){
                    capText.setText(caption);
                }
            }catch (IOException e){
                alert("Exception Warning", e.getMessage());
            }
        } else {
            this.finish();
        }
    }

    /** function for save button, call to save the caption */
    public void saveCaption(View view){
        EditText capText = findViewById(R.id.capView);
        if(currentPhotoPath != null){
            try {
                exif = new ExifInterface(currentPhotoPath);
                exif.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, capText.getText().toString());
                exif.saveAttributes();
            }catch (IOException e){
                alert("Exception Warning", e.getMessage());
            }
        } else {
            this.finish();
        }
    }

    /** function for cancel button, to stop caption activity */
    public void cancel(View view){
        this.finish();
    }

    /** function for dialog of alert */
    public void alert(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
