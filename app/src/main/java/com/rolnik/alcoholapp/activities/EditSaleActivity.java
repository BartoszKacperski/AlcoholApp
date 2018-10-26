package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.asynctasks.AutoTextService;
import com.rolnik.alcoholapp.dao.RestDaoFactory;
import com.rolnik.alcoholapp.dao.SaleRestDao;
import com.rolnik.alcoholapp.databinding.ActivityEditSaleBinding;
import com.rolnik.alcoholapp.model.Alcohol;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.Shop;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.vstechlab.easyfonts.EasyFonts;

import org.springframework.web.client.HttpClientErrorException;

import java.lang.ref.WeakReference;

public class EditSaleActivity extends AppCompatActivity {
    private ConstraintLayout root;
    private LinearLayout description;
    private LinearLayout rates;

    private CustomProgressBar customProgressBar;

    private AutoCompleteTextView shops;
    private AutoTextService<Shop> fillShops;
    private AutoCompleteTextView alcohols;
    private AutoTextService<Alcohol> fillAlcohols;

    private Handler handler;

    private EditText price;
    private TextView positiveNumber;
    private TextView negativeNumber;

    private Sale saleToUpdate;
    private Alcohol alcoholToUpdate;
    private Shop shopToUpdate;
    private double oldPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEditSaleBinding activityEditSaleBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_sale);

        if (!getIntent().hasExtra(getString(R.string.sale))) {
            Log.w("Sale description", "Sale is null");
            backToMySales();
        }

        root = findViewById(R.id.root);
        description = findViewById(R.id.description);
        rates = findViewById(R.id.rates);

        customProgressBar = findViewById(R.id.customProgressBar);

        shops = findViewById(R.id.shops);
        alcohols = findViewById(R.id.alcohols);
        price = findViewById(R.id.price);

        positiveNumber = findViewById(R.id.positiveNumber);
        negativeNumber = findViewById(R.id.negativeNumber);


        saleToUpdate = (Sale) getIntent().getSerializableExtra(getString(R.string.sale));
        alcoholToUpdate = saleToUpdate.getAlcohol();
        shopToUpdate = saleToUpdate.getShop();
        oldPrice = saleToUpdate.getPrice();
        activityEditSaleBinding.setSale(saleToUpdate);

        changeTypeFace(EasyFonts.walkwayBlack(getApplication()));

        waitTillAsyncTasksCompleted();
        initializeShops();
        initializeAlcohols();
    }

    private void waitTillAsyncTasksCompleted() {
        customProgressBar.startAnimation();
        handler = new Handler(new Handler.Callback() {
            private int tasksFinished = 0;
            private int tasksToFinish = 0;

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case AutoTextService.START: {
                        tasksToFinish++;
                        Log.i("Search activity", "Tasks to finish " + tasksToFinish);
                        break;
                    }
                    case AutoTextService.END: {
                        tasksFinished++;
                        Log.i("Search activity", tasksFinished + " tasks finished");
                        if (tasksFinished >= tasksToFinish) {
                            Log.i("Search activity", "All tasks finished");
                            loadUI();
                        }
                        break;
                    }
                    case AutoTextService.ERROR: {
                        Exception cause = (Exception) msg.obj;
                        Log.i("Search activity", "Exception " + cause + " occurs");
                        hideUIWithBackToMySales(cause.getMessage());
                        break;
                    }

                }
                return false;
            }
        });
    }

    private void hideUIWithBackToMySales(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        backToMySales();
    }

    private void loadUI() {
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        description.setVisibility(View.VISIBLE);
        rates.setVisibility(View.VISIBLE);
    }

    private void initializeAlcohols() {
        fillAlcohols = new AutoTextService<>(this, alcohols.getId(), RestDaoFactory.getAlcoholDao(), handler);

        fillAlcohols.execute();

        alcohols.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alcoholToUpdate = (Alcohol) alcohols.getAdapter().getItem(position);
            }
        });

        alcohols.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alcoholToUpdate = null;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initializeShops() {
        fillShops = new AutoTextService<>(this, shops.getId(), RestDaoFactory.getShopDao(), handler);

        fillShops.execute();

        shops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shopToUpdate = (Shop) shops.getAdapter().getItem(position);
            }
        });

        shops.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shopToUpdate = null;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void changeTypeFace(Typeface typeface) {
        price.setTypeface(typeface);
        negativeNumber.setTypeface(typeface);
        positiveNumber.setTypeface(typeface);
    }

    private void backToMySales() {
        Intent sales = new Intent(this, MySalesActivity.class);

        startActivity(sales);
    }

    private void onUpdating() {
        TransitionManager.beginDelayedTransition(root);
        description.setVisibility(View.GONE);
        rates.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
    }

    private void onUpdateSuccess() {
        customProgressBar.endAnimation();
        backToMySales();
    }

    private void onUpdateError(String message) {
        loadUI();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void update(View view) {
        if (alcoholToUpdate == null) {
            Toast.makeText(this, "Wybierz alkohol!!!!!", Toast.LENGTH_LONG).show();
        } else if (shopToUpdate == null) {
            Toast.makeText(this, "Wybierz sklep!!!!!", Toast.LENGTH_LONG).show();
        } else if(saleToUpdate.getAlcohol().getId() == alcoholToUpdate.getId() && saleToUpdate.getShop().getId() == shopToUpdate.getId() && oldPrice == saleToUpdate.getPrice()){
            Toast.makeText(this, "NIC NIE ZMIENIŁEŚ SPRYCIARZU XD", Toast.LENGTH_LONG).show();
        } else {
            saleToUpdate.setAlcohol(alcoholToUpdate);
            saleToUpdate.setShop(shopToUpdate);

            ChangeSale changeSale = new ChangeSale(this, saleToUpdate, ChangeSale.UPDATE);
            changeSale.execute();
        }
    }

    public void remove(View view) {
        ChangeSale changeSale = new ChangeSale(this, saleToUpdate, ChangeSale.REMOVE);

        changeSale.execute();
    }

    private static class ChangeSale extends AsyncTask<Void, Void, Void> {
        public static final int UPDATE = 1;
        public static final int REMOVE = 2;

        private WeakReference<EditSaleActivity> editSaleActivityWeakReference;
        private Sale saleToUpdate;
        private Exception exception = null;
        private int mode;

        public ChangeSale(EditSaleActivity editSaleActivity, Sale sale, int mode) {
            this.editSaleActivityWeakReference = new WeakReference<>(editSaleActivity);
            this.saleToUpdate = sale;
            this.mode = mode;
        }

        @Override
        protected void onPreExecute() {
            EditSaleActivity editSaleActivity = editSaleActivityWeakReference.get();

            if (editSaleActivity == null || editSaleActivity.isFinishing()) {
                Log.e("Change sale", "Activity is null");
                cancel(true);
                return;
            }

            editSaleActivity.onUpdating();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!isCancelled()) {
                SaleRestDao saleRestDao = SaleRestDao.getInstance();

                Log.i("Change sale", "Starting " + (mode == UPDATE ? "update" : "remove"));

                try {
                    if (mode == UPDATE) {
                        saleRestDao.update(saleToUpdate);
                    } else {
                        saleRestDao.remove(saleToUpdate);
                    }
                } catch (Exception e) {
                    exception = e;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _void) {
            EditSaleActivity editSaleActivity = editSaleActivityWeakReference.get();

            if (editSaleActivity == null || editSaleActivity.isFinishing()) {
                Log.e("Change sale", "Activity is null");
                return;
            } else if (exception != null) {
                Log.e("Change sale", "Exception " + exception + " occurs");

                if(exception instanceof  HttpClientErrorException){
                    showErrorDependsOnHttpStatus((HttpClientErrorException) exception, editSaleActivity);
                } else {
                    String message = editSaleActivity.getString(R.string.unknown_exception_message);
                    editSaleActivity.onUpdateError(message);
                }

                return;
            }

            Log.i("Change sale", "Everthing is ok");
            editSaleActivity.onUpdateSuccess();
        }

        private void showErrorDependsOnHttpStatus(HttpClientErrorException exception, EditSaleActivity activity){
            switch (exception.getStatusCode()){
                case BAD_REQUEST: {
                    String message = activity.getString(R.string.edit_sale_exception);
                    activity.onUpdateError(message);
                    break;
                }
                default: {
                    String message = activity.getString(R.string.unknown_exception_message);
                    activity.onUpdateError(message);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        backToMySales();
    }
}
