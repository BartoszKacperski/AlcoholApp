package com.rolnik.alcoholapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.dao.UserRestDao;
import com.rolnik.alcoholapp.model.User;

import java.lang.ref.WeakReference;

public class ResendUserEmail extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<Context> contextWeakReference;
    private User user;

    public ResendUserEmail(Context context, User user){
        contextWeakReference = new WeakReference<>(context);
        this.user = user;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        UserRestDao userRestDao = UserRestDao.getInstance();

        Log.i("Resend email", "Resending..");
        try {
            return userRestDao.resendEmail(user);
        } catch (Exception e){
            Log.e("Resend email", "Exception " + e + " occurs");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success){
        Context context = contextWeakReference.get();

        if(context == null){
            Log.e("Resending email", "Resending success = " + success + ", but context is null");
            return;
        }

        Log.i("Resending email", "Resending success = " + success);

        if(success){
            showSuccesResend(context);
        } else {
            showFailedResend(context);
        }
    }

    private void showSuccesResend(Context context){
        Toast.makeText(context, context.getString(R.string.send_mail_success), Toast.LENGTH_LONG).show();
    }

    private void showFailedResend(Context context){
        Toast.makeText(context, context.getString(R.string.send_mail_failed), Toast.LENGTH_LONG).show();
    }
}
