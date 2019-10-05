package com.example.photocaptioner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import FilterImages.Filter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public String selectedPhoto;
    public View selectedPhotoView;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public ImageAdapter iAdapter;
    public Filter iFilter;
    public boolean activeFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activeFilter = false;

        checkPermission();

        iFilter = new Filter();

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
                selectedPhotoView = v;
                try {
                    ExifInterface e = new ExifInterface(selectedPhoto);
                    Toast toast = Toast.makeText(MainActivity.this, "CAPTION: " + e.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION) +
                            "\n DATE: " + e.getAttribute(ExifInterface.TAG_DATETIME), Toast.LENGTH_LONG);
                    TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                    textView.setTextColor(Color.RED);
                    toast.show();
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

        Button addButton = (Button) findViewById(R.id.btnPicture);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iAdapter.updateList();
                iAdapter.notifyDataSetChanged();
            }
        });

        Button filterButton = (Button) findViewById(R.id.btnFilter);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText searchText = findViewById(R.id.searchView);
                filterPhotos(view, searchText.getText().toString());
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!activeFilter){
            iAdapter.updateList();
        }
        iAdapter.notifyDataSetChanged();
    }

    /** function for Filter button */
    public void filterPhotos(View view, String path) {
        File[] images = iAdapter.getImages();
        iFilter.filterImages(images, path);
        iAdapter.notifyDataSetChanged();
        iAdapter.updateList();
    }

    /** function for Caption button */
    public void editCaption(View view, String path) {
        Intent intent = new Intent(this, CaptionActivity.class);
        intent.putExtra(EXTRA_MESSAGE, path);
        startActivity(intent);
    }

    /** function to ask permission of storage */

    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    /** function to start activity for camera portion of app */

    public void openCameraScreen(View v){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    /** function for Filter button */
    public void openFilterActivity(View v){
        System.out.println("Made it inside openFilterActivity");
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
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
