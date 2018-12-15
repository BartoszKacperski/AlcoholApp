package com.rolnik.alcoholapp.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.dao.Dao;
import com.rolnik.alcoholapp.dao.RestDaoFactory;
import com.rolnik.alcoholapp.databinding.ActivityEditSaleBinding;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.restUtils.ResponseHandler;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.rolnik.alcoholapp.views.PricePickerDialog;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class EditSaleActivity extends AppCompatActivity implements ResponseHandler<Response<Void>> {
    @BindView(R.id.root)
    ConstraintLayout root;
    @BindView(R.id.description)
    LinearLayout description;
    @BindView(R.id.rates)
    LinearLayout rates;

    @BindView(R.id.customProgressBar)
    CustomProgressBar customProgressBar;

    @BindView(R.id.shops)
    TextView shops;
    @BindView(R.id.alcohols)
    TextView alcohols;

    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.positiveNumber)
    TextView positiveNumber;
    @BindView(R.id.negativeNumber)
    TextView negativeNumber;


    private CompositeDisposable disposables = new CompositeDisposable();

    private Sale saleToUpdate;
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
        oldPrice = saleToUpdate.getPrice();

        activityEditSaleBinding.setSale(saleToUpdate);

        changeTypeFace(EasyFonts.walkwayBlack(getApplication()));
    }


    public void update(View view) {
        if (oldPrice == saleToUpdate.getPrice()) {
            Toast.makeText(this, "Ta sama cena", Toast.LENGTH_LONG).show();
        } else {
            update();
        }
    }

    public void pickPrice(View view) {
        final PricePickerDialog pricePickerDialog = new PricePickerDialog(this);
        pricePickerDialog.setPrice(saleToUpdate.getPrice());

        pricePickerDialog.setOkButtonActionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price.setText(pricePickerDialog.getCurrencyFormattedPriceValue(new Locale("pl", "PL")));
                pricePickerDialog.dismiss();
            }
        });

        pricePickerDialog.show();
    }

    private void changeTypeFace(Typeface typeface) {
        price.setTypeface(typeface);
        negativeNumber.setTypeface(typeface);
        positiveNumber.setTypeface(typeface);
    }

    private void backToMySales() {
        Intent sales = new Intent(this, MySalesActivity.class);

        startActivity(sales);
    }

    private void hideGUI() {
        TransitionManager.beginDelayedTransition(root);
        description.setVisibility(View.GONE);
        rates.setVisibility(View.GONE);
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.startAnimation();
    }

    private void showGUI() {
        TransitionManager.beginDelayedTransition(root);
        customProgressBar.endAnimation();
        customProgressBar.setVisibility(View.GONE);
        description.setVisibility(View.VISIBLE);
        rates.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposables.add(d);
        hideGUI();
    }

    @Override
    public void onNext(Response<Void> response) {
        if(response.isSuccessful()){
            Toast.makeText(this, R.string.edit_sale_succes,Toast.LENGTH_LONG).show();
        } else {
            switch (response.code()){
                case 400: {
                    onBadRequest();
                    break;
                }
                case 401:{
                    onNotAuthorized();
                    break;
                }
                default: {
                    onUnknownError();
                    break;
                }
            }
        }
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

    private void update() {
        Dao<Sale> saleDao = RestDaoFactory.getSaleDao();

        Observable<Response<Void>> observable = saleDao.update(saleToUpdate);

    }


    @Override
    public void onBackPressed() {
        backToMySales();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (disposables != null) {
            disposables.dispose();
        }
    }
}
