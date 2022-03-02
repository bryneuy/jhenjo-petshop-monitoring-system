package com.example.finaltest_version_one.Admin.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class purchasedRecyclerAdapter extends RecyclerView.Adapter<purchasedRecyclerAdapter.CategoryViewHolder> implements SubcategoryRecyclerAdapter.SubcategoryListener, nInventoryRecyclerAdapter.nInventoryListiner {

    private static final String TAG = "CategoryRecyclerAdapter";
    private ArrayList<String> categories;
    private ArrayList<String> subcategory = new ArrayList<>();
    private CollectionReference inventoryReference = FirebaseFirestore.getInstance().collection("nInventory");
    private CollectionReference finalRef = null;
    private Context context;


    private CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("nInventory");
    private DocumentReference accessoriesRef = collectionReference.document("Accessories");
    private DocumentReference animalRef = collectionReference.document("Animal");
    private DocumentReference aquariumRef = collectionReference.document("Aquarium");
    private DocumentReference cageRef = collectionReference.document("Cage");
    private DocumentReference foodRef = collectionReference.document("Food");
    private DocumentReference pumpRef = collectionReference.document("Pump");

    private ArrayList<String> accessories = new ArrayList<>();
    private ArrayList<String> animal = new ArrayList<>();
    private ArrayList<String> aquarium = new ArrayList<>();
    private ArrayList<String> cage = new ArrayList<>();
    private ArrayList<String> food = new ArrayList<>();
    private ArrayList<String> pump = new ArrayList<>();

    private RecyclerView recyclerView;
    private AlertDialog currentInventoryAlertDialog;
    private AlertDialog updateAlertDialog;

    private static final String[] spStatus = new String[]{"Enable", "Disable"};
    private static final String[] autoCategories = new String[]{"Accessories", "Animal", "Aquarium", "Cage", "Food", "Pump"};

    private String clickedCategory = null;
    private String clickedStatus = null;

    private ArrayList<String> checkedSubcategory = new ArrayList<>();
    private ArrayList<String> existingQRcode = new ArrayList<>();
    private ArrayList<String> existingItemName = new ArrayList<>();

    private CircleImageView circleImageView;

    //search filter
    private ArrayList<nInventory> customInventory = new ArrayList<>();
    private CollectionReference searchFilter = FirebaseFirestore.getInstance().collection("searchFilter");
    //loading Purpose
    private ArrayList<String> itemCountChecker = new ArrayList<>();



    public purchasedRecyclerAdapter(ArrayList<String> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.admin_ninventory_row, parent, false);
        return new CategoryViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position) {

        holder.search.setHint(categories.get(position));

        String category = categories.get(position);

        inventoryReference.document(category).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                subcategory = (ArrayList<String>) snapshot.get("subcategory");

                SubcategoryRecyclerAdapter subcategoryRecyclerAdapter = new SubcategoryRecyclerAdapter(subcategory, purchasedRecyclerAdapter.this);
                holder.search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        subcategoryRecyclerAdapter.getFilter().filter(s);
                    }
                });

                holder.mRecyclerView.setAdapter(subcategoryRecyclerAdapter);

                //Divider (for border outline of each view holder)
                holder.mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));




            }
        });


    }


    @Override
    public int getItemCount() {
        return categories.size();
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        EditText search;
        ImageButton fullscreen;
        RecyclerView mRecyclerView;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            search = itemView.findViewById(R.id.nSearch);
            mRecyclerView = itemView.findViewById(R.id.mRecyclerView);

        }

    }

    @Override
    public void viewSubcategory(final String currentSubcategory) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.custom_admin_current_inventory, null);

        final EditText search = v.findViewById(R.id.caSearch);
        final ImageButton back = v.findViewById(R.id.caBack);
        recyclerView = v.findViewById(R.id.currentInventoryRecyclerView);

//        //Divider (for border outline of each view holder)
//        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));


        //For debugging purpose
        Log.d(TAG, "viewSubcategory: " + currentSubcategory);

        accessoriesRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                accessories = (ArrayList<String>) snapshot.get("subcategory");
                Log.d(TAG, "onFetching Docs:" + accessories);
                if (accessories.contains(currentSubcategory)) {
                    finalRef = accessoriesRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status", "Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                    recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                itemCountChecker.add(nInventory.getName());
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
                                                    itemCountChecker.clear();
                                                }
                                            });

                                            Query queryy = finalRef.whereEqualTo("status", "Enable");

                                            FirestoreRecyclerOptions<nInventory> optionss = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                    .setQuery(queryy, nInventory.class)
                                                    .build();

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                        itemCountChecker.add(nInventory.getName());
                                                    }
                                                }
                                                Query query = searchFilter.whereEqualTo("status","Enable");

                                                FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                        .setQuery(query, nInventory.class)
                                                        .build();

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                                recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                                nInventoryRecyclerAdapter.startListening();

                                            }
                                        });
                                    }

                                });

                            }

                        }

                    });

                }
            }
        });

        animalRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                animal = (ArrayList<String>) snapshot.get("subcategory");
                Log.d(TAG, "onFetching Docs:" + animal);
                if (animal.contains(currentSubcategory)) {
                    finalRef = animalRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status", "Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                    recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                itemCountChecker.add(nInventory.getName());
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
                                                    itemCountChecker.clear();
                                                }
                                            });

                                            Query queryy = finalRef.whereEqualTo("status", "Enable");

                                            FirestoreRecyclerOptions<nInventory> optionss = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                    .setQuery(queryy, nInventory.class)
                                                    .build();

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                        itemCountChecker.add(nInventory.getName());
                                                    }
                                                }
                                                Query query = searchFilter.whereEqualTo("status","Enable");

                                                FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                        .setQuery(query, nInventory.class)
                                                        .build();

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                                recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                                nInventoryRecyclerAdapter.startListening();

                                            }
                                        });
                                    }

                                });

                            }

                        }

                    });
                }
            }
        });

        aquariumRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                aquarium = (ArrayList<String>) snapshot.get("subcategory");
                Log.d(TAG, "onFetching Docs:" + aquarium);
                if (aquarium.contains(currentSubcategory)) {
                    finalRef = aquariumRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status", "Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                    recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                itemCountChecker.add(nInventory.getName());
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
                                                    itemCountChecker.clear();
                                                }
                                            });

                                            Query queryy = finalRef.whereEqualTo("status", "Enable");

                                            FirestoreRecyclerOptions<nInventory> optionss = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                    .setQuery(queryy, nInventory.class)
                                                    .build();

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                        itemCountChecker.add(nInventory.getName());
                                                    }
                                                }
                                                Query query = searchFilter.whereEqualTo("status","Enable");

                                                FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                        .setQuery(query, nInventory.class)
                                                        .build();

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                                recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                                nInventoryRecyclerAdapter.startListening();

                                            }
                                        });
                                    }

                                });

                            }

                        }

                    });
                }
            }
        });


        cageRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                cage = (ArrayList<String>) snapshot.get("subcategory");
                Log.d(TAG, "onFetching Docs:" + cage);
                if (cage.contains(currentSubcategory)) {
                    finalRef = cageRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status", "Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                    recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                itemCountChecker.add(nInventory.getName());
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
                                                    itemCountChecker.clear();
                                                }
                                            });

                                            Query queryy = finalRef.whereEqualTo("status", "Enable");

                                            FirestoreRecyclerOptions<nInventory> optionss = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                    .setQuery(queryy, nInventory.class)
                                                    .build();

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                        itemCountChecker.add(nInventory.getName());
                                                    }
                                                }
                                                Query query = searchFilter.whereEqualTo("status","Enable");

                                                FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                        .setQuery(query, nInventory.class)
                                                        .build();

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                                recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                                nInventoryRecyclerAdapter.startListening();

                                            }
                                        });
                                    }

                                });

                            }

                        }

                    });

                }
            }
        });

        foodRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                food = (ArrayList<String>) snapshot.get("subcategory");
                Log.d(TAG, "onFetching Docs:" + food);
                if (food.contains(currentSubcategory)) {
                    finalRef = foodRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status", "Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                    recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                itemCountChecker.add(nInventory.getName());
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
                                                    itemCountChecker.clear();
                                                }
                                            });

                                            Query queryy = finalRef.whereEqualTo("status", "Enable");

                                            FirestoreRecyclerOptions<nInventory> optionss = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                    .setQuery(queryy, nInventory.class)
                                                    .build();

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                        itemCountChecker.add(nInventory.getName());
                                                    }
                                                }
                                                Query query = searchFilter.whereEqualTo("status","Enable");

                                                FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                        .setQuery(query, nInventory.class)
                                                        .build();

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                                recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                                nInventoryRecyclerAdapter.startListening();

                                            }
                                        });
                                    }

                                });

                            }

                        }

                    });
                }
            }
        });

        pumpRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                pump = (ArrayList<String>) snapshot.get("subcategory");
                Log.d(TAG, "onFetching Docs:" + pump);
                if (pump.contains(currentSubcategory)) {
                    finalRef = pumpRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status", "Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                    recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                itemCountChecker.add(nInventory.getName());
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
                                                    itemCountChecker.clear();
                                                }
                                            });

                                            Query queryy = finalRef.whereEqualTo("status", "Enable");

                                            FirestoreRecyclerOptions<nInventory> optionss = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                    .setQuery(queryy, nInventory.class)
                                                    .build();

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
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
//                                                        itemCountChecker.add(nInventory.getName());
                                                    }
                                                }
                                                Query query = searchFilter.whereEqualTo("status","Enable");

                                                FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                                                        .setQuery(query, nInventory.class)
                                                        .build();

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, purchasedRecyclerAdapter.this);
                                                recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                                nInventoryRecyclerAdapter.startListening();

                                            }
                                        });
                                    }

                                });

                            }

                        }

                    });
                }
            }
        });

        //Setting up custom dialog layout
        currentInventoryAlertDialog = new AlertDialog.Builder(context)
                .setView(v)
                .create();
        currentInventoryAlertDialog.show();

    }
    //adding item to purchase recycler
    @Override
    public void editInventoryItem(final DocumentSnapshot documentSnapshot) {

        currentInventoryAlertDialog.dismiss();

        final nInventory nInventory = documentSnapshot.toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.custom_add_purchase, null);
        final TextView purchaseNameTextView = v.findViewById(R.id.purchaseNameTextView);
        final TextView purchasePriceTextView = v.findViewById(R.id.purchasePriceTextView);
        final EditText purchaseQuantityEditText = v.findViewById(R.id.purchaseQuantityEditText);
        final Button btnAddPurchase = v.findViewById(R.id.btnAddPurchase);
        final ImageView imageViewPurchase = v.findViewById(R.id.imageViewPurchase);
        Glide.with(context)
                .asBitmap()
                .load(nInventory.getDownloadURL())
                .into(imageViewPurchase);
        purchaseNameTextView.setText(nInventory.getName());
        purchasePriceTextView.setText(String.valueOf(nInventory.getPrice()));
        final TextView remQuantity = v.findViewById(R.id.remQuantity);
        remQuantity.setText("( " + String.valueOf(nInventory.getQuantity()) +" left)");

        //Setting up custom dialog layout
        updateAlertDialog = new AlertDialog.Builder(context)
                .setView(v)
                .create();
        updateAlertDialog.show();

        //adder
        btnAddPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (purchaseQuantityEditText.getText().toString().isEmpty()){
                    purchaseQuantityEditText.setError("Enter quantity first");
                }else{
                    if (Integer.parseInt(purchaseQuantityEditText.getText().toString()) <= nInventory.getQuantity()) {
                        nInventory nInventory1 = nInventory;
                        nInventory1.setQuantity(Integer.parseInt(purchaseQuantityEditText.getText().toString()));
                        double newPrice = nInventory.getPrice() * nInventory1.getQuantity();
                        nInventory1.setPrice(newPrice);
                        FirebaseFirestore.getInstance().collection("Purchased").document(nInventory1.getName()).set(nInventory1);
                        updateAlertDialog.dismiss();
                    }
                    else{
                        purchaseQuantityEditText.setError("Insufficient Quantity");
                    }
                }

            }
        });
    }


    @Override
    public void disableInventoryItem(final DocumentSnapshot documentSnapshot) {

    }
}