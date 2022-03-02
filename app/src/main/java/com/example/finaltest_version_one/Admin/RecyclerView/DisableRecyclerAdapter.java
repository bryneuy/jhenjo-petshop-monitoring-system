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

public class DisableRecyclerAdapter extends FirestoreRecyclerAdapter<nInventory, DisableRecyclerAdapter.DisableViewHolder> {

    private DisableListener disableListener;
    public DisableRecyclerAdapter(@NonNull FirestoreRecyclerOptions<nInventory> options,DisableRecyclerAdapter.DisableListener disableListener) {
        super(options);
        this.disableListener = disableListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull DisableViewHolder holder, int position, @NonNull nInventory nInventory) {

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
    public DisableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.admin_disable_child, null);
        return new DisableViewHolder(v);
    }

    public class DisableViewHolder extends RecyclerView.ViewHolder {

        TextView name, category, quantity;
        CircleImageView circleImageView;

        public DisableViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.disName);
            category = itemView.findViewById(R.id.disCategory);
            quantity = itemView.findViewById(R.id.disQuantity);
            circleImageView = itemView.findViewById(R.id.disCircleImageView);

        }
        public void deleteItem(){
            disableListener.deleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
        public void enableItem(){
            disableListener.enableItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

    public interface DisableListener {
        void deleteItem(DocumentSnapshot documentSnapshot);
        void enableItem(DocumentSnapshot documentSnapshot);
    }

    }
//public class DisableRecyclerAdapter extends RecyclerView.Adapter<DisableRecyclerAdapter.DisableViewHolder>{
//
//    private ArrayList<nInventory> disabledItems;
//    private DisableListener disableListener;
//    public DisableRecyclerAdapter(ArrayList<nInventory> disabledItems, DisableListener disableListener) {
//        this.disabledItems = disabledItems;
//        this.disableListener = disableListener;
//    }
//
//    @NonNull
//    @Override
//    public DisableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View v = inflater.inflate(R.layout.admin_disable_child, null);
//        return new DisableViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull DisableViewHolder holder, int position) {
//
//        holder.name.setText(disabledItems.get(position).getName());
//        holder.category.setText(disabledItems.get(position).getCategory());
//        holder.quantity.setText(String.valueOf(disabledItems.get(position).getQuantity()));
//
//        Glide.with(holder.itemView)
//                .asBitmap()
//                .load(disabledItems.get(position).getDownloadURL())
//                .into(holder.circleImageView);
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return disabledItems.size();
//    }
//
//    public class DisableViewHolder extends RecyclerView.ViewHolder {
//
//        TextView name, category, quantity;
//        CircleImageView circleImageView;
//        public DisableViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            name = itemView.findViewById(R.id.disName);
//            category = itemView.findViewById(R.id.disCategory);
//            quantity = itemView.findViewById(R.id.disQuantity);
//            circleImageView = itemView.findViewById(R.id.disCircleImageView);
//
//        }
//        public void deleteItem(){
//            disableListener.deleteItem(disabledItems.get(getAdapterPosition()));
//        }
//        public void enableItem(){
//            disableListener.enableItem(disabledItems.get(getAdapterPosition()));
//        }
//    }
//
//    public interface DisableListener {
//        void deleteItem(nInventory nInventory);
//        void enableItem(nInventory nInventory);
//    }
//}