package com.bcit.comp7082.group1;


import android.os.Environment;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest1 {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void searchActivityFunctions() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime after = now.minusMinutes(5);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        File file = new File("/storage/emulated/0/Android/data/com.bcit.comp7082.group1/files/Pictures/testFile.jpg");
        file.createNewFile();
        onView(withId(R.id.Search)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(typeText(now.format(f)), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(typeText(after.format(f)), closeSoftKeyboard());
        //onView(withId(R.id.etKeywords)).perform(typeText("caption"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.Gallery)).check(matches(withResourceName("testFile.jpg")));
        //onView(withId(R.id.Captions)).check(matches(withText("caption")));
        onView(withId(R.id.RightButton)).perform(click());
        onView(withId(R.id.LeftButton)).perform(click());
    }

}
