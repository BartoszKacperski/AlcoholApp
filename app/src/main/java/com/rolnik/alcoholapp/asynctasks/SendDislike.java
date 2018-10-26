package com.rolnik.alcoholapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rolnik.alcoholapp.adapters.SalesAdapter;
import com.rolnik.alcoholapp.dao.RestService;
import com.rolnik.alcoholapp.dao.UserOpinionDatabase;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.model.UserOpinion;
import com.rolnik.alcoholapp.utils.UserService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;

public class SendDislike extends AsyncTask<Void, Void, Void> {
    private WeakReference<Context> contextWeakReference;
    private WeakReference<SalesAdapter.MyViewHolder> myViewHolderWeakReference;
    private RestTemplate restTemplate;

    private Exception exception;
    private String URL;

    private Sale sale;
    private int baseDislikes;

    public SendDislike(SalesAdapter.MyViewHolder myViewHolder, Sale sale, Context context) {
        this.contextWeakReference = new WeakReference<>(context);
        this.myViewHolderWeakReference =  new WeakReference<>(myViewHolder);
        URL = RestService.BASE_ADDRESS + "/sales" + "/" + sale.getId() + "/dislike";
        this.sale = sale;
        this.baseDislikes = sale.getRate().getNegativeRates();
    }


    @Override
    protected void onPreExecute() {
        SalesAdapter.MyViewHolder myViewHolder = myViewHolderWeakReference.get();

        if(myViewHolder == null) {
            Log.e("Send dislike", "Holder is null");
            return;
        }
        restTemplate = new RestTemplate();
        sale.getRate().setNegativeRates(baseDislikes + 1);
        sale.setWasDisliked(true);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            sendDislikeToServer();
            sendDislikeToLocalDatabase(sale);
        } catch (Exception e){
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void _void) {
        SalesAdapter.MyViewHolder myViewHolder = myViewHolderWeakReference.get();

        if(myViewHolder == null){
            Log.e("Send dislike", "Holder is null");
            return;
        } else if (exception != null){
            Log.e("Send dislike", "Exception " + exception.getMessage() + " occurs");
            sale.getRate().setNegativeRates(baseDislikes);
            sale.setWasDisliked(false);
        } else {
            Log.i("Send dislike", "Add rate successful");
        }
    }

    private User getUser(){
        Context context = contextWeakReference.get();

        if(context == null){
            Log.e("Send like", "Getting logged user failed");
            throw new RuntimeException("Context is null");
        }

        UserService userService = new UserService(context);

        return userService.getLoggedUser();
    }

    private void sendDislikeToServer(){
        User user = getUser();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> entity = new HttpEntity<>(Integer.toString(user.getId()), httpHeaders);

        restTemplate.exchange(URL, HttpMethod.PUT, entity, String.class);
    }

    private void sendDislikeToLocalDatabase(Sale sale) {
        Context context = contextWeakReference.get();

        if(context == null){
            Log.e("Send like", "Saving in local database failed");
            return;
        }

        UserOpinionDatabase userOpinionDatabase = UserOpinionDatabase.getUserOpinionDatabase(context);

        UserOpinion userOpinion = new UserOpinion();

        userOpinion.setOpinion(-1);
        userOpinion.setSale(sale);

        userOpinionDatabase.userOpinionDao().insert(userOpinion);
        Log.i("Send like", "Saving in local database success");
    }
}