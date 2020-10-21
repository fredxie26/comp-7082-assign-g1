package com.bcit.com7082.group1.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcit.com7082.group1.presenter.Helper;
import com.bcit.com7082.group1.presenter.MainPresenter;
import com.bcit.comp7082.group1.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;

    private ArrayList<String> photos = null;
    private int index = 0;
    File photoFile = null;
    TextView textview_location, textview_time;
    EditText edittext_captions;
    ImageView imageView;
    private FusedLocationProviderClient fusedLocationClient;
    private MainPresenter mainPresenter;
    public static Context context;
    private String photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPresenter = new MainPresenter(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        imageView = findViewById(R.id.Gallery);
        context = getApplicationContext();
        textview_location = (TextView) findViewById(R.id.Location);
        textview_time = (TextView) findViewById(R.id.Timestamp);
        edittext_captions = (EditText) findViewById(R.id.Captions);
        photos = mainPresenter.findPhotos(new Date(Long.MIN_VALUE), new Date(), "", null, null);

        if (photos.size() == 0) {
            mainPresenter.displayPhotoInfo(null, imageView, textview_time, edittext_captions);
            mainPresenter.displayLocationInfo(null, textview_location);
        } else {
            mainPresenter.displayPhotoInfo(photos.get(index), imageView, textview_time, edittext_captions);
            mainPresenter.displayLocationInfo(photos.get(index), textview_location);
        }

        Button snap_button = (Button) findViewById(R.id.snap_button);
        Button share_button = (Button) findViewById(R.id.share_button);
        Button search_button = (Button) findViewById(R.id.search_button);

        snap_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                photoFile = mainPresenter.dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE, context, imageView, textview_time, edittext_captions);
            }
        });
        share_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainPresenter.shareToSocial(imageView, index, photos);
            }
        });
        search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainPresenter.searchImage(context);
            }
        });
    }

    public void scrollPhotos(View v) {
        if (!photos.isEmpty()) {
            File to = mainPresenter.updatePhoto(photos.get(index), ((EditText) findViewById(R.id.Captions)).getText().toString());
            if (to != null) {
                photos.set(index, to.getPath());
            }

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
            mainPresenter.displayPhotoInfo(photos.get(index), imageView, textview_time, edittext_captions);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Date startTimestamp = null, endTimestamp = null;
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
                } catch (ParseException pex) {
                    startTimestamp = null;
                    endTimestamp = null;
                } catch (NumberFormatException nfex) {
                    latRange = null;
                    lonRange = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");

                index = 0;
                photos = mainPresenter.findPhotos(startTimestamp, endTimestamp, keywords, latRange, lonRange);

                if (photos.size() == 0) {
                    mainPresenter.displayPhotoInfo(null, imageView, textview_time, edittext_captions);
                } else {
                    mainPresenter.displayPhotoInfo(photos.get(index), imageView, textview_time, edittext_captions);
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d("Onactivity Result", requestCode + "second if statement" + resultCode);
            photos = mainPresenter.findPhotos(new Date(Long.MIN_VALUE), new Date(), "", null, null);
            Log.d("photos", "size: " + photos.size());

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission not granted!", Toast.LENGTH_LONG).show();
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                mainPresenter.geoTagImage(photoFile, location);
                                mainPresenter.displayLocationInfo(photoFile.getPath(), textview_location);
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
