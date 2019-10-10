package com.example.photocaptioner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Twitter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public String selectedPhoto;
    public ImageView selectedPhotoView;
    public ImageView previousPhotoView;
    public int previousPosition = -1;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final int FILTER_REQUEST = 0;
    public static final int FILTER_APPLIED = 1;
    public static final int FILTER_CLEARED = -1;
    public ImageAdapter iAdapter;
    public boolean activeFilter;
    public AlertDialog.Builder dialogSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activeFilter = false;

        //Initialize Twitter instance
        Twitter.initialize(this);

        checkPermission();

        /** Creates a gridview and adapter to handle the images for the gridview.
         * The adapter creation and methods are handled by ImageAdapter.
         * Added an onclick listener for photo selection purposes.
         */
        GridView gv = (GridView) findViewById(R.id.gridView);
         iAdapter = new ImageAdapter(this);
        gv.setAdapter(iAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectedPhoto =  iAdapter.getPath(position);
                selectedPhotoView = (ImageView) v;

                // If a previous image was selected, make it appear normal again
                if (previousPhotoView != null){
                    previousPhotoView.setAlpha(1.0f);
                    previousPhotoView.clearColorFilter();
                }

                // Make selected image appear semi-transparent to indicate user's choice
                selectedPhotoView.setAlpha(0.5f);

                // Retrieves int color value from current primary color of the app's layout
                int highlightColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);

                // Make selected image appear tinted in color
                selectedPhotoView.setColorFilter(highlightColor, PorterDuff.Mode.SCREEN);

                // Store previousPhotoView variable to keep track of previous photo selected
                previousPhotoView = selectedPhotoView;
                //  Store previousPosition variable to keep track of previous photo selected
                previousPosition = position;

                try {
                    // Show Toast
                    ExifInterface e = new ExifInterface(selectedPhoto);
                    Toast toast = Toast.makeText(MainActivity.this, "CAPTION: " + e.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION) +
                            "\n DATE: " + e.getAttribute(ExifInterface.TAG_DATETIME), Toast.LENGTH_LONG);
                    TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                    textView.setTextColor(Color.RED);
                    toast.show();

                    // Create alert box
                    final AlertDialog pictureAlert = dialogSetup.create();

                    // Create image view
                    ImageView selectedPictureView = new ImageView(MainActivity.this);

                    // Adapted from caption activity
                    // (set bitmap in image view from selectedPhoto path)
                    ExifInterface exif = new ExifInterface(selectedPhoto);
                    Bitmap myBitmap = BitmapFactory.decodeFile(selectedPhoto);
                    selectedPictureView.setImageBitmap(myBitmap);

                    // Set title of alert box
                    pictureAlert.setTitle("Selected Photo");

                    // Adds image view to alert box
                    pictureAlert.setView(selectedPictureView);

                    // Define onClick listener to dismiss alert when tapping image
                    selectedPictureView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pictureAlert.dismiss();
                        }
                    });

                    // Show the alert box with the picture
                    pictureAlert.show();

                    // Retrieve screen dimensions for app's layout
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;

                    // Adjust size of alert box (80% wide and 60% tall)
                    Objects.requireNonNull(pictureAlert.getWindow()).setLayout((int)(screenWidth * 0.8) , (int)(screenHeight * 0.6));

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        /** Below are the buttons for the three functions.  The caption button is working
         * but the other buttons need to be implemented.  The filtering of photos can be
         * handled inside ImageAdapter using a filtering of the exif metadata.
         */

        Button captionButton = (Button) findViewById(R.id.btnCaption);
        captionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCaption(view, selectedPhoto);
            }
        });

        Button filterButton = (Button) findViewById(R.id.btnFilter);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterActivity(view);
            }
        });

        Button addButton = (Button) findViewById(R.id.btnSettings);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iAdapter.updateList();
                iAdapter.notifyDataSetChanged();
            }
        });

        dialogSetup = new AlertDialog.Builder(MainActivity.this);

    }

    @Override
    public void onResume(){
        super.onResume();
        if(!activeFilter){
            iAdapter.updateList();
        }
        iAdapter.notifyDataSetChanged();

        // If a photo is already selected, set it to appear semi-transparent
        // WIP: This won't change the transparency of selectedPhotoView for some reason
        if (previousPosition != -1){
            selectedPhotoView = (ImageView) iAdapter.getView(previousPosition, null,  null);
            selectedPhotoView.setAlpha(0.5f);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILTER_REQUEST){
            if(resultCode == FILTER_APPLIED){
                activeFilter = true;
                Bundle b = data.getBundleExtra("file bundle");
                File[] filteredList = (File[])b.getSerializable("filtered list");
                iAdapter.setImages(filteredList);
                iAdapter.notifyDataSetChanged();
            } else if(resultCode == FILTER_CLEARED){
                activeFilter = false;
                iAdapter.updateList();
                iAdapter.notifyDataSetChanged();
            }
        }
    }

    /** function for Caption button */
    public void editCaption(View view, String path) {
        Intent intent = new Intent(this, CaptionActivity.class);
        intent.putExtra(EXTRA_MESSAGE, path);
        startActivity(intent);
    }

    /** function for Settings button */
    public void openSettings(View v) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    /** function to ask permission of storage */

    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // temporary fix to prevent crash if current Android version lower than required SDK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    /** function to start activity for camera portion of app */

    public void openCameraScreen(View v){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    /** function for Filter button */
    public void openFilterActivity(View v){
        Intent intent = new Intent(this, SearchActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("file list", iAdapter.getImages());
        intent.putExtra("file bundle", b);
        startActivityForResult(intent, FILTER_REQUEST);
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
