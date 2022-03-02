package com.example.finaltest_version_one.Main.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Document;

public class InventoryRecyclerAdapter extends FirestoreRecyclerAdapter<nInventory, InventoryRecyclerAdapter.InventoryViewHolder> {

    private Context context;
    private InventoryRecyclerAdapterListener inventoryRecyclerAdapterListener;
    public InventoryRecyclerAdapter(@NonNull FirestoreRecyclerOptions<nInventory> options, Context context, InventoryRecyclerAdapterListener inventoryRecyclerAdapterListener) {
        super(options);
        this.context = context;
        this.inventoryRecyclerAdapterListener = inventoryRecyclerAdapterListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull InventoryViewHolder holder, int position, @NonNull nInventory nInventory) {
        holder.itemName.setText(nInventory.getName());

        Glide.with(context)
                .asBitmap()
                .load(nInventory.getDownloadURL())
                .into(holder.innerImageView);

//        holder.itemName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                inventoryRecyclerAdapterListener.viewInventoryItem(nInventory);
//            }
//        });

    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.main_childfragment_inner_row,null);
        return new InventoryViewHolder(v);
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder{

        ImageView innerImageView;
        TextView itemName;
        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            innerImageView = itemView.findViewById(R.id.mInnerImageView);
            itemName = itemView.findViewById(R.id.mItemName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    inventoryRecyclerAdapterListener.viewInventoryItem(snapshot);
                }
            });
        }
    }

    public interface InventoryRecyclerAdapterListener {
        void viewInventoryItem(DocumentSnapshot documentSnapshot);
    }
}
