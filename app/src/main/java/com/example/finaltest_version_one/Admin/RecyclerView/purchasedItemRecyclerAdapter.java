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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class purchasedItemRecyclerAdapter extends FirestoreRecyclerAdapter<nInventory, purchasedItemRecyclerAdapter.nInventoryViewHolder>{

    private nInventoryListiner nInventoryListiner;

    public purchasedItemRecyclerAdapter(@NonNull FirestoreRecyclerOptions<nInventory> options, purchasedItemRecyclerAdapter.nInventoryListiner nInventoryListiner) {
        super(options);
        this.nInventoryListiner = nInventoryListiner;
    }

    @Override
    protected void onBindViewHolder(@NonNull nInventoryViewHolder holder, int position, @NonNull nInventory nInventory) {

        holder.name.setText(nInventory.getName());
        holder.price.setText(String.valueOf(nInventory.getPrice()));
        holder.quantity.setText(String.valueOf(nInventory.getQuantity()));

        Glide.with(holder.itemView)
                .asBitmap()
                .load(nInventory.getDownloadURL())
                .into(holder.circleImageView);

    }

    @NonNull
    @Override
    public nInventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.purchaseviewer,parent, false);
        return new nInventoryViewHolder(v);
    }

    public class nInventoryViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, quantity;
        CircleImageView circleImageView;

        public nInventoryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.purchaseName);
            price = itemView.findViewById(R.id.purchasePrice);
            quantity = itemView.findViewById(R.id.purchaseQuantity);
            circleImageView = itemView.findViewById(R.id.circleView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    nInventoryListiner.editInventoryItem(snapshot);
                }
            });
        }
    }

    public interface nInventoryListiner {
        void editInventoryItem(DocumentSnapshot documentSnapshot);
    }
}
