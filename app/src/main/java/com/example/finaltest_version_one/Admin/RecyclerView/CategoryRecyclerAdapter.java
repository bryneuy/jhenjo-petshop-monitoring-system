package com.example.finaltest_version_one.Admin.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import com.google.common.collect.Collections2;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder> implements SubcategoryRecyclerAdapter.SubcategoryListener, nInventoryRecyclerAdapter.nInventoryListiner, customInventoryRecyclerAdapter.customInventoryListener {

    //Initialization for CategoryRecyclerAdapter
    private static final String TAG = "CategoryRecyclerAdapter";
    private ArrayList<String> categories;
    private ArrayList<String> subcategory = new ArrayList<>();
    private Context context;

    //Initialization for nInventoryRecyclerAdapter
    private RecyclerView recyclerView;

    //Initialization for both nInventory & Category RecyclerAdapter
    private CircleImageView circleImageView;

    //FireStore Collection Reference
    private CollectionReference finalRef = null;
    private CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("nInventory");
    private CollectionReference QRcodeReference = FirebaseFirestore.getInstance().collection("QRcode");
    private CollectionReference itemNamesReference = FirebaseFirestore.getInstance().collection("itemNames");
    private CollectionReference inventoryReference = FirebaseFirestore.getInstance().collection("nInventory");

    //FireStore Document Reference
    private DocumentReference documentReference = null;
    private DocumentReference accessoriesRef = collectionReference.document("Accessories");
    private DocumentReference animalRef = collectionReference.document("Animal");
    private DocumentReference aquariumRef = collectionReference.document("Aquarium");
    private DocumentReference cageRef = collectionReference.document("Cage");
    private DocumentReference foodRef = collectionReference.document("Food");
    private DocumentReference pumpRef = collectionReference.document("Pump");

    //Container for each category
    //For fetching category data from FireStore
    private ArrayList<String> accessories = new ArrayList<>();
    private ArrayList<String> animal = new ArrayList<>();
    private ArrayList<String> aquarium = new ArrayList<>();
    private ArrayList<String> cage = new ArrayList<>();
    private ArrayList<String> food = new ArrayList<>();
    private ArrayList<String> pump = new ArrayList<>();

    //AlertDialog for nInventoryRecyclerAdapter
    private AlertDialog currentInventoryAlertDialog;
    private AlertDialog updateAlertDialog;

    //Selection for Spinners (Category & Status)
    private static final String[] spStatus = new String[] {"Enable", "Disable"};
    private static final String[] autoCategories = new String [] {"Accessories", "Animal", "Aquarium", "Cage", "Food", "Pump"};

    //for fetching what selectionItem from the spinners are clicked by the user
    //Container
    private String clickedCategory = null;
    private String clickedStatus = null;

    //for checking of already existing variables
    //for filtering and restrictions
    private ArrayList<String> checkedSubcategory = new ArrayList<>();

    //search filter
    private ArrayList<nInventory> customInventory = new ArrayList<>();
    private CollectionReference searchFilter = FirebaseFirestore.getInstance().collection("searchFilter");
    //loading Purpose
    private ArrayList<String> itemCountChecker = new ArrayList<>();



    public CategoryRecyclerAdapter(ArrayList<String> categories, Context context) {
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

                SubcategoryRecyclerAdapter subcategoryRecyclerAdapter = new SubcategoryRecyclerAdapter(subcategory, CategoryRecyclerAdapter.this);

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

//                holder.search.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        filter(s.toString());
//
//                    }
//
//                    public void filter(String text) {
//                        //new array list that will hold the filtered data
//                        ArrayList<String> filteredNames = new ArrayList<>();
//                        filteredNames.clear();
//
//                        //looping through existing subcategories
//                        for (String s: subcategory) {
//                            //if the existing subcategories contain the search input
//                            if (s.toLowerCase().contains(text.toLowerCase())){
//                                filteredNames.add(s);
//                            }
//                        }
//
//                        //calling a method of the adapter class and passing the filtered list
//                        subcategoryRecyclerAdapter.filterlist(filteredNames);
//
//                    }
//                });
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

    @Override
    public void editInventoryItem(final nInventory nInventory) {
        final DocumentReference finalRef = FirebaseFirestore.getInstance().collection("nInventory").document(nInventory.getCategory()).collection(nInventory.getSubcategory().toUpperCase())
                .document(nInventory.getName().toUpperCase());

        final ArrayList<String> existingQRcode = new ArrayList<>();
        final ArrayList<String> existingItemName = new ArrayList<>();


        //Temporarily closing the AlertDialog for currentInventory
        currentInventoryAlertDialog.dismiss();

        //Getting all existing QR codes
        QRcodeReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null){

                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();

                    //Adding each document's name one at a time inside the container "existingQRcode" (which is technically an ArrayList)
                    for (DocumentChange documentChange: documentChangeList) {
                        existingQRcode.add(documentChange.getDocument().getReference().getId());
                    }
                    existingQRcode.remove(nInventory.getQRcode());

                    //for debugging purpose
                    Log.d(TAG, "existing QR code: " + existingQRcode);

                    //Currently the QR code collection is empty, thus code underneath will commence
                } else {
                    //for debugging purpose
                    Log.d(TAG, "onCheckingExistingQRcode: QRcode collection is currently empty");
                }
            }
            //End of checking all existing QR codes
        });

        //Getting all names of inventory items
        itemNamesReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null){

                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();

                    //Adding each document's name one at a time inside the container "existingItemName" (which is technically an ArrayList)
                    for (DocumentChange documentChange: documentChangeList) {
                        existingItemName.add(documentChange.getDocument().getReference().getId());
                    }
                    existingItemName.remove(nInventory.getName().toUpperCase());

                    //for debugging purpose
                    Log.d(TAG, "existing itemNames " + existingItemName);


                    //Currently the itemNames collection is empty, thus code underneath will commence
                } else {
                    //for debugging purose
                    Log.d(TAG, "onCheckingExistingItemNames: itemNames collection is currently empty");
                }
            }
        });


//--------------------------------------------------------------------------------------------------------------------------------------------

        //Setting up layout for Updating of Inventory Item
        //AlertDialog Layout
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.custom_edit_ninventory,null);

        //Hooks for custom layout for Inventory Item creation
        final EditText mQRcode = v.findViewById(R.id.enQRcode);
        final TextView mCategory = v.findViewById(R.id.espCategory);
        final Spinner mStatus = v.findViewById(R.id.espStatus);
        final TextView mSubcategory = v.findViewById(R.id.eaSubcategory);
        final EditText mName = v.findViewById(R.id.enName);
        final EditText mQuantity = v.findViewById(R.id.enQuantity);
        final EditText mCapital = v.findViewById(R.id.enCapital);
        final EditText mPrice = v.findViewById(R.id.enPrice);
        final EditText mCriticalValue = v.findViewById(R.id.enCriticalValue);


        //Setting current value for the selected inventory item to be updated
        mQRcode.setText(nInventory.getQRcode());
        mName.setText(nInventory.getName());
        mQuantity.setText(String.valueOf(nInventory.getQuantity()));
        mCapital.setText(String.valueOf(nInventory.getCapital()));
        mPrice.setText(String.valueOf(nInventory.getPrice()));
        mCriticalValue.setText(String.valueOf(nInventory.getCriticalValue()));
        mSubcategory.setText(nInventory.getSubcategory());
        mCategory.setText(nInventory.getCategory());

        //Setting cursor at the end of the edit text
        mQRcode.setSelection(mQRcode.getText().length());
        mName.setSelection(mName.getText().length());
        mQuantity.setSelection(mQuantity.getText().length());
        mCapital.setSelection(mCapital.getText().length());
        mPrice.setSelection(mPrice.getText().length());
        mCriticalValue.setSelection(mCriticalValue.getText().length());


        //fetching current data of the selected inventory item to be updated

        final String currentQuantity = String.valueOf(nInventory.getQuantity());
        final String currentCapital = String.valueOf(nInventory.getCapital());
        final String currentPrice = String.valueOf(nInventory.getPrice());
        final String currentCriticalValue = String.valueOf(nInventory.getCriticalValue());

        //data that are sensitive and part of complicated structure of the FireStore database
        final String currentQRcode = nInventory.getQRcode();
        final String currentName = nInventory.getName();


        //Buttons
        final Button update = v.findViewById(R.id.enUpdate);
        final Button cancel = v.findViewById(R.id.enCancel);


        //Status Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1, spStatus);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStatus.setAdapter(statusAdapter);

        //selecting current status of the inventory item to be updated
        final String currentStatus = nInventory.getStatus();
        final int currentStatusPosition = statusAdapter.getPosition(currentStatus);
        mStatus.setSelection(currentStatusPosition);

        //Fetching clicked status by the user
        mStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clickedStatus = parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE); /* if you want your item to be white */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //CircleImageView
        circleImageView = v.findViewById(R.id.enCircleImageView);

        //Setting up the current designated image based from what downloadURL will be fetched in FireStore
        Glide.with(context)
                .asBitmap()
                .load(nInventory.getDownloadURL())
                .into(circleImageView);


        //Setting up custom dialog layout
        updateAlertDialog = new AlertDialog.Builder(context)
                .setView(v)
                .create();
        updateAlertDialog.show();


        //ONCE UPDATE BUTTON IS CLICKED BY THE USER
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //Ensuring that the user won't be able to update the inventory item -->
                //--> as long as at least one user input is null for string variables
                if (TextUtils.isEmpty(mSubcategory.getText().toString())) {
                    mSubcategory.setError("Assign subcategory for this inventory item.");
                    return;
                }

                if (TextUtils.isEmpty(mQRcode.getText().toString())) {
                    mQRcode.setError("Please assign QR code value.");
                    return;
                }

                if (TextUtils.isEmpty(mName.getText().toString())) {
                    mName.setError("Insert name for this inventory item.");
                    return;
                }


                //Fetching data input from the user
                final String QRcode = mQRcode.getText().toString();
                final String status = clickedStatus;
                final String name = mName.getText().toString();

                //Making sure that fetched number data are with values prior (NOT NULL) to proceeding --->
                //--> with initializing number data types variables
                if (TextUtils.isEmpty(mQuantity.getText().toString())) {
                    mQuantity.setError("Insert currently available quantity for " + name);
                    return;
                }
                if (TextUtils.isEmpty(mCapital.getText().toString())) {
                    mCapital.setError("Insert capital cost for " + name);
                    return;
                }
                if (TextUtils.isEmpty(mPrice.getText().toString())){
                    mPrice.setError("Insert retail price for " + name);
                    return;
                }
                if (TextUtils.isEmpty(mCriticalValue.getText().toString())) {
                    mCriticalValue.setError("Insert critical quantity for " + name);
                    return;
                }
                //END OF ENSURING NUMBER DATA editText ARE NOT NULL ------------------------------------

                //Continuation of fetching data input from the user
                final int quantity = Integer.parseInt(mQuantity.getText().toString());
                final double capital = Double.parseDouble(mCapital.getText().toString());
                final double price = Double.parseDouble(mPrice.getText().toString());
                final int criticalValue = Integer.parseInt(mCriticalValue.getText().toString());
                final String state;

                if (capital > price){
                    mCapital.setError("Capital cost should be less than or equal the retail price");
                    return;
                }

                if (quantity <= criticalValue){
                    state = "Critical";
                }else{
                    state = "Sufficient";
                }

                //FILTERING PROCESS---------------------------------------------------------------------

                //If QR code input by the user already exist, code underneath will commence
                if (existingQRcode.contains(QRcode)){
                    mQRcode.setError("QR code already exist. Create a new one.");
                    return;
                }
                //If inventory item name input by the user already exist, code underneath will commence
                if (existingItemName.contains(name.toUpperCase())) {
                    mName.setError("Inventory item name already exist. Create a new one.");
                    return;
                }

                if (existingItemName.contains(name.toUpperCase()) && existingQRcode.contains(QRcode)) {

                    //Just to make sure that the user won't be able to add inventory item in FireStore database if some of -->
                    //--> its detail already exist
                    updateAlertDialog.cancel();
                    Toast.makeText(context, "Either QR code or inventory item name already exist. Inventory item creation cancelled.", Toast.LENGTH_SHORT).show();

                    //END OF FILTERING PROCESS-------------------------------------------------------------

                    //If the data input by the user pass the filter process as viewed above, code underneath will commence
                } else {



                    //If all data are still the same for the selected inventory item, no need for update
                    if (currentName.equals(name) && currentQRcode.equals(QRcode) && currentQuantity.equals(String.valueOf(quantity))
                            && currentCapital.equals(String.valueOf(capital)) && currentPrice.equals(String.valueOf(price))
                            && currentCriticalValue.equals(String.valueOf(criticalValue))) {

                        Toast.makeText(context, "All inventory item details are still the same. No need for update.", Toast.LENGTH_SHORT).show();
                        updateAlertDialog.dismiss();

                    }else {
                        //Determining whether the name is still the same with the former (original) name of the inventory
                        if (currentName.equals(name)){
                            //for debugging purpose
                            Log.d(TAG, "StatusOfInventoryName: same");
                        }
                        //If the inventory name has also been changed then the former name should be deleted from the -->
                        //--> itemNames container since it is already non-existent
                        else{
                            //for debugging purpose
                            Log.d(TAG, "StatusOfInventoryName: changed");

                            //deletion of the document of the former inventory item name
                            itemNamesReference.document(currentName.toUpperCase()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //for debugging purpose
                                        Log.d(TAG, "DeletionOfFormerInventoryName: success");

                                        //Adding the change name (which is unique since it has pass the filtration process) -->
                                        //--> to the itemNames container (technically, a collection of all inventory items in the form of documents)
                                        Map<String, Object> changeName = new HashMap<>();
                                        changeName.put("name",name);

                                        itemNamesReference.document(name.toUpperCase()).set(changeName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    //for debugging purpose
                                                    Log.d(TAG, "onAddingChangeInventoryItemName: success");
                                                }else{
                                                    //for debugging purpose
                                                    Log.d(TAG, "onAddingChangeInventoryItemName: failed");
                                                }
                                            }
                                        });

                                    }else {
                                        //for debugging purpose
                                        Log.d(TAG, "DeletionOfFormerInventoryName: failed");
                                    }
                                }
                            });
                        }

                        //Determining whether the QR code is still the same with the former (original) QR code of the inventory
                        if (currentQRcode.equals(QRcode)){
                            //for debugging purpose
                            Log.d(TAG, "StatusOfInventoryQRcode: same");

                        }
                        //If the QR code has also been changed then the former QR code should be deleted from the -->
                        //--> QRcode container since it is already non-existent
                        else{
                            //for debugging purpose
                            Log.d(TAG, "StatusOfInventoryQRcode: changed");

                            //deletion of the document of the former QR code
                            QRcodeReference.document(currentQRcode).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //for debugging purpose
                                        Log.d(TAG, "DeletionOfFormerQRcode: success");

                                        //Adding the change QR code (which is unique since it has pass the filtration process) -->
                                        //--> to the QRcode container (technically, a collection of all QR codes in the form of documents)
                                        Map<String, Object> changeQRcode = new HashMap<>();
                                        changeQRcode.put("QRcode",QRcode);

                                        QRcodeReference.document(QRcode).set(changeQRcode).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    //for debugging purpose
                                                    Log.d(TAG, "onAddingChangeQRcode: success");

                                                }else{
                                                    //for debugging purpose
                                                    Log.d(TAG, "onAddingChangeQRcode: failed");
                                                }
                                            }
                                        });

                                    }else{
                                        //for debugging purpose
                                        Log.d(TAG, "DeletionOfFormerQRcode: failed");
                                    }
                                }
                            });
                        }

                        //Determining whether state for such inventory is critical or sufficient
                        //I'll later on use this for querying all inventory items with critical state

                        nInventory.setName(name);
                        nInventory.setStatus(status);
                        nInventory.setQRcode(QRcode);
                        nInventory.setQuantity(quantity);
                        nInventory.setPrice(price);
                        nInventory.setCriticalValue(criticalValue);
                        nInventory.setCapital(capital);
                        nInventory.setState(state);

                        finalRef.set(nInventory).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    //for debugging purpose
                                    Log.d(TAG, "onUpdatingSelectedItem: success");
                                    Toast.makeText(context, nInventory.getName() + " successfully updated.", Toast.LENGTH_SHORT).show();
                                }else{
                                    //for debugging purpose
                                    Log.d(TAG, "onUpdatingSelectedItem: failed");
                                }
                            }
                        });

                        //Entire update process ended, the custom layout will now be dismissed.
                        updateAlertDialog.dismiss();
                    }
                }

                currentInventoryAlertDialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateAlertDialog.cancel();
                currentInventoryAlertDialog.show();

            }
        });

    }

    @Override
    public void disableInventoryItem(nInventory customInventory) {

    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        EditText search;
        RecyclerView mRecyclerView;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            search = itemView.findViewById(R.id.nSearch);
            mRecyclerView = itemView.findViewById(R.id.mRecyclerView);




        }

    }

    @Override
    public void viewSubcategory(final String currentSubcategory) {

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
//        itemCountChecker.clear();
        customInventory.clear();
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.custom_admin_current_inventory,null);

        final EditText search = v.findViewById(R.id.caSearch);
        final ImageButton back = v.findViewById(R.id.caBack);
//        final ProgressBar loading = v.findViewById(R.id.loading);
        recyclerView = v.findViewById(R.id.currentInventoryRecyclerView);

//        loading.setVisibility(View.GONE);

//        //Divider (for border outline of each view holder)
//        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        //Item Touch Helper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //For debugging purpose
        Log.d(TAG, "viewSubcategory: " + currentSubcategory);

        accessoriesRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                accessories = (ArrayList<String>) snapshot.get("subcategory");
                Log.d(TAG, "onFetching Docs:" + accessories);
                if (accessories.contains(currentSubcategory)){
                    finalRef = accessoriesRef.collection(currentSubcategory.toUpperCase());
                    Query query = finalRef.whereEqualTo("status","Enable");
//                    if (search.getText().toString().isEmpty()){
                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();
//
                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, CategoryRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
                                                recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                                nInventoryRecyclerAdapter.startListening();

                                            }
                                        });
                                    }

                                });

                            }

                        }

                    });

/*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange : documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
                                customInventory.add(nInventory);
                            }
                            customInventoryRecyclerAdapter customInventoryRecyclerAdapter = new customInventoryRecyclerAdapter(customInventory, CategoryRecyclerAdapter.this);
                            recyclerView.setAdapter(customInventoryRecyclerAdapter);

                            search.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    customInventoryRecyclerAdapter.getFilter().filter(s);
                        }
                    });
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
//                    } else{

//                            if (search.getText().toString().isEmpty()){
//                                customInventory.clear();
//                                FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
//                                        .setQuery(query, nInventory.class)
//                                        .build();
//
//                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
//                                recyclerView.setAdapter(nInventoryRecyclerAdapter);
//                                nInventoryRecyclerAdapter.startListening();
//                                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                                        List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
//                                        for (DocumentChange documentChange : documentChangeList) {
//                                            final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
//                                            customInventory.add(nInventory);
//                                        }
//                                    }
//
//                                });
//                            }else{
//                                customInventoryRecyclerAdapter customInventoryRecyclerAdapter = new customInventoryRecyclerAdapter(customInventory);
//                                recyclerView.setAdapter(customInventoryRecyclerAdapter);

//                            }
//                        }
//                    });
                }
            }
        });

        animalRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                animal = (ArrayList<String>) snapshot.get("subcategory");
                Log.d(TAG, "onFetching Docs:" + animal);
                if (animal.contains(currentSubcategory)){
                    finalRef = animalRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status","Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, CategoryRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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
                if (aquarium.contains(currentSubcategory)){
                    finalRef = aquariumRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status","Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, CategoryRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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
                if (cage.contains(currentSubcategory)){
                    finalRef = cageRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status","Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, CategoryRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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
                if (food.contains(currentSubcategory)){
                    finalRef = foodRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status","Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, CategoryRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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
                if (pump.contains(currentSubcategory)){
                    finalRef = pumpRef.collection(currentSubcategory.toUpperCase());

                    Query query = finalRef.whereEqualTo("status","Enable");

                    FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                            .setQuery(query, nInventory.class)
                            .build();

                    nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(optionss, CategoryRecyclerAdapter.this);
                                            recyclerView.setAdapter(nInventoryRecyclerAdapter);
                                            nInventoryRecyclerAdapter.startListening();
                                        }else{

                                            nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

                                                nInventoryRecyclerAdapter nInventoryRecyclerAdapter = new nInventoryRecyclerAdapter(options, CategoryRecyclerAdapter.this);
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInventoryAlertDialog.cancel();
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
            }
        });

        currentInventoryAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    currentInventoryAlertDialog.cancel();
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
                }
                return true;
            }
        });

//        if (itemCountChecker.isEmpty()){
//            loading.setVisibility(View.VISIBLE);
//        }else{
//            loading.setVisibility(View.GONE);
//        }
    }

    //Setting up swipe action for the currently selected inventory item
    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            if (direction == ItemTouchHelper.LEFT) {

                nInventoryRecyclerAdapter.nInventoryViewHolder nInventoryViewHolder = (nInventoryRecyclerAdapter.nInventoryViewHolder) viewHolder;
                nInventoryViewHolder.disableItem();

            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(context,R.color.disable))
                    .addSwipeLeftActionIcon(R.drawable.disable)
                    .addSwipeLeftLabel("Disable")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(context,R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    //Responsible for updating the currently selected inventory item
    @Override
    public void editInventoryItem(final DocumentSnapshot documentSnapshot) {

        final ArrayList<String> existingQRcode = new ArrayList<>();
        final ArrayList<String> existingItemName = new ArrayList<>();

        //Fetching document snapshot of the currently clicked inventory item by the user
        final nInventory nInventory = documentSnapshot.toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

        //Temporarily closing the AlertDialog for currentInventory
        currentInventoryAlertDialog.dismiss();

        //Getting all existing QR codes
        QRcodeReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null){

                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();

                    //Adding each document's name one at a time inside the container "existingQRcode" (which is technically an ArrayList)
                    for (DocumentChange documentChange: documentChangeList) {
                        existingQRcode.add(documentChange.getDocument().getReference().getId());
                    }
                    existingQRcode.remove(nInventory.getQRcode());

                    //for debugging purpose
                    Log.d(TAG, "existing QR code: " + existingQRcode);

                    //Currently the QR code collection is empty, thus code underneath will commence
                } else {
                    //for debugging purpose
                    Log.d(TAG, "onCheckingExistingQRcode: QRcode collection is currently empty");
                }
            }
            //End of checking all existing QR codes
        });

        //Getting all names of inventory items
        itemNamesReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null){

                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();

                    //Adding each document's name one at a time inside the container "existingItemName" (which is technically an ArrayList)
                    for (DocumentChange documentChange: documentChangeList) {
                        existingItemName.add(documentChange.getDocument().getReference().getId());
                    }
                    existingItemName.remove(nInventory.getName().toUpperCase());

                    //for debugging purpose
                    Log.d(TAG, "existing itemNames " + existingItemName);


                    //Currently the itemNames collection is empty, thus code underneath will commence
                } else {
                    //for debugging purose
                    Log.d(TAG, "onCheckingExistingItemNames: itemNames collection is currently empty");
                }
            }
        });


//--------------------------------------------------------------------------------------------------------------------------------------------

        //Setting up layout for Updating of Inventory Item
        //AlertDialog Layout
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.custom_edit_ninventory,null);

        //Hooks for custom layout for Inventory Item creation
        final EditText mQRcode = v.findViewById(R.id.enQRcode);
        final TextView mCategory = v.findViewById(R.id.espCategory);
        final Spinner mStatus = v.findViewById(R.id.espStatus);
        final TextView mSubcategory = v.findViewById(R.id.eaSubcategory);
        final EditText mName = v.findViewById(R.id.enName);
        final EditText mQuantity = v.findViewById(R.id.enQuantity);
        final EditText mCapital = v.findViewById(R.id.enCapital);
        final EditText mPrice = v.findViewById(R.id.enPrice);
        final EditText mCriticalValue = v.findViewById(R.id.enCriticalValue);


        //Setting current value for the selected inventory item to be updated
        mQRcode.setText(nInventory.getQRcode());
        mName.setText(nInventory.getName());
        mQuantity.setText(String.valueOf(nInventory.getQuantity()));
        mCapital.setText(String.valueOf(nInventory.getCapital()));
        mPrice.setText(String.valueOf(nInventory.getPrice()));
        mCriticalValue.setText(String.valueOf(nInventory.getCriticalValue()));
        mSubcategory.setText(nInventory.getSubcategory());
        mCategory.setText(nInventory.getCategory());

        //Setting cursor at the end of the edit text
         mQRcode.setSelection(mQRcode.getText().length());
         mName.setSelection(mName.getText().length());
         mQuantity.setSelection(mQuantity.getText().length());
         mCapital.setSelection(mCapital.getText().length());
         mPrice.setSelection(mPrice.getText().length());
         mCriticalValue.setSelection(mCriticalValue.getText().length());


        //fetching current data of the selected inventory item to be updated

        final String currentQuantity = String.valueOf(nInventory.getQuantity());
        final String currentCapital = String.valueOf(nInventory.getCapital());
        final String currentPrice = String.valueOf(nInventory.getPrice());
        final String currentCriticalValue = String.valueOf(nInventory.getCriticalValue());

        //for redirecting to original documentReference *added June 5, 2020*
        final String currentCategory = nInventory.getCategory();
        final String currentSubcategory = nInventory.getSubcategory().toUpperCase();



        //data that are sensitive and part of complicated structure of the FireStore database
        final String currentQRcode = nInventory.getQRcode();
        final String currentName = nInventory.getName();


        //Buttons
        final Button update = v.findViewById(R.id.enUpdate);
        final Button cancel = v.findViewById(R.id.enCancel);


        //Status Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1, spStatus);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStatus.setAdapter(statusAdapter);

        //selecting current status of the inventory item to be updated
        final String currentStatus = nInventory.getStatus();
        final int currentStatusPosition = statusAdapter.getPosition(currentStatus);
        mStatus.setSelection(currentStatusPosition);

        //Fetching clicked status by the user
        mStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clickedStatus = parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE); /* if you want your item to be white */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //CircleImageView
        circleImageView = v.findViewById(R.id.enCircleImageView);

        //Setting up the current designated image based from what downloadURL will be fetched in FireStore
        Glide.with(context)
                .asBitmap()
                .load(nInventory.getDownloadURL())
                .into(circleImageView);


        //Setting up custom dialog layout
        updateAlertDialog = new AlertDialog.Builder(context)
                .setView(v)
                .create();
        updateAlertDialog.show();


        //ONCE UPDATE BUTTON IS CLICKED BY THE USER
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //Ensuring that the user won't be able to update the inventory item -->
                //--> as long as at least one user input is null for string variables
                if (TextUtils.isEmpty(mSubcategory.getText().toString())) {
                    mSubcategory.setError("Assign subcategory for this inventory item.");
                    return;
                }

                if (TextUtils.isEmpty(mQRcode.getText().toString())) {
                    mQRcode.setError("Please assign QR code value.");
                    return;
                }

                if (TextUtils.isEmpty(mName.getText().toString())) {
                    mName.setError("Insert name for this inventory item.");
                    return;
                }


                //Fetching data input from the user
                final String QRcode = mQRcode.getText().toString();
                final String status = clickedStatus;
                final String name = mName.getText().toString();

                //Making sure that fetched number data are with values prior (NOT NULL) to proceeding --->
                //--> with initializing number data types variables
                if (TextUtils.isEmpty(mQuantity.getText().toString())) {
                    mQuantity.setError("Insert currently available quantity for " + name);
                    return;
                }
                if (TextUtils.isEmpty(mCapital.getText().toString())) {
                    mCapital.setError("Insert capital cost for " + name);
                    return;
                }
                if (TextUtils.isEmpty(mPrice.getText().toString())){
                    mPrice.setError("Insert retail price for " + name);
                    return;
                }
                if (TextUtils.isEmpty(mCriticalValue.getText().toString())) {
                    mCriticalValue.setError("Insert critical quantity for " + name);
                    return;
                }
                //END OF ENSURING NUMBER DATA editText ARE NOT NULL ------------------------------------

                //Continuation of fetching data input from the user
                final int quantity = Integer.parseInt(mQuantity.getText().toString());
                final double capital = Double.parseDouble(mCapital.getText().toString());
                final double price = Double.parseDouble(mPrice.getText().toString());
                final int criticalValue = Integer.parseInt(mCriticalValue.getText().toString());
                final String state;

                if (capital > price){
                    mCapital.setError("Capital cost should be less than or equal the retail price");
                    return;
                }

                if (quantity <= criticalValue){
                    state = "Critical";
                }else{
                    state = "Sufficient";
                }

                //FILTERING PROCESS---------------------------------------------------------------------

                //If QR code input by the user already exist, code underneath will commence
                if (existingQRcode.contains(QRcode)){
                    mQRcode.setError("QR code already exist. Create a new one.");
                    return;
                }
                //If inventory item name input by the user already exist, code underneath will commence
                if (existingItemName.contains(name.toUpperCase())) {
                    mName.setError("Inventory item name already exist. Create a new one.");
                    return;
                }

                if (existingItemName.contains(name.toUpperCase()) && existingQRcode.contains(QRcode)) {

                    //Just to make sure that the user won't be able to add inventory item in FireStore database if some of -->
                    //--> its detail already exist
                    updateAlertDialog.cancel();
                    Toast.makeText(context, "Either QR code or inventory item name already exist. Inventory item creation cancelled.", Toast.LENGTH_SHORT).show();

                    //END OF FILTERING PROCESS-------------------------------------------------------------

                    //If the data input by the user pass the filter process as viewed above, code underneath will commence
                } else {



                    //If all data are still the same for the selected inventory item, no need for update
                    if (currentName.equals(name) && currentQRcode.equals(QRcode) && currentQuantity.equals(String.valueOf(quantity))
                            && currentCapital.equals(String.valueOf(capital)) && currentPrice.equals(String.valueOf(price))
                            && currentCriticalValue.equals(String.valueOf(criticalValue))) {

                        Toast.makeText(context, "All inventory item details are still the same. No need for update.", Toast.LENGTH_SHORT).show();
                        updateAlertDialog.dismiss();

                    }else {
                        //Determining whether the name is still the same with the former (original) name of the inventory
                        if (currentName.equals(name)){
                            //for debugging purpose
                            Log.d(TAG, "StatusOfInventoryName: same");
                        }
                        //If the inventory name has also been changed then the former name should be deleted from the -->
                        //--> itemNames container since it is already non-existent
                        else{
                            //for debugging purpose
                            Log.d(TAG, "StatusOfInventoryName: changed");

                            //deletion of the document of the former inventory item name
                            itemNamesReference.document(currentName.toUpperCase()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //for debugging purpose
                                        Log.d(TAG, "DeletionOfFormerInventoryName: success");

                                        //Adding the change name (which is unique since it has pass the filtration process) -->
                                        //--> to the itemNames container (technically, a collection of all inventory items in the form of documents)
                                        Map<String, Object> changeName = new HashMap<>();
                                        changeName.put("name",name);

                                        itemNamesReference.document(name.toUpperCase()).set(changeName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    //for debugging purpose
                                                    Log.d(TAG, "onAddingChangeInventoryItemName: success");
                                                }else{
                                                    //for debugging purpose
                                                    Log.d(TAG, "onAddingChangeInventoryItemName: failed");
                                                }
                                            }
                                        });

                                    }else {
                                        //for debugging purpose
                                        Log.d(TAG, "DeletionOfFormerInventoryName: failed");
                                    }
                                }
                            });
                        }

                        //Determining whether the QR code is still the same with the former (original) QR code of the inventory
                        if (currentQRcode.equals(QRcode)){
                            //for debugging purpose
                            Log.d(TAG, "StatusOfInventoryQRcode: same");

                        }
                        //If the QR code has also been changed then the former QR code should be deleted from the -->
                        //--> QRcode container since it is already non-existent
                        else{
                            //for debugging purpose
                            Log.d(TAG, "StatusOfInventoryQRcode: changed");

                            //deletion of the document of the former QR code
                            QRcodeReference.document(currentQRcode).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //for debugging purpose
                                        Log.d(TAG, "DeletionOfFormerQRcode: success");

                                        //Adding the change QR code (which is unique since it has pass the filtration process) -->
                                        //--> to the QRcode container (technically, a collection of all QR codes in the form of documents)
                                        Map<String, Object> changeQRcode = new HashMap<>();
                                        changeQRcode.put("QRcode",QRcode);

                                        QRcodeReference.document(QRcode).set(changeQRcode).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    //for debugging purpose
                                                    Log.d(TAG, "onAddingChangeQRcode: success");

                                                }else{
                                                    //for debugging purpose
                                                    Log.d(TAG, "onAddingChangeQRcode: failed");
                                                }
                                            }
                                        });

                                    }else{
                                        //for debugging purpose
                                        Log.d(TAG, "DeletionOfFormerQRcode: failed");
                                    }
                                }
                            });
                        }

                        //Determining whether state for such inventory is critical or sufficient
                        //I'll later on use this for querying all inventory items with critical state

                        nInventory.setName(name);
                        nInventory.setStatus(status);
                        nInventory.setQRcode(QRcode);
                        nInventory.setQuantity(quantity);
                        nInventory.setPrice(price);
                        nInventory.setCriticalValue(criticalValue);
                        nInventory.setCapital(capital);
                        nInventory.setState(state);

                        DocumentReference finalDocRef = documentSnapshot.getReference();

                        finalDocRef.set(nInventory).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    //for debugging purpose
                                    Log.d(TAG, "onUpdatingSelectedItem: success");
                                    Toast.makeText(context, nInventory.getName() + " successfully updated.", Toast.LENGTH_SHORT).show();

                                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("nInventory")
                                            .document(currentCategory).collection(currentSubcategory).document(currentName.toUpperCase());

                                    documentReference.set(nInventory).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                }else{
                                    //for debugging purpose
                                    Log.d(TAG, "onUpdatingSelectedItem: failed");
                                }
                            }
                        });

                        //Entire update process ended, the custom layout will now be dismissed.
                        updateAlertDialog.dismiss();
                    }
                }

                currentInventoryAlertDialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateAlertDialog.cancel();
                currentInventoryAlertDialog.show();

            }
        });






    }


    //Responsible for disabling status of the currently selected inventory item
    @Override
    public void disableInventoryItem(final DocumentSnapshot documentSnapshot) {

        final nInventory nInventory = documentSnapshot.toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

        // for redirecting purpose of searchFilter *added June 5, 2020*
        final String category = nInventory.getCategory();
        final String subcategory = nInventory.getSubcategory().toUpperCase();
        final String itemName = nInventory.getName().toUpperCase();
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("nInventory")
                .document(category).collection(subcategory).document(itemName);

        assert nInventory !=null;
        nInventory.setStatus("Disable");
        final DocumentReference targetRef = documentSnapshot.getReference();
        //redirecting for searchFilter *added June 5, 2020*
        documentReference.set(nInventory);
        targetRef.set(nInventory).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Placing the disabled item in a specific collection, so that it will be easy to fetch such data in a realtime manner
                    //DisabledItems Collection
                    CollectionReference disabledItemsRef = FirebaseFirestore.getInstance().collection("Disabled Items");
                    String disabledItemName = nInventory.getName().toUpperCase();
                    DocumentReference disabledItemDocRef = disabledItemsRef.document(disabledItemName);
                    disabledItemDocRef.set(nInventory).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "onAddingItemInDisabledItemsCollection: successful");
                                targetRef.delete();
                            }else{
                                Log.d(TAG, "onAddingItemInDisabledItemsCollection: failed");
                            }
                        }
                    });

                    //Undo disabling of status
                    Snackbar.make(recyclerView, nInventory.getName() + " disabled.", Snackbar.LENGTH_LONG )
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    targetRef.set(documentSnapshot.toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class));
                                    documentReference.update("status","Enable");
                                    //Deleting chosen disabled Item in the disabledItem Collection, so that it won't be part of the disableRecyclerView
                                    disabledItemDocRef.delete();
                                }
                            }).show();
                }
            }
        });

    }

}