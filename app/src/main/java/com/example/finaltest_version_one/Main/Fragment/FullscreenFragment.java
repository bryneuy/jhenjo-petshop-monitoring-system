package com.example.finaltest_version_one.Main.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.Main.RecyclerView.InventoryRecyclerAdapter;
import com.example.finaltest_version_one.Main.RecyclerView.SubcategoryRecyclerAdapter.SubcategoryListener;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class FullscreenFragment extends Fragment implements SubcategoryListener, InventoryRecyclerAdapter.InventoryRecyclerAdapterListener {

    private RecyclerView fsRecyclerView;
    private TextView subTextView;
    private InventoryRecyclerAdapter landingInventoryRecyclerAdapter;
    private FloatingActionButton back;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fullscreen, container, false);
        fsRecyclerView = v.findViewById(R.id.fsRecyclerView);
        subTextView = v.findViewById(R.id.subTextView);
        back = v.findViewById(R.id.back);
        return v;
    }

    @Override
    public void fetchQueryAndSub(Query query, String subcategory) {

        subTextView.setText(subcategory);
        FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                .setQuery(query,nInventory.class)
                .build();

        landingInventoryRecyclerAdapter = new InventoryRecyclerAdapter(options,getContext(), this);
        fsRecyclerView.setAdapter(landingInventoryRecyclerAdapter);
        landingInventoryRecyclerAdapter.startListening();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.main_container,new LandingPageFragment()).commit();
            }
        });
    }

    @Override
    public void viewInventoryItem(DocumentSnapshot documentSnapshot) {

    }
}
