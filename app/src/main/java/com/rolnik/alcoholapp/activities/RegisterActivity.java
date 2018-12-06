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

public class RegisterActivity extends AppCompatActivity {
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

    private User userToRegister;

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
        userToRegister = new User();
        activityRegisterBinding.setUser(userToRegister);
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

    private void showUI(){
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        registerRoot.setVisibility(View.VISIBLE);
    }

    private void onRegistering(){
        TransitionManager.beginDelayedTransition(root);
        registerRoot.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
    }

    private void onRegisterSuccess(){
        showUI();
        moveToStartActivity();
    }

    private void onRegisterError(String message){
        showUI();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void register(){
        UserRestDao userRestDao = UserRestDao.getInstance();

        userToRegister.setPassword(Hashing.sha256().hashString(userToRegister.getPassword(), StandardCharsets.UTF_16).toString());

        Observable<Integer> observable = userRestDao.register(userToRegister);

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
                onRegistering();
                Log.i("Register", "Subscribed");
            }

            @Override
            public void onNext(Integer integer) {
                Log.i("Register", "Completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Register", "Exception " + e.getCause() + " occurs");
                if(e instanceof HttpException){
                    showErrorDependsOnHttpStatus((HttpException) e);
                } else {
                    onRegisterError(getString(R.string.unknown_exception_message));
                }
            }

            @Override
            public void onComplete() {
                onRegisterSuccess();
            }
        });
    }

    private void showErrorDependsOnHttpStatus(HttpException exception){
        switch (exception.code()){
            case 400: {
                onRegisterError(getString(R.string.user_add_already_exist_excpetion));
                break;
            }
            default: {
                onRegisterError(getString(R.string.unknown_exception_message));
                break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        if(disposable != null){
            disposable.dispose();
        }

        Intent start = new Intent(this, StartActivity.class);

        startActivity(start);
    }
}
