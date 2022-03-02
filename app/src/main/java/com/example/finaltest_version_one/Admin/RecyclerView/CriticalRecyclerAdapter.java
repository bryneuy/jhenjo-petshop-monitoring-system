package com.example.finaltest_version_one.Admin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CriticalRecyclerAdapter extends RecyclerView.Adapter<CriticalRecyclerAdapter.CriticalViewHolder>{

    private ArrayList<nInventory> criticalItems;

    public CriticalRecyclerAdapter(ArrayList<nInventory> criticalItems) {
        this.criticalItems = criticalItems;
    }

    @NonNull
    @Override
    public CriticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.admin_critical_child, null);
        return new CriticalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CriticalViewHolder holder, int position) {

        holder.name.setText(criticalItems.get(position).getName());
        holder.category.setText(criticalItems.get(position).getCategory());
        holder.quantity.setText(String.valueOf(criticalItems.get(position).getQuantity()));

        Glide.with(holder.itemView)
                .asBitmap()
                .load(criticalItems.get(position).getDownloadURL())
                .into(holder.circleImageView);

    }

    @Override
    public int getItemCount() {
        return criticalItems.size();
    }

    public class CriticalViewHolder extends RecyclerView.ViewHolder {

        TextView name, category, quantity;
        CircleImageView circleImageView;
        public CriticalViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.critName);
            category = itemView.findViewById(R.id.critCategory);
            quantity = itemView.findViewById(R.id.critQuantity);
            circleImageView = itemView.findViewById(R.id.critCircleImageView);

        }
    }

}


