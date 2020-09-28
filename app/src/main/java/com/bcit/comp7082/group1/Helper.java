package com.bcit.comp7082.group1;

import android.media.ExifInterface;

import java.io.IOException;

public class Helper {
    public void geoTag(String filename){
        ExifInterface exif;
        try {
            exif = new ExifInterface(filename);
            String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            System.out.println("*******************");
            System.out.println(filename);
            System.out.println(lat);
            System.out.println(lng);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
