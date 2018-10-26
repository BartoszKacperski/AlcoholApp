package com.rolnik.alcoholapp.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.asynctasks.AutoTextService;
import com.rolnik.alcoholapp.dao.Dao;
import com.rolnik.alcoholapp.dao.RestDaoFactory;
import com.rolnik.alcoholapp.databinding.ActivityAddSaleBinding;
import com.rolnik.alcoholapp.model.Alcohol;
import com.rolnik.alcoholapp.model.Rate;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.Shop;
import com.rolnik.alcoholapp.utils.UserService;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.rolnik.alcoholapp.views.PricePickerDialog;

import org.springframework.web.client.HttpClientErrorException;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class AddSaleActivity extends AppCompatActivity {
    private ConstraintLayout root;
    private LinearLayout addSaleRoot;
    private CustomProgressBar customProgressBar;

    private Handler handler;

    private AutoCompleteTextView shops;
    private AutoCompleteTextView alcohols;
    private Button addButton;
    private TextView salePrice;


    private Sale saleToAdd;
    private Alcohol saleAlcohol;
    private Shop saleShop;

    private ActivityAddSaleBinding activityAddSaleBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddSaleBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_sale);

        initializeViews();
        bindSale();
        waitTillAsyncTasksCompleted();
        initializeShops();
        initializeAlcohols();
    }

    private void initializeViews() {
        root = findViewById(R.id.root);
        addSaleRoot = findViewById(R.id.addSaleRoot);
        customProgressBar = findViewById(R.id.customProgressBar);
        addButton = findViewById(R.id.addButton);
        salePrice = findViewById(R.id.salePrice);
    }

    private void bindSale(){
        saleToAdd = new Sale();

        activityAddSaleBinding.setSale(saleToAdd);

    }

    public void pickPrice(View view) {
        final PricePickerDialog pricePickerDialog = new PricePickerDialog(this);

        pricePickerDialog.setOkButtonActionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salePrice.setText(pricePickerDialog.getCurrencyFormattedPriceValue(new Locale("pl", "PL")));
                pricePickerDialog.dismiss();
            }
        });

        pricePickerDialog.show();
    }


    private void waitTillAsyncTasksCompleted() {
        customProgressBar.startAnimation();
        handler = new Handler(new Handler.Callback() {
            private int tasksFinished = 0;
            private int taskToFinish = 0;

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case AutoTextService.START: {
                        taskToFinish++;
                    }
                    case AutoTextService.END: {
                        tasksFinished++;
                        Log.i("Task finished", tasksFinished + " tasks finished");
                        if (tasksFinished >= taskToFinish) {
                            Log.i("Task finished", "All tasks finished");
                            loadUI();
                        }
                        break;
                    }
                    case AutoTextService.ERROR: {
                        Exception cause = (Exception) msg.obj;
                        Log.i("Task error", "Exception " + cause + " occurs");
                        hideUIWithBackToMenu(cause.getMessage());
                        break;
                    }

                }
                return false;
            }
        });
    }

    private void hideUIWithBackToMenu(String message) {
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
        backToMenu();
    }

    private void loadUI() {
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        addSaleRoot.setVisibility(View.VISIBLE);
    }

    private void initializeAlcohols() {
        alcohols = findViewById(R.id.alcohols);

        alcohols.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                saleAlcohol = ((Alcohol) alcohols.getAdapter().getItem(position));
            }
        });

        new AutoTextService<>(this, alcohols.getId(), RestDaoFactory.getAlcoholDao(), handler).execute();
    }

    private void initializeShops() {
        shops = findViewById(R.id.shops);

        shops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                saleShop = ((Shop) shops.getAdapter().getItem(position));
            }
        });

        new AutoTextService<>(this, shops.getId(), RestDaoFactory.getShopDao(), handler).execute();
    }


    private void backToMenu() {
        Intent menu = new Intent(this, MainActivity.class);

        startActivity(menu);
    }

    private void onAdding() {
        TransitionManager.beginDelayedTransition(root);
        addSaleRoot.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
    }

    private void onAddSuccess() {
        loadUI();
        Toast.makeText(this, "Pomyślnie dodano promocje", Toast.LENGTH_LONG).show();
    }


    private void onAddError(String message) {
        loadUI();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void addSale(View view) {
        if (saleAlcohol == null) {
            Toast.makeText(this, "Wybierz jeden z alkoholi", Toast.LENGTH_LONG).show();
        } else if (saleShop == null) {
            Toast.makeText(this, "Wybierz jeden ze sklepów", Toast.LENGTH_LONG).show();
        } else {
            UserService userService = new UserService(this);
            saleToAdd.setShop(saleShop);
            saleToAdd.setAlcohol(saleAlcohol);
            saleToAdd.setUser(userService.getLoggedUser());
            saleToAdd.setRate(new Rate());

            new AddSaleAsyncTask(this, saleToAdd).execute();
        }
    }

    private static class AddSaleAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<AddSaleActivity> activityWeakReference;
        private Exception exception = null;
        private Sale sale;

        public AddSaleAsyncTask(AddSaleActivity addSaleActivity, Sale sale) {
            activityWeakReference = new WeakReference<>(addSaleActivity);
            this.sale = sale;
        }

        @Override
        protected void onPreExecute() {
            AddSaleActivity activity = activityWeakReference.get();

            if (activity == null || activity.isFinishing()) {
                Log.e("Before add sale", "activity doesn't exist");
                cancel(true);
                return;
            }
            activity.onAdding();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!isCancelled()) {
                Dao<Sale> dao = RestDaoFactory.getSaleDao();

                try {
                    dao.add(sale);
                } catch (Exception e) {
                    exception = e;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _void) {
            AddSaleActivity activity = activityWeakReference.get();

            if (activity == null || activity.isFinishing()) {
                Log.e("Before add sale", "activity doesn't exist");
                return;
            } else if (exception != null) {
                Log.e("Before add sale", "Exception " + exception + " occurs");
                if(exception instanceof HttpClientErrorException){
                    showErrorDependsOnHttpStatus((HttpClientErrorException)exception, activity);
                } else {
                    String message = activity.getString(R.string.unknown_exception_message);
                    activity.onAddError(message);
                }
            } else {
                Log.i("After add sale", "Everything is ok");
            }

            activity.onAddSuccess();
        }

        private void showErrorDependsOnHttpStatus(HttpClientErrorException exception, AddSaleActivity activity){
            switch (exception.getStatusCode()){
                case BAD_REQUEST: {
                    String message = activity.getString(R.string.add_sale_exception);
                    activity.onAddError(message);
                    break;
                }
                default: {
                    String message = activity.getString(R.string.unknown_exception_message);
                    activity.onAddError(message);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(this, MainActivity.class);

        startActivity(main);
    }
}
