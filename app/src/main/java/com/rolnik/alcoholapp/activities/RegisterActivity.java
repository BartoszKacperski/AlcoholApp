package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rolnik.alcoholapp.MyApplication;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.clientservices.UserClientService;
import com.rolnik.alcoholapp.databinding.ActivityRegisterBinding;
import com.rolnik.alcoholapp.dto.User;
import com.rolnik.alcoholapp.rests.UserRest;
import com.rolnik.alcoholapp.restutils.AsyncResponse;
import com.rolnik.alcoholapp.restutils.ResponseHandler;
import com.rolnik.alcoholapp.views.CustomProgressBar;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity implements ResponseHandler<Response<Void>> {
    @BindView(R.id.root)
    ConstraintLayout root;

    @BindView(R.id.customProgressBar)
    CustomProgressBar customProgressBar;

    @BindView(R.id.registerRoot)
    LinearLayout registerRoot;
    @BindView(R.id.login)
    EditText login;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.passwordConfirm)
    EditText passwordConfirm;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.registerButton)
    Button registerButton;


    @Named("without_cookie")
    @Inject
    Retrofit retrofit;
    private ActivityRegisterBinding activityRegisterBinding;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).getNetComponent().inject(this);
        bindUser();
    }

    public void tryToRegister(View view) {
        if (!checkIfEditTextsAreFill()) {
            Toast.makeText(this, getString(R.string.register_empty_input), Toast.LENGTH_LONG).show();
        } else if (!checkIfPasswordsAreEqual()) {
            Toast.makeText(this, getString(R.string.different_passwords), Toast.LENGTH_LONG).show();
        } else if (!checkIfEmailIsCorrect()) {
            Toast.makeText(this, getString(R.string.bad_email), Toast.LENGTH_LONG).show();
        } else {
            register();
        }
    }


    private void bindUser() {
        activityRegisterBinding.setUser(new User());
    }

    private boolean checkIfEditTextsAreFill() {
        return checkIfEditTextIsFill(login) && checkIfEditTextIsFill(password) && checkIfEditTextIsFill(passwordConfirm) && checkIfEditTextIsFill(email);
    }

    private boolean checkIfEditTextIsFill(EditText editText) {
        String text = editText.getText().toString();
        return text.length() > 0;
    }

    private boolean checkIfPasswordsAreEqual() {
        String firstPassword = password.getText().toString();
        String secondPassword = passwordConfirm.getText().toString();

        return firstPassword.equals(secondPassword);
    }

    private boolean checkIfEmailIsCorrect() {
        String emailText = email.getText().toString();

        return emailText.matches("^[A-Za-z0-9+_.-]+@(.+)\\.(.+)$");
    }

    private void moveToStartActivity() {
        Intent intent = new Intent(this, StartActivity.class);

        startActivity(intent);
    }

    private void showGUI() {
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        registerRoot.setVisibility(View.VISIBLE);
    }

    private void hideGUI() {
        TransitionManager.beginDelayedTransition(root);
        registerRoot.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
        hideGUI();
    }

    @Override
    public void onNext(Response<Void> response) {
        if (response.isSuccessful()) {
            showGUI();
            moveToStartActivity();
        } else {
            switch (response.code()) {
                case 400:
                    onBadRequest();
                    break;
                case 401:
                    onNotAuthorized();
                    break;
                default:
                    onUnknownError();
                    break;
            }
        }

    }

    @Override
    public void onComplete() {

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
        showError(getString(R.string.register_exception));
    }

    @Override
    public void onUnknownError() {
        showError(getString(R.string.unknown_exception_message));
    }

    @Override
    public void showError(String message) {
        showGUI();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    private void register() {
        UserClientService userClientService = new UserClientService(retrofit.create(UserRest.class));

        AsyncResponse<Response<Void>> asyncResponse = new AsyncResponse<>(userClientService.register(activityRegisterBinding.getUser()), this);

        asyncResponse.execute();
    }


    @Override
    public void onBackPressed() {
        Intent start = new Intent(this, StartActivity.class);

        startActivity(start);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (disposable != null) {
            disposable.dispose();
        }
    }


}
