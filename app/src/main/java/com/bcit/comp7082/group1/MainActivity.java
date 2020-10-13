package com.bcit.comp7082.group1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;

    private ArrayList<String> photos = null;
    private int index = 0;
    File photoFile = null;

    ImageView imageView;
    private FusedLocationProviderClient fusedLocationClient;
    private MainPresenter mainPresenter;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPresenter = new MainPresenter(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        imageView = findViewById(R.id.Gallery);
        context = getApplicationContext();

        photos = mainPresenter.findPhotos(new Date(Long.MIN_VALUE), new Date(), "", null, null);
        if (photos.size() == 0) {
            mainPresenter.displayPhotoInfo(null);
            mainPresenter.displayLocationInfo(null);
        } else {
            mainPresenter.displayPhotoInfo(photos.get(index));
            mainPresenter.displayLocationInfo(photos.get(index));
        }

        Button snap_button = (Button) findViewById(R.id.snap_button);
        Button share_button = (Button) findViewById(R.id.share_button);
        Button search_button = (Button) findViewById(R.id.search_button);

        snap_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                photoFile = mainPresenter.dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE, context);
            }
        });
        share_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainPresenter.shareToSocial(imageView, index,photos);
            }
        });
        search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainPresenter.searchImage(context);
            }
        });
    }

    public void displayPhoto(String dateTimeTag, String caption, Bitmap image, String path) {
        ImageView iv = (ImageView) findViewById(R.id.Gallery);
        TextView tv = (TextView) findViewById(R.id.Timestamp);
        EditText et = (EditText) findViewById(R.id.Captions);
        if(image == null) {
            iv.setImageResource(R.mipmap.ic_launcher);
        } else {
            iv.setImageBitmap(image);
        }
        iv.setTag(path);
        et.setText(caption);
        tv.setText(dateTimeTag);
    }

    public void displayLocation(String location) {
        TextView lv = (TextView) findViewById(R.id.Location);
        lv.setText(location);
    }

//    public void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.bcit.comp7082.group1.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                mainPresenter.displayPhotoInfo(currentPhotoPath);
//            }
//        }
//    }

//    public void searchImage() {
//        Intent intent = new Intent(this, SearchActivity.class);
//        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
//    }

    public File getPhotoStoragePath() {
        return getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private void updatePhoto(String path, String caption) {
        if (path != null && caption != null) {
            String[] attr = path.split("_");
            if (attr.length >= 3) {
                File to = new File(attr[0] + "_" + attr[1] + "_" + attr[2] + "_" + caption + "_" + attr[4]);
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
            mainPresenter.displayPhotoInfo(photos.get(index));
        }
    }

//    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String ImageFileName = "JPEG_" + timeStamp + "_" + "caption" + "_";
//        File storageDir = getPhotoStoragePath();
//        File image = File.createTempFile(ImageFileName, ".jpg", storageDir);
//
//        currentPhotoPath = image.getAbsolutePath();
//        mainPresenter.displayPhotoInfo(currentPhotoPath);
//        return image;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Date startTimestamp, endTimestamp;
                double[] latRange = new double[2];
                double[] lonRange = new double[2];
                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    String latFrom = (String) data.getStringExtra("LATITUDEFROM");
                    String latTo = (String) data.getStringExtra("LATITUDETO");
                    String lonFrom = (String) data.getStringExtra("LONGITUDEFROM");
                    String lonTo = (String) data.getStringExtra("LONGITUDETO");

                    startTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(from);
                    endTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(to);

                    latRange[0] = Double.parseDouble(latFrom);
                    latRange[1] = Double.parseDouble(latTo);
                    lonRange[0] = Double.parseDouble(lonFrom);
                    lonRange[1] = Double.parseDouble(lonTo);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                    latRange = null;
                    lonRange = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");

                index = 0;
                photos = mainPresenter.findPhotos(startTimestamp, endTimestamp, keywords, latRange, lonRange);

                if (photos.size() == 0) {
                    mainPresenter.displayPhotoInfo(null);
                } else {
                    mainPresenter.displayPhotoInfo(photos.get(index));
                }
            }
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d("Onactivity Result", requestCode + "second if statement" + resultCode);
            photos = mainPresenter.findPhotos(new Date(Long.MIN_VALUE), new Date(), "", null, null);
            Log.d("photos", "size: " + photos.size());

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission not granted!");
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Helper.geoTag(photoFile.getPath(), location.getLatitude(), location.getLongitude());
                                mainPresenter.displayLocationInfo(photoFile.getPath());
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


}
