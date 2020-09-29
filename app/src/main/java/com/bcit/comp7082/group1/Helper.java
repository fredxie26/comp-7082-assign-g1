package com.bcit.comp7082.group1;

import android.widget.EditText;

import androidx.exifinterface.media.ExifInterface;

public class Helper {
    public void geoTag(String filename, Double lng, Double lat){
        ExifInterface exif;
        try {
            exif = new ExifInterface(filename);
            System.out.println("**************");
            System.out.println(filename);
            System.out.println(lng);
//            location = new Location("providername");
//            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, Double.toString(lat));
//            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, Double.toString(lng));
            exif.setLatLong(lng, lat);
            exif.saveAttributes();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public double[] retrieveGeoLocation(String filename) {
        ExifInterface exif;
        double[] latLong = null;
        try {
            exif= new ExifInterface(filename);
            latLong = exif.getLatLong();
            if (latLong != null) {
                System.out.println("Latitude: " + latLong[0]);
                System.out.println("Longitude: " + latLong[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latLong;
    }
}