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
import com.rolnik.alcoholapp.model.User;
import com.vstechlab.easyfonts.EasyFonts;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.root)
    ConstraintLayout root;

    @BindView(R.id.menuRoot)
    ConstraintLayout menuRoot;
    @BindView(R.id.customProgressBar)
    CustomProgressBar customProgressBar;

    @BindView(R.id.loginRoot)
    ConstraintLayout loginRoot;
    @BindView(R.id.loginImage)
    ImageButton loginImage;
    @BindView(R.id.loginText)
    TextView loginText;
    @BindView(R.id.registerRoot)
    ConstraintLayout registerRoot;
    @BindView(R.id.registerImage)
    ImageButton registerImage;
    @BindView(R.id.registerText)
    TextView registerText;

    private Handler loginHandler = new Handler();
    private Runnable loginRunnable;

    private Handler registerHandler = new Handler();
    private Runnable registerRunnable;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        changeTypeface(EasyFonts.captureIt(getApplication()));

        tryToAuthenticateUser();
    }


    private void changeTypeface(Typeface typeface){
        title.setTypeface(typeface);
        loginText.setTypeface(typeface);
        registerText.setTypeface(typeface);
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

    private void tryToAuthenticateUser(){
        UserRestDao userRestDao = UserRestDao.getInstance();
        UserService userService = new UserService(this);

        Observable<Integer> observable = userRestDao.login(userService.getLoggedUser());

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
                customProgressBar.startAnimation();
                Log.i("Checking user", "Subscribed");
            }

            @Override
            public void onNext(Integer integer) {
                if(integer == null){
                    Log.w("Checking user", "User not valid");
                    onCheckingFailed();
                } else {
                    Log.i("Checking user", "User valid, moving to menu");
                    onCheckingSuccess();
                }
            }

            @Override
            public void onError(Throwable e) {
                onCheckingFailed();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        TransitionManager.beginDelayedTransition(root);
        loginText.setVisibility(View.GONE);
        registerText.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(disposable != null){
            disposable.dispose();
        }
    }
}
