package com.rolnik.alcoholapp.activities;

import android.content.ComponentName;
import android.content.Context;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.dto.User;
import com.rolnik.alcoholapp.sharedpreferenceservices.CookieSharedPreferencesService;
import com.rolnik.alcoholapp.sharedpreferenceservices.UserSharedPreferencesService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.internal.inject.InstrumentationContext;
import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.containsString;

public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class, true, false);


    @Before
    public void setUp() throws Exception {
        initLoggedUser(InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext());
        intentsTestRule.launchActivity(null);
    }

    @Test
    public void correctUserName(){
        initLoggedUser(intentsTestRule.getActivity());
        onView(withId(R.id.welcomeText)).check(matches(withText(containsString("login"))));
    }

    @Test
    public void openSearchActivity(){
        onView(withId(R.id.searchButton)).perform(click());
        intended(hasComponent(new ComponentName(InstrumentationRegistry.getInstrumentation().getTargetContext(), FilterSalesActivity.class)));
    }
    @Test
    public void openMySalesActivity(){
        onView(withId(R.id.mySalesButton)).perform(click());
        intended(hasComponent(new ComponentName(InstrumentationRegistry.getInstrumentation().getTargetContext(), MySalesActivity.class)));
    }
    @Test
    public void openAddSaleActivity(){
        onView(withId(R.id.addButton)).perform(click());
        intended(hasComponent(new ComponentName(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddSaleActivity.class)));
    }

    @Test
    public void logOut(){
        onView(withId(R.id.logoutButton)).perform(click());
        intended(hasComponent(new ComponentName(InstrumentationRegistry.getInstrumentation().getTargetContext(), StartActivity.class)));

        UserSharedPreferencesService userSharedPreferencesService = new UserSharedPreferencesService(intentsTestRule.getActivity());
        CookieSharedPreferencesService cookieSharedPreferencesService = new CookieSharedPreferencesService(intentsTestRule.getActivity());

        assertFalse(userSharedPreferencesService.checkIfUserSaved());
        assertFalse(cookieSharedPreferencesService.isCookieExists());
    }

    private void initLoggedUser(Context context) {
        UserSharedPreferencesService userSharedPreferencesService = new UserSharedPreferencesService(context);

        User user = User.builder().id(1).login("login").password("password").email("email@email.com").build();

        userSharedPreferencesService.save(user);
    }
}