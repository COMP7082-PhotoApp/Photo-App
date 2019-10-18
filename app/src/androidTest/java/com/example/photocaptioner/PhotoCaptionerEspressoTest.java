package com.example.photocaptioner;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.anything;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class PhotoCaptionerEspressoTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Before
    public void setup() {
        MainActivity main = activityRule.getActivity();
        main.loadTestImageData();
    }

    @Test
    public void ensureCaptionCreatingOrEditing() throws InterruptedException {
        onView((withId(R.id.btnFilter))).perform(click());
        Thread.sleep(500);

        onView(((withId(R.id.btnCancel)))).perform(click());
        Thread.sleep(500);

        onData(anything()).inAdapterView(withId(R.id.gridView)).atPosition(0).perform(click());
        Thread.sleep(500);

        Espresso.pressBack();
        Thread.sleep(500);

        onView(withId(R.id.btnCaption)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.capView)).perform(scrollTo(), clearText(), typeText("This is a Caption"), closeSoftKeyboard());
        Thread.sleep(500);

        onView(withId(R.id.btnSave)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.btnCaption)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.capView)).check(matches(withText("This is a Caption")));

    }

    @Test
    public void dateTest() throws InterruptedException{
        onData(anything()).inAdapterView(withId(R.id.gridView)).atPosition(11).perform(click());
        Thread.sleep(500);

        Espresso.pressBack();
        Thread.sleep(500);

        onView(withId(R.id.btnCaption)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.capView)).perform(scrollTo(), clearText(), typeText("Falls!"), closeSoftKeyboard());
        Thread.sleep(500);

        onView(withId(R.id.btnSave)).perform(click());
        Thread.sleep(500);

        onView((withId(R.id.btnFilter))).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.date_from_input)).perform(clearText(), typeText("2019-08-21"), closeSoftKeyboard());
        onView(withId(R.id.date_to_input)).perform(clearText(), typeText("2019-10-23"), closeSoftKeyboard());
        onView((withId(R.id.btnFilter))).perform(click());
        Thread.sleep(500);

        onData(anything()).inAdapterView(withId(R.id.gridView)).atPosition(0).perform(click());
        Thread.sleep(500);
        onView(withText("Caption: Falls!\nDate: 2019:10:08 13:20:04")).check(matches(isDisplayed()));
    }

    @Test
    public void gpsTest() throws InterruptedException{
        onData(anything()).inAdapterView(withId(R.id.gridView)).atPosition(3).perform(click());
        Thread.sleep(500);

        Espresso.pressBack();
        Thread.sleep(500);

        onView(withId(R.id.btnCaption)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.capView)).perform(scrollTo(), clearText(), typeText("GPS"), closeSoftKeyboard());
        Thread.sleep(500);

        onView(withId(R.id.btnSave)).perform(click());
        Thread.sleep(500);

        onView((withId(R.id.btnFilter))).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.gps_top_left_lat_input)).perform(clearText(), typeText("54/1"), closeSoftKeyboard());
        onView(withId(R.id.gps_top_left_long_input)).perform(clearText(), typeText("118/1"), closeSoftKeyboard());
        onView(withId(R.id.gps_bottom_right_lat_input)).perform(clearText(), typeText("52/1"), closeSoftKeyboard());
        onView(withId(R.id.gps_bottom_right_long_input)).perform(clearText(), typeText("116/1"), closeSoftKeyboard());
        onView((withId(R.id.btnFilter))).perform(click());
        Thread.sleep(500);

        onData(anything()).inAdapterView(withId(R.id.gridView)).atPosition(0).perform(click());
        Thread.sleep(500);
        onView(withText("Caption: GPS\nDate: 2016:09:01 13:35:35")).check(matches(isDisplayed()));
    }
}
