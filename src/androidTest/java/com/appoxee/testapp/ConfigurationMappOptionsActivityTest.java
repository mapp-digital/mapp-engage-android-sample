package com.appoxee.testapp;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class ConfigurationMappOptionsActivityTest {

    private ConfigurationMappOptionsActivity configurationMappOptionsActivity = null;
    private String mSdkKey = "5b56f2bae61a14";
    private String mGoogleProjectId = "1028993954364";
    private String mCepUrl = "https://jamie-test.shortest-route.com/";
    private String mAppId = "262750";
    private String mTenantId = "55";

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
        onView(withId(R.id.etxt_set_google_project_id)).perform(typeText(mGoogleProjectId));
        onView(withId(R.id.etxt_set_cep_url)).perform(typeText(mCepUrl));
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