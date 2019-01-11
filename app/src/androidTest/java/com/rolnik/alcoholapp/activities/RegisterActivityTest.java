package com.rolnik.alcoholapp.activities;

import android.content.ComponentName;

import com.rolnik.alcoholapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.platform.app.InstrumentationRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@Ignore
public class RegisterActivityTest {
    @Rule
    public IntentsTestRule<RegisterActivity> intentsTestRule = new IntentsTestRule<>(RegisterActivity.class, true, false);

    private MockWebServer mockWebServer = new MockWebServer();

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8080);

        intentsTestRule.launchActivity(null);
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void successfulRegister(){
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("1"));

        onView(withId(R.id.login)).perform(typeText("login"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText("email@email.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.passwordConfirm)).perform(typeText("password"), closeSoftKeyboard());

        onView(withId(R.id.registerButton)).perform(click());

        intended(hasComponent(new ComponentName(InstrumentationRegistry.getInstrumentation().getTargetContext(), StartActivity.class)));
    }

    @Test
    public void unsuccessfulRegister(){
        onView(withId(R.id.registerButton)).perform(click());

        onView(withText(R.string.register_empty_input)).inRoot(withDecorView(not(intentsTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        onView(withId(R.id.login)).perform(typeText("login"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText("email@email.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.passwordConfirm)).perform(typeText("password1"), closeSoftKeyboard());

        onView(withText(R.string.diffrent_passwords)).inRoot(withDecorView(not(intentsTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        onView(withId(R.id.passwordConfirm)).perform(typeText("password"), closeSoftKeyboard());

        onView(withId(R.id.email)).perform(typeText("emailemail.com"), closeSoftKeyboard());

        onView(withText(R.string.bad_email)).inRoot(withDecorView(not(intentsTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        onView(withId(R.id.email)).perform(typeText("email@emailcom"), closeSoftKeyboard());

        onView(withText(R.string.bad_email)).inRoot(withDecorView(not(intentsTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

}