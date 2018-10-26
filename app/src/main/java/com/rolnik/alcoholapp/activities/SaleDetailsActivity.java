package com.rolnik.alcoholapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.databinding.ActivitySaleDetailsBinding;
import com.rolnik.alcoholapp.model.Sale;
import com.vstechlab.easyfonts.EasyFonts;

import java.text.NumberFormat;

public class SaleDetailsActivity extends AppCompatActivity {
    private ConstraintLayout root;
    private LinearLayout descprition;
    private ImageView images;
    private TextView alcoholName;
    private TextView brandName;
    private TextView kindName;
    private TextView alcoholicStrength;
    private TextView volume;
    private TextView shopName;
    private TextView price;

    private TextView positiveNumber;
    private TextView negativeNumber;

    private  ActivitySaleDetailsBinding activitySaleDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySaleDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_sale_details);

        if(!getIntent().hasExtra(getString(R.string.sale))){
            Log.w("Sale description", "Sale is null");
            backToSales();
        }

        initializeViews();
        bindSale();
        changeTypeFace(EasyFonts.walkwayBlack(getApplication()));
    }

    private void initializeViews(){
        root = findViewById(R.id.root);
        descprition = findViewById(R.id.description);
        images = findViewById(R.id.images);
        alcoholName = findViewById(R.id.alcoholName);
        brandName = findViewById(R.id.brandName);
        kindName = findViewById(R.id.kindName);
        alcoholicStrength = findViewById(R.id.alcoholicStrength);
        volume = findViewById(R.id.volume);
        shopName = findViewById(R.id.shopName);
        price = findViewById(R.id.price);
        positiveNumber = findViewById(R.id.positiveNumber);
        negativeNumber = findViewById(R.id.negativeNumber);
    }

    private void bindSale(){
        Sale sale = (Sale) getIntent().getSerializableExtra(getString(R.string.sale));
        activitySaleDetailsBinding.setSale(sale);
    }


    private void changeTypeFace(Typeface typeface) {
        alcoholName.setTypeface(typeface);
        brandName.setTypeface(typeface);
        kindName.setTypeface(typeface);
        alcoholicStrength.setTypeface(typeface);
        volume.setTypeface(typeface);
        shopName.setTypeface(typeface);
        price.setTypeface(typeface);
        negativeNumber.setTypeface(typeface);
        positiveNumber.setTypeface(typeface);
    }

    private void backToSales(){
        Intent sales = new Intent(this, SearchActivity.class);

        startActivity(sales);
    }
}
