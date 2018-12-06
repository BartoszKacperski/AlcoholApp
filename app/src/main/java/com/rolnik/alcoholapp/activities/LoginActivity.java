package com.rolnik.alcoholapp.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.common.hash.Hashing;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.dao.UserRestDao;
import com.rolnik.alcoholapp.databinding.ActivityLoginBinding;
import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.utils.TextAndNumberUtils;
import com.rolnik.alcoholapp.utils.UserService;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.rolnik.alcoholapp.views.ErrorDialog;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class LoginActivity extends AppCompatActivity {
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

    private User userToLogin;

    private CompositeDisposable disposables = new CompositeDisposable();

    private ActivityLoginBinding activityLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        ButterKnife.bind(this);

        bindUser();
    }

    private void bindUser(){
        userToLogin = new User();
        activityLoginBinding.setUser(userToLogin);
    }

    private Context getContext(){
        return this;
    }

    public void tryToLogin(View view) {
        if(checkIfLoginFormFilled()){
            login();
        } else {
            Toast.makeText(this, "Uzupełnij dane do logowania", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfLoginFormFilled() {
        return userToLogin.getLogin().length() > 0 && userToLogin.getPassword().length() > 0;
    }


    public void logIn(User user){
        Intent menu = new Intent(this, MainActivity.class);

        saveUserName(user);

        startActivity(menu);
    }

    private void showUI(){
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        loginRoot.setVisibility(View.VISIBLE);
    }

    private void onLoginError(String message){
        userToLogin.setLogin("");
        userToLogin.setPassword("");
        showUI();

        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
    }

    private void onLogging(){
        TransitionManager.beginDelayedTransition(root);
        loginRoot.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
    }

    private void askForResendingUserEmail(final User user){
        showUI();
        final ErrorDialog errorDialog = new ErrorDialog(this);

        String errorMessage = getString(R.string.user_not_activated) + "\n" + getString(R.string.ask_to_send_email_again);

        errorDialog.setCancelButtonText(getString(R.string.cancel));
        errorDialog.setOkButtonText(getString(R.string.send));
        errorDialog.setErrorMessage(errorMessage, null);

        errorDialog.setOkButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendRegisterEmail();
                errorDialog.close();
            }
        });

        Objects.requireNonNull(errorDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        errorDialog.show();
    }

    private void resendRegisterEmail(){
        UserRestDao userRestDao = UserRestDao.getInstance();

        Observable<Boolean> observable = userRestDao.resendEmail(userToLogin);

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                Log.i("Resend email", "Subscribed");
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){
                    showSuccessResend();
                    Log.i("Resend email", "Success");
                } else {
                    showFailedResend();
                    Log.i("Resend email", "Failed");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Resend email", "Exception " + e.getCause() + " occurs");
                showFailedResend();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void showSuccessResend(){
        Toast.makeText(this, getString(R.string.send_mail_success), Toast.LENGTH_LONG).show();
    }

    private void showFailedResend(){
        Toast.makeText(this, getString(R.string.send_mail_failed), Toast.LENGTH_LONG).show();
    }

    private void saveUserName(User user){
        UserService userService = new UserService(getApplication());

        userService.logInUser(user) ;
    }

    private void login(){
        UserRestDao userRestDao = UserRestDao.getInstance();

        userToLogin.setPassword(Hashing.sha256().hashString(userToLogin.getPassword(), StandardCharsets.UTF_16).toString());

        Observable<Integer> observable = userRestDao.login(userToLogin);

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                Log.i("Logging", "Subscribed");
                onLogging();
            }

            @Override
            public void onNext(Integer integer) {
                Log.i("Logging", "Everything is ok");
                userToLogin.setId(integer);
                logIn(userToLogin);
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException && ((HttpException) e).code() == 401){
                    String body  = (String) ((HttpException) e).response().body();

                    if(TextAndNumberUtils.isNumber(body)){
                        Log.w("Logging", "User not activated");
                        askForResendingUserEmail(userToLogin);
                    } else {
                        Log.e("Logging", "User doesn't exist");
                        onLoginError(getString(R.string.user_data_not_exists));
                    }
                } else {
                    onLoginError(getString(R.string.unknown_exception_message));
                }
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(disposables != null) {
             disposables.dispose();
        }

        Intent start = new Intent(this, StartActivity.class);

        startActivity(start);
    }
}
