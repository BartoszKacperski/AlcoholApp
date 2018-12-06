package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rolnik.alcoholapp.utils.CustomItemDecorator;
import com.rolnik.alcoholapp.utils.ItemClickListener;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.utils.UserService;
import com.rolnik.alcoholapp.adapters.SalesEditAdapter;
import com.rolnik.alcoholapp.dao.SaleRestDao;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.views.CustomProgressBar;

import org.springframework.web.client.HttpClientErrorException;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class MySalesActivity extends AppCompatActivity {
    @BindView(R.id.root)
    ConstraintLayout root;
    @BindView(R.id.customProgressBar)
    CustomProgressBar customProgressBar;
    @BindView(R.id.mySalesRoot)
    LinearLayout mySalesRoot;
    @BindView(R.id.mySales)
    RecyclerView mySales;

    private SalesEditAdapter adapter;
    private Disposable disposable;

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

    private void onDownloading() {
        TransitionManager.beginDelayedTransition(root);
        mySalesRoot.setVisibility(View.GONE);
        customProgressBar.startAnimation();
        customProgressBar.setVisibility(View.VISIBLE);
    }

    private void showUI(){
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        mySalesRoot.setVisibility(View.VISIBLE);
    }

    private void onDownloadSuccess() {
        showUI();
    }

    private void onDownloadError(String message){
        showUI();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void downloadMySales(){
        UserService userService = new UserService(this);
        SaleRestDao saleRestDao = SaleRestDao.getInstance();

        Observable<List<Sale>> observable = saleRestDao.getUserSales(userService.getLoggedUserId());

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Sale>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
                onDownloading();
                Log.e("Download my sales", "Subscribed");
            }

            @Override
            public void onNext(List<Sale> sales) {
                Log.i("Download my sales", "Everthing is ok");
                adapter.addAll(sales);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Download my sales", "Exception " + e.getCause() + "occurs");
                if(e instanceof HttpException){
                    showErrorDependsOnHttpStatus((HttpException) e);
                } else {
                    onDownloadError(getString(R.string.unknown_exception_message));
                }
            }

            @Override
            public void onComplete() {
                onDownloadSuccess();
            }
        });

    }

    private void showErrorDependsOnHttpStatus(HttpException exception){
        switch (exception.code()){
            case 404: {
                onDownloadError(getString(R.string.download_my_sale_exception));
                break;
            }
            default: {
                onDownloadError(getString(R.string.unknown_exception_message));
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(disposable != null){
            disposable.dispose();
        }

        Intent main = new Intent(this, MainActivity.class);

        startActivity(main);
    }
}
