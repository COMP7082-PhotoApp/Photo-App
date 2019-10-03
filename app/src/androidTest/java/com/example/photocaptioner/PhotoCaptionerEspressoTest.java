package com.example.photocaptioner;

import androidx.test.rule.ActivityTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class PhotoCaptionerEspressoTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void ensureCaptionCreatingOrEditing() {
        //onView(withId(R.id.btnPhoto)).perform(click());
        //onView(withId(R.id.takePhotoButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.gridView)).atPosition(0).perform(click());
        onView(withId(R.id.btnCaption)).perform(click());
        onView(withId(R.id.capView)).perform(clearText(), typeText("This is a Caption"), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(click());
        onView(withId(R.id.btnCaption)).perform(click());
        onView(withId(R.id.capView)).check(matches(withText("This is a Caption")));
    }
}
