package com.example.finaltest_version_one.Admin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltest_version_one.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

public class SubcategoryRecyclerAdapter extends RecyclerView.Adapter<SubcategoryRecyclerAdapter.SubcategoryViewHolder> implements Filterable {

    private ArrayList<String> cSubcategory;
    private ArrayList<String> cSubcategoryAll;
    private SubcategoryListener subcategoryListener;

    public SubcategoryRecyclerAdapter(ArrayList<String> subcategory, SubcategoryListener subcategoryListener) {
        this.cSubcategory = subcategory;
        this.cSubcategoryAll = new ArrayList<>(subcategory);
        this.subcategoryListener = subcategoryListener;
    }

    @NonNull
    @Override
    public SubcategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.admin_subcategory_row, parent, false);
        return new SubcategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoryViewHolder holder, final int position) {

        holder.subcategory.setText(cSubcategory.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentSubcategory = cSubcategory.get(position);
                subcategoryListener.viewSubcategory(currentSubcategory);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cSubcategory.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<String> filteredSubcategory = new ArrayList<>();

            if (constraint.toString().isEmpty()){
                filteredSubcategory.addAll(cSubcategoryAll);
            }else{
                for (String s: cSubcategoryAll){
                    if (s.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredSubcategory.add(s);

                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredSubcategory;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            cSubcategory.clear();
            cSubcategory.addAll((Collection<? extends String>) results.values);
            notifyDataSetChanged();
        }
    };

//    public void filterlist(ArrayList<String> filteredNames) {
//        this.cSubcategory = filteredNames;
//        notifyDataSetChanged();
//
//    }

    public class SubcategoryViewHolder extends RecyclerView.ViewHolder{

        TextView subcategory;

        SubcategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            subcategory = itemView.findViewById(R.id.scSubcategory);

        }
    }

    public interface SubcategoryListener {
        void viewSubcategory (String currentSubcategory);
    }
}
