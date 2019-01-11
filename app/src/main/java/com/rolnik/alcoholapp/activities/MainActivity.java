package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.sharedpreferenceservices.CookieSharedPreferencesService;
import com.rolnik.alcoholapp.sharedpreferenceservices.UserSharedPreferencesService;
import com.vstechlab.easyfonts.EasyFonts;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.searchButton)
    Button searchButton;
    @BindView(R.id.addButton)
    Button addButton;
    @BindView(R.id.mySalesButton)
    Button mySalesButton;
    @BindView(R.id.welcomeText)
    TextView welcomeText;

    private UserSharedPreferencesService userService;
    private CookieSharedPreferencesService cookieSharedPreferencesService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        userService = new UserSharedPreferencesService(this);
        cookieSharedPreferencesService = new CookieSharedPreferencesService(this);

        if(!userService.checkIfUserSaved()){
            moveToStartActivity();
        }

        changeTypeface(EasyFonts.caviarDreams(this));
        sayHello();
    }

    private void changeTypeface(Typeface typeface){
        searchButton.setTypeface(typeface);
        addButton.setTypeface(typeface);
        mySalesButton.setTypeface(typeface);
        welcomeText.setTypeface(typeface);
    }

    public void searchButtonOnClick(View view) {
        Intent searchActivity = new Intent(this, FilterSalesActivity.class);

        startActivity(searchActivity);
    }

    public void addButtonOnClick(View view) {
        Intent addSaleActivity = new Intent(this, AddSaleActivity.class);

        startActivity(addSaleActivity);
    }

    public void mySalesButtonOnClick(View view) {
        Intent mySalesActivity = new Intent(this, MySalesActivity.class);

        startActivity(mySalesActivity);
    }

    public void logOut(View view) {
        userService.logOutUser();
        cookieSharedPreferencesService.deleteCookie();
        moveToStartActivity();
    }


    private void sayHello(){
        String welcome = getString(R.string.welcome);
        String userId = userService.getLoggedUserLogin();

        welcomeText.setText(String.format(welcome, userId));
    }


    private void moveToStartActivity(){
        Intent start = new Intent(this, StartActivity.class);

        startActivity(start);
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!userService.checkIfUserSaved()){
            moveToStartActivity();
        }
    }
}
