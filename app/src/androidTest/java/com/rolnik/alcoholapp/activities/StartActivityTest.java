package com.rolnik.alcoholapp.activities;


import android.content.ComponentName;

import com.rolnik.alcoholapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@LargeTest
public class StartActivityTest {
    @Rule
    public IntentsTestRule<StartActivity> intentsTestRule = new IntentsTestRule<>(StartActivity.class, true, false);

    private MockWebServer mockWebServer = new MockWebServer();

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8080);
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));
        intentsTestRule.launchActivity(null);
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void buttonsTextVisibilityTest() {
        onView(withId(R.id.loginImage)).perform(click());
        onView(withId(R.id.loginText)).check(matches(isDisplayed()));
        onView(withId(R.id.registerImage)).perform(click());
        onView(withId(R.id.registerText)).check(matches(isDisplayed()));
    }

    @Test
    public void infoActivityOpenTest() {
        onView(withId(R.id.infoButton)).perform(click());
        intended(hasComponent(new ComponentName(InstrumentationRegistry.getInstrumentation().getTargetContext(), CreditsActivity.class)));
    }

    @Test
    public void loginActivityOpenTest() {
        onView(withId(R.id.loginImage)).perform(click());
        onView(withId(R.id.loginText)).perform(click());
        intended(hasComponent(new ComponentName(InstrumentationRegistry.getInstrumentation().getTargetContext(), LoginActivity.class)));
    }

    @Test
    public void registerActivityOpenTest() {
        onView(withId(R.id.registerImage)).perform(click());
        onView(withId(R.id.registerText)).perform(click());
        intended(hasComponent(new ComponentName(InstrumentationRegistry.getInstrumentation().getTargetContext(), RegisterActivity.class)));
    }

}