
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
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
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        LocalDateTime after = now.plusMinutes(2);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowS = now.format(f);
        String afterS = after.format(f);
        File file = createImageFile("myCaption");

        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(clearText());
        onView(withId(R.id.etToDateTime)).perform(clearText());
        onView(withId(R.id.etFromDateTime)).perform(typeText(nowS), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(typeText(afterS), closeSoftKeyboard());

        onView(withId(R.id.etKeywords)).perform(typeText("myCaption"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        String path = file.getAbsolutePath();
        onView(withId(R.id.Gallery)).check(matches(ImageViewSameFilenameMatcher.matchesImage(path)));

//        onView(withId(R.id.favorite)).perform(click());
        onView(withId(R.id.Captions)).check(matches(withText("myCaption")));
        onView(withId(R.id.RightButton)).perform(click());
        onView(withId(R.id.Gallery)).check(matches(ImageViewSameFilenameMatcher.matchesImage(path)));
        onView(withId(R.id.LeftButton)).perform(click());
        onView(withId(R.id.Gallery)).check(matches(ImageViewSameFilenameMatcher.matchesImage(path)));
//        onView(withId(R.id.remove_pic)).perform(click());


    }

    private File createImageFile(String caption) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + caption + "_";
        File storageDir = new File("/storage/emulated/0/Android/data/com.bcit.comp7082.group1/files/Pictures/");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }
}