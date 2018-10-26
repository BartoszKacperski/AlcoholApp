package com.rolnik.alcoholapp.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rolnik.alcoholapp.utils.ItemClickListener;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.databinding.MySalesSaleLayoutBinding;
import com.rolnik.alcoholapp.model.Sale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SalesEditAdapter extends RecyclerView.Adapter<SalesEditAdapter.MyViewHolder> {

    private List<Sale> sales;
    private Context myContext;
    private LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;

    public SalesEditAdapter(Context context, ItemClickListener itemClickListener) {
        this.sales = new ArrayList<>();
        this.myContext = context;
        this.itemClickListener = itemClickListener;
    }

    public boolean add(Sale sale) {
        boolean succeed = sales.add(sale);
        notifyItemInserted(sales.size() - 1);

        return succeed;
    }

    public void addAll(List<Sale> sales) {
        for (Sale sale : sales) {
            this.add(sale);
        }
    }

    public boolean remove(Sale sale) {
        int index = sales.indexOf(sale);
        boolean succeed = sales.remove(sale);
        notifyItemRemoved(index);

        return succeed;
    }

    public void removeAll(List<Sale> sales) {
        for (Sale sale : sales) {
            this.remove(sale);
        }
    }

    public void clear(){
        int size = sales.size();
        sales.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void sort(Comparator<Sale> comparator) {
        Collections.sort(sales, comparator);
        notifyItemRangeChanged(0, sales.size());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private MySalesSaleLayoutBinding mySalesSaleLayoutBinding;

        public MyViewHolder(MySalesSaleLayoutBinding mySalesSaleLayoutBinding, final ItemClickListener itemClickListener) {
            super(mySalesSaleLayoutBinding.getRoot());
            this.mySalesSaleLayoutBinding = mySalesSaleLayoutBinding;

            mySalesSaleLayoutBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(v, getAdapterPosition());
                }
            });
        }

        public void bindSale(Sale sale){
            mySalesSaleLayoutBinding.setSale(sale);
            mySalesSaleLayoutBinding.executePendingBindings();
        }
    }

    @NonNull
    @Override
    public SalesEditAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        MySalesSaleLayoutBinding mySalesSaleLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.my_sales_sale_layout, parent, false);

        return new SalesEditAdapter.MyViewHolder(mySalesSaleLayoutBinding, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesEditAdapter.MyViewHolder holder, int position) {
        Sale sale = sales.get(position);
        holder.bindSale(sale);
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    public Sale getItem(int position){
        return sales.get(position);
    }
}
