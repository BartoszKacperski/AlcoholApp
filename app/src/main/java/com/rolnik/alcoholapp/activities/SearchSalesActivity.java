package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.adapters.SalesAdapter;
import com.rolnik.alcoholapp.dao.SaleRestDao;
import com.rolnik.alcoholapp.dao.UserOpinionRestDao;
import com.rolnik.alcoholapp.model.Kind;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.Shop;
import com.rolnik.alcoholapp.model.UserOpinion;
import com.rolnik.alcoholapp.utils.CustomItemDecorator;
import com.rolnik.alcoholapp.utils.ItemClickListener;
import com.rolnik.alcoholapp.utils.OpinionsClickListener;
import com.rolnik.alcoholapp.utils.UserService;
import com.rolnik.alcoholapp.views.CustomProgressBar;

import java.util.ArrayList;
import java.util.Comparator;
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

public class SearchSalesActivity extends AppCompatActivity {
    @BindView(R.id.root)
    ConstraintLayout root;
    @BindView(R.id.searchRoot)
    ConstraintLayout searchRoot;
    @BindView(R.id.customProgressBar)
    CustomProgressBar customProgressBar;

    @BindView(R.id.alcoholName)
    SearchView alcoholName;

    @BindView(R.id.sales)
    RecyclerView sales;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.sortRoot)
    LinearLayout sortRoot;
    @BindView(R.id.sortButton)
    ImageButton sortButton;
    @BindView(R.id.sortAZ)
    ImageButton sortAZ;
    @BindView(R.id.sortZA)
    ImageButton sortZA;
    @BindView(R.id.sort19)
    ImageButton sort19;
    @BindView(R.id.sort91)
    ImageButton sort91;

    private SalesAdapter adapter;
    private boolean areSortsShowed = false;

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        if(!this.getIntent().hasExtra(getString(R.string.kind)) && !this.getIntent().hasExtra(getString(R.string.shop))){
            moveToFilter();
        } else {
            initializeRefreshLayout();
            initializeAlcoholName();
            initializeSalesRecyclerView(new ArrayList<Sale>());
            downloadSales((Shop)getIntent().getSerializableExtra(getString(R.string.shop)), (Kind)getIntent().getSerializableExtra(getString(R.string.kind)));
        }
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

    private void initializeAlcoholName() {
        alcoholName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }



    private void initializeSalesRecyclerView(List<Sale> salesList) {
        adapter = new SalesAdapter(getApplication(), getItemClickListener(), getOpinionsClickListener(), salesList);

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

    private OpinionsClickListener getOpinionsClickListener(){
        return new OpinionsClickListener() {
            @Override
            public void onLike(View view, int position) {
                sendLike(adapter.getItem(position));
            }

            @Override
            public void onDislike(View view, int position) {
                sendDislike(adapter.getItem(position));
            }
        };
    }

    private void sendLike(final Sale sale) {
        UserOpinionRestDao userOpinionRestDao = UserOpinionRestDao.getInstance();
        UserService userService = new UserService(this);

        Observable<Response<Void>> observable = userOpinionRestDao.sendLike(userService.getLoggedUser());

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<Void>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                Log.i("Send like", "Subscribed");
            }

            @Override
            public void onNext(Response<Void> response) {
                if(response.isSuccessful()) {
                    sale.addLike();
                } else {
                    Log.e("Send dislike", "Status code = " + response.code());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Send like", "Exception " + e.getCause() + " occurs");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void sendDislike(final Sale sale) {
        UserOpinionRestDao userOpinionRestDao = UserOpinionRestDao.getInstance();
        UserService userService = new UserService(this);

        Observable<Response<Void>> observable = userOpinionRestDao.sendDislike(userService.getLoggedUser());

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<Void>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                Log.i("Send dislike", "Subscribed");
            }

            @Override
            public void onNext(Response<Void> response) {
                if(response.isSuccessful()) {
                    sale.addDislike();
                } else {
                    Log.e("Send dislike", "Status code = " + response.code());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Send dislike", "Exception " + e.getCause() + " occurs");
            }

            @Override
            public void onComplete() {

            }
        });
    }


    private void initializeRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.lightSalomon));
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.grey));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter.getItemCount() > 0) {
                } else {
                    refreshLayout.setRefreshing(false);
                }

            }
        });
    }




    private void moveToFilter() {
        Intent filter = new Intent(this, FilterSalesActivity.class);

        startActivity(filter);
    }

    private void loadUI(){
        TransitionManager.beginDelayedTransition(root);
        refreshLayout.setRefreshing(false);
    }

    private void onDownloading() {
        refreshLayout.setRefreshing(true);
        adapter.clear();
    }

    private void onDownloadSuccess(List<Sale> sales) {
        loadUI();
        adapter.addAll(sales);
    }

    private void onDownloadError(String message) {
        loadUI();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void downloadSales(Shop shop, Kind kind){
        SaleRestDao saleRestDao = SaleRestDao.getInstance();
        UserOpinionRestDao userOpinionRestDao = UserOpinionRestDao.getInstance();
        UserService userService = new UserService(this);

        Observable<List<UserOpinion>> userOpinions = userOpinionRestDao.getUserOpinions(userService.getLoggedUser());
        Observable<List<Sale>> sales;

        if(shop == null){
            sales = saleRestDao.getAllWhereKind(kind.getId());
        } else if (kind == null){
            sales = saleRestDao.getAllWhereShop(shop.getId());
        } else {
            sales = saleRestDao.getAllWhere(shop.getId(), kind.getId());
        }

        Observable.zip(sales, userOpinions, new BiFunction<List<Sale>, List<UserOpinion>, List<Sale>>() {
            @Override
            public List<Sale> apply(List<Sale> sales, List<UserOpinion> userOpinions){
                for(Sale sale : sales) {
                    for(UserOpinion userOpinion : userOpinions){
                        if(userOpinion.getSaleId() == sale.getId()){
                            if(userOpinion.getOpinion() == UserOpinion.LIKE){
                                sale.setWasLiked(true);
                                sale.setWasDisliked(false);
                            } else {
                                sale.setWasDisliked(true);
                                sale.setWasLiked(false);
                            }
                        }
                    }
                }
                return sales;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Sale>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                onDownloading();
                Log.i("Downloading sales", "Subscribed");
            }

            @Override
            public void onNext(List<Sale> sales) {
                onDownloadSuccess(sales);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Download sales", "Exception " + e.getCause() + " " +  e.getMessage() + " occurs");
                onDownloadError(getString(R.string.unknown_exception_message));
            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent main = new Intent(this, MainActivity.class);

        startActivity(main);
    }
}
