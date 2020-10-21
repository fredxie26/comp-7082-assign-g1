package com.bcit.comp7082.group1;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.bcit.com7082.group1.activity.MainActivity;

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

public class GeoUItest{

    //Help.geoTag(Fakefile, 50, -100)

    private File createImageFile(String caption) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + caption + "_";
        File storageDir = new File("/storage/emulated/0/Android/data/com.bcit.comp7082.group1/files/Pictures/");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void GeoTest() throws IOException
    {

        //Searches for GPS, hard coded for now no way to get Location from mainactivity, adjust to what your location is
        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.etLatitudeFrom)).perform(typeText("37"), closeSoftKeyboard());
        onView(withId(R.id.etLatitudeTo)).perform(typeText("90"), closeSoftKeyboard());
        onView(withId(R.id.etLongitudeFrom)).perform(typeText("-150"), closeSoftKeyboard());
        onView(withId(R.id.etLongitudeTo)).perform(typeText("-100"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());

    }


}
