package com.example.finaltest_version_one.Admin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltest_version_one.Admin.Fragment.Admin_FinanceFragment;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FinanceRecyclerAdapter extends FirestoreRecyclerAdapter<nInventory, FinanceRecyclerAdapter.FinanceViewHolder> {

    public FinanceRecyclerAdapter(@NonNull FirestoreRecyclerOptions<nInventory> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FinanceViewHolder holder, int position, @NonNull nInventory inventory) {
        holder.quantity.setText(String.valueOf(inventory.getQuantity()));
        holder.name.setText(inventory.getName());
        holder.price.setText(String.valueOf(inventory.getPrice() / inventory.getQuantity()));
        holder.totalprice.setText(String.valueOf(inventory.getPrice()));
    }

    @NonNull
    @Override
    public FinanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.financeadapter,parent, false);
        return new FinanceViewHolder(v);
    }

    public class FinanceViewHolder extends RecyclerView.ViewHolder{
        TextView quantity,name,price,totalprice;
        public FinanceViewHolder(@NonNull View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.q1);
            name = itemView.findViewById(R.id.n1);
            price = itemView.findViewById(R.id.p1);
            totalprice = itemView.findViewById(R.id.t1);
        }
    }

}
