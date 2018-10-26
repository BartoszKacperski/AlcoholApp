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

public class SendLike extends AsyncTask<Void, Void, Void> {
    private WeakReference<Context> contextWeakReference;
    private WeakReference<SalesAdapter.MyViewHolder> myViewHolderWeakReference;
    private RestTemplate restTemplate;

    private Exception exception;
    private String URL;

    private Sale sale;
    private int baseLikes;

    public SendLike(SalesAdapter.MyViewHolder myViewHolder, Sale sale, Context context) {
        this.myViewHolderWeakReference =  new WeakReference<>(myViewHolder);
        URL = RestService.BASE_ADDRESS + "/sales" + "/" + sale.getId() + "/like";
        this.sale = sale;
        this.baseLikes = sale.getRate().getPositiveRates();
        this.contextWeakReference = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        SalesAdapter.MyViewHolder myViewHolder = myViewHolderWeakReference.get();

        if(myViewHolder == null) {
            Log.e("Send like", "Holder is null");
            return;
        }
        restTemplate = new RestTemplate();
        sale.getRate().setPositiveRates(baseLikes + 1);
        sale.setWasLiked(true);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            sendLikeToServer();
            sendLikeToLocalDatabase(sale);
        } catch (Exception e){
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void _void) {
        SalesAdapter.MyViewHolder myViewHolder = myViewHolderWeakReference.get();

        if(myViewHolder == null){
            Log.e("Send like", "Holder is null");
            return;
        } else if (exception != null){
            Log.e("Send like", "Exception " + exception.getMessage() + " occurs");
            sale.getRate().setPositiveRates(baseLikes);
            sale.setWasLiked(false);
        } else {
            Log.i("Send like", "Add rate successful");
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

    private void sendLikeToServer(){
        User user = getUser();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> entity = new HttpEntity<>(Integer.toString(user.getId()), httpHeaders);

        restTemplate.exchange(URL, HttpMethod.PUT, entity, String.class);
    }

    private void sendLikeToLocalDatabase(Sale sale) {
        Context context = contextWeakReference.get();

        if(context == null){
            Log.e("Send like", "Saving in local database failed");
            return;
        }

        UserOpinionDatabase userOpinionDatabase = UserOpinionDatabase.getUserOpinionDatabase(context);

        UserOpinion userOpinion = new UserOpinion();

        userOpinion.setOpinion(1);
        userOpinion.setSale(sale);

        userOpinionDatabase.userOpinionDao().insert(userOpinion);
        Log.i("Send like", "Saving in local database success");
    }
}