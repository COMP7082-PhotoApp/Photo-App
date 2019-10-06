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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class Filter {

    public static File[] filterImages(File[] images, String searchWord, String dateFrom, String dateTo, String gpsLat, String gpsLong){
        File[] filteredImages;
        List<File> filteredList = new ArrayList<File>();
        for(int i = 0; i < images.length; i++){// loop through images
            try{
                ExifInterface exif = new ExifInterface(images[i].getAbsolutePath()); // create an exif for the current image

                String caption = exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION); // save exif caption as string
                String dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String dateText;
                String[] captions;
                Date date;

                // Set up date formats for exif data as well as input data
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd");
                SimpleDateFormat lazyFormat = new SimpleDateFormat("yyyyMMdd");

                // Checks for actual date/time data in the exif datetime tag
                if(dateTime != null && !dateTime.isEmpty() && !dateTime.equals("null")){
                    // Grab only the date portion of the date/time data
                    dateText = dateTime.split(" ")[0];
                } else{
                    // Grab today's date
                    Date today = Calendar.getInstance().getTime();
                    dateText = sdf.format(today);
                }

                try {
                    Date fromDate;
                    Date toDate;
                    // Creates a date object for the date data from the image
                    date = sdf.parse(dateText);

                    // Sets the from-date and to-date to input parameters, or sets to ridiculously far away dates if empty
                    if(dateFrom != null && !dateFrom.isEmpty() && !dateFrom.equals("null")){
                        fromDate = lazyFormat.parse(dateFrom);
                    } else{
                        fromDate = lazyFormat.parse("09000101");
                    }
                    if(dateTo != null && !dateTo.isEmpty() && !dateTo.equals("null")){
                        toDate = lazyFormat.parse(dateTo);
                    } else{
                        toDate = lazyFormat.parse("99990101");
                    }

                    // Filters for date
                    if (date.after(fromDate) && date.before(toDate)){
                        // Check if there is a caption parameter
                        if(caption != null && !caption.isEmpty() && !caption.equals("null")){
                            captions = caption.split(" "); // split the caption into words using space char as delimiter

                            for(int j = 0; j < captions.length; j++){ // loop through each word of the caption and compare it with the search word
                                if(captions[j].compareTo(searchWord) == 0){ // if we get a match add it to the filtered list
                                    filteredList.add(images[i]);
                                    break;
                                }
                            }
                        } else{
                            filteredList.add(images[i]);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        filteredImages = filteredList.toArray(new File[filteredList.size()]);
        return filteredImages;
    }
}
