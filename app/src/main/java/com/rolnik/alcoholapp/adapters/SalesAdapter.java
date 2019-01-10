package com.rolnik.alcoholapp.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;

import com.rolnik.alcoholapp.listeners.ItemClickListener;
import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.databinding.SearchSalesSaleLayoutBinding;
import com.rolnik.alcoholapp.dto.Sale;
import com.rolnik.alcoholapp.listeners.OpinionsClickListener;
import com.rolnik.alcoholapp.utils.TextAndNumberUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.MyViewHolder> implements Filterable {

    private List<Sale> sales;
    private List<Sale> filteredSales;
    private Context myContext;
    private LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;
    private OpinionsClickListener opinionsClickListener;

    public SalesAdapter(Context context, ItemClickListener itemClickListener, OpinionsClickListener opinionsClickListener, List<Sale> sales) {
        this.sales = sales;
        this.filteredSales = sales;
        this.myContext = context;
        this.itemClickListener = itemClickListener;
        this.opinionsClickListener = opinionsClickListener;
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

        public MyViewHolder(final SearchSalesSaleLayoutBinding searchSalesSaleLayoutBinding,
                            final ItemClickListener itemClickListener,
                            final OpinionsClickListener opinionsClickListener) {
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

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opinionsClickListener.onLike(view, getAdapterPosition());
                }
            });

            dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opinionsClickListener.onDislike(view, getAdapterPosition());
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

        return new MyViewHolder(searchSalesSaleLayoutBinding, this.itemClickListener, this.opinionsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Sale sale = filteredSales.get(position);
        holder.bind(sale);
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder){
    }

    @Override
    public int getItemCount() {
        return filteredSales.size();
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filter = charSequence.toString();

                if(filter.isEmpty()){
                    filteredSales = sales;
                } else {
                    List<Sale> filtered = new ArrayList<>();
                    for(Sale sale : sales){
                        if(TextAndNumberUtils.NFDcontains(sale.getAlcohol().getName(), filter)){
                            filtered.add(sale);
                        }
                    }

                    filteredSales = filtered;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredSales;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredSales = (ArrayList<Sale>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public Sale getItem(int position){
        return filteredSales.get(position);
    }
}
