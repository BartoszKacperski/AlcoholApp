package com.rolnik.alcoholapp.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import com.rolnik.alcoholapp.asynctasks.ResendUserEmail;
import com.rolnik.alcoholapp.utils.UserService;
import com.rolnik.alcoholapp.dao.UserRestDao;
import com.rolnik.alcoholapp.databinding.ActivityLoginBinding;
import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.rolnik.alcoholapp.views.ErrorDialog;

import org.springframework.web.client.HttpClientErrorException;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {
    private ConstraintLayout root;

    private EditText login;
    private EditText password;
    private Button loginButton;
    private ImageView loginImage;
    private LinearLayout loginRoot;

    private CustomProgressBar customProgressBar;

    private User userToLogin;

    private ActivityLoginBinding activityLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        initializeViews();
        bindUser();
    }

    private void initializeViews(){
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        loginImage = findViewById(R.id.loginImage);
        loginRoot = findViewById(R.id.loginRoot);
        root = findViewById(R.id.root);

        customProgressBar = findViewById(R.id.customProgressBar);
    }

    private void bindUser(){
        userToLogin = new User();
        activityLoginBinding.setUser(userToLogin);
    }

    private Context getContext(){
        return this;
    }

    public void tryToLogin(View view) {
        if(checkIfEditTextsAreFill()){
            new LoginAsyncTask(this, userToLogin).execute();
        } else {
            Toast.makeText(this, "Uzupełnij dane do logowania", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfEditTextsAreFill(){
        return checkIfEditTextIsFill(login) && checkIfEditTextIsFill(password);
    }

    private boolean checkIfEditTextIsFill(EditText editText){
        String text = editText.getText().toString();
        return text.length() > 0;
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
                ResendUserEmail resendUserEmail = new ResendUserEmail(getContext(), user);
                resendUserEmail.execute();
                errorDialog.close();
            }
        });

        errorDialog.show();
    }


    private void saveUserName(User user){
        UserService userService = new UserService(getApplication());

        userService.logInUser(user) ;
    }

    private static class LoginAsyncTask extends AsyncTask<Void, Void, Integer>{
        private WeakReference<LoginActivity> loginActivityWeakReference;
        private Exception exception = null;
        private User user;

        public LoginAsyncTask(LoginActivity loginActivity, User user){
            loginActivityWeakReference = new WeakReference<>(loginActivity);
            this.user = copyUser(user);
        }

        private User copyUser(User userToCopy){
            User user = new User();
            user.setLogin(userToCopy.getLogin());
            user.setPassword(userToCopy.getPassword());

            return user;
        }

        @Override
        protected void onPreExecute(){
            LoginActivity loginActivity = loginActivityWeakReference.get();

            if(loginActivity == null || loginActivity.isFinishing()){
                Log.e("Logging", "Activity doesn't exist");
                return;
            }

            loginActivity.onLogging();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            UserRestDao userDao = UserRestDao.getInstance();
            String password = user.getPassword();
            String hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_16).toString();
            user.setPassword(hashedPassword);
            try {
                return userDao.login(user);
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Integer userId){
            LoginActivity loginActivity = loginActivityWeakReference.get();

            if(loginActivity == null || loginActivity.isFinishing()){
                Log.e("Logging", "Activity doesn't exist");
                return;
            } else if(exception != null) {
                Log.e("Logging", "Exception " + exception.getMessage() + " occurs");

                if(exception instanceof HttpClientErrorException){
                    userId = Integer.valueOf(((HttpClientErrorException) exception).getResponseBodyAsString());
                } else {
                    String message = loginActivity.getString(R.string.unknown_exception_message);
                    loginActivity.onLoginError(message);
                    return;
                }
            }

            Log.i("Logging", "User id = " + userId);

            if(userId == null){
                Log.e("Logging", "User doesn't exist");
                String message = loginActivity.getString(R.string.user_not_exists);
                loginActivity.onLoginError(message);
            } else if(userId >= 0) {
                Log.i("Logging", "Everything is ok");
                user.setId(userId);
                loginActivity.logIn(user);
            } else {
                Log.w("Logging", "User not activated");
                user.setId(-userId);
                loginActivity.askForResendingUserEmail(user);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent start = new Intent(this, StartActivity.class);

        startActivity(start);
    }
}
