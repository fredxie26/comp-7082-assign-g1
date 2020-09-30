package com.bcit.comp7082.group1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    String currentPhotoPath;

    private ArrayList<String> photos = null;
    private int index = 0;
    public static final String EXTRA_MESSAGE = "com.bcit.comp7082.MESSAGE";
    File photoFile = null;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.Gallery);

        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
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
            }
        }
    }

    public void searchImage(View view) {
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
                SimpleDateFormat fileToDateConversion = new SimpleDateFormat("yyyyMMddHHmmss");
                SimpleDateFormat timeStampDisplay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date dateTime = fileToDateConversion.parse(attr[1] + attr[2]);
                    String dateTimeTag = timeStampDisplay.format(dateTime);
                    tv.setText(dateTimeTag);
                } catch (ParseException ex) {
                    tv.setText("");
                }
                et.setText(attr[3]);
            } else {
                et.setText("");
                tv.setText("");
            }
        }
        iv.setTag(path);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ImageFileName = "JPEG_" + timeStamp + "_" + "caption" + "_";
        File storageDir = getPhotoStoragePath();
        File image = File.createTempFile(ImageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        displayPhoto(currentPhotoPath);
        return image;
    }

    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
        File path = getPhotoStoragePath();
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = path.listFiles();
        if (fList != null) {
            for (File f : fList) {
                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
                        && f.lastModified() <= endTimestamp.getTime())
                ) && (keywords == "" || f.getPath().contains(keywords)))
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
                index = 0;
                photos = findPhotos(startTimestamp, endTimestamp, keywords);

                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(photos.get(index));
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d("Onactivity Result", requestCode + "second if statement" + resultCode);
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
            Log.d("photos", "size: " + photos.size());
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