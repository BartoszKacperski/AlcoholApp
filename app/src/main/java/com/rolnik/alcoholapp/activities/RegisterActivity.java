package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.hash.Hashing;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.dao.UserRestDao;
import com.rolnik.alcoholapp.restUtils.AsyncResponse;
import com.rolnik.alcoholapp.restUtils.ResponseHandler;
import com.rolnik.alcoholapp.utils.UserService;
import com.rolnik.alcoholapp.dao.Dao;
import com.rolnik.alcoholapp.dao.RestDaoFactory;
import com.rolnik.alcoholapp.databinding.ActivityRegisterBinding;
import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.views.CustomProgressBar;

import org.springframework.web.client.HttpClientErrorException;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements ResponseHandler<Integer> {
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

    private ActivityRegisterBinding activityRegisterBinding;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        ButterKnife.bind(this);

        bindUser();
    }

    public void tryToRegister(View view) {
        if(!checkIfEditTextsAreFill()){
            Toast.makeText(this, "Uzupełnij wszystki pola!", Toast.LENGTH_LONG).show();
        } else if(!checkIfPasswordsAreEqual()){
            Toast.makeText(this, "Hasła są różne", Toast.LENGTH_LONG).show();
        } else if(!checkIfEmailIsCorrect()) {
            Toast.makeText(this, "Zły format emailu", Toast.LENGTH_LONG).show();
        } else {
            register();
        }
    }


    private void bindUser() {
        activityRegisterBinding.setUser(new User());
    }

    private boolean checkIfEditTextsAreFill(){
        return checkIfEditTextIsFill(login) && checkIfEditTextIsFill(password) && checkIfEditTextIsFill(passwordConfirm) && checkIfEditTextIsFill(email);
    }

    private boolean checkIfEditTextIsFill(EditText editText){
        String text = editText.getText().toString();
        return text.length() > 0;
    }

    private boolean checkIfPasswordsAreEqual(){
        String firstPassword = password.getText().toString();
        String secondPassword = password.getText().toString();

        return firstPassword.equals(secondPassword);
    }

    private boolean checkIfEmailIsCorrect(){
        String emailText = email.getText().toString();

        return emailText.matches("^[A-Za-z0-9+_.-]+@(.+)\\.(.+)$");
    }

    private void moveToStartActivity(){
        Intent intent = new Intent(this, StartActivity.class);

        startActivity(intent);
    }

    private void showGUI(){
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        registerRoot.setVisibility(View.VISIBLE);
    }

    private void hideGUI(){
        TransitionManager.beginDelayedTransition(root);
        registerRoot.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
    }

    @Override
    public void onNext(Integer integerResponse) {
        moveToStartActivity();
        showGUI();
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


    private void register(){
        UserRestDao userRestDao = UserRestDao.getInstance();

        AsyncResponse<Integer> asyncResponse = new AsyncResponse<>(userRestDao.register(activityRegisterBinding.getUser()), this, false);

        asyncResponse.execute();
    }



    @Override
    public void onBackPressed() {
        Intent start = new Intent(this, StartActivity.class);

        startActivity(start);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(disposable != null){
            disposable.dispose();
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
        hideGUI();
    }

}
