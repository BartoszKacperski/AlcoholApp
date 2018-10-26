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

public class RegisterActivity extends AppCompatActivity {
    private ConstraintLayout root;

    private CustomProgressBar customProgressBar;

    private LinearLayout registerRoot;
    private EditText login;
    private EditText password;
    private EditText passwordConfirm;
    private EditText email;
    private Button registerButton;

    private User userToRegister;

    private ActivityRegisterBinding activityRegisterBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        initializeViews();
        bindUser();
    }

    private void initializeViews(){
        root = findViewById(R.id.root);

        customProgressBar = findViewById(R.id.customProgressBar);

        registerRoot = findViewById(R.id.registerRoot);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        passwordConfirm = findViewById(R.id.passwordConfirm);
        registerButton = findViewById(R.id.registerButton);
        email = findViewById(R.id.email);
    }

    private void bindUser() {
        userToRegister = new User();
        activityRegisterBinding.setUser(userToRegister);
    }

    public void addAccount(View view) {
        if(!checkIfEditTextsAreFill()){
            Toast.makeText(this, "Uzupełnij wszystki pola!", Toast.LENGTH_LONG).show();
        } else if(!checkIfPasswordsAreEqual()){
            Toast.makeText(this, "Hasła są różne", Toast.LENGTH_LONG).show();
        } else if(!checkIfEmailIsCorrect()) {
            Toast.makeText(this, "Zły format emailu", Toast.LENGTH_LONG).show();
        } else {
            new AddAccountAsyncTask(this, userToRegister).execute();
        }
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

    public void moveToStartActivity(){
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

    private static class AddAccountAsyncTask extends AsyncTask<Void, Void, Integer>{
        private WeakReference<RegisterActivity> registerActivityWeakReference;
        private Exception exception = null;
        private User user;

        public AddAccountAsyncTask(RegisterActivity registerActivity, User user){
            registerActivityWeakReference = new WeakReference<>(registerActivity);
            this.user = copyUser(user);
        }

        private User copyUser(User userToCopy){
            User user = new User();

            user.setLogin(userToCopy.getLogin());
            user.setPassword(userToCopy.getPassword());
            user.setEmail(userToCopy.getEmail());

            return user;
        }

        @Override
        protected void onPreExecute(){
            RegisterActivity registerActivity = registerActivityWeakReference.get();

            if(registerActivity == null || registerActivity.isFinishing()){
                cancel(true);
                Log.e("Add user", "Activity doesn't exist");
                return;
            }

            registerActivity.onRegistering();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if(!isCancelled()){
                UserRestDao userDao = UserRestDao.getInstance();
                String password = user.getPassword();
                String hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_16).toString();
                user.setPassword(hashedPassword);

                try {
                    return userDao.register(user);
                } catch (HttpClientErrorException e) {
                    exception = e;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer id){
            RegisterActivity registerActivity = registerActivityWeakReference.get();

            if(registerActivity == null || registerActivity.isFinishing()){
                Log.e("Add user", "Activity doesn't exist");
                return;
            } else if(exception != null) {
                Log.e("Add user", "Exception " + exception.getMessage() + " occurs");

                if(exception instanceof HttpClientErrorException){
                    showErrorDependsOnHttpStatus((HttpClientErrorException)exception, registerActivity);
                } else {
                    String message = registerActivity.getString(R.string.unknown_exception_message);
                    registerActivity.onRegisterError(message);
                }

                return;
            }

            if(id != null){
                registerActivity.onRegisterSuccess();
            }
        }

        private void showErrorDependsOnHttpStatus(HttpClientErrorException exception, RegisterActivity activity){
            switch (exception.getStatusCode()){
                case BAD_REQUEST:a: {
                    String message = activity.getString(R.string.user_add_already_exist_excpetion);
                    activity.onRegisterError(message);
                    break;
                }
                default: {
                    String message = activity.getString(R.string.unknown_exception_message);
                    activity.onRegisterError(message);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent start = new Intent(this, StartActivity.class);

        startActivity(start);
    }
}
