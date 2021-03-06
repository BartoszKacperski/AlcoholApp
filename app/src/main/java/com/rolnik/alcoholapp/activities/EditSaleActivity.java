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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rolnik.alcoholapp.MyApplication;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.clientservices.SaleClientService;
import com.rolnik.alcoholapp.databinding.ActivityEditSaleBinding;
import com.rolnik.alcoholapp.dto.Sale;
import com.rolnik.alcoholapp.rests.SaleRest;
import com.rolnik.alcoholapp.restutils.AsyncResponse;
import com.rolnik.alcoholapp.restutils.ResponseHandler;
import com.rolnik.alcoholapp.views.CustomProgressBar;
import com.rolnik.alcoholapp.views.PricePickerDialog;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;
import retrofit2.Retrofit;

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

    @Inject
    @Named("with_cookie")
    Retrofit retrofit;
    private ActivityEditSaleBinding activityEditSaleBinding;
    private CompositeDisposable disposables = new CompositeDisposable();
    private double oldPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditSaleBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_sale);
        ButterKnife.bind(this);

        if (!getIntent().hasExtra(getString(R.string.sale))) {
            Log.w("Sale description", "Sale is null");
            backToMySales();
        } else {
            ((MyApplication) getApplication()).getNetComponent().inject(this);
            Sale saleToUpdate = (Sale) getIntent().getSerializableExtra(getString(R.string.sale));
            oldPrice = saleToUpdate.getPrice();
            activityEditSaleBinding.setSale(saleToUpdate);
            changeTypeFace(EasyFonts.walkwayBlack(getApplication()));
        }
    }


    public void update(View view) {
        if (oldPrice == activityEditSaleBinding.getSale().getPrice()) {
            Toast.makeText(this, "Ta sama cena", Toast.LENGTH_LONG).show();
        } else {
            update();
        }
    }

    public void pickPrice(View view) {
        final PricePickerDialog pricePickerDialog = new PricePickerDialog(this);
        pricePickerDialog.setPrice(activityEditSaleBinding.getSale().getPrice());

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
        SaleClientService saleClientService = new SaleClientService(retrofit.create(SaleRest.class));

        AsyncResponse<Response<Void>> asyncResponse = new AsyncResponse<>(saleClientService.update(activityEditSaleBinding.getSale()), this);

        asyncResponse.execute();
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
