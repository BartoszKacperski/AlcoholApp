package com.rolnik.alcoholapp.activities;

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

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.dao.UserRestDao;
import com.rolnik.alcoholapp.databinding.ActivityLoginBinding;
import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.restUtils.AsyncResponse;
import com.rolnik.alcoholapp.restUtils.ResponseHandler;
import com.rolnik.alcoholapp.utils.CookieService;
import com.rolnik.alcoholapp.utils.UserService;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.rolnik.alcoholapp.views.ErrorDialog;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements ResponseHandler<Response<Void>> {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityLoginBinding.setUser(new User());
        ButterKnife.bind(this);
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
        CookieService cookieService = new CookieService(this);
        Log.i("Saving cookie", "Cookie = " + cookie);
        cookieService.saveCookie(cookie);
    }

    private void saveUserName(User user){
        UserService userService = new UserService(getApplication());

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
        showError(getString(R.string.user_not_exists));
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

    private Observable<Response<Void>> getPaperedObservable(){
        UserRestDao userRestDao = UserRestDao.getInstance();

        return userRestDao.login(activityLoginBinding.getUser());
    }

    private void login(){
        AsyncResponse<Response<Void>> asyncResponse = new AsyncResponse<>(getPaperedObservable(), this);

        asyncResponse.execute();
    }

    private void askForResendingUserEmail(final User user){
        showGUI();
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

        Observable<Boolean> observable = userRestDao.resendEmail(activityLoginBinding.getUser());

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
