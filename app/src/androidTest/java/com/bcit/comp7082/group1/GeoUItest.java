package com.bcit.comp7082.group1;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

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

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void GeoTest() throws IOException
    {

        //Searches for GPS, hard coded for now no way to get Location from mainactivity, adjust to what your location is
        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.etLatitude)).perform(typeText("37"), closeSoftKeyboard());
        onView(withId(R.id.etLongitude)).perform(typeText("-122"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());

    }


}
