package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.dao.UserOpinionDatabase;
import com.rolnik.alcoholapp.dao.UserOpinionRestDao;
import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.model.UserOpinion;
import com.rolnik.alcoholapp.utils.UserService;
import com.vstechlab.easyfonts.EasyFonts;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button searchButton;
    private Button addButton;
    private Button mySalesButton;
    private TextView welcomeText;
    private UserService userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userService = new UserService(getApplication());

        if(!userService.checkIfUserLogged()){
            moveToStartActivity();
        }

        initializeViews();
        changeTypeface(EasyFonts.caviarDreams(this));
        sayHello();
        downloadMyOpinions();
    }

    private void initializeViews(){
        searchButton = findViewById(R.id.searchButton);
        addButton = findViewById(R.id.addButton);
        mySalesButton = findViewById(R.id.mySalesButton);
        welcomeText = findViewById(R.id.welcomeText);
    }

    private void changeTypeface(Typeface typeface){
        searchButton.setTypeface(typeface);
        addButton.setTypeface(typeface);
        mySalesButton.setTypeface(typeface);
        welcomeText.setTypeface(typeface);
    }

    public void searchButtonOnClick(View view) {
        Intent searchActivity = new Intent(this, SearchActivity.class);

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
        moveToStartActivity();
    }


    private void sayHello(){
        String welcome = getString(R.string.welcome);
        String userId = userService.getLoggedUserLogin();

        welcomeText.setText(String.format(welcome, userId));
    }

    private void downloadMyOpinions(){
        DownloadMyOpinions downloadMyOpinions = new DownloadMyOpinions(this, userService.getLoggedUser());

        downloadMyOpinions.execute();
    }


    private void moveToStartActivity(){
        Intent start = new Intent(this, StartActivity.class);

        startActivity(start);
    }

    private static class DownloadMyOpinions extends AsyncTask<Void, Void, Void>{
        private WeakReference<MainActivity> mainActivityWeakReference;
        private User user;
        private UserOpinionDatabase userOpinionDatabase;

        public DownloadMyOpinions(MainActivity mainActivity, User user){
            this.mainActivityWeakReference = new WeakReference<>(mainActivity);
            this.user = user;
        }

        @Override
        protected void onPreExecute(){
            MainActivity mainActivity = mainActivityWeakReference.get();

            if(mainActivity == null || mainActivity.isFinishing()){
                Log.e("Downlaod my opnions", "Activity is null");
                cancel(true);
                return;
            }

            userOpinionDatabase = UserOpinionDatabase.getUserOpinionDatabase(mainActivity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isCancelled()){
                try {
                    UserOpinionRestDao userOpinionRestDao = UserOpinionRestDao.getInstance();

                    List<UserOpinion> userOpinions = userOpinionRestDao.getUserOpinions(user);

                    userOpinionDatabase.clearAllTables();
                    userOpinionDatabase.userOpinionDao().insertAll(userOpinions);

                    Log.i("Download my opinions", "Everything is ok");
                } catch (Exception e){
                    Log.e("Download my opinions", "Exception " + e + " occurs");
                }
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!userService.checkIfUserLogged()){
            moveToStartActivity();
        }
    }
}
