package com.rolnik.alcoholapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.adapters.CustomArrayAdapter;
import com.rolnik.alcoholapp.adapters.SalesAdapter;
import com.rolnik.alcoholapp.asynctasks.AutoTextService;
import com.rolnik.alcoholapp.dao.RestDaoFactory;
import com.rolnik.alcoholapp.dao.SaleRestDao;
import com.rolnik.alcoholapp.dao.UserOpinionDatabase;
import com.rolnik.alcoholapp.model.Kind;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.Shop;
import com.rolnik.alcoholapp.utils.CustomItemDecorator;
import com.rolnik.alcoholapp.utils.ItemClickListener;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.vstechlab.easyfonts.EasyFonts;

import org.springframework.web.client.HttpClientErrorException;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ConstraintLayout root;
    private ConstraintLayout searchRoot;
    private CustomProgressBar customProgressBar;
    private Handler handler;


    private RelativeLayout shopRoot;
    private ImageButton showShopChoice;
    private AutoCompleteTextView shops;
    private AutoTextService<Shop> fillShops;
    private boolean areShopsShowed = false;

    private RelativeLayout kindRoot;
    private ImageButton showKindChoice;
    private AutoCompleteTextView kinds;
    private AutoTextService<Kind> fillKinds;
    private boolean areKindsShowed = false;

    private ImageButton searchButton;

    private RecyclerView sales;
    private SalesAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    private LinearLayout sortRoot;
    private ImageButton sortButton;
    private ImageButton sortAZ;
    private ImageButton sortZA;
    private ImageButton sort19;
    private ImageButton sort91;
    private boolean areSortsShowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeViews();

        waitTillAsyncTasksCompleted();
        initializeShops();
        initializeKinds();
    }

    private void initializeViews() {
        root = findViewById(R.id.root);
        searchRoot = findViewById(R.id.searchRoot);
        customProgressBar = findViewById(R.id.customProgressBar);
        shopRoot = findViewById(R.id.shopRoot);
        showShopChoice = findViewById(R.id.showShopChoice);
        kindRoot = findViewById(R.id.kindRoot);
        showKindChoice = findViewById(R.id.showKindChoice);
        searchButton = findViewById(R.id.searchButton);

        initializeSortViews();
        initializeSalesRecyclerView();
        initializeRefreshLayout();

    }

    private void initializeSortViews() {
        sortRoot = findViewById(R.id.sortRoot);
        sortButton = findViewById(R.id.sortButton);
        sortAZ = findViewById(R.id.sortAZ);
        sortZA = findViewById(R.id.sortZA);
        sort19 = findViewById(R.id.sort19);
        sort91 = findViewById(R.id.sort91);
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
                            showUI();
                        }
                        break;
                    }
                    case AutoTextService.ERROR: {
                        Exception cause = (Exception) msg.obj;
                        Log.i("Search activity", "Exception " + cause + " occurs");
                        hidUIAndMoveToMain(cause.getMessage());
                        break;
                    }

                }
                return false;
            }
        });
    }

    private void showUI() {
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        searchRoot.setVisibility(View.VISIBLE);

    }

    private void hidUIAndMoveToMain(String errorMessage) {
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        Toast.makeText(getApplication(), errorMessage, Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveToMain();
            }
        }, 2000);
    }

    private void initializeSalesRecyclerView() {
        sales = findViewById(R.id.sales);
        adapter = new SalesAdapter(getApplication(), getItemClickListener());

        sales.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        sales.setLayoutManager(linearLayoutManager);
        sales.addItemDecoration(new CustomItemDecorator());
    }

    private ItemClickListener getItemClickListener() {
        return new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent salesDescriptions = new Intent(getApplicationContext(), SaleDetailsActivity.class);
                salesDescriptions.putExtra(getString(R.string.sale), adapter.getItem(position));

                startActivity(salesDescriptions);
            }
        };
    }


    private void initializeRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.lightSalomon));
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.grey));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter.getItemCount() > 0) {
                    executeDownloadSalesTask();
                } else {
                    refreshLayout.setRefreshing(false);
                }

            }
        });

        fillRecycler();
    }

    private void executeDownloadSalesTask() {
        int shopId = getShopId();
        int kindId = getKindId();
        new DownloadSales(this, shopId, kindId).execute();

    }

    private void initializeShops() {
        shops = findViewById(R.id.shops);
        shops.setTypeface(EasyFonts.captureIt(this));

        fillShops = new AutoTextService<>(this, shops.getId(), RestDaoFactory.getShopDao(), handler);
        fillShops.execute();
    }

    private void initializeKinds() {
        kinds = findViewById(R.id.kinds);
        kinds.setTypeface(EasyFonts.captureIt(this));

        fillKinds = new AutoTextService<>(this, kinds.getId(), RestDaoFactory.getKindDao(), handler);
        fillKinds.execute();
    }

    private int getShopId() {
        String name = shops.getText().toString();

        Shop shop = (Shop) ((CustomArrayAdapter) shops.getAdapter()).getByName(name);

        if (shop == null) {
            return -1;
        }

        return shop.getId();

    }

    private int getKindId() {
        String name = kinds.getText().toString();

        Kind kind = (Kind) ((CustomArrayAdapter) kinds.getAdapter()).getByName(name);

        if (kind == null) {
            return -1;
        }

        return kind.getId();
    }

    private void fillRecycler() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(searchRoot);

        if (!areKindsShowed && !areShopsShowed) {
            constraintSet.connect(R.id.refreshLayout, ConstraintSet.TOP, R.id.searchRoot, ConstraintSet.TOP, 0);
            Log.i("Search activity", "Move sales recyclerview to TOP of root");
        } else if (areShopsShowed && !areKindsShowed) {
            constraintSet.connect(R.id.refreshLayout, ConstraintSet.TOP, R.id.shopRoot, ConstraintSet.BOTTOM, 0);
            Log.i("Search activity", "Move sales recyclerview to BOTTOM of shop");
        } else {
            constraintSet.connect(R.id.refreshLayout, ConstraintSet.TOP, R.id.kindRoot, ConstraintSet.BOTTOM);
            Log.i("Search activity", "Move sales recyclerview to BOTTOM of kind");
        }

        constraintSet.applyTo(searchRoot);
    }

    private void moveToMain() {
        Intent main = new Intent(this, MainActivity.class);

        startActivity(main);
    }


    public void search(View view) {
        executeDownloadSalesTask();
    }

    public void showKinds(View view) {
        int visibility = (areKindsShowed ? View.GONE : View.VISIBLE);

        TransitionManager.beginDelayedTransition(root);
        kinds.setVisibility(visibility);

        areKindsShowed = !areKindsShowed;
        fillRecycler();
    }

    public void showShops(View view) {
        int visibility = (areShopsShowed ? View.GONE : View.VISIBLE);

        TransitionManager.beginDelayedTransition(root);
        shops.setVisibility(visibility);

        areShopsShowed = !areShopsShowed;
        fillRecycler();
    }

    public void showSorts(View view) {
        int visibility = (areSortsShowed ? View.GONE : View.VISIBLE);

        TransitionManager.beginDelayedTransition(sortRoot);

        sortAZ.setVisibility(visibility);
        sortZA.setVisibility(visibility);
        sort19.setVisibility(visibility);
        sort91.setVisibility(visibility);

        areSortsShowed = !areSortsShowed;
    }

    public void priceSortAsc(View view) {
        adapter.sort(new Comparator<Sale>() {
            @Override
            public int compare(Sale o1, Sale o2) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
        });
    }

    public void priceSortDesc(View view) {
        adapter.sort(new Comparator<Sale>() {
            @Override
            public int compare(Sale o1, Sale o2) {
                return Double.compare(o2.getPrice(), o1.getPrice());
            }
        });
    }

    public void sortNameDesc(View view) {
        adapter.sort(new Comparator<Sale>() {
            @Override
            public int compare(Sale o1, Sale o2) {
                return o1.getAlcohol().getName().compareTo(o2.getAlcohol().getName());
            }
        });
    }

    public void sortNameAsc(View view) {
        adapter.sort(new Comparator<Sale>() {
            @Override
            public int compare(Sale o1, Sale o2) {
                return o2.getAlcohol().getName().compareTo(o1.getAlcohol().getName());
            }
        });
    }

    private void loadUI(){
        TransitionManager.beginDelayedTransition(root);
        refreshLayout.setRefreshing(false);
        searchButton.setEnabled(true);
    }

    private void onDownloading() {
        refreshLayout.setRefreshing(true);
        searchButton.setEnabled(false);
    }

    private void onDownloadSuccess() {
        loadUI();
    }

    private void onDownloadError(String message) {
        loadUI();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private static class DownloadSales extends AsyncTask<Void, Void, List<Sale>> {
        private WeakReference<SearchActivity> searchActivityWeakReference;
        private Exception exception = null;
        private int shopId;
        private int kindId;

        public DownloadSales(SearchActivity searchActivity, int shopId, int kindId) {
            searchActivityWeakReference = new WeakReference<>(searchActivity);
            this.shopId = shopId;
            this.kindId = kindId;
        }


        @Override
        protected void onPreExecute() {
            SearchActivity searchActivity = searchActivityWeakReference.get();
            if (searchActivity == null || searchActivity.isFinishing()) {
                this.cancel(true);
                Log.e("Before download sales", "Activity doesn't exist, async task cancelled");
                return;
            }
            searchActivity.onDownloading();
        }

        @Override
        protected List<Sale> doInBackground(Void... voids) {
            if (!isCancelled()) {
                SaleRestDao saleRestDao = SaleRestDao.getInstance();

                try {
                    if (kindId == -1) {
                        return saleRestDao.getAllWhereShop(shopId);
                    } else if (shopId == -1) {
                        return saleRestDao.getAllWhereKind(kindId);
                    } else {
                        return saleRestDao.getAllWhere(shopId, kindId);
                    }
                } catch (Exception e) {
                    exception = e;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Sale> sales) {
            SearchActivity searchActivity = searchActivityWeakReference.get();

            if (searchActivity == null || searchActivity.isFinishing()) {
                Log.e("After download sales", "Activity doesn't exist");
                return;
            } else if (exception != null) {
                Log.e("After download sales", "Exception" + exception + " occurs");

                if (exception instanceof HttpClientErrorException) {
                    showErrorDependsOnHttpStatus((HttpClientErrorException) exception, searchActivity);
                } else {
                    String message = searchActivity.getString(R.string.unknown_exception_message);
                    searchActivity.onDownloadError(message);
                }
                return;
            }

            new CheckWhichSalesWereLiked(searchActivity, sales).execute();
            searchActivity.adapter.addAll(sales);
            searchActivity.onDownloadSuccess();
            Log.i("After download sales", "Everything is ok");
        }

        private void showErrorDependsOnHttpStatus(HttpClientErrorException exception, SearchActivity activity) {
            switch (exception.getStatusCode()) {
                case NOT_FOUND: {
                    String message = activity.getString(R.string.download_sale_exception);
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

    private static class CheckWhichSalesWereLiked extends AsyncTask<Void, Void, Void>{
        private WeakReference<Context> contextWeakReference;
        private UserOpinionDatabase userOpinionDatabase;
        private List<Sale> sales;

        public CheckWhichSalesWereLiked(Context context, List<Sale> sales){
            this.contextWeakReference = new WeakReference<>(context);
            this.sales = sales;
        }

        @Override
        protected void onPreExecute(){
            Context context = contextWeakReference.get();

            if(context == null){
                Log.e("Filling opinions", "Context is null");
                cancel(true);
                return;
            }

            userOpinionDatabase = UserOpinionDatabase.getUserOpinionDatabase(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isCancelled()){
                for(Sale sale : sales){
                    try {
                        boolean wasLiked = userOpinionDatabase.userOpinionDao().wasSaleLiked(sale) > 0 ;
                        boolean wasDisliked = userOpinionDatabase.userOpinionDao().wasSaleDisliked(sale) > 0 ;

                        System.out.println(wasLiked + " " + wasDisliked);
                        sale.setWasLiked(wasLiked);
                        sale.setWasDisliked(wasDisliked);
                    } catch (Exception e){
                        Log.e("Filling opinions", "Exception " + e + " occurs");
                    }
                }

                Log.i("Filling opinions", "Opinions filled correctly");
            }

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(this, MainActivity.class);

        startActivity(main);
    }
}
