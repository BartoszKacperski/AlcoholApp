package com.rolnik.alcoholapp.activities;

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
import com.rolnik.alcoholapp.dto.Sale;
import com.vstechlab.easyfonts.EasyFonts;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleDetailsActivity extends AppCompatActivity {
    @BindView(R.id.root)
    ConstraintLayout root;
    @BindView(R.id.description)
    LinearLayout description;
    @BindView(R.id.images)
    ImageView images;
    @BindView(R.id.alcoholName)
    TextView alcoholName;
    @BindView(R.id.brandName)
    TextView brandName;
    @BindView(R.id.kindName)
    TextView kindName;
    @BindView(R.id.alcoholicStrength)
    TextView alcoholicStrength;
    @BindView(R.id.volume)
    TextView volume;
    @BindView(R.id.shopName)
    TextView shopName;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.positiveNumber)
    TextView positiveNumber;
    @BindView(R.id.negativeNumber)
    TextView negativeNumber;

    private  ActivitySaleDetailsBinding activitySaleDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySaleDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_sale_details);
        ButterKnife.bind(this);

        if(!getIntent().hasExtra(getString(R.string.sale))){
            Log.w("Sale description", "Sale is null");
            backToSales();
        }

        bindSale();
        changeTypeFace(EasyFonts.walkwayBlack(getApplication()));
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
        Intent sales = new Intent(this, SearchSalesActivity.class);

        startActivity(sales);
    }
}
