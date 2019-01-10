package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolnik.alcoholapp.MyApplication;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.clientservices.UserClientService;
import com.rolnik.alcoholapp.databinding.ActivityLoginBinding;
import com.rolnik.alcoholapp.dto.Error;
import com.rolnik.alcoholapp.dto.User;
import com.rolnik.alcoholapp.rests.UserRest;
import com.rolnik.alcoholapp.restutils.AsyncResponse;
import com.rolnik.alcoholapp.restutils.ResponseHandler;
import com.rolnik.alcoholapp.clientservices.AuthorizationClientService;
import com.rolnik.alcoholapp.sharedpreferenceservices.CookieSharedPreferencesService;
import com.rolnik.alcoholapp.sharedpreferenceservices.UserSharedPreferencesService;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.rolnik.alcoholapp.views.ResendEmailDialog;

import java.io.IOException;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements ResponseHandler<Response<Void>> {
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.root)
    ConstraintLayout root;

    @BindView(R.id.login)
    EditText login;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.loginButton)
    Button loginButton;
    @BindView(R.id.loginImage)
    ImageView loginImage;
    @BindView(R.id.loginRoot)
    LinearLayout loginRoot;

    @BindView(R.id.customProgressBar)
    CustomProgressBar customProgressBar;

    private CompositeDisposable disposables = new CompositeDisposable();

    private ActivityLoginBinding activityLoginBinding;

    @Named("without_cookie")
    @Inject
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityLoginBinding.setUser(new User());
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).getNetComponent().inject(this);
    }


    public void tryToLogin(View view) {
        if(checkIfLoginFormFilled()){
            login();
        } else {
            Toast.makeText(this, "Uzupełnij dane do logowania", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfLoginFormFilled() {
        return activityLoginBinding.getUser().getLogin().length() > 0 && activityLoginBinding.getUser().getPassword().length() > 0;
    }


    public void logIn(String cookie){
        Intent menu = new Intent(this, MainActivity.class);

        saveUserName(activityLoginBinding.getUser());
        saveCookie(cookie);

        startActivity(menu);
    }

    private void saveCookie(String cookie) {
        CookieSharedPreferencesService cookieSharedPreferencesService = new CookieSharedPreferencesService(this);
        Log.i("Saving cookie", "Cookie = " + cookie);
        cookieSharedPreferencesService.saveCookie(cookie);
    }

    private void saveUserName(User user){
        UserSharedPreferencesService userService = new UserSharedPreferencesService(getApplication());

        userService.logInUser(user) ;
    }


    private void showGUI(){
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        loginRoot.setVisibility(View.VISIBLE);
    }

    private void hideGUI(){
        TransitionManager.beginDelayedTransition(root);
        loginRoot.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposables.add(d);
        hideGUI();
    }

    @Override
    public void onNext(Response<Void> response) {
        if(response.isSuccessful()){
            logIn(response.headers().get("Set-Cookie"));
        } else {
            switch (response.code()){
                case 400: {
                    onBadRequest();
                    break;
                }
                case 401:{
                    onLoginAuthError(response);
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

    public void onLoginAuthError(Response<Void> response){
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();

                Error error = new ObjectMapper().readValue(errorJson, Error.class);

                if(error.isBadCredentialsError()){
                    showError(getString(R.string.user_data_not_exists));
                } else if (error.isUserNotActivatedError()){
                    askForResendingUserEmail(getString(R.string.email_not_confirmed));
                } else {
                    onUnknownError();
                }
            }
        } catch (IOException e) {
            Log.e("Login auth error", e.getMessage());
            onUnknownError();
        }
    }

    private Observable<Response<Void>> getPaperedObservable(){
        AuthorizationClientService authorizationClientService = new AuthorizationClientService(retrofit);

        return authorizationClientService.login(activityLoginBinding.getUser());
    }

    private void login(){
        AsyncResponse<Response<Void>> asyncResponse = new AsyncResponse<>(getPaperedObservable(), this);

        asyncResponse.execute();
    }

    private void askForResendingUserEmail(String errorMessage){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_LONG).setAction(R.string.resend, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResendEmailDialog();
            }
        });

        ((TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(getColor(R.color.lightSalomon));
        ((TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_action)).setTextColor(getColor(R.color.salomon));

        snackbar.show();
    }

    private void showResendEmailDialog(){
        showGUI();
        final ResendEmailDialog resendEmailDialog = new ResendEmailDialog(this);

        resendEmailDialog.setOkButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendRegisterEmail(resendEmailDialog.getEmail());
                resendEmailDialog.close();
            }
        });

        Objects.requireNonNull(resendEmailDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        resendEmailDialog.show();
    }

    private void resendRegisterEmail(String email){
        UserClientService userClientService = new UserClientService(retrofit.create(UserRest.class));

        Observable<Response<Void>> observable = userClientService.resendEmail(email);

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<Void>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                Log.i("Resend email", "Subscribed");
            }

            @Override
            public void onNext(Response<Void> response) {
                if(response.isSuccessful()){
                    showSuccessResend();
                    Log.i("Resend email", "Success");
                } else {
                    askForResendingUserEmail(getString(R.string.resend_mail_failed));
                    Log.i("Resend email", "Failed");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Resend email", "Exception " + e.getCause() + " occurs");
                askForResendingUserEmail(getString(R.string.resend_mail_failed));
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void showSuccessResend(){
        Toast.makeText(this, getString(R.string.resend_mail_success), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onBackPressed() {
        Intent start = new Intent(this, StartActivity.class);

        startActivity(start);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(disposables != null) {
            disposables.dispose();
        }
    }
}
