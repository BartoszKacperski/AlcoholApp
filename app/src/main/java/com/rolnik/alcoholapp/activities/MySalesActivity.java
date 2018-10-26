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

public class MySalesActivity extends AppCompatActivity {
    private ConstraintLayout root;
    private CustomProgressBar customProgressBar;
    private LinearLayout mySalesRoot;
    private RecyclerView mySales;
    private SalesEditAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sales);

        initializeViews();
        initializeSalesRecyclerView();
        downloadMySales();
    }

    private void initializeViews(){
        root = findViewById(R.id.root);
        mySalesRoot = findViewById(R.id.mySalesRoot);
        customProgressBar = findViewById(R.id.customProgressBar);
    }

    private void initializeSalesRecyclerView() {
        mySales = findViewById(R.id.mySales);
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

        DownloadMySales downloadMySales = new DownloadMySales(this, userService.getLoggedUser());

        downloadMySales.execute();
    }

    private static class DownloadMySales extends AsyncTask<Void, Void, List<Sale>> {
        private WeakReference<MySalesActivity> mySalesActivityWeakReference;
        private User currentUser;
        private Exception exception = null;

        public DownloadMySales(MySalesActivity mySalesActivity, User currentUser) {
            this.mySalesActivityWeakReference = new WeakReference<>(mySalesActivity);
            this.currentUser = currentUser;
        }

        @Override
        protected void onPreExecute() {
            MySalesActivity mySalesActivity = mySalesActivityWeakReference.get();

            if (mySalesActivity == null || mySalesActivity.isFinishing()) {
                Log.e("Download my sales", "Acitivity is null");
                cancel(true);
                return;
            }

            mySalesActivity.onDownloading();
        }


        @Override
        protected List<Sale> doInBackground(Void... voids) {
            if (!isCancelled()) {
                SaleRestDao saleRestDao = SaleRestDao.getInstance();

                try {
                    return saleRestDao.getUserSales(currentUser.getId());
                } catch (Exception e) {
                    exception = e;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Sale> sales){
            MySalesActivity mySalesActivity = mySalesActivityWeakReference.get();

            if(mySalesActivity == null || mySalesActivity.isFinishing()){
                Log.e("Download my sales", "Acitivity is null");
                return;
            } else if(exception != null){
                Log.e("Download my sales", "Exception " + exception + " occurs");

                if(exception instanceof HttpClientErrorException){
                    showErrorDependsOnHttpStatus((HttpClientErrorException)exception, mySalesActivity);
                } else {
                    String message = mySalesActivity.getString(R.string.unknown_exception_message);
                    mySalesActivity.onDownloadError(message);
                }

                return;
            }

            Log.i("Download my sales", "Everthing is ok");
            mySalesActivity.adapter.addAll(sales);
            mySalesActivity.onDownloadSuccess();
        }

        private void showErrorDependsOnHttpStatus(HttpClientErrorException exception, MySalesActivity activity){
            switch (exception.getStatusCode()){
                case NOT_FOUND: {
                    String message = activity.getString(R.string.download_my_sale_exception);
                    activity.onDownloadError(message);
                    break;
                }
                default: {
                    String message = activity.getString(R.string.unknown_exception_message);
                    activity.onDownloadError(message);
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
