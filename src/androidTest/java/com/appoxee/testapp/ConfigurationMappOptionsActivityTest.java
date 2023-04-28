package com.appoxee.testapp;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class ConfigurationMappOptionsActivityTest {

    private ConfigurationMappOptionsActivity configurationMappOptionsActivity = null;
    private String mSdkKey = BuildConfig.SDK_KEY;
    private String mAppId = BuildConfig.APP_ID;
    private String mTenantId = BuildConfig.TENANT_ID;

    @Rule
    public ActivityTestRule<ConfigurationMappOptionsActivity> mActivityRule = new ActivityTestRule(ConfigurationMappOptionsActivity.class);

    @Before
    public void setUp() throws Exception {
        configurationMappOptionsActivity = mActivityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        configurationMappOptionsActivity = null;
    }

    @Test
    public void testSetConfigurationOnButtonClick() {

        onView(withId(R.id.etxt_set_sdk_key)).perform(typeText(mSdkKey));
        onView(withId(R.id.etxt_set_app_id)).perform(typeText(mAppId));
        onView(withId(R.id.etxt_set_tenant_id)).perform(typeText(mTenantId));
        closeSoftKeyboard();
        onView(withId(R.id.btn_configure_appoxee_options)).perform(click());

    }


    @Test
    public void testRefreshConfigurationOnButtonClick() {
        testDeleteAllFieldsOnButtonClick();
        testSetConfigurationOnButtonClick();
        onView(withId(R.id.btn_refresh)).perform(click());

    }

    @Test
    public void testDeleteAllFieldsOnButtonClick() {

        testSetConfigurationOnButtonClick();
        onView(withId(R.id.btn_delete_all_fields)).perform(click());

    }

    @Test
    public void testGetConfigurationOnButtonClick() {
        testDeleteAllFieldsOnButtonClick();
        onView(withId(R.id.btn_get_configuration)).perform(click());
    }

}