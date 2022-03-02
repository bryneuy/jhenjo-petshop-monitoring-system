package com.example.finaltest_version_one.Main.ChildFragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.Main.RecyclerView.LandingnInventoryRecyclerAdapter;
import com.example.finaltest_version_one.Main.RecyclerView.SubcategoryRecyclerAdapter;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class AquariumChildFragment extends Fragment implements SubcategoryRecyclerAdapter.SubcategoryListener, LandingnInventoryRecyclerAdapter.nInventoryListiner {

    private ArrayList<String> subcategory = new ArrayList<>();
    private static String TAG = "MainChildFragment";

    //added June 6, 2020
    private RecyclerView recyclerView;
    private AlertDialog currentInventoryAlertDialog;
    private AlertDialog currentViewedInventoryAlertDialog;
    private CollectionReference searchFilter = FirebaseFirestore.getInstance().collection("searchFilter");
    private CollectionReference finalRef = null;
    //For semi fullscreen and search functionality
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_accessories_child, container, false);
        FirebaseFirestore.getInstance().collection("nInventory").document("Aquarium").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()){
                    subcategory = (ArrayList<String>)documentSnapshot.get("subcategory");

                    if (subcategory.isEmpty()){
                        subcategory.add("This category is currently empty...");
                        SubcategoryRecyclerAdapter subcategoryRecyclerAdapter = new SubcategoryRecyclerAdapter("Aquarium",subcategory,getContext(), AquariumChildFragment.this);
                        RecyclerView recyclerView;
                        recyclerView = v.findViewById(R.id.cFragRecyclerView);
                        recyclerView.setAdapter(subcategoryRecyclerAdapter);
                    }else{
                        SubcategoryRecyclerAdapter subcategoryRecyclerAdapter = new SubcategoryRecyclerAdapter("Aquarium",subcategory,getContext(), AquariumChildFragment.this);
                        RecyclerView recyclerView;
                        recyclerView = v.findViewById(R.id.cFragRecyclerView);
                        recyclerView.setAdapter(subcategoryRecyclerAdapter);
                    }

                }else{
                    subcategory.add("This category is currently empty...");
                    SubcategoryRecyclerAdapter subcategoryRecyclerAdapter = new SubcategoryRecyclerAdapter("Aquarium",subcategory,getContext(), AquariumChildFragment.this);
                    RecyclerView recyclerView;
                    recyclerView = v.findViewById(R.id.cFragRecyclerView);
                    recyclerView.setAdapter(subcategoryRecyclerAdapter);
                }
            }
        });
        return v;
    }

    @Override
    public void fetchQueryAndSub(Query query, String subcategory) {

        searchFilter.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    batch.delete(searchFilter.document(documentSnapshot.getId()));
                }
                batch.commit();
            }
        });

        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_landing_current_inventory,null);

        final EditText search = v.findViewById(R.id.caSearch);
        final ImageButton back = v.findViewById(R.id.caBack);

        recyclerView = v.findViewById(R.id.currentInventoryRecyclerView);

        FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                .setQuery(query, nInventory.class)
                .build();

        LandingnInventoryRecyclerAdapter nInventoryRecyclerAdapter = new LandingnInventoryRecyclerAdapter(options, AquariumChildFragment.this, getContext());
        recyclerView.setAdapter(nInventoryRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        nInventoryRecyclerAdapter.startListening();

        search.addTextChangedListener(new TextWatcher() {
            int state = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                finalRef = FirebaseFirestore.getInstance().collection("nInventory").document("Aquarium").collection(subcategory.toUpperCase());
                if (s.toString().length() < 2){
                    state = 0;
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange : documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
                                if (nInventory.getName().toLowerCase().substring(0,s.length()).equals(s.toString().toLowerCase().substring(0,s.length())) ){

                                    searchFilter.document(nInventory.getName()).set(nInventory);
                                }
                            }
                            Query query = searchFilter.whereEqualTo("status","Enable");

                            FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                                    .setQuery(query, nInventory.class)
                                    .build();

                            if (search.getText().toString().isEmpty()) {

                                searchFilter.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                            batch.delete(searchFilter.document(documentSnapshot.getId()));
                                        }
                                        batch.commit();

                                    }
                                });

                                Query queryy = finalRef.whereEqualTo("status", "Enable");

                                FirestoreRecyclerOptions<nInventory> optionss = new FirestoreRecyclerOptions.Builder<nInventory>()
                                        .setQuery(queryy, nInventory.class)
                                        .build();

                                LandingnInventoryRecyclerAdapter nInventoryRecyclerAdapter = new LandingnInventoryRecyclerAdapter(optionss, AquariumChildFragment.this, getContext());
                                recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
                                nInventoryRecyclerAdapter.startListening();
                            }else{

                                LandingnInventoryRecyclerAdapter nInventoryRecyclerAdapter = new LandingnInventoryRecyclerAdapter(options, AquariumChildFragment.this, getContext());
                                recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
                                nInventoryRecyclerAdapter.startListening();
                            }
                        }
                    });
                } if ((s.toString().length() >= 2) && (state < 1)){
                    state = 1;
                    searchFilter.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            WriteBatch batch = FirebaseFirestore.getInstance().batch();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                batch.delete(searchFilter.document(documentSnapshot.getId()));
                            }
                            batch.commit();

                            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                                    for (DocumentChange documentChange : documentChangeList) {
                                        final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
                                        if (nInventory.getName().toLowerCase().substring(0,s.length()).equals(s.toString().toLowerCase().substring(0,s.length())) ){

                                            searchFilter.document(nInventory.getName()).set(nInventory);

                                        }
                                    }
                                    Query query = searchFilter.whereEqualTo("status","Enable");

                                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                                            .setQuery(query, nInventory.class)
                                            .build();

                                    LandingnInventoryRecyclerAdapter nInventoryRecyclerAdapter = new LandingnInventoryRecyclerAdapter(options, AquariumChildFragment.this, getContext());
                                    recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
                                    nInventoryRecyclerAdapter.startListening();

                                }
                            });
                        }

                    });

                }

            }

        });

        //Setting up custom dialog layout
        currentInventoryAlertDialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .create();
        currentInventoryAlertDialog.show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInventoryAlertDialog.cancel();
            }
        });
        currentInventoryAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    currentInventoryAlertDialog.cancel();
                }
                return true;
            }
        });
    }

    @Override
    public void editInventoryItem(DocumentSnapshot documentSnapshot) {

        currentInventoryAlertDialog.dismiss();

        //Setting up the custom layout
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_landing_view_inventory_item, null);

        final TextView itemName = v.findViewById(R.id.viewItemName);
        final ImageView imageView = v.findViewById(R.id.viewImageView);
        final ImageButton back = v.findViewById(R.id.landingBack);

        itemName.setText(documentSnapshot.toObject(nInventory.class).getName());

        Glide.with(getContext())
                .asBitmap()
                .load(documentSnapshot.toObject(nInventory.class).getDownloadURL())
                .into(imageView);

        //Setting up custom dialog layout
        currentViewedInventoryAlertDialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .create();
        currentViewedInventoryAlertDialog.show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentViewedInventoryAlertDialog.cancel();
                currentInventoryAlertDialog.show();
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

    @Override
    public void disableInventoryItem(DocumentSnapshot documentSnapshot) {

    }
}
