package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rolnik.alcoholapp.MyApplication;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.clientservices.KindClientService;
import com.rolnik.alcoholapp.clientservices.ShopClientService;
import com.rolnik.alcoholapp.dto.Kind;
import com.rolnik.alcoholapp.dto.Shop;
import com.rolnik.alcoholapp.rests.KindRest;
import com.rolnik.alcoholapp.rests.ShopRest;
import com.rolnik.alcoholapp.restutils.AsyncResponse;
import com.rolnik.alcoholapp.restutils.ResponseHandler;
import com.rolnik.alcoholapp.views.CustomProgressBar;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import retrofit2.Retrofit;

public class FilterSalesActivity extends AppCompatActivity implements ResponseHandler<Pair<List<Kind>, List<Shop>>> {
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

    @Named("with_cookie")
    @Inject
    Retrofit retrofit;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_sales);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).getNetComponent().inject(this);
        initializeAutoText();
    }

    public void search(View view) {
        Intent search = new Intent(this, SearchSalesActivity.class);

        search.putExtra(getString(R.string.kind), (Kind) kinds.getSelectedItem());
        search.putExtra(getString(R.string.shop), (Shop) shops.getSelectedItem());

        startActivity(search);
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposables.add(d);
        hideGUI();
    }

    @Override
    public void onNext(Pair<List<Kind>, List<Shop>> listListPair) {
        initializeKinds(listListPair.first);
        initializeShops(listListPair.second);
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
        showGUI();
    }

    private Observable<Pair<List<Kind>, List<Shop>>> getPreparedObservable() {
        KindClientService kindClientService = new KindClientService(retrofit.create(KindRest.class));
        ShopClientService shopClientService = new ShopClientService(retrofit.create(ShopRest.class));

        Observable<List<Kind>> kinds = kindClientService.getAll();
        Observable<List<Shop>> shops = shopClientService.getAll();

        return Observable.zip(kinds, shops, new BiFunction<List<Kind>, List<Shop>, Pair<List<Kind>, List<Shop>>>() {
            @Override
            public Pair<List<Kind>, List<Shop>> apply(List<Kind> kinds, List<Shop> shops) {
                return new Pair<>(kinds, shops);
            }
        });
    }

    private void initializeAutoText() {
        AsyncResponse<Pair<List<Kind>, List<Shop>>> asyncResponse = new AsyncResponse<>(getPreparedObservable(), this);

        asyncResponse.execute();
    }


    private void showGUI() {
        TransitionManager.beginDelayedTransition(root);

        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        filterRoot.setVisibility(View.VISIBLE);
    }

    private void hideGUI() {
        TransitionManager.beginDelayedTransition(root);

        filterRoot.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
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
    protected void onDestroy() {
        super.onDestroy();

        if (disposables != null) {
            disposables.dispose();
        }
    }
}
