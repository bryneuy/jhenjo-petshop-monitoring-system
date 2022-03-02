package com.example.finaltest_version_one.Admin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;

import de.hdodenhof.circleimageview.CircleImageView;

public class customInventoryRecyclerAdapter extends RecyclerView.Adapter<customInventoryRecyclerAdapter.customInventoryViewHolder> implements Filterable {

    private ArrayList<nInventory> customInventory;
    private ArrayList<nInventory> customInventoryAll;
    private customInventoryListener customInventoryListener;

    public customInventoryRecyclerAdapter(ArrayList<nInventory> customInventory, customInventoryRecyclerAdapter.customInventoryListener customInventoryListener) {
        this.customInventory = customInventory;
        this.customInventoryAll = new ArrayList<>(customInventory);
        this.customInventoryListener = customInventoryListener;
    }

    @NonNull
    @Override
    public customInventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.admin_ninventory_child_row,parent, false);
        return new customInventoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull customInventoryViewHolder holder, int position) {
        holder.name.setText(customInventory.get(position).getName());
        holder.quantity.setText(String.valueOf(customInventory.get(position).getQuantity()));
        holder.category.setText(customInventory.get(position).getCategory());

        Glide.with(holder.itemView)
                .asBitmap()
                .load(customInventory.get(position).getDownloadURL())
                .into(holder.circleImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customInventoryListener.editInventoryItem(customInventory.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return customInventory.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<nInventory> filteredInventory = new ArrayList<>();

            if (constraint.toString().isEmpty()){
                filteredInventory.addAll(customInventoryAll);
            }else{
                for (nInventory s: customInventoryAll){
                    if (s.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredInventory.add(s);

                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredInventory;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            customInventory.clear();
            customInventory.addAll((Collection<? extends nInventory>) results.values);
            notifyDataSetChanged();

        }
    };

    public class customInventoryViewHolder extends RecyclerView.ViewHolder {

        TextView name, quantity, category;
        CircleImageView circleImageView;

        public customInventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ncName);
            quantity = itemView.findViewById(R.id.ncQuantity);
            category = itemView.findViewById(R.id.ncCategory);
            circleImageView = itemView.findViewById(R.id.ncCircleImageView);

        }

    }

    public interface customInventoryListener {
        void editInventoryItem(nInventory customInventory);
        void disableInventoryItem(nInventory customInventory);
    }
}
