package com.bcit.com7082.group1.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import android.location.Location;

import com.bcit.com7082.group1.activity.MainActivity;
import com.bcit.comp7082.group1.R;
import com.bcit.com7082.group1.activity.SearchActivity;
import com.bcit.comp7082.group1.aspects.FindPhotosLoggingBehaviour;

public class MainPresenter {
    private final MainActivity view;
    String currentPhotoPath;

    public MainPresenter(final MainActivity view) {
        this.view = view;
    }

    public void displayPhotoInfo(String path, ImageView iv, TextView tv, EditText et, ImageButton fb) {
        String dateTimeTag = "", caption = "";
        Bitmap image = null;
        if (path != null && !path.isEmpty()) {
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
        displayPhoto(dateTimeTag, caption, image, path, iv,tv,et, fb);
    }

    public void displayLocationInfo(String path, TextView textview_location) {
        double[] laglon = null;
        String location = "";
        if(path != null && !path.isEmpty()) {
            laglon = Helper.retrieveGeoLocation(path);
        }
        if(laglon != null && laglon.length > 1) {
            location = "Latitude: " + Double.toString(laglon[0]) + System.lineSeparator();
            location += "Longitude: " + Double.toString(laglon[1]);
        }
        displayLocation(location, textview_location);
    }

    @FindPhotosLoggingBehaviour
    public ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords,
                                        double[] latRange, double[] lonRange) {
        File path = getPhotoStoragePath();
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = path.listFiles();
        List<File> fListAL = Arrays.asList(fList);
        if (fList != null) {
            fListAL.stream()
                    .filter(file -> isPhotoMatch(file, startTimestamp, endTimestamp, keywords, latRange, lonRange))
                    .collect(Collectors.toList())
                    .forEach(file -> photos.add(file.getPath()));
        }
        photos.sort(Collections.<String>reverseOrder());
        return photos;
    }

    public boolean isPhotoMatch(File f, Date startTimestamp, Date endTimestamp, String keywords,
                                double[] latRange, double[] lonRange) {
        long millisec;
        Date dt;
        millisec = f.lastModified();
        dt = new Date(millisec);
        double[] laglon = Helper.retrieveGeoLocation(f.getPath());

        return (startTimestamp == null || dt.getTime() >= startTimestamp.getTime()) &&
                (endTimestamp == null || dt.getTime() <= endTimestamp.getTime()) &&
                (keywords.equals("") || keywords.isEmpty() || f.getPath().contains(keywords)) &&
                (latRange == null || (laglon != null && laglon[0] >= Math.min(latRange[0], latRange[1]) && laglon[0] <= Math.max(latRange[0], latRange[1]))) &&
                (lonRange == null || (laglon != null && laglon[1] >= Math.min(lonRange[0], lonRange[1]) && laglon[1] <= Math.max(lonRange[0], lonRange[1])));
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
    public File dispatchTakePictureIntent(int REQUEST_IMAGE_CAPTURE, Context context, ImageView iv, TextView tv, EditText et, ImageButton fb) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        if (takePictureIntent.resolveActivity(view.getPackageManager()) != null) {
            try {
                photoFile = createImageFile(iv,tv,et,fb);
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
                displayPhotoInfo(currentPhotoPath,iv,tv,et,fb);
            }
        }
        return photoFile;
    }
    private File createImageFile(ImageView iv, TextView tv, EditText et, ImageButton fb) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ImageFileName = "JPEG_" + timeStamp + "_" + "caption" + "_";
        File storageDir = getPhotoStoragePath();
        File image = File.createTempFile(ImageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        displayPhotoInfo(currentPhotoPath, iv, tv, et, fb);
        return image;
    }
    public File getPhotoStoragePath() {
        return view.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    public void displayLocation(String location, TextView textview_location) {
//        TextView lv = (TextView) findViewById(R.id.Location);
        textview_location.setText(location);
    }
    private void displayPhoto(String dateTimeTag, String caption, Bitmap image, String path, ImageView iv, TextView tv, EditText et, ImageButton fb) {
        if(image == null) {
            iv.setImageResource(R.mipmap.ic_launcher);
        } else {
            iv.setImageBitmap(image);
        }
        iv.setTag(path);
        et.setText(caption);
        tv.setText(dateTimeTag);
        boolean isFavourite = readState(path);
        if (isFavourite) {
            fb.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            fb.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    public File updatePhoto(String path, String caption) {
        File to = null;
        if (path != null && caption != null) {
            String[] attr = path.split("_");
            if (attr.length >= 3) {
                to = new File(attr[0] + "_" + attr[1] + "_" + attr[2] + "_" + caption + "_" + attr[4]);
                File from = new File(path);
                from.renameTo(to);
            }
        }
        return to;
    }

    public void geoTagImage(File photoFile,  Location location) {
        Helper.geoTag(photoFile.getPath(), location.getLatitude(), location.getLongitude());
    }

    public void removeImage(String path) {
        if(path != null && !path.isEmpty()) {
            File file = new File(path);
            file.delete();
        }
    }

    public void saveState(boolean isFavourite, String path) {
        Helper.setFileTag(isFavourite, path);
    }

    public boolean readState(String path) {
        return Helper.getFileTag(path);
    }
}

