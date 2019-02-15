package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rolnik.alcoholapp.MyApplication;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.clientservices.AuthorizationClientService;
import com.rolnik.alcoholapp.dto.User;
import com.rolnik.alcoholapp.restutils.AsyncResponse;
import com.rolnik.alcoholapp.restutils.ResponseHandler;
import com.rolnik.alcoholapp.sharedpreferenceservices.CookieSharedPreferencesService;
import com.rolnik.alcoholapp.sharedpreferenceservices.UserSharedPreferencesService;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.vstechlab.easyfonts.EasyFonts;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;
import retrofit2.Retrofit;



public class StartActivity extends AppCompatActivity implements ResponseHandler<Response<Void>> {
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

    private Handler registerHandler = new Handler();

    private CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    @Named("with_cookie")
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        changeTypeface(EasyFonts.captureIt(getApplication()));
        ((MyApplication) getApplication()).getNetComponent().inject(this);


        tryToAuthenticateUser();
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

        loginHandler.postDelayed(new Runnable() {
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

        registerHandler.postDelayed(new Runnable() {
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

    private void changeTypeface(Typeface typeface) {
        title.setTypeface(typeface);
        loginText.setTypeface(typeface);
        registerText.setTypeface(typeface);
    }

    private void moveToMenu() {
        Intent main = new Intent(this, MainActivity.class);

        startActivity(main);
    }

    private void showGUI() {
        TransitionManager.beginDelayedTransition(root);

        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        menuRoot.setVisibility(View.VISIBLE);
    }

    private void hideGUI() {
        TransitionManager.beginDelayedTransition(root);

        menuRoot.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
    }

    private void logIn(String cookie) {
        CookieSharedPreferencesService cookieSharedPreferencesService = new CookieSharedPreferencesService(this);

        if (cookie != null && !cookie.isEmpty() && !cookieSharedPreferencesService.getCookie().equals(cookie)) {
            cookieSharedPreferencesService.saveCookie(cookie);
        }

        moveToMenu();
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposables.add(d);
        hideGUI();
    }

    @Override
    public void onNext(Response<Void> response) {
        if (response.isSuccessful()) {
            logIn(response.headers().get("Set-Cookie"));
        } else {
            switch (response.code()) {
                case 400: {
                    onBadRequest();
                    break;
                }
                case 401: {
                    onNotAuthorized();
                    break;
                }
                default: {
                    onUnknownError();
                    break;
                }
            }
        }
    }

    @Override
    public void onComplete() {
        showGUI();
    }

    @Override
    public void onSocketTimeout() {
        showError(getString(R.string.socket_timeout_exception));
    }

    @Override
    public void onNotAuthorized() {
        showError(getString(R.string.authorization_exception));
    }

    @Override
    public void onBadRequest() {
        showError(getString(R.string.user_data_not_exists));
    }

    @Override
    public void onUnknownError() {
        showError(getString(R.string.unknown_exception_message));
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        showGUI();
    }

    private Observable<Response<Void>> getPreparedObservable(User user) {
        AuthorizationClientService authorizationClientService = new AuthorizationClientService(retrofit);

        return authorizationClientService.login(user);
    }

    private void tryToAuthenticateUser() {
        UserSharedPreferencesService userService = new UserSharedPreferencesService(this);

        if (userService.checkIfUserSaved()) {
            AsyncResponse<Response<Void>> asyncResponse = new AsyncResponse<>(getPreparedObservable(userService.getLoggedUser()), this);

            asyncResponse.execute();
        } else {
            showGUI();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        TransitionManager.beginDelayedTransition(root);

        loginText.setVisibility(View.GONE);
        registerText.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposables != null) {
            disposables.dispose();
        }
    }
}
