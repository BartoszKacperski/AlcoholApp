package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rolnik.alcoholapp.dao.UserRestDao;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.utils.UserService;
import com.rolnik.alcoholapp.dao.Dao;
import com.rolnik.alcoholapp.dao.RestDaoFactory;
import com.rolnik.alcoholapp.model.User;
import com.vstechlab.easyfonts.EasyFonts;

import java.lang.ref.WeakReference;

public class StartActivity extends AppCompatActivity {

    private TextView title;
    private ConstraintLayout root;

    private ConstraintLayout menuRoot;
    private CustomProgressBar customProgressBar;

    private ConstraintLayout loginRoot;
    private ImageButton loginImage;
    private TextView loginText;
    private Handler loginHandler = new Handler();
    private Runnable loginRunnable;


    private ConstraintLayout registerRoot;
    private ImageButton registerImage;
    private TextView registerText;
    private Handler registerHandler = new Handler();
    private Runnable registerRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initializeViews();
        changeTypeface(EasyFonts.captureIt(getApplication()));

        checkIfUserLoggedAndExistInDatabse();
    }

    private void initializeViews(){
        title = findViewById(R.id.title);
        root = findViewById(R.id.root);
        menuRoot = findViewById(R.id.menuRoot);
        customProgressBar = findViewById(R.id.customProgressBar);

        initializeLoginView();
        initializeRegisterView();
    }

    private void initializeLoginView(){
        loginRoot = findViewById(R.id.loginRoot);
        loginImage = findViewById(R.id.loginImage);
        loginText = findViewById(R.id.loginText);
    }

    private void initializeRegisterView(){
        registerRoot = findViewById(R.id.registerRoot);
        registerImage = findViewById(R.id.registerImage);
        registerText = findViewById(R.id.registerText);
    }

    private void changeTypeface(Typeface typeface){
        title.setTypeface(typeface);
        loginText.setTypeface(typeface);
        registerText.setTypeface(typeface);
    }

    private void checkIfUserLoggedAndExistInDatabse() {
        new CheckIfUserExistsInDb(this).execute();
    }


    private void moveToMenu() {
        Intent main = new Intent(this, MainActivity.class);

        startActivity(main);
    }


    public void register(View view) {
        Intent register = new Intent(this, RegisterActivity.class);

        startActivity(register);
    }

    public void login(View view) {
        Intent login = new Intent(this, LoginActivity.class);

        startActivity(login);
    }

    public void showLoginText(View view) {
        TransitionManager.beginDelayedTransition(menuRoot);
        loginText.setVisibility(View.VISIBLE);

        loginHandler.postDelayed( loginRunnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TransitionManager.beginDelayedTransition(menuRoot);
                        loginText.setVisibility(View.GONE);
                    }
                });
            }
        }, 2000);
    }

    public void showRegisterText(View view) {
        TransitionManager.beginDelayedTransition(menuRoot);
        registerText.setVisibility(View.VISIBLE);

        registerHandler.postDelayed( registerRunnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TransitionManager.beginDelayedTransition(menuRoot);
                        registerText.setVisibility(View.GONE);
                    }
                });
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TransitionManager.beginDelayedTransition(root);
        loginText.setVisibility(View.GONE);
        registerText.setVisibility(View.GONE);
    }

    public void showInfo(View view) {
        Intent credits = new Intent(this, CreditsActivity.class);

        startActivity(credits);
    }

    private void onCheckingFailed(){
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        menuRoot.setVisibility(View.VISIBLE);
    }

    private void onCheckingSuccess(){
        customProgressBar.endAnimation();
        moveToMenu();
    }

    private static class CheckIfUserExistsInDb extends AsyncTask<Void, Void , Integer>{
        private WeakReference<StartActivity> startActivityWeakReference;
        private Exception exception = null;
        private UserService userService;

        CheckIfUserExistsInDb(StartActivity startActivity){
            this.startActivityWeakReference = new WeakReference<>(startActivity);
            userService = new UserService(startActivity);
        }

        @Override
        protected void onPreExecute(){
            StartActivity startActivity = startActivityWeakReference.get();

            if(startActivity == null || startActivity.isFinishing()){
                Log.e("Checking user", "Activity doesn't exist");
                cancel(true);
                return;
            }

            startActivity.customProgressBar.startAnimation();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if(userService.checkIfUserLogged()){
                UserRestDao userDao = UserRestDao.getInstance();

                User user = userService.getLoggedUser();

                try {
                    return userDao.login(user);
                } catch (Exception e) {
                    exception = e;
                    userService.logOutUser();
                    return  null;
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer userId){
            StartActivity startActivity = startActivityWeakReference.get();

            if(startActivity == null || startActivity.isFinishing()){
                Log.e("Checking user", "Activity doesn't exist");
                return;
            } else if(exception != null){
                Log.e("Checking user", "Exception " + exception + " occurs");
                startActivity.onCheckingFailed();
                return;
            }

            if(userId == null){
                Log.w("Checking user", "User not valid");
                startActivity.onCheckingFailed();
            } else {
                Log.i("Checking user", "User valid, moving to menu");
                startActivity.onCheckingSuccess();
            }

        }
    }
}
