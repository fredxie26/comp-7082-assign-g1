package com.bcit.comp7082.group1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainPresenter {
    private final MainActivity view;
    String currentPhotoPath;

    public MainPresenter(final MainActivity view) {
        this.view = view;
    }

    public void displayPhotoInfo(String path) {
        String dateTimeTag = "", caption = "";
        Bitmap image = null;
        if (path != null || !path.equals("")) {
            image = BitmapFactory.decodeFile(path);
            if (path.contains("_")) {
                String[] attr = path.split("_");
                SimpleDateFormat fileToDateConversion = new SimpleDateFormat("yyyyMMddHHmmss");
                SimpleDateFormat timeStampDisplay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date dateTime = fileToDateConversion.parse(attr[1] + attr[2]);
                    dateTimeTag = timeStampDisplay.format(dateTime);
                    caption = attr[3];
                } catch (ParseException ex) {
                    dateTimeTag = "";
                    caption = "";
                }
            }
        }
        view.displayPhoto(dateTimeTag, caption, image, path);
    }

    public void displayLocationInfo(String path) {
        double[] laglon = null;
        String location = "";
        if(path != null) {
            laglon = Helper.retrieveGeoLocation(path);
        }
        if(laglon != null) {
            location = "Latitude: " + Double.toString(laglon[0]) + System.lineSeparator();
            location += "Longitude: " + Double.toString(laglon[1]);
        }
        view.displayLocation(location);
    }

    public ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords,
                                         double[] latRange, double[] lonRange) {

        File path = view.getPhotoStoragePath();
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = path.listFiles();
        long millisec;
        Date dt;
        if (fList != null) {
            for (File f : fList) {
                millisec = f.lastModified();
                dt = new Date(millisec);
                double[] laglon = Helper.retrieveGeoLocation(f.getPath());
                if ((startTimestamp == null || dt.getTime() >= startTimestamp.getTime()) &&
                        (endTimestamp == null || dt.getTime() <= endTimestamp.getTime()) &&
                        (keywords.equals("") || keywords.isEmpty() || f.getPath().contains(keywords)) &&
                        (latRange == null || (laglon != null && laglon[0] >= Math.min(latRange[0], latRange[1]) && laglon[0] <= Math.max(latRange[0], latRange[1]) )) &&
                        (lonRange == null || (laglon != null && laglon[1] >= Math.min(lonRange[0], lonRange[1]) && laglon[1] <= Math.max(lonRange[0], lonRange[1]) )))
                {
                    photos.add(f.getPath());
                }
            }
        }
        photos.sort(Collections.<String>reverseOrder());
        return photos;
    }

    public void searchImage(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        view.startActivityForResult(intent, view.SEARCH_ACTIVITY_REQUEST_CODE);
    }
    public void shareToSocial(ImageView imageView, int index,ArrayList<String> photos) {

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String savedFile = photos.get(index);

        Uri imageUri = Uri.parse(savedFile);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);

        view.startActivity(Intent.createChooser(share, "Share Image"));

    }
    public File dispatchTakePictureIntent(int REQUEST_IMAGE_CAPTURE, Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        if (takePictureIntent.resolveActivity(view.getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.bcit.comp7082.group1.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                view.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                displayPhotoInfo(currentPhotoPath);
            }
        }
        return photoFile;
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ImageFileName = "JPEG_" + timeStamp + "_" + "caption" + "_";
        File storageDir = getPhotoStoragePath();
        File image = File.createTempFile(ImageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        displayPhotoInfo(currentPhotoPath);
        return image;
    }
    public File getPhotoStoragePath() {
        return view.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

}

