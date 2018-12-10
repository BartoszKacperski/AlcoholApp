package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rolnik.alcoholapp.restUtils.AsyncResponse;
import com.rolnik.alcoholapp.restUtils.ResponseHandler;
import com.rolnik.alcoholapp.utils.CustomItemDecorator;
import com.rolnik.alcoholapp.utils.ItemClickListener;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.adapters.SalesEditAdapter;
import com.rolnik.alcoholapp.dao.SaleRestDao;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.views.CustomProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MySalesActivity extends AppCompatActivity implements ResponseHandler<List<Sale>> {
    @BindView(R.id.root)
    ConstraintLayout root;
    @BindView(R.id.customProgressBar)
    CustomProgressBar customProgressBar;
    @BindView(R.id.mySalesRoot)
    LinearLayout mySalesRoot;
    @BindView(R.id.mySales)
    RecyclerView mySales;

    private SalesEditAdapter adapter;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sales);
        ButterKnife.bind(this);

        initializeSalesRecyclerView();
        downloadMySales();
    }

    private void initializeSalesRecyclerView() {
        adapter = new SalesEditAdapter(getApplication(), getItemClickListener());

        mySales.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mySales.setLayoutManager(linearLayoutManager);
        mySales.addItemDecoration(new CustomItemDecorator());
    }

    private ItemClickListener getItemClickListener() {
        return new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent salesDescriptions = new Intent(getApplicationContext(), EditSaleActivity.class);
                salesDescriptions.putExtra(getString(R.string.sale), adapter.getItem(position));

                startActivity(salesDescriptions);
            }
        };
    }

    private void showGUI(){
        TransitionManager.beginDelayedTransition(root);

        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        mySalesRoot.setVisibility(View.VISIBLE);
    }

    private void hideGUI(){
        TransitionManager.beginDelayedTransition(root);

        mySalesRoot.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.endAnimation();
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposables.add(d);
        hideGUI();
    }

    @Override
    public void onNext(List<Sale> sales) {
        adapter.addAll(sales);
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
        showError(getString(R.string.download_sale_exception));
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

    private Observable<List<Sale>> getPreparedObservable(){
        SaleRestDao saleRestDao = SaleRestDao.getInstance();

        return saleRestDao.getUserSales();
    }

    private void downloadMySales(){
        AsyncResponse<List<Sale>> asyncResponse = new AsyncResponse<>(getPreparedObservable(), this);

        asyncResponse.execute();
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(this, MainActivity.class);

        startActivity(main);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(disposables != null){
            disposables.dispose();
        }

    }

}
