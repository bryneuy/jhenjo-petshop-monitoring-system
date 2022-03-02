package com.example.finaltest_version_one.Employee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;


public class Employee_nInventoryRecyclerAdapter extends FirestoreRecyclerAdapter<nInventory, Employee_nInventoryRecyclerAdapter.nInventoryViewHolder> {

    private nInventoryListiner nInventoryListiner;

    public Employee_nInventoryRecyclerAdapter(@NonNull FirestoreRecyclerOptions<nInventory> options, Employee_nInventoryRecyclerAdapter.nInventoryListiner nInventoryListiner) {
        super(options);
        this.nInventoryListiner = nInventoryListiner;
    }

    @Override
    protected void onBindViewHolder(@NonNull nInventoryViewHolder holder, int position, @NonNull nInventory nInventory) {

        holder.name.setText(nInventory.getName());
        holder.quantity.setText(String.valueOf(nInventory.getQuantity()));
        holder.category.setText(nInventory.getCategory());

        Glide.with(holder.itemView)
                .asBitmap()
                .load(nInventory.getDownloadURL())
                .into(holder.circleImageView);

    }

    @NonNull
    @Override
    public nInventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.admin_ninventory_child_row,parent, false);
        return new nInventoryViewHolder(v);
    }

    public class nInventoryViewHolder extends RecyclerView.ViewHolder {

        TextView name, quantity, category;
        CircleImageView circleImageView;

        public nInventoryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.ncName);
            quantity = itemView.findViewById(R.id.ncQuantity);
            category = itemView.findViewById(R.id.ncCategory);
            circleImageView = itemView.findViewById(R.id.ncCircleImageView);

        }
        

    }


    public interface nInventoryListiner {
        void editInventoryItem(DocumentSnapshot documentSnapshot);
        void disableInventoryItem(DocumentSnapshot documentSnapshot);
    }
}
