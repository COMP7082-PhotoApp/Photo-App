package FilterImages;

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
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class Filter {

    public File[] filterImages(File[] images, String searchWord){

        List<File> filteredList = new ArrayList<File>();
        for(int i = 0; i < images.length; i++){// loop through images
            try{
                ExifInterface exif = new ExifInterface(images[i].getAbsolutePath()); // create an exif for the current image
                String caption = exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION); // save exif caption as string
                String[] data = caption.split(" "); // split the caption into words using space char as delimiter
                for(int j = 0; j < data.length; j++){ // loop through each word of the caption and compare it with the search word
                    if(data[j].compareTo(searchWord) == 0){ // if we get a match add it to the filtered list
                        System.out.println("data[j]:  " + data[j]);
                        System.out.println(("searchword: " + searchWord));
                        filteredList.add(images[i]);
                        break;
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }

        }
        images = filteredList.toArray(new File[filteredList.size()]);
        System.out.println("filtered length: " + filteredList.size() + "\n\n\n\n\n\n\n");
        return images;


    }
}
