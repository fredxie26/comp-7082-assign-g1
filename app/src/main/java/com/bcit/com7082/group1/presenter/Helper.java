package com.bcit.com7082.group1.presenter;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Helper {
    public static void geoTag(String filename, Double lng, Double lat){
        ExifInterface exif;
        try {
            exif = new ExifInterface(filename);
//            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, Double.toString(lat));
//            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, Double.toString(lng));
            exif.setLatLong(lng, lat);
            exif.saveAttributes();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static double[] retrieveGeoLocation(String filename) {
        ExifInterface exif;
        double[] latLong = null;
        try {
            exif= new ExifInterface(filename);
            latLong = exif.getLatLong();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latLong;
    }

    public static void setFileTag(boolean isFavourite, String filename) {
        if(filename != null && !filename.isEmpty()) {
            ExifInterface exif;
            try {
                exif= new ExifInterface(filename);
                exif.setAttribute("UserComment", Boolean.toString(isFavourite));
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean getFileTag(String filename) {
        boolean isFavourite = false;
        if(filename != null && !filename.isEmpty()) {
            ExifInterface exif;
            try {
                exif= new ExifInterface(filename);
                isFavourite = Boolean.parseBoolean(exif.getAttribute("UserComment"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isFavourite;
    }
}