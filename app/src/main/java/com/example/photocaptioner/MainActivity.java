package com.example.photocaptioner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

        checkPermission();

        File dir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
        if (!dir.exists() || !dir.isDirectory()){
            dir.mkdir();
        }

        //Initialize Twitter instance
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("CONSUMER_KEY", "CONSUMER_SECRET"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        /** Creates a gridview and adapter to handle the images for the gridview.
         * The adapter creation and methods are handled by ImageAdapter.
         * Added an onclick listener for photo selection purposes.
         */
        GridView gv = (GridView) findViewById(R.id.gridView);
         iAdapter = new ImageAdapter(this);
        gv.setAdapter(iAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View v, int position, long id) {

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
                    final ExifInterface e = new ExifInterface(selectedPhoto);
                    String imageInfo = "Caption: " + e.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION) + "\nDate: " + e.getAttribute(ExifInterface.TAG_DATETIME);

                    // Create alert box
                    final AlertDialog pictureAlert = dialogSetup.create();

                    // Create image view
                    ImageView selectedPictureView = new ImageView(MainActivity.this);
                    // Adapted from caption activity
                    // (set bitmap in image view from selectedPhoto path)
                    Bitmap myBitmap = BitmapFactory.decodeFile(selectedPhoto);
                    selectedPictureView.setImageBitmap(myBitmap);


                    // Create "Share on Twitter" button only if there is an existing twitter session
                    if (TwitterCore.getInstance().getSessionManager()
                            .getActiveSession() != null) {

                        pictureAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Share on Twitter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // Declare String for caption to be extracted with exif
                                String captionText;
                                // Store caption in string
                                captionText = e.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);

                                // Retrieve Uri for current photo
                                Uri photoUri = Uri.fromFile(new File(selectedPhoto));

                                // Set default tweet text if no caption retrieved from exif earlier
                                if( captionText == null || captionText.isEmpty() ){
                                    captionText = "A COMP 7082 Photo Captioner tweet";
                                }

                                // Access the current twitter session
                                final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                                        .getActiveSession();

                                // Start twitter by passing an intent with information to construct the tweet
                                final Intent intent = new ComposerActivity.Builder(MainActivity.this)
                                        .session(session)
                                        .image(photoUri)
                                        .text(captionText)
                                        .hashtags("#photocaptioner")
                                        .createIntent();
                                startActivity(intent);

                            }
                        });

                    } else {

                        // Creates a button the sends user to SettingsActivity so they can login
                        pictureAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Login to Twitter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                openSettings(v);

                            }
                        });


                    }

                    // Set title of alert box
                    pictureAlert.setTitle(imageInfo);

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

        Button settingsButton = (Button) findViewById(R.id.btnSettings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettings(view);

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
        Intent intent = new Intent(this, SettingsActivity.class);
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
        iAdapter.updateList();
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

    public void loadTestImageData(){
        AssetManager aManager = getAssets();
        String[] testImages = null;
        try{
            testImages = aManager.list("");
        } catch(IOException e){
            Log.e("tag", "No test images found.", e);
        }

        if (testImages != null) for (String image : testImages) {
            InputStream in = null;
            OutputStream out = null;
            try {
                if(!image.startsWith("images") && !image.startsWith("webkit")){
                    in = aManager.open(image);
                    File outFile = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), image);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                }
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + image, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}