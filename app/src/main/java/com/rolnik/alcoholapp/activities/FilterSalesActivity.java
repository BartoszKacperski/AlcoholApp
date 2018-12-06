package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.adapters.CustomArrayAdapter;
import com.rolnik.alcoholapp.dao.Dao;
import com.rolnik.alcoholapp.dao.RestDaoFactory;
import com.rolnik.alcoholapp.model.Kind;
import com.rolnik.alcoholapp.model.Shop;
import com.rolnik.alcoholapp.views.CustomProgressBar;

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

public class FilterSalesActivity extends AppCompatActivity {
    @BindView(R.id.root)
    ConstraintLayout root;

    @BindView(R.id.filterRoot)
    ConstraintLayout filterRoot;
    @BindView(R.id.customProgressBar)
    CustomProgressBar customProgressBar;


    @BindView(R.id.kindRoot)
    LinearLayout kindRoot;
    @BindView(R.id.kindText)
    TextView kindText;
    @BindView(R.id.kinds)
    Spinner kinds;

    @BindView(R.id.shopRoot)
    LinearLayout shopRoot;
    @BindView(R.id.shopText)
    TextView shopText;
    @BindView(R.id.shops)
    Spinner shops;

    @BindView(R.id.filterButton)
    ImageButton filterButton;

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_sales);
        ButterKnife.bind(this);


        initializeAutoText();
    }

    public void search(View view) {
        Intent search = new Intent(this, SearchSalesActivity.class);

        search.putExtra(getString(R.string.kind), (Kind)kinds.getSelectedItem());
        search.putExtra(getString(R.string.shop), (Shop)shops.getSelectedItem());

        startActivity(search);
    }

    private void initializeAutoText(){
        Dao<Kind> kindDao = RestDaoFactory.getKindDao();
        Dao<Shop> shopDao = RestDaoFactory.getShopDao();

        Observable<List<Kind>> kinds = kindDao.getAll();
        Observable<List<Shop>> shops = shopDao.getAll();

        Observable.combineLatest(kinds, shops, new BiFunction<List<Kind>, List<Shop>, Pair<List<Kind>, List<Shop>>>() {
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
                moveToMain();
            }

            @Override
            public void onComplete() {
                Log.i("Initialize autoText", "All autoText filled");
                showUI();
            }
        });
    }

    private void moveToMain() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

    private void showUI() {
        TransitionManager.beginDelayedTransition(root);

        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        filterRoot.setVisibility(View.VISIBLE);
    }

    private void initializeKinds(List<Kind> kindsList) {
        ArrayAdapter<Kind> kindArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, kindsList);
        kindArrayAdapter.setDropDownViewResource(R.layout.autocomplete_layout);
        kinds.setAdapter(kindArrayAdapter);
    }

    private void initializeShops(List<Shop> shopsList) {
        ArrayAdapter<Shop> shopArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, shopsList);
        shopArrayAdapter.setDropDownViewResource(R.layout.autocomplete_layout);
        shops.setAdapter(shopArrayAdapter);
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(disposables != null){
            disposables.dispose();
        }
    }
}
