package com.rolnik.alcoholapp.adapters;

import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.model.GetNameProvider;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomArrayAdapter<T extends GetNameProvider> extends ArrayAdapter<T> {
    private List<T> objects;
    private List<T> allObjects;
    private Context myContext;

    public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.myContext = context;
        this.allObjects = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(myContext).inflate(R.layout.autocomplete_layout,parent,false);

        T currentObject = objects.get(position);

        TextView name = listItem.findViewById(R.id.name);
        name.setTypeface(EasyFonts.captureIt(myContext));
        name.setText(currentObject.getName());

        return listItem;
    }

    @Override
    public T getItem(int position){
        return objects.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return mFilter;
    }

    public List<T> getAll(){
        return Collections.unmodifiableList(allObjects);
    }

    public T getByName(String name){
        for(T t : allObjects){
            if(t.getName().toLowerCase().equals(name.toLowerCase())){
                return t;
            }
        }
        return null;
    }

    private Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((GetNameProvider)resultValue).getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<T> suggestions = new ArrayList<>();

            if(constraint != null){
                for(T object : allObjects){
                    if(object.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        suggestions.add(object);

                    }
                }
            }

            filterResults.count = suggestions.size();
            filterResults.values = suggestions;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            objects.clear();
            if(results != null && results.count > 0){
                addAll((List<T>) results.values);
                notifyDataSetChanged();
            } else if(constraint == null || constraint.length() == 0){
                addAll(allObjects);
                notifyDataSetInvalidated();
            }
        }
    };
}
