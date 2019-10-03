package com.example.photocaptioner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public File[] images;

    public ImageAdapter(Context c) {
        mContext = c;
        images = updateImageList();
    }

    /**
     * Updates the array of image file paths from the app's downloads folder.
     *
     * @return returns an array of file paths;
     */
    public File[] updateImageList() {
        File directory = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
        return directory.listFiles();
    }

    public void updateList(){
        File directory = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
        images = directory.listFiles();
    }

    public void setImages(File[] filteredList){
        images = filteredList;
    }

    public File[] getImages(){
        return images;
    }
    /**
     * This function gives the file path for a particular image in the images array.
     * @param position this is the position of the photo selected, starting from index 0
     * @return returns the path of the image from the selected position
     */
    public String getPath(int position){
        return images[position].getAbsolutePath();
    }
    public int getCount() {
        return images.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(8, 8, 8, 8);
        int orientation;

        if(position < images.length){
            Bitmap original = BitmapFactory.decodeFile(images[position].getAbsolutePath());
            Bitmap myBitmap = Bitmap.createScaledBitmap (original, 200, 200, true);
            if (original != myBitmap)
                original.recycle();
            original = null;

            Matrix m = new Matrix();
            try {
                ExifInterface exif = new ExifInterface(getPath (position));

                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                System.out.println ("Orientation code: " + orientation);

                switch (orientation){
                    case ExifInterface.ORIENTATION_NORMAL:
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        m.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        m.postRotate (180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        m.postRotate (270);
                        break;
                    default:
                        break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(Bitmap.createBitmap (myBitmap, 0, 0, myBitmap.getWidth (), myBitmap.getHeight (), m, true));
            myBitmap.recycle ();
            myBitmap = null;
        }
        return imageView;
    }
}