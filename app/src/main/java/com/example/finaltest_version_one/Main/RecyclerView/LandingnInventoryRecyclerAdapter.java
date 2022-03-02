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

import de.hdodenhof.circleimageview.CircleImageView;


public class LandingnInventoryRecyclerAdapter extends FirestoreRecyclerAdapter<nInventory, LandingnInventoryRecyclerAdapter.nInventoryViewHolder> {

    private nInventoryListiner nInventoryListiner;
    private Context context;

    public LandingnInventoryRecyclerAdapter(@NonNull FirestoreRecyclerOptions<nInventory> options, LandingnInventoryRecyclerAdapter.nInventoryListiner nInventoryListiner, Context context) {
        super(options);
        this.nInventoryListiner = nInventoryListiner;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull nInventoryViewHolder holder, int position, @NonNull nInventory nInventory) {

        holder.itemName.setText(nInventory.getName());

        Glide.with(context)
                .asBitmap()
                .load(nInventory.getDownloadURL())
                .into(holder.innerImageView);

    }

    @NonNull
    @Override
    public nInventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.landing_childfragment_inner_row,parent, false);
        return new nInventoryViewHolder(v);
    }

    public class nInventoryViewHolder extends RecyclerView.ViewHolder {

        ImageView innerImageView;
        TextView itemName;

        public nInventoryViewHolder(@NonNull View itemView) {
            super(itemView);

            innerImageView = itemView.findViewById(R.id.innerImageView);
            itemName = itemView.findViewById(R.id.itemName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    nInventoryListiner.editInventoryItem(snapshot);
                }
            });
        }

        public void disableItem (){
            nInventoryListiner.disableInventoryItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }

    }


    public interface nInventoryListiner {
        void editInventoryItem(DocumentSnapshot documentSnapshot);


        void disableInventoryItem(DocumentSnapshot documentSnapshot);
    }
}
