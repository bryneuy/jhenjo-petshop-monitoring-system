package com.example.finaltest_version_one.Admin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.Inventory;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class InventoryRecyclerAdapter extends FirestoreRecyclerAdapter<Inventory, InventoryRecyclerAdapter.InventoryVieHolder> {

    private InventoryListener inventoryListener;


    public InventoryRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Inventory> options, InventoryListener inventoryListener) {
        super(options);
        this.inventoryListener = inventoryListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull InventoryVieHolder holder, int position, @NonNull Inventory inventory) {
        holder.name.setText(inventory.getName());
        holder.quantity.setText(String.valueOf(inventory.getQuantity()));
        holder.category.setText(inventory.getCategory());

        Glide.with(holder.itemView)
                .asBitmap()
                .load(inventory.getDownloadURL())
                .into(holder.circleImageView);

    }

    @NonNull
    @Override
    public InventoryVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.admin_inventory_row,parent, false);
        return new InventoryVieHolder(v);
    }

     public class InventoryVieHolder extends RecyclerView.ViewHolder {
        TextView name,quantity,category;
        CircleImageView circleImageView;

        InventoryVieHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.iName);
            quantity = itemView. findViewById(R.id.iQuantity);
            category = itemView.findViewById(R.id.iCategory);
            circleImageView = itemView.findViewById(R.id.circleImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    inventoryListener.editInventory(snapshot);

                }
            });
        }

        public void disableItem (){
            inventoryListener.disableItemInventory(getSnapshots().getSnapshot(getAdapterPosition()));
        }


    }
    public interface InventoryListener {
        void editInventory (DocumentSnapshot snapshot);
        void disableItemInventory(DocumentSnapshot snapshot);
    }
 }
