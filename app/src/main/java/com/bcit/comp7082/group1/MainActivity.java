package com.bcit.comp7082.group1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    private Helper helper = new Helper();
    String currentPhotoPath;

    private ArrayList<String> photos = null;
    private int index = 0;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    File photoFile = null;

    ImageView imageView;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        imageView = findViewById(R.id.Gallery);

        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "", "", "");
        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }

    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.bcit.comp7082.group1.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                displayPhoto(currentPhotoPath);
            }
        }
    }

    public void searchImage(View view) {
        TextView tv = (TextView) findViewById(R.id.Location);
        tv.setText("");
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    public void shareToSocial(View view) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String savedFile = photos.get(index);

        Uri imageUri = Uri.parse(savedFile);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Share Image"));

    }

    private File getPhotoStoragePath() {
        return getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private void updatePhoto(String path, String caption) {
        if (path != null && caption != null) {
            String[] attr = path.split("_");
            if (attr.length >= 3) {
                File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
                File from = new File(path);
                from.renameTo(to);
                photos.set(index, to.getPath());
            }
        }
    }

    public void scrollPhotos(View v) {
        if (!photos.isEmpty()) {
            updatePhoto(photos.get(index), ((EditText) findViewById(R.id.Captions)).getText().toString());

            switch (v.getId()) {
                case R.id.LeftButton:
                    if (index > 0) {
                        index = index - 1;
                    }
                    break;
                case R.id.RightButton:
                    if (index < (photos.size() - 1)) {
                        index++;
                    }
                    break;
                default:
                    break;
            }
            displayPhoto(photos.get(index));
        }
    }

    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.Gallery);
        TextView tv = (TextView) findViewById(R.id.Timestamp);
        EditText et = (EditText) findViewById(R.id.Captions);
        if (path == null || path.equals("")) {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            if (path.contains("_")) {
                String[] attr = path.split("_");
                et.setText(attr[1]);
                tv.setText(attr[2]);
                displayLocation(path);
            } else {
                et.setText("");
                tv.setText("");
            }
        }
        iv.setTag(path);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ImageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getPhotoStoragePath();
        File image = File.createTempFile(ImageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords,
                                         String latitude, String longitude) {

        File path = getPhotoStoragePath();
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = path.listFiles();

        if (fList != null && fList.length != 0) {
            for (File f : fList) {
                double[] laglon = helper.retrieveGeoLocation(f.getPath());
                if (((startTimestamp == null && endTimestamp == null) ||
                        (f.lastModified() >= startTimestamp.getTime() && f.lastModified() <= endTimestamp.getTime())) &&
                        (keywords == "" || f.getPath().contains(keywords)) &&
                        (((latitude == "" || latitude.isEmpty()) && (longitude == "" || longitude.isEmpty())) ||
                        (laglon != null && latitude.equals(Double.toString(laglon[0])) && longitude.equals(Double.toString(laglon[1])) )))
                    photos.add(f.getPath());
            }
        }
        photos.sort(Collections.<String>reverseOrder());
        return photos;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Date startTimestamp, endTimestamp;
                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(from);
                    endTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(to);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");
                String latitude = (String) data.getStringExtra("LATITUDE");
                String longitude = (String) data.getStringExtra("LONGITUDE");
                index = 0;
                photos = findPhotos(startTimestamp, endTimestamp, keywords, latitude, longitude);

                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(photos.get(index));
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d("Onactivity Result", requestCode + "second if statement" + resultCode);
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "", "", "");
            Log.d("photos", "size: " + photos.size());

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permisson not granted!");
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                helper.geoTag(photoFile.getPath(), location.getLatitude(), location.getLongitude());
                                displayLocation(photoFile.getPath());
                            }
                        }
                    });

            Uri uri = Uri.fromFile(photoFile);
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void displayLocation(String path) {
        double[] laglon = null;
        if(path != null) {
            laglon = helper.retrieveGeoLocation(path);
        }
        if(laglon != null) {
            String text = "Latitude: " + Double.toString(laglon[0]) + System.lineSeparator();
            text += "Longitude: " + Double.toString(laglon[1]);
            TextView tv = (TextView) findViewById(R.id.Location);
            tv.setText(text);
        }

    }
}
