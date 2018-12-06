package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.adapters.CustomArrayAdapter;
import com.rolnik.alcoholapp.dao.Dao;
import com.rolnik.alcoholapp.dao.RestDaoFactory;
import com.rolnik.alcoholapp.databinding.ActivityEditSaleBinding;
import com.rolnik.alcoholapp.model.Alcohol;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.Shop;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class EditSaleActivity extends AppCompatActivity {
    @BindView(R.id.root)
    ConstraintLayout root;
    @BindView(R.id.description)
    LinearLayout description;
    @BindView(R.id.rates)
    LinearLayout rates;

    @BindView(R.id.customProgressBar)
    CustomProgressBar customProgressBar;

    @BindView(R.id.shops)
    AutoCompleteTextView shops;
    @BindView(R.id.alcohols)
    AutoCompleteTextView alcohols;

    @BindView(R.id.price)
    EditText price;
    @BindView(R.id.positiveNumber)
    TextView positiveNumber;
    @BindView(R.id.negativeNumber)
    TextView negativeNumber;


    private CompositeDisposable disposables = new CompositeDisposable();

    private Sale saleToUpdate;
    private Alcohol alcoholToUpdate;
    private Shop shopToUpdate;
    private double oldPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEditSaleBinding activityEditSaleBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_sale);
        ButterKnife.bind(this);

        if (!getIntent().hasExtra(getString(R.string.sale))) {
            Log.w("Sale description", "Sale is null");
            backToMySales();
        }


        saleToUpdate = (Sale) getIntent().getSerializableExtra(getString(R.string.sale));
        alcoholToUpdate = saleToUpdate.getAlcohol();
        shopToUpdate = saleToUpdate.getShop();
        oldPrice = saleToUpdate.getPrice();

        activityEditSaleBinding.setSale(saleToUpdate);

        changeTypeFace(EasyFonts.walkwayBlack(getApplication()));

        initializeAutoText();
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

            update();
        }
    }

    public void remove(View view) {
        remove();
    }

    private void initializeAutoText(){
        Dao<Alcohol> alcoholDao = RestDaoFactory.getAlcoholDao();
        Dao<Shop> shopDao = RestDaoFactory.getShopDao();

        Observable<List<Alcohol>> alcohols = alcoholDao.getAll();
        Observable<List<Shop>> shops = shopDao.getAll();

        Observable.combineLatest(alcohols, shops, new BiFunction<List<Alcohol>, List<Shop>, Pair<List<Alcohol>, List<Shop>>>() {
            @Override
            public Pair<List<Alcohol>, List<Shop>> apply(List<Alcohol> alcohols, List<Shop> shops) {
                return new Pair<>(alcohols, shops);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Pair<List<Alcohol>, List<Shop>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                Log.i("Initialize autoText", "Subscribed");
                customProgressBar.startAnimation();
            }

            @Override
            public void onNext(Pair<List<Alcohol>, List<Shop>> result) {
                initializeAlcohols(result.first);
                initializeShops(result.second);
            }

            @Override
            public void onError(Throwable e) {
                hideUIWithBackToMySales(e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i("Initialize autoText", "All autoText filled");
                loadUI();
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

    private void initializeAlcohols(List<Alcohol> alcoholList) {
        ArrayAdapter<Alcohol> adapter = new CustomArrayAdapter<>(this, R.layout.autocomplete_layout, alcoholList);

        alcohols.setAdapter(adapter);

        alcohols.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                alcoholToUpdate = (Alcohol)alcohols.getAdapter().getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                alcoholToUpdate = null;
            }
        });

    }

    private void initializeShops(List<Shop> shopList) {
        ArrayAdapter<Shop> adapter = new CustomArrayAdapter<>(this, R.layout.autocomplete_layout, shopList);

        shops.setAdapter(adapter);

        shops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                shopToUpdate = (Shop) shops.getAdapter().getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                shopToUpdate = null;
            }
        });

    }

    private void changeTypeFace(Typeface typeface) {
        price.setTypeface(typeface);
        negativeNumber.setTypeface(typeface);
        positiveNumber.setTypeface(typeface);
    }

    private void backToMySales() {
        if(disposables != null){
            disposables.dispose();
        }

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

    private void update() {
        Dao<Sale> saleDao = RestDaoFactory.getSaleDao();

        Observable<Response<Void>> observable = saleDao.update(saleToUpdate);

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<Void>>() {
            @Override
            public void onSubscribe(Disposable d) {
                onUpdating();
                disposables.add(d);
                Log.i("Updating sale", "Subscribed");
            }

            @Override
            public void onNext(Response<Void> response) {
                if(!response.isSuccessful()){
                    Log.e("Updating sale", "Http reponse code" + response.code());
                    onUpdateError(getString(R.string.edit_sale_exception));
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Updating sale", "Excpetion " + e.getCause() + "occurs");
                onUpdateError(getString(R.string.unknown_exception_message));
            }

            @Override
            public void onComplete() {
                Log.i("Updating sale", "Completed");
                onUpdateSuccess();
            }
        });
    }

    private void remove() {
        Dao<Sale> saleDao = RestDaoFactory.getSaleDao();

        Observable<Response<Void>> observable = saleDao.remove(saleToUpdate);

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<Void>>() {
            @Override
            public void onSubscribe(Disposable d) {
                onUpdating();
                disposables.add(d);
                Log.i("Removing sale", "Subscribed");
            }

            @Override
            public void onNext(Response<Void> response) {
                if(!response.isSuccessful()){
                    Log.e("Removing sale", "Http reponse code" + response.code());
                    onUpdateError(getString(R.string.remove_sale_exception));
                } else {
                    hideUIWithBackToMySales("");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Removing sale", "Excpetion " + e.getCause() + "occurs");
                onUpdateError(getString(R.string.unknown_exception_message));
            }

            @Override
            public void onComplete() {
                Log.i("Removing sale", "Completed");
            }
        });
    }


    @Override
    public void onBackPressed() {
        backToMySales();
    }
}
