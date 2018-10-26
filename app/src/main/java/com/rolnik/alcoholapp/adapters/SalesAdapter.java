package com.rolnik.alcoholapp.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.rolnik.alcoholapp.asynctasks.SendDislike;
import com.rolnik.alcoholapp.asynctasks.SendLike;
import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.utils.ItemClickListener;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.dao.RestService;
import com.rolnik.alcoholapp.databinding.SearchSalesSaleLayoutBinding;
import com.rolnik.alcoholapp.model.Sale;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.MyViewHolder> {

    private List<Sale> sales;
    private Context myContext;
    private LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;

    public SalesAdapter(Context context, ItemClickListener itemClickListener) {
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

        private SearchSalesSaleLayoutBinding searchSalesSaleLayoutBinding ;
        private ImageButton likeButton;
        private ImageButton dislikeButton;

        public MyViewHolder(final SearchSalesSaleLayoutBinding searchSalesSaleLayoutBinding , final ItemClickListener itemClickListener) {
            super(searchSalesSaleLayoutBinding.getRoot());
            this.searchSalesSaleLayoutBinding = searchSalesSaleLayoutBinding;

            likeButton = searchSalesSaleLayoutBinding.getRoot().findViewById(R.id.likeButton);
            dislikeButton = searchSalesSaleLayoutBinding.getRoot().findViewById(R.id.dislikeButton);

            searchSalesSaleLayoutBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(v, getAdapterPosition());
                }
            });
        }

        public void bind(Sale sale){
            searchSalesSaleLayoutBinding.setSale(sale);
            searchSalesSaleLayoutBinding.executePendingBindings();
        }


    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        SearchSalesSaleLayoutBinding searchSalesSaleLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.search_sales_sale_layout, parent, false);

        final MyViewHolder holder = new MyViewHolder(searchSalesSaleLayoutBinding, this.itemClickListener);

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendLike(holder, sales.get(holder.getAdapterPosition()), myContext).execute();
            }
        });

        holder.dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendDislike(holder, sales.get(holder.getAdapterPosition()), myContext).execute();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Sale sale = sales.get(position);
        holder.bind(sale);
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder){
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    public Sale getItem(int position){
        return sales.get(position);
    }
}
