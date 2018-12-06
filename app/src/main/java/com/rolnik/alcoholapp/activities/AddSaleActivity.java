package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
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
import com.rolnik.alcoholapp.utils.UserService;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.rolnik.alcoholapp.views.PricePickerDialog;

import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Locale;

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

public class AddSaleActivity extends AppCompatActivity {
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

    private Sale saleToAdd;

    private ActivityAddSaleBinding activityAddSaleBinding;

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddSaleBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_sale);
        ButterKnife.bind(this);

        bindSale();
        initializeSpinners();
    }

    public void addSale(View view) {
        UserService userService = new UserService(this);

        saleToAdd.setShop((Shop) shops.getSelectedItem());
        saleToAdd.setAlcohol((Alcohol) alcohols.getSelectedItem());
        saleToAdd.setUser(userService.getLoggedUser());
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
                disposables.add(d);
                Log.i("Initialize autoText", "Subscribed");
                customProgressBar.startAnimation();
            }

            @Override
            public void onNext(Pair<List<Kind>, List<Shop>> result) {
                initializeKinds(result.first);
                initializeShops(result.second);
            }

            @Override
            public void onError(Throwable e) {
                hideUIWithBackToMenu(e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i("Initialize autoText", "All autoText filled");
                initializeKindSpinnerListener();
                loadUI();
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
                Toast.makeText(getBaseContext(), String.format(getString(R.string.download_alcohol_of_kind), kind.getName()), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
                setAlcoholsVisibility(View.VISIBLE);
            }
        });
    }

    private void bindSale() {
        saleToAdd = new Sale();

        activityAddSaleBinding.setSale(saleToAdd);
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
        TransitionManager.beginDelayedTransition(alcoholsRoot);

        alcoholRoot.setVisibility(visibility);
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

    private void addSale(Sale sale) {
        Dao<Sale> saleRestDao = RestDaoFactory.getSaleDao();

        Observable<Integer> observable = saleRestDao.add(sale);

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                onAdding();
                Log.e("Before add sale", "Subscribed, UI changed");
            }

            @Override
            public void onNext(Integer integer) {
                Log.i("Adding sale", "SaleId = " + integer);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpException) {
                    showErrorDependsOnHttpStatus((HttpException) e);
                } else {
                    String message = getString(R.string.unknown_exception_message);
                    onAddError(message);
                }
            }

            @Override
            public void onComplete() {
                Log.i("After add sale", "Everything is ok, changing UI");
                onAddSuccess();
            }
        });
    }

    private void showErrorDependsOnHttpStatus(HttpException exception) {
        switch (exception.code()) {
            case 400: {
                String message = getString(R.string.add_sale_exception);
                onAddError(message);
                break;
            }
            default: {
                String message = getString(R.string.unknown_exception_message);
                onAddError(message);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        backToMenu();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (disposables != null) {
            disposables.dispose();
        }
    }
}
