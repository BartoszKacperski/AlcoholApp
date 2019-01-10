package com.rolnik.alcoholapp.activities;

import android.content.ComponentName;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.dto.Error;
import com.rolnik.alcoholapp.sharedpreferenceservices.CookieSharedPreferencesService;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
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
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@LargeTest
public class LoginActivityTest {

    @Rule
    public IntentsTestRule<LoginActivity> testRule = new IntentsTestRule<>(LoginActivity.class, true, false);

    private MockWebServer mockWebServer = new MockWebServer();

    @Before
    public void setUp() throws Exception {
        mockWebServer.start(8080);
        testRule.launchActivity(null);
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void unsuccessfulLogin() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        onView(withId(R.id.login)).perform(typeText("login"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        onView(withText(R.string.user_data_not_exists)).inRoot(withDecorView(not(testRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void successfulLogin() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).addHeader("Set-Cookie", "ABCD"));

        onView(withId(R.id.login)).perform(typeText("login"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        intended(hasComponent(new ComponentName(InstrumentationRegistry.getInstrumentation().getTargetContext(), MainActivity.class)));

        CookieSharedPreferencesService cookieSharedPreferencesService = new CookieSharedPreferencesService(testRule.getActivity());

        assertEquals(cookieSharedPreferencesService.getCookie(), "ABCD");
    }

    @Test
    public void notActivatedAccount() {
        Error error = new Error("User is disabled");
        mockWebServer.enqueue(new MockResponse().setResponseCode(401).setBody(objectToJson(error)));

        onView(withId(R.id.login)).perform(typeText("login"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.email_not_confirmed)))
                .check(matches(isDisplayed()));

        onView(allOf(withId(android.support.design.R.id.snackbar_action), withText(R.string.resend))).perform(click());
        onView(withText(R.string.ask_to_send_email_again)).check(matches(isDisplayed()));

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        onView(withId(R.id.email)).perform(typeText("aaa"), closeSoftKeyboard());
        onView(withId(R.id.okButton)).perform(click());

        onView(withText(R.string.resend_mail_success)).inRoot(withDecorView(not(testRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }


    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail();
            return "";
        }
    }
}