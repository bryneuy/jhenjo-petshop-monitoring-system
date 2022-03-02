package com.example.finaltest_version_one.Admin.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.Admin.RecyclerView.CategoryRecyclerAdapter;
import com.example.finaltest_version_one.Admin.RecyclerView.CriticalRecyclerAdapter;
import com.example.finaltest_version_one.Admin.RecyclerView.DisableRecyclerAdapter;
import com.example.finaltest_version_one.Main.MainActivity;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.app.Activity.RESULT_OK;

public class Admin_nInventoryFragment extends Fragment implements DisableRecyclerAdapter.DisableListener {

    private static final int PICK_IMAGE_REQUEST = 3;
    private Uri ImageUri;
    private CircleImageView circleImageView;
    private static final String TAG = "Admin_nInventoryFragmen";
    private RecyclerView recyclerView;
    private RecyclerView disableRecyclerView;
    private RecyclerView critRecyclerView;
    private CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("nInventory");
    private CollectionReference QRcodeReference = FirebaseFirestore.getInstance().collection("QRcode");
    private CollectionReference itemNamesReference = FirebaseFirestore.getInstance().collection("itemNames");
    private CollectionReference dCollectionReference = FirebaseFirestore.getInstance().collection("defaultImage");
    private CollectionReference disabledItemsRef = FirebaseFirestore.getInstance().collection("Disabled Items");

    //FireStore Document Reference
    private DocumentReference accessoriesRef = collectionReference.document("Accessories");
    private DocumentReference animalRef = collectionReference.document("Animal");
    private DocumentReference aquariumRef = collectionReference.document("Aquarium");
    private DocumentReference cageRef = collectionReference.document("Cage");
    private DocumentReference foodRef = collectionReference.document("Food");
    private DocumentReference pumpRef = collectionReference.document("Pump");

    //Container for all critical level inventory items
    private ArrayList<nInventory> criticalItems = new ArrayList<>();
    private ArrayList<nInventory> disabledItems = new ArrayList<>();

    //Container for each category
    //For fetching category data from FireStore
    private ArrayList<String> accessories = new ArrayList<>();
    private ArrayList<String> animal = new ArrayList<>();
    private ArrayList<String> aquarium = new ArrayList<>();
    private ArrayList<String> cage = new ArrayList<>();
    private ArrayList<String> food = new ArrayList<>();
    private ArrayList<String> pump = new ArrayList<>();

    //Alert Dialog
    private AlertDialog disableAlertDialog;

    //Floating Action Bar
    private FloatingActionButton critical;
    private FloatingActionButton disable;

    private DocumentReference documentReference = null;
    private ArrayList<String> Categories = new ArrayList<>();
    private ArrayList<String> checkedSubcategory = new ArrayList<>();
    private ArrayList<String> existingQRcode = new ArrayList<>();
    private ArrayList<String> existingItemName = new ArrayList<>();
    private String clickedCategory = null;
    private String clickedStatus = null;
    private String state = null;
    private static final String[] spStatus = new String[]{"Enable", "Disable"};
    private static final String[] autoCategories = new String[]{"Accessories", "Animal", "Aquarium", "Cage", "Food", "Pump"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_n_inventory, container, false);
        recyclerView = v.findViewById(R.id.gRecyclerView);


//        //Getting all the names of Documents realtime
//        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (queryDocumentSnapshots != null){
//
//                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
//
//                    //Adding each document's name one at a time inside the container "Categories" (which is technically an ArrayList)
//                    for (DocumentChange documentChange: documentChangeList) {
//                        Categories.add(documentChange.getDocument().getReference().getId());
//                        Log.d(TAG, "Category Name: " + documentChange.getDocument().getReference().getId());
//                    }
//
//
//                    //Currently the inventory is empty, so we try'na proceed with this code underneath Dawg
//                }else {
//                    Toast.makeText(getActivity(), "Inventory List is currently empty.", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "onEvent: " + "Inventory List is currently empty.");
//                }
//            }
//        });


        //Assigning values to container named "Categories" which should be fixed
        Categories.add("Accessories");
        Categories.add("Animal");
        Categories.add("Aquarium");
        Categories.add("Cage");
        Categories.add("Food");
        Categories.add("Pump");

        //For Logging out, a dedicated "setting" button has been placed to every dashboards
        Button settings = v.findViewById(R.id.inventorySettings);
        FrameLayout fl = v.findViewById(R.id.inventory_fl_logout);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fl.isShown()) {
                    fl.setVisibility(View.GONE);
                } else {
                    fl.setVisibility(View.VISIBLE);
                }
            }
        });

        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Logging Out")
                        .setMessage("Are you sure you want to log out?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                //Signing out currently signed in account
                                FirebaseAuth.getInstance().signOut();

                                //After signing out, redirecting user to landing page
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                                Objects.requireNonNull(getActivity()).finish();
                            }
                        }).create().show();

                fl.setVisibility(View.GONE);

            }
        });

        //Setting adapter for parent recyclerView
        CategoryRecyclerAdapter categoryRecyclerAdapter = new CategoryRecyclerAdapter(Categories, getContext());
        recyclerView.setAdapter(categoryRecyclerAdapter);

        //Initialization for floating action bar
        final FloatingActionButton add = v.findViewById(R.id.nInventory_add);
        disable = v.findViewById(R.id.nInventory_disable);
        critical = v.findViewById(R.id.nInventory_critical);
        FrameLayout cd = v.findViewById(R.id.cd_buttons);

        //onLongClick of add button, it will be expanded

        add.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(cd.getVisibility() == View.VISIBLE) {
                    cd.setVisibility(View.GONE);
                } else {
                    cd.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        //To make floating action bar disappear when the recyclerView is being scrolled
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && add.isShown())
                    add.hide();
                    disable.hide();
                    critical.hide();

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    add.show();
                    disable.show();
                    critical.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //Once the the button assigned for adding inventory item is clicked, code inside the listener will commence
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //See method below for Reference
                showAlertDialog();
            }
        });


        //Once the button assigned for checking critical level inventory item is clicked
        critical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criticalAlertDialog();
            }
        });


        //Once the button assigned for checking disabled inventory item is clicked
        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAlertDialog();
            }
        });

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();

        accessoriesRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                accessories = (ArrayList<String>) snapshot.get("subcategory");
                Log.d(TAG, "ACCESSORIES onFetching Docs:" + accessories);

                for (int i = 0; i < accessories.size(); i++) {
                    final int position = i;
                    accessoriesRef.collection(accessories.get(i).toUpperCase()).whereEqualTo("state", "Critical").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "ACCESSORIES CRITICAL VALUE ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange : documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

                                criticalItems.add(nInventory);
                                Log.d(TAG, position + ") accessories critical item: " + criticalItems);

                            }
                        }
                    });
                    accessoriesRef.collection(accessories.get(i).toUpperCase()).whereEqualTo("status","Disable").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "ACCESSORIES DISABLED ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange: documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

                                disabledItems.add(nInventory);
                                Log.d(TAG, position + ") accessories disabled item: " + disabledItems);
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
                Log.d(TAG, "ANIMAL onFetching Docs:" + animal);

                for (int i = 0; i < animal.size(); i++) {
                    final int position = i;
                    animalRef.collection(animal.get(i).toUpperCase()).whereEqualTo("state", "Critical").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "ACCESSORIES CRITICAL VALUE ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange : documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

                                criticalItems.add(nInventory);
                                Log.d(TAG, position + ") animal critical item: " + criticalItems);

                            }
                        }
                    });
                    animalRef.collection(animal.get(i).toUpperCase()).whereEqualTo("status","Disable").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "ANIMAL DISABLED ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange: documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

                                disabledItems.add(nInventory);
                                Log.d(TAG, position + ") animal disabled item: " + disabledItems);
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
                Log.d(TAG, "AQUARIUM onFetching Docs:" + aquarium);

                for (int i = 0; i < aquarium.size(); i++) {
                    final int position = i;
                    aquariumRef.collection(aquarium.get(i).toUpperCase()).whereEqualTo("state", "Critical").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "ACCESSORIES CRITICAL VALUE ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange : documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

                                criticalItems.add(nInventory);
                                Log.d(TAG, position + ") aquarium critical item: " + criticalItems);

                            }
                        }
                    });
                    aquariumRef.collection(aquarium.get(i).toUpperCase()).whereEqualTo("status","Disable").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "AQUARIUM DISABLED ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange: documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

                                disabledItems.add(nInventory);
                                Log.d(TAG, position + ") aquarium disabled item: " + disabledItems);
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
                Log.d(TAG, "CAGE onFetching Docs:" + cage);

                for (int i = 0; i < cage.size(); i++) {
                    final int position = i;
                    cageRef.collection(cage.get(i).toUpperCase()).whereEqualTo("state", "Critical").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "CAGE CRITICAL VALUE ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange : documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

                                criticalItems.add(nInventory);
                                Log.d(TAG, position + ") cage critical item: " + criticalItems);

                            }
                        }
                    });
                    cageRef.collection(cage.get(i).toUpperCase()).whereEqualTo("status","Disable").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "CAGE DISABLED ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange: documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

                                disabledItems.add(nInventory);
                                Log.d(TAG, position + ") cage disabled item: " + disabledItems);
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
                Log.d(TAG, "FOOD onFetching Docs:" + food);

                for (int i = 0; i < food.size(); i++) {
                    final int position = i;
                    foodRef.collection(food.get(i).toUpperCase()).whereEqualTo("state", "Critical").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "FOOD CRITICAL VALUE ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange : documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

                                criticalItems.add(nInventory);
                                Log.d(TAG, position + ") food critical item: " + criticalItems);

                            }
                        }
                    });
                    foodRef.collection(food.get(i).toUpperCase()).whereEqualTo("status","Disable").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "FOOD DISABLED ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange: documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

                                disabledItems.add(nInventory);
                                Log.d(TAG, position + ") food disabled item: " + disabledItems);
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
                Log.d(TAG, "PUMP onFetching Docs:" + pump);

                for (int i = 0; i < pump.size(); i++) {
                    final int position = i;
                    pumpRef.collection(pump.get(i).toUpperCase()).whereEqualTo("state", "Critical").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "PUMP CRITICAL VALUE ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange : documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
                                criticalItems.add(nInventory);
                                Log.d(TAG, position + ") pump critical item: " + criticalItems);

                            }
                        }
                    });
                    pumpRef.collection(pump.get(i).toUpperCase()).whereEqualTo("status","Disable").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Log.d(TAG, "PUMP DISABLED ITEM");
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            for (DocumentChange documentChange: documentChangeList) {
                                final nInventory nInventory = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
                                disabledItems.add(nInventory);
                                Log.d(TAG, position + ") pump disabled item: " + disabledItems);
                            }
                        }
                    });

                }
            }
        });
    }

    private void criticalAlertDialog() {
        //Initializing view for the custom layout of viewing critical level inventory item
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.custom_admin_critical, null);

        //Initializing critical level RecyclerView
        critRecyclerView = v.findViewById(R.id.criticalRecyclerView);

//        //Divider (for border outline of each view holder)
//        critRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        //CRITICAL LEVEL INVENTORY ITEM--------------------------------------------------------------------
        //Setting adapter for critical level inventory items recyclerView
        final CriticalRecyclerAdapter criticalRecyclerAdapter = new CriticalRecyclerAdapter(criticalItems);
        critRecyclerView.setAdapter(criticalRecyclerAdapter);
        //CRITICAL LEVEL INVENTORY ITEM--------------------------------------------------------------------

        final FloatingActionButton close = v.findViewById(R.id.close);
        //Setting up custom dialog layout

        if (critRecyclerView.getAdapter().getItemCount() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Critical Level Inventory Item");
            builder.setMessage("There is currently no inventory item with critical level quantity");
            builder.setPositiveButton("Ok",null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }else{

            criticalItems.clear();
            onResume();
            final AlertDialog criticalAlertDialog = new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .create();
            criticalAlertDialog.show();
            criticalAlertDialog.setCancelable(false);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    criticalAlertDialog.dismiss();
                }
            });
        }

    }

    private void disableAlertDialog() {

        //Ensuring that checkDisabledItems is always empty first
        //Initializing view for the custom layout of viewing disabled inventory item
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_admin_disable, null);

        //DISABLED INVENTORY ITEM--------------------------------------------------------------------------
        //Initializing critical level RecyclerView
        disableRecyclerView = v.findViewById(R.id.disableRecyclerView);

        //Item Touch Helper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(disableRecyclerView);


        //Setting adapter for disabled inventory item recyclerVIew
        Query query = disabledItemsRef;

        FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                .setQuery(query, nInventory.class)
                .build();

        final DisableRecyclerAdapter disableRecyclerAdapter = new DisableRecyclerAdapter(options, this);
        disableRecyclerView.setAdapter(disableRecyclerAdapter);
        disableRecyclerAdapter.startListening();
        //DISABLED INVENTORY ITEM--------------------------------------------------------------------------

        final FloatingActionButton close = v.findViewById(R.id.dClose);
        //Setting up custom dialog layout

        disabledItemsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //If there is currently no disabled items
                if (task.getResult().getDocumentChanges().isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Disabled Inventory Item");
                    builder.setMessage("There is currently no disabled item");
                    builder.setPositiveButton("Ok",null);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                //If there is currently disabled item/s
                }else{
                    disableAlertDialog = new AlertDialog.Builder(getActivity())
                            .setView(v)
                            .create();
                    disableAlertDialog.show();
                    disableAlertDialog.setCancelable(false);

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            disableAlertDialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void deleteItem(DocumentSnapshot documentSnapshot) {
        final nInventory nInventory = documentSnapshot.toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

        final DocumentReference docRef = collectionReference.document(nInventory.getCategory()).collection(nInventory.getSubcategory().toUpperCase())
                .document(nInventory.getName().toUpperCase());

        docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //deleting the enabled item in the Disabled Items collection
                    CollectionReference disabledItemsRef = FirebaseFirestore.getInstance().collection("Disabled Items");
                    disabledItemsRef.document(nInventory.getName().toUpperCase()).delete();
                    //deleting QR code of the item from the QRcode collection, so that it will be usable again
                    QRcodeReference.document(nInventory.getQRcode()).delete();
                    //deleting unique name of the item from the itemNames collection, so that it will be usable again
                    itemNamesReference.document(nInventory.getName().toUpperCase()).delete();
                    //Undo deletion of selected inventory item
                    Snackbar.make(disableRecyclerView, documentSnapshot.toObject(nInventory.class).getName() + " deleted.", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    docRef.set(nInventory);
                                    //Adding, once again, the previously disabled item since the enabling has been undo
                                    disabledItemsRef.document(nInventory.getName().toUpperCase()).set(documentSnapshot.toObject(nInventory.class));
                                    //Undo deletion for both unique QR code and item name from the collection container
                                    //QR Code
                                    Map<String, Object> qrCode = new HashMap<>();
                                    qrCode.put("QRcode",nInventory.getQRcode());
                                    QRcodeReference.document(nInventory.getQRcode()).set(qrCode);
                                    //Item Name
                                    Map<String, Object> itemName = new HashMap<>();
                                    itemName.put("name", nInventory.getName());
                                    itemNamesReference.document(nInventory.getName().toUpperCase()).set(itemName);
                                }
                            }).show();

                    collectionReference.document(nInventory.getCategory()).collection(nInventory.getSubcategory().toUpperCase()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null){
                                Log.w(TAG, "Admin_nInventory - Deletion Check Listen failed.", e);
                                return;
                            }
                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                            if (documentChangeList.isEmpty()){
                                collectionReference.document(nInventory.getCategory()).update("subcategory",FieldValue.arrayRemove(nInventory.getSubcategory()));
                            }

                        }
                    });
                }
            }
        });
    }

    @Override
    public void enableItem(DocumentSnapshot documentSnapshot) {
        final nInventory nInventory = documentSnapshot.toObject(nInventory.class);

        assert nInventory != null;
        nInventory.setStatus("Enable");

        final DocumentReference docRef = collectionReference.document(nInventory.getCategory()).collection(nInventory.getSubcategory().toUpperCase())
                .document(nInventory.getName().toUpperCase());

        docRef.set(nInventory).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //deleting the enabled item in the Disabled Items collection
                    CollectionReference disabledItemsRef = FirebaseFirestore.getInstance().collection("Disabled Items");
                    disabledItemsRef.document(nInventory.getName().toUpperCase()).delete();
                    //Undo disabling of status
                    Snackbar.make(disableRecyclerView, nInventory.getName() + " enabled.", Snackbar.LENGTH_LONG )
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    docRef.set(documentSnapshot.toObject(nInventory.class));
                                    //Adding, once again, the previously disabled item since the enabling has been undo
                                    disabledItemsRef.document(nInventory.getName().toUpperCase()).set(documentSnapshot.toObject(nInventory.class));
                                }
                            }).show();
                }
            }
        });
    }

    //Setting up swipe action for the currently selected inventory item
    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            if (direction == ItemTouchHelper.LEFT) {

                DisableRecyclerAdapter.DisableViewHolder disableViewHolder = (DisableRecyclerAdapter.DisableViewHolder) viewHolder;
                disableViewHolder.deleteItem();

            }

            else if (direction == ItemTouchHelper.RIGHT) {

                DisableRecyclerAdapter.DisableViewHolder disableViewHolder = (DisableRecyclerAdapter.DisableViewHolder) viewHolder;
                disableViewHolder.enableItem();
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(),R.color.disable))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(),R.color.enable))
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .addSwipeRightActionIcon(R.drawable.enable)
                    .addSwipeLeftLabel("Delete")
                    .addSwipeRightLabel("Enable")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(getActivity(),R.color.white))
                    .setSwipeRightLabelColor(ContextCompat.getColor(getActivity(),R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void showAlertDialog() {

        //Getting all existing QR codes
        QRcodeReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {

                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();

                    //Adding each document's name one at a time inside the container "existingQRcode" (which is technically an ArrayList)
                    for (DocumentChange documentChange : documentChangeList) {
                        existingQRcode.add(documentChange.getDocument().getReference().getId());
                    }
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
                if (queryDocumentSnapshots != null) {

                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();

                    //Adding each document's name one at a time inside the container "existingItemName" (which is technically an ArrayList)
                    for (DocumentChange documentChange : documentChangeList) {
                        existingItemName.add(documentChange.getDocument().getReference().getId());
                    }
                    //for debugging purpose
                    Log.d(TAG, "existing itemNames " + existingItemName);

                    //Currently the itemNames collection is empty, thus code underneath will commence
                } else {
                    //for debugging purose
                    Log.d(TAG, "onCheckingExistingItemNames: itemNames collection is currently empty");
                }
            }
        });

        //Initializing view for the custom layout
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_add_ninventory, null);

        //Hooks for custom layout for Inventory Item creation
        final EditText mQRcode = v.findViewById(R.id.nQRcode);
        final Spinner mCategory = v.findViewById(R.id.spCategory);
        final Spinner mStatus = v.findViewById(R.id.spStatus);
        final AutoCompleteTextView mSubcategory = v.findViewById(R.id.aSubcategory);
        final EditText mName = v.findViewById(R.id.nName);
        final EditText mQuantity = v.findViewById(R.id.nQuantity);
        final EditText mCapital = v.findViewById(R.id.nCapital);
        final EditText mPrice = v.findViewById(R.id.nPrice);
        final EditText mCriticalValue = v.findViewById(R.id.nCriticalValue);


        //Buttons
        Button create = v.findViewById(R.id.nCreate);
        Button cancel = v.findViewById(R.id.nCancel);

        //Category Spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, autoCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(categoryAdapter);

        //Fetching clicked category by the user
        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clickedCategory = parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE); /* if you want your item to be white */

                //Setting ArrayAdapter for autocomplete text based from what category was clicked
                collectionReference.document(clickedCategory).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                        //NOTE: Array document field in FireStore is only limited to List<String> & ArrayList<String> as its data type
                        checkedSubcategory = (ArrayList<String>) snapshot.get("subcategory");
                        Log.d(TAG, "onCheckingSubcategoryOfClickedCategory: " + checkedSubcategory);


                        if (checkedSubcategory != null) {
                            //ArrayAdapter for autocomplete textView only accepts String Array
                            //Thus, the ArrayList fetched from the documentSnapshot should first be converted into Object Array
                            Object[] latestSubcategory = checkedSubcategory.toArray();

                            //After converting to Object Array, convert once more to String Array
                            //NOTE: ArrayList cannot be directly converted to String Array, thus, undertaking such thorough conversion of data type is a must
                            String[] superLatestSubCategory = Arrays.copyOf(latestSubcategory, latestSubcategory.length, String[].class);
                            //Setting arrayAdapter for autocomplete textView subcategory
                            ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, superLatestSubCategory);
                            mSubcategory.setAdapter(subcategoryAdapter);
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Status Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, spStatus);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStatus.setAdapter(statusAdapter);

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
        circleImageView = v.findViewById(R.id.nCircleImageView);

        //Ensuring that there's a default user Image in case no chosen image upon registration of account
        dCollectionReference.document("defaultImage").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String downloadURL = Objects.requireNonNull(task.getResult()).getString("URL");
                        Glide.with(Objects.requireNonNull(getActivity()))
                                .asBitmap()
                                .load(downloadURL)
                                .into(circleImageView);
                    }
                });

        //Once the circle image itself has been clicked, it will first ask for access permission of storage Device
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(Objects.requireNonNull(getActivity()), "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        //If permission granted, code beneath will commence. See the designated method below for reference
                        openFileChooser();
                    }
                }
            }
        });

        //Setting up custom dialog layout
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .create();
        alertDialog.show();


        //Upon clicking create, codes beneath will commence
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Ensuring that the user won't be able to create the inventory item -->
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
                final String category = clickedCategory;
                final String subcategory = mSubcategory.getText().toString();
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
                if (TextUtils.isEmpty(mPrice.getText().toString())) {
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

                if (capital > price) {
                    mCapital.setError("Capital cost should be less than or equal the retail price");
                    return;
                }

                //FILTERING PROCESS---------------------------------------------------------------------

                //If QR code input by the user already exist, code underneath will commence
                if (existingQRcode.contains(QRcode)) {
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
                    alertDialog.cancel();
                    Toast.makeText(getActivity(), "Either QR code or inventory item name already exist. Inventory item creation cancelled.", Toast.LENGTH_SHORT).show();

                    //END OF FILTERING PROCESS-------------------------------------------------------------

                    //If the data input by the user pass the filter process as viewed above, code underneath will commence
                } else {


                    //Determining whether state for such inventory is critical or sufficient
                    //I'll later on use this for querying all inventory items with critical state
                    if (quantity <= criticalValue) {
                        state = "Critical";
                    } else {
                        state = "Sufficient";
                    }

                    //Setting FireStore Database Path for the chosen Category
                    documentReference = collectionReference.document(category);

                    //Properly capitalizing first letter of subcategory
                    final String cSubcategory = subcategory.substring(0, 1).toUpperCase() + subcategory.substring(1);
                    //Adding field value for the Category's Subcategories' Arrays in the form of Strings
                    Map<String, Object> subCategory = new HashMap<>();
                    subCategory.put("subcategory", FieldValue.arrayUnion(cSubcategory));

                    //Updating the list of subcategory for the chosen category
                    documentReference.update(subCategory).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                //Fetching input of the user and setting it to nInventory custom class
                                final nInventory nInventory = new nInventory(QRcode, status, category, cSubcategory, name,
                                        quantity, capital, price, criticalValue, null, state);

                                //Assigning final path for the creation of new data
                                final DocumentReference latestDocRef = documentReference.collection(subcategory.toUpperCase()).document(name.toUpperCase());

                                //Creating new data for the documentReference
                                latestDocRef.set(nInventory).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(getActivity(), name + " successfully added", Toast.LENGTH_SHORT).show();
                                            //For debugging purposes
                                            Log.d(TAG, "InventoryItemAdding: Successful added " + name + ".");

                                            //Since the created data is unique and new, its QRcode and name will be added to -->
                                            //--> QRcode collection and itemName collection (serves as container for checking of existing QRcode and itemNames)

                                            //putting the QRcode of the added inventory item to the QRcode container
                                            Map<String, Object> newQRcode = new HashMap<>();
                                            newQRcode.put("QRcode", QRcode);

                                            //No need to set the documentID toUpperCase since QR code is case sensitive
                                            QRcodeReference.document(QRcode).set(newQRcode).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    //For debugging purpose
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "Adding to QRcode collection: successful");
                                                    } else {
                                                        Log.d(TAG, "Adding to QRcode collection: failed");
                                                    }

                                                }
                                            });

                                            //putting the name of the added inventory item to the itemName container
                                            Map<String, Object> itemName = new HashMap<>();
                                            itemName.put("name", name);

                                            //Needs to set documentID of inventory item toUpperCase since regardless of its casing, each name of inventory items -->
                                            //--> should be entirely unique in the context of its name
                                            itemNamesReference.document(name.toUpperCase()).set(itemName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    //For debugging purpose
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "Adding to itemName collection: Successful");
                                                    } else {
                                                        Log.d(TAG, "Adding to itemName collection: Failed ");
                                                    }
                                                }
                                            });

                                            //Uploading Selected Image to FireBase Storage
                                            FirebaseStorage storage = FirebaseStorage.getInstance();

                                            //Setting FireBaseStorage Path of the image to be uploaded
                                            final StorageReference uploadRef = storage.getReference().child("nInventory").child(category.toUpperCase())
                                                    .child(subcategory.toUpperCase()).child(name.toUpperCase());
                                            Bitmap sampleBitmap = ((BitmapDrawable) circleImageView.getDrawable()).getBitmap();
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            sampleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            UploadTask uploadTask;
                                            uploadTask = uploadRef.putBytes(baos.toByteArray());
                                            //To ensure that uploading the chosen image should finish first, prior to proceeding with getting its downloadURL
                                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                @Override
                                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                    if (!task.isSuccessful()) {
                                                        throw task.getException();
                                                    }
                                                    //Getting download URL of selected image
                                                    return uploadRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Uri> task) {

                                                                    if (task.isSuccessful()) {

                                                                        final String downloadURL = Objects.requireNonNull(task.getResult()).toString();
                                                                        //Updating the downloadURL field of the documentReference which was initially set to null
                                                                        latestDocRef.update("downloadURL", downloadURL)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Log.d(TAG, "onUpdatingDownloadURL: Successful");
                                                                                        } else {
                                                                                            Log.d(TAG, "onUpdatingDownloadURL: Failed");
                                                                                        }

                                                                                    }
                                                                                });

                                                                        Log.d(TAG, "onFetchingDownloadURL: Successful");
                                                                    } else {
                                                                        Log.d(TAG, "onFetchingDownloadURL: Failed");

                                                                        //Ensuring that the downloadURL will be fetched
                                                                        //Not sure tho if this works perfectly fine, but hopefully yes
                                                                        final String downloadURL = uploadRef.getDownloadUrl().getResult().toString();

                                                                        //Updating the downloadURL field of the documentReference which was initially set to null
                                                                        latestDocRef.update("downloadURL", downloadURL)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Log.d(TAG, "onUpdatingDownloadURL: Successful");
                                                                                        } else {
                                                                                            Log.d(TAG, "onUpdatingDownloadURL: Failed");
                                                                                        }

                                                                                    }
                                                                                });
                                                                    }

                                                                }
                                                            });
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {

                                                }
                                            });

                                        } else {
                                            Toast.makeText(getActivity(), "Adding of inventory item failed", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "InventoryItemAdding: Failed");
                                        }

                                    }
                                    //End of creating data in FireStore
                                });
                            } else {
                                Log.d(TAG, "onUpdatingSubcategory of " + category + ": Failed.");
                            }

                        }
                        //End of updating Subcategory of the chosen category
                    });

                    //Entire creation process ended, the custom layout will now be dismissed.
                    alertDialog.dismiss();
                }
            }

        });

        //Upon clicking cancel by the user, the custom layout will be dismissed
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreatingInventoryItem: cancelled");
                alertDialog.cancel();
            }
        });

    }


    // We gon' be using this method when the user try'na upload some image from the Device's storage Dawg
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Fetching the selected image from the device storage of the user
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            ImageUri = data.getData();
            glide();
        } else {
            Toast.makeText(getActivity(), "No chosen image", Toast.LENGTH_SHORT).show();
        }
    }

    // Setting the image of circleImageView to the selected image from the Device Storage
    private void glide() {
        Glide.with(this)
                .asBitmap()
                .load(ImageUri)
                .into(circleImageView);
    }
}
