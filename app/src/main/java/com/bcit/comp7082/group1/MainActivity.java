package com.bcit.comp7082.group1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_IMAGE = 2;
    String currentPhotoPath;

    private ArrayList<String> photos = null;
    private int index = 0;
    public static final String EXTRA_MESSAGE = "com.bcit.comp7082.MESSAGE";
    File photoFile = null;

    Button btnCamera;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.Gallery);
        File[] imageFiles = getPhotoStoragePath().listFiles();
        System.out.println("$$$$$$$$$$ ");
        if(imageFiles != null && imageFiles.length > 0) {
            displayPhoto(imageFiles[0].toString());
        }
        btnCamera = findViewById(R.id.snap_button);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
//            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.bcit.comp7082.group1.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
//        EditText editText = findViewById(R.id.editText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

//        startActivity(intent);
    }

    private File getPhotoStoragePath() {
        return getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private ArrayList<String> findPhotos(String startTime, String endTime, String keyword) {
        Pattern pattern = Pattern.compile(".*(\\d{8}_\\d{6}).*");
        File path = getPhotoStoragePath();
        ArrayList<String> photos = new ArrayList<>();
        File[] fList = path.listFiles();
        if (fList != null) {
            for (File f : fList) {
                String searchTimestamp = pattern.matcher(f.getPath()).group(1);
                if ((keyword == null || f.getPath().contains(keyword)) &&
                        (startTime == null || searchTimestamp.compareTo(startTime) == 1) &&
                        (endTime == null || endTime.compareTo(searchTimestamp) == 1)) {
                    photos.add(f.getPath());
                }
            }
        }
        return photos;
    }

    private void updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            File to = new File(attr[0] + "_" + caption + attr[2] + "_" + attr[3]);
            File from = new File(path);
            from.renameTo(to);
        }
    }

    public void scrollPhotos(View v) {
        updatePhoto(photos.get(index), ((EditText) findViewById(R.id.Captions)).getText().toString());

        switch (v.getId()) {
            case R.id.LeftButton:
                if (index > 0) {
                    index--;
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

    private void displayPhoto(String path) {
        ImageView iv = findViewById(R.id.Gallery);
        TextView tv = findViewById(R.id.Timestamp);
        EditText et = findViewById(R.id.Captions);
        if (path != null && !path.equals("")) {
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setImageBitmap(BitmapFactory.decodeFile(path, new BitmapFactory.Options()));
            String[] attr = path.split("_");
            et.setText(attr[1]);
            tv.setText(attr[2]);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ImageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getPhotoStoragePath();
        File image = File.createTempFile(ImageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String startTimestamp = data.getStringExtra(SearchActivity.STARTTIMESTAMP);
        String endTimestamp = data.getStringExtra(SearchActivity.ENDTIMESTAMP);
        String keyword = data.getStringExtra(SearchActivity.KEYWORDS);
        /*ArrayList<String> fileList = findPhotos(startTimestamp, endTimestamp, keyword);
        if (!fileList.isEmpty()) {
            photoFile = new File(fileList.get(0));
        }*/

        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
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