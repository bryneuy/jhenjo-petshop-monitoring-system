package com.example.finaltest_version_one.Main.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class SubcategoryRecyclerAdapter extends RecyclerView.Adapter<SubcategoryRecyclerAdapter.SubcategoryViewHolder> implements InventoryRecyclerAdapter.InventoryRecyclerAdapterListener {

    private String category;
    private ArrayList<String> subcategory;
    private Context context;
    private InventoryRecyclerAdapter landingInventoryRecyclerAdapter;
    private SubcategoryListener subcategoryListener;
    //added June 6, 2020
    private RecyclerView recyclerView;
    private AlertDialog currentInventoryAlertDialog;
    private AlertDialog currentViewedInventoryAlertDialog;
    private CollectionReference searchFilter = FirebaseFirestore.getInstance().collection("searchFilter");
    private CollectionReference finalRef = null;
    //For semi fullscreen and search functionality

    public SubcategoryRecyclerAdapter(String category, ArrayList<String> subcategory, Context context, SubcategoryListener subcategoryListener) {
        this.category = category;
        this.subcategory = subcategory;
        this.context = context;
        this.subcategoryListener = subcategoryListener;
    }

    @NonNull
    @Override
    public SubcategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.main_childfragment_root_row,null);
        return new SubcategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoryViewHolder holder, int position) {

        holder.subcategory.setText(subcategory.get(position));
        if (subcategory.get(position).equals("This category is currently empty...")){
            holder.fullscreen.setVisibility(View.GONE);

        }
        Query query = FirebaseFirestore.getInstance().collection("nInventory").document(category).collection(subcategory.get(position).toUpperCase()).whereEqualTo("status","Enable");
        FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                .setQuery(query,nInventory.class)
                .build();


        landingInventoryRecyclerAdapter = new InventoryRecyclerAdapter(options,context, this);
        holder.innerRecyclerView.setAdapter(landingInventoryRecyclerAdapter);
        landingInventoryRecyclerAdapter.startListening();

        holder.subcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                subcategoryListener.fetchQueryAndSub(query,subcategory.get(position));

            }
        });

        holder.fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subcategoryListener.fetchQueryAndSub(query,subcategory.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return subcategory.size();
    }


    @Override
    public void viewInventoryItem(DocumentSnapshot documentSnapshot) {
        nInventory nInventory = documentSnapshot.toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
        //Setting up the custom layout
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.custom_landing_view_inventory_item, null);
//
        final TextView itemName = v.findViewById(R.id.viewItemName);
        final ImageView imageView = v.findViewById(R.id.viewImageView);
        final ImageButton back = v.findViewById(R.id.landingBack);

        itemName.setText(nInventory.getName());

        Glide.with(context)
                .asBitmap()
                .load(nInventory.getDownloadURL())
                .into(imageView);

        //Setting up custom dialog layout
        currentViewedInventoryAlertDialog = new AlertDialog.Builder(context)
                .setView(v)
                .create();
        currentViewedInventoryAlertDialog.show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentViewedInventoryAlertDialog.cancel();
            }
        });
        currentViewedInventoryAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    currentViewedInventoryAlertDialog.cancel();
                }
                return true;
            }
        });
    }

    public class SubcategoryViewHolder extends RecyclerView.ViewHolder{

        TextView subcategory;
        ImageButton fullscreen;
        RecyclerView innerRecyclerView;

        public SubcategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            subcategory = itemView.findViewById(R.id.subcategoryTextView);
            fullscreen = itemView.findViewById(R.id.fullscreen);
            innerRecyclerView = itemView.findViewById(R.id.innerRecyclerView);

        }

    }
    public interface SubcategoryListener{
        void fetchQueryAndSub(Query query, String subcategory);

    }
}
