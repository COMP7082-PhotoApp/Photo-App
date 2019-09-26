package com.example.photocaptioner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public File[] images;

    public ImageAdapter(Context c) {
        mContext = c;
        images = updateImageList();
    }

    public File[] updateImageList() {
        File directory = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
        return directory.listFiles();
    }

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

        if(position < images.length){
            Bitmap myBitmap = BitmapFactory.decodeFile(images[position].getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
        return imageView;
    }
}