package com.appoxee.testapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.os.SystemClock;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    //monitor to check the second activity is activated.
    private Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SecondActivity.class.getName(), null, false);

    private MainActivity mainActivity = null;
    private String mAlias = "test2@test.com";
    private String mTag = "Tag1";
    private String mAttribute = "Attribute2";

    @Before
    public void setUp() throws Exception {
        mainActivity = mActivityRule.getActivity();
    }

    @Test
    public void testGetDeviceInfoOnButtonClick() {

        onView(withId(R.id.buttonDeviceInfo)).perform(click());
    }

    @Test
    public void testGetInfoOnButtonClick() {

        onView(withId(R.id.device_info)).perform(click());
    }

    @Test
    public void testGetAliasOnButtonClick() {

        String mGetAlias = mainActivity.getAlias();

        assertNotNull(mGetAlias);
    }

    @Test
    public void testSetAliasOnButtonClick() {

        onView(withId(R.id.etxt_set_alias)).perform(typeText(mAlias));
        closeSoftKeyboard();
        onView(withId(R.id.btn_set_alias)).perform(click());
        assertEquals(mAlias, mainActivity.getAlias());
    }

    @Test
    public void testLaunchOfSecondActivityOnButtonClick() {

        assertNotNull(mainActivity.findViewById(R.id.second_activity));
        onView(withId(R.id.second_activity)).perform(click());
        Activity secondActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNotNull(secondActivity);
        secondActivity.finish();
    }

    @Test
    public void testSetTagOnButtonClick() {

        onView(withId(R.id.etxt_set_tag)).perform(scrollTo(), typeText(mTag));
        closeSoftKeyboard();
        onView(withId(R.id.btn_set_tag)).perform(click());

    }

    @Test
    public void testRemoveTagOnButtonClick() {

        onView(withId(R.id.etxt_remove_tag)).perform(scrollTo(), typeText(mTag));
        closeSoftKeyboard();
        onView(withId(R.id.btn_remove_tag)).perform(click());

    }

    @Test
    public void testSetAttributeOnButtonClick() {

        onView(withId(R.id.etxt_set_attribute)).perform(scrollTo(), typeText(mAttribute));
        closeSoftKeyboard();
        onView(withId(R.id.btn_set_attribute)).perform(click());

    }

    @Test
    public void testGetAttributeOnButtonClick() {

        assertNotNull(R.id.etxt_get_attribute);
        onView(withId(R.id.etxt_get_attribute)).perform(scrollTo(), typeText(mAttribute));
        closeSoftKeyboard();
        onView(withId(R.id.btn_get_attribute)).perform(scrollTo(), click());

    }

    @Test
    public void testRemoveAttributeOnButtonClick() {

        assertNotNull(R.id.etxt_remove_attribute);
        onView(withId(R.id.etxt_remove_attribute)).perform(scrollTo(), typeText(mAttribute));
        closeSoftKeyboard();
        onView(withId(R.id.btn_remove_attribute)).perform(scrollTo(), click());

    }


    @Test
    public void testRemoveBadgeNumberOnButtonClick() {

        onView(withId(R.id.btn_remove_badge_number)).perform(scrollTo(), click());

    }

    @Test
    public void testLockScreenOrientationOnButtonClick() {

        String[] myArray = mActivityRule.getActivity().getResources().getStringArray(R.array.screen_orientation);

        assertNotNull(mainActivity.findViewById(R.id.btn_orientation));


        int size = myArray.length;
        for (int i = 0; i < size; i++) {
            // Find the spinner and click on it.
            onView(withId(R.id.btn_orientation)).perform(scrollTo(), click());
            onData(is(myArray[i])).perform(click());

            SystemClock.sleep(1500);

        }


    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }


}