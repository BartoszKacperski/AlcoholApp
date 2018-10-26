package com.rolnik.alcoholapp.asynctasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.adapters.CustomArrayAdapter;
import com.rolnik.alcoholapp.dao.Dao;
import com.rolnik.alcoholapp.dao.RestDaoFactory;
import com.rolnik.alcoholapp.model.Alcohol;
import com.rolnik.alcoholapp.model.GetNameProvider;


import java.lang.ref.WeakReference;
import java.util.List;

public class AutoTextService<T extends GetNameProvider> extends AsyncTask<Void, Void, List<T>> {
    public static final int START = 0;
    public static final int RUN = 1;
    public static final int END = 2;
    public static final int ERROR = 3;

    private WeakReference<Activity> activityWeakReference;
    private Exception exception = null;
    private int autoTextResource;
    private Dao<T> dao;
    private Handler handler;

    public AutoTextService(Activity activity, int autoTextResource, Dao<T> dao, Handler handler) {
        activityWeakReference = new WeakReference<>(activity);
        this.autoTextResource = autoTextResource;
        this.dao = dao;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute(){
        Activity activity = activityWeakReference.get();
        handler.sendEmptyMessage(START);

        if(activity == null || activity.isFinishing()) {
            cancel(true);
            Log.e("Before download", "Activity doesn't exits, asynctask cancelled");
        }
    }

    @Override
    protected List<T> doInBackground(Void... voids) {
        if(!isCancelled()){
            try {
                handler.sendEmptyMessage(RUN);
                return dao.getAll();
            } catch (Exception e) {
                exception = e;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<T> alcohols) {
        Activity activity = activityWeakReference.get();

        if (activity == null || activity.isFinishing()) {
            Log.e("After download", "Activity doesn't exist");
        } else if (exception != null) {
            Log.e("After download", "Exception " + exception.getMessage() + " occurs");
            Message errorMessage = new Message();
            errorMessage.what = ERROR;
            errorMessage.obj = exception;
            handler.sendMessage(errorMessage);
        } else {
            AutoCompleteTextView autoCompleteTextView = activity.findViewById(autoTextResource);

            ArrayAdapter<T> adapter = new CustomArrayAdapter<>(activity, R.layout.autocomplete_layout, alcohols);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setThreshold(1);
            Log.i("After download", "Everything is ok");
            handler.sendEmptyMessage(END);
        }

    }
}
