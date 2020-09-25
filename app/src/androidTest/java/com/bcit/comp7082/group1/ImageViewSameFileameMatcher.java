package com.bcit.comp7082.group1;

import androidx.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A Matcher for Espresso that checks if an ImageView's tag matches refImageFile (image pathname).
 */
public class ImageViewSameFileameMatcher {

    public static Matcher<View> matchesImage(final String refImageFile) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("ImageView with this tag: " + refImageFile);
            }

            @Override
            public boolean matchesSafely(ImageView imageView) {
                String currentImageFilename = imageView.getTag().toString();
                return currentImageFilename.equals(refImageFile);
            }
        };
    }
}
