package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.dao.AlcoholRestDao;
import com.rolnik.alcoholapp.dao.Dao;
import com.rolnik.alcoholapp.dao.RestDaoFactory;
import com.rolnik.alcoholapp.databinding.ActivityAddSaleBinding;
import com.rolnik.alcoholapp.model.Alcohol;
import com.rolnik.alcoholapp.model.Kind;
import com.rolnik.alcoholapp.model.Rate;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.Shop;
import com.rolnik.alcoholapp.restUtils.AsyncResponse;
import com.rolnik.alcoholapp.restUtils.ResponseHandler;
import com.rolnik.alcoholapp.utils.UserService;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.rolnik.alcoholapp.views.PricePickerDialog;

import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class AddSaleActivity extends AppCompatActivity implements ResponseHandler<Integer> {
    @BindView(R.id.root)
    ConstraintLayout root;

    @BindView(R.id.customProgressBar)
    CustomProgressBar customProgressBar;

    @BindView(R.id.addSaleRoot)
    LinearLayout addSaleRoot;


    @BindView(R.id.salePrice)
    TextView salePrice;
    @BindView(R.id.shops)
    Spinner shops;

    @BindView(R.id.alcoholsRoot)
    LinearLayout alcoholsRoot;
    @BindView(R.id.alcoholRoot)
    LinearLayout alcoholRoot;
    @BindView(R.id.alcohols)
    Spinner alcohols;
    @BindView(R.id.kindRoot)
    LinearLayout kindRoot;
    @BindView(R.id.kinds)
    Spinner kinds;

    @BindView(R.id.addButton)
    Button addButton;


    private ActivityAddSaleBinding activityAddSaleBinding;

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddSaleBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_sale);
        activityAddSaleBinding.setSale(new Sale());
        ButterKnife.bind(this);

        initializeSpinners();
    }

    public void addSale(View view) {
        Sale saleToAdd = activityAddSaleBinding.getSale();

        saleToAdd.setShop((Shop) shops.getSelectedItem());
        saleToAdd.setAlcohol((Alcohol) alcohols.getSelectedItem());
        saleToAdd.setRate(new Rate());

        addSale(saleToAdd);
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

        Objects.requireNonNull(pricePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        pricePickerDialog.show();
    }

    private void initializeSpinners() {
        Dao<Kind> kindDao = RestDaoFactory.getKindDao();
        Dao<Shop> shopDao = RestDaoFactory.getShopDao();

        Observable<List<Kind>> kinds = kindDao.getAll();
        Observable<List<Shop>> shops = shopDao.getAll();

        Observable.zip(kinds, shops, new BiFunction<List<Kind>, List<Shop>, Pair<List<Kind>, List<Shop>>>() {
            @Override
            public Pair<List<Kind>, List<Shop>> apply(List<Kind> kinds, List<Shop> shops) {
                return new Pair<>(kinds, shops);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Pair<List<Kind>, List<Shop>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                AddSaleActivity.this.onSubscribe(d);
                Log.i("Initialize autoText", "Subscribed");
                hideGUI();
            }

            @Override
            public void onNext(Pair<List<Kind>, List<Shop>> result) {
                initializeKinds(result.first);
                initializeShops(result.second);
            }

            @Override
            public void onError(Throwable e) {
                AddSaleActivity.this.finish();
            }

            @Override
            public void onComplete() {
                Log.i("Initialize autoText", "All autoText filled");
                initializeKindSpinnerListener();
                AddSaleActivity.this.onComplete();
            }
        });
    }

    private void downloadAlcoholsOf(final Kind kind){
        AlcoholRestDao alcoholRestDao = AlcoholRestDao.getInstance();

        Observable<List<Alcohol>> observable = alcoholRestDao.getAllOfKind(kind);

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Alcohol>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                Log.i("Download alcohols", "Subscribed");
            }

            @Override
            public void onNext(List<Alcohol> alcohol) {
                initializeAlcohols(alcohol);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getBaseContext(), String.format(getString(R.string.download_alcohol_of_kind), kind.getName()), Toast.LENGTH_SHORT).show();
                initializeAlcohols(Collections.<Alcohol>emptyList());
            }

            @Override
            public void onComplete() {
                setAlcoholsVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeKindSpinnerListener(){
        kinds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                downloadAlcoholsOf((Kind) kinds.getAdapter().getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setAlcoholsVisibility(View.GONE);
            }
        });
    }
    private void initializeKinds(List<Kind> kindsList) {
        ArrayAdapter<Kind> adapter = new ArrayAdapter<>(this, R.layout.spinner_layout, kindsList);
        adapter.setDropDownViewResource(R.layout.autocomplete_layout);
        kinds.setAdapter(adapter);
    }

    private void initializeAlcohols(List<Alcohol> alcoholList) {
        ArrayAdapter<Alcohol> adapter = new ArrayAdapter<>(this, R.layout.spinner_layout, alcoholList);
        adapter.setDropDownViewResource(R.layout.autocomplete_layout);
        alcohols.setAdapter(adapter);
    }

    private void initializeShops(List<Shop> shopList) {
        ArrayAdapter<Shop> adapter = new ArrayAdapter<>(this, R.layout.spinner_layout, shopList);
        adapter.setDropDownViewResource(R.layout.autocomplete_layout);
        shops.setAdapter(adapter);
    }

    private void setAlcoholsVisibility(int visibility){
        TransitionManager.beginDelayedTransition(root);

        alcoholRoot.setVisibility(visibility);
    }

    private void hideGUI(){
        TransitionManager.beginDelayedTransition(root);
        addSaleRoot.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
    }

    private void showGUI(){
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        addSaleRoot.setVisibility(View.VISIBLE);
    }


    @Override
    public void onSubscribe(Disposable d) {
        disposables.add(d);
        hideGUI();
    }

    @Override
    public void onNext(Integer integer) {
        Toast.makeText(this, R.string.add_sale_success, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onComplete() {
       showGUI();
    }

    @Override
    public void onSocketTimeout() {
        showError(getString(R.string.socket_timeout_exception));
    }

    @Override
    public void onNotAuthorized() {
        showError(getString(R.string.authorization_exception));
    }

    @Override
    public void onBadRequest() {
        showError(getString(R.string.add_sale_exception));
    }

    @Override
    public void onUnknownError() {
        showError(getString(R.string.unknown_exception_message));
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void addSale(Sale sale) {
        Dao<Sale> saleRestDao = RestDaoFactory.getSaleDao();

        AsyncResponse<Integer> asyncResponse = new AsyncResponse<>(saleRestDao.add(sale), this);

        asyncResponse.execute();
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (disposables != null) {
            disposables.dispose();
        }
    }
}
