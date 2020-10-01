package com.bcit.comp7082.group1;

import androidx.exifinterface.media.ExifInterface;

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
}