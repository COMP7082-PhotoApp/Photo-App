package FilterImages;

import android.media.ExifInterface;
import java.io.File;
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
                String[] captions;
                // Checks to see if filterByDate returns false.  If this condition is met, iterate to the next pass of the loop.
                if(!filterByDate(dateTime, dateFrom, dateTo)){
                    continue;
                }
                // Checks to see if caption is empty and if filterByCaption returns false.
                // If this condition is met, iterate to the next pass of the loop.
                if(caption != null && !caption.isEmpty() && !caption.equals("null")){
                    captions = caption.split(" "); // split the caption into words using space char as delimiter
                    if (!filterByCaption(captions, searchWord)){
                        continue;
                    }
                }
                // Do a check for GPS coordinates below here and before adding to filteredList
                filteredList.add(images[i]);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        filteredImages = filteredList.toArray(new File[filteredList.size()]);
        return filteredImages;
    }

    public static boolean filterByDate(String inputDate, String startDate, String endDate){
        Date date;
        Date fromDate;
        Date toDate;
        String dateText;

        // Set up date formats for exif data as well as input data
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat lazyFormat = new SimpleDateFormat("yyyyMMdd");

        // Checks for actual date/time data in the exif datetime tag
        if(inputDate != null && !inputDate.isEmpty() && !inputDate.equals("null")){
            // Grab only the date portion of the date/time data
            dateText = inputDate.split(" ")[0];
        } else{
            // Grab today's date
            Date today = Calendar.getInstance().getTime();
            dateText = sdf.format(today);
        }

        try {
            // Creates a date object for the date data from the image
            date = sdf.parse(dateText);

            // Sets the from-date and to-date to input parameters, or sets to ridiculously far away dates if empty
            if(startDate != null && !startDate.isEmpty() && !startDate.equals("null")){
                fromDate = lazyFormat.parse(startDate);
            } else{
                fromDate = lazyFormat.parse("09000101");
            }

            if(endDate != null && !endDate.isEmpty() && !endDate.equals("null")){
                toDate = lazyFormat.parse(endDate);
            } else{
                toDate = lazyFormat.parse("99990101");
            }

            // Filters for date
            if (date.after(fromDate) && date.before(toDate)){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean filterByCaption(String[] captions, String keyWord){
        for(int j = 0; j < captions.length; j++){ // loop through each word of the caption and compare it with the search word
            if(captions[j].compareTo(keyWord) == 0){ // if we get a match add it to the filtered list
                return true;
            }
        }
        return false;
    }
}
