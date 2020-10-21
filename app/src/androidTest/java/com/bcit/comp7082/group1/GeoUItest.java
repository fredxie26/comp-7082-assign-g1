package com.bcit.comp7082.group1;

import android.location.Location;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.bcit.com7082.group1.activity.MainActivity;
import com.bcit.com7082.group1.presenter.Helper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class GeoUItest {

    public void geoTagImage(File photoFile, Location location) {
        Helper.geoTag(photoFile.getPath(), location.getLatitude(), location.getLongitude());
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + "TestCaption" + "_";

        Helper.geoTag(new File("/storage/emulated/0/Android/data/com.bcit.comp7082.group1/files/Pictures/").getPath(), 200.0, 50.0);

        File storageDir = new File("/storage/emulated/0/Android/data/com.bcit.comp7082.group1/files/Pictures/");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void GeoTest() throws IOException {
        File file = createImageFile();

        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.etLatitudeFrom)).perform(typeText("100.0"), closeSoftKeyboard());
        onView(withId(R.id.etLatitudeTo)).perform(typeText("300.0"), closeSoftKeyboard());
        onView(withId(R.id.etLongitudeFrom)).perform(typeText("50.0"), closeSoftKeyboard());
        onView(withId(R.id.etLongitudeTo)).perform(typeText("150.0"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.RightButton)).perform(click());
        onView(withId(R.id.LeftButton)).perform(click());
    }
}
