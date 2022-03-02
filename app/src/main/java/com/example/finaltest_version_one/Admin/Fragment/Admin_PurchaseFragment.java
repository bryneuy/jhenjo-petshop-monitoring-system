package com.example.finaltest_version_one.Admin.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.LogClass;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.Admin.RecyclerView.purchasedItemRecyclerAdapter;
import com.example.finaltest_version_one.Admin.RecyclerView.purchasedRecyclerAdapter;
import com.example.finaltest_version_one.Main.MainActivity;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Admin_PurchaseFragment extends Fragment implements purchasedItemRecyclerAdapter.nInventoryListiner{

    private static final String TAG = "Admin_nInventoryFragmen";
    private CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("nInventory");

    private CollectionReference purchaseRef = FirebaseFirestore.getInstance().collection("Purchased");
    private CollectionReference LogRef = FirebaseFirestore.getInstance().collection("Logs");
    private RecyclerView itemRecyclerView,purchaseRecyclerView;
    private TextView totalpriceTextView;
    private Button btnDone,btnScan;
    private CodeScanner codeScanner;
    private ArrayList<String> Categories = new ArrayList<>();

    //FireStore Document Reference *added June 7 2020*
    //for fetching QR Codes
    private DocumentReference accessoriesRef = collectionReference.document("Accessories");
    private DocumentReference animalRef = collectionReference.document("Animal");
    private DocumentReference aquariumRef = collectionReference.document("Aquarium");
    private DocumentReference cageRef = collectionReference.document("Cage");
    private DocumentReference foodRef = collectionReference.document("Food");
    private DocumentReference pumpRef = collectionReference.document("Pump");


    //Container for each category
    //For fetching category data from FireStore
    //Added june 7 2020
    private ArrayList<String> accessories = new ArrayList<>();
    private ArrayList<String> animal = new ArrayList<>();
    private ArrayList<String> aquarium = new ArrayList<>();
    private ArrayList<String> cage = new ArrayList<>();
    private ArrayList<String> food = new ArrayList<>();
    private ArrayList<String> pump = new ArrayList<>();

    private List<Double> docuPrices = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin__purchase,container,false);
        Categories.add("Accessories");
        Categories.add("Animal");
        Categories.add("Aquarium");
        Categories.add("Cage");
        Categories.add("Food");
        Categories.add("Pump");
        totalpriceTextView = v.findViewById(R.id.totalpriceTextView);
        btnDone = v.findViewById(R.id.btnDone);
        btnScan = v.findViewById(R.id.btnScan);
        itemRecyclerView = v.findViewById(R.id.itemRecyclerView);
        purchaseRecyclerView = v.findViewById(R.id.purchaseRecyclerView);

        purchasedRecyclerAdapter purchasedRecyclerAdapter = new purchasedRecyclerAdapter(Categories, getContext());
        itemRecyclerView.setAdapter(purchasedRecyclerAdapter);

        FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                .setQuery(purchaseRef, nInventory.class)
                .build();
        purchasedItemRecyclerAdapter purchasedItemRecyclerAdapter = new purchasedItemRecyclerAdapter(options, (com.example.finaltest_version_one.Admin.RecyclerView.purchasedItemRecyclerAdapter.nInventoryListiner) this);
        purchaseRecyclerView.setAdapter(purchasedItemRecyclerAdapter);
        purchasedItemRecyclerAdapter.startListening();


        //For Logging out, a dedicated "setting" button has been placed to every dashboards
        Button settings = v.findViewById(R.id.purchaseSettings);
        FrameLayout fl = v.findViewById(R.id.purchase_fl_logout);

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

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int counter = 0;
                        if (task.isSuccessful()){
                            counter = task.getResult().size();
//                            Toast.makeText(getContext(), String.valueOf(counter), Toast.LENGTH_SHORT).show();
                        }
                        if (counter > 0) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Purchase Confirmation");
                            alertDialog.setMessage("Are you sure?");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                    CollectionReference Logs = FirebaseFirestore.getInstance().collection("Logss");
                                    Date currentTime = Calendar.getInstance().getTime();
                                    purchaseRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                                            for (DocumentSnapshot snapshot: documentSnapshots){
                                                LogClass logClass = new LogClass(currentTime,Double.parseDouble(totalpriceTextView.getText().toString()));
                                                Logs.document(logClass.getValue().toString()).set(logClass);
                                                Logs.document(logClass.getValue().toString())
                                                        .collection(logClass.getValue().toString())
                                                        .document(snapshot.toObject(nInventory.class).getName())
                                                        .set(snapshot.toObject(nInventory.class));
                                                DocumentReference documentReference = collectionReference.document(snapshot.getString("category"))
                                                        .collection(snapshot.getString("subcategory").toUpperCase())
                                                        .document(snapshot.getString("name").toUpperCase());
                                                documentReference.update("quantity", FieldValue
                                                        .increment(-(snapshot
                                                                .toObject(nInventory.class)
                                                                .getQuantity()))).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getActivity(), "Transaction Successful", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    purchaseRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            WriteBatch batch =FirebaseFirestore.getInstance().batch();
                                            List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                                            for (DocumentSnapshot documentSnapshot:snapshots) {
                                                batch.delete(documentSnapshot.getReference());
                                            }
                                            batch.commit();
                                            Toast.makeText(getContext(), "Confirmed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                    Toast.makeText(getActivity(), "Purchase Order Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });
                            alertDialog.show();
                        }
                        else{
                            Toast.makeText(getContext(), "Cart is currently empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        return v;
    }

    public void showAlertDialog(){
        //Initializing view for the custom layout
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.qr_code_scanner,null);
        CodeScannerView scanner_view = v.findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(Objects.requireNonNull(getActivity()),scanner_view);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .create();
        alertDialog.show();
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        codeScanner.startPreview();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getActivity(), "CAMERA PERMISSION REQUIRED", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String qrCode = result.getText();
                        alertDialog.dismiss();

                        Toast.makeText(getActivity(), "QR Code: " + result.getText(), Toast.LENGTH_SHORT).show();

                        final LayoutInflater inflater = LayoutInflater.from(getContext());
                        View v = inflater.inflate(R.layout.custom_add_purchase, null);
                        final TextView purchaseNameTextView = v.findViewById(R.id.purchaseNameTextView);
                        final TextView purchasePriceTextView = v.findViewById(R.id.purchasePriceTextView);
                        final EditText purchaseQuantityEditText = v.findViewById(R.id.purchaseQuantityEditText);
                        final Button btnAddPurchase = v.findViewById(R.id.btnAddPurchase);
                        final ImageView imageViewPurchase = v.findViewById(R.id.imageViewPurchase);
                        final TextView remQuantity = v.findViewById(R.id.remQuantity);

                        accessoriesRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                accessories = (ArrayList<String>) snapshot.get("subcategory");

                                for (int i = 0; i < accessories.size(); i++) {
                                    final int position = i;
                                    accessoriesRef.collection(accessories.get(i).toUpperCase()).whereEqualTo("qrcode", qrCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                                            for (DocumentChange documentChange : documentChangeList) {
                                                final nInventory nInventoryQr = documentChange.getDocument().toObject(nInventory.class);
                                                if (nInventoryQr.getQRcode().equals(qrCode)) {
                                                    Glide.with(getContext())
                                                            .asBitmap()
                                                            .load(nInventoryQr.getDownloadURL())
                                                            .into(imageViewPurchase);

                                                    purchaseNameTextView.setText(nInventoryQr.getName());
                                                    purchasePriceTextView.setText(String.valueOf(nInventoryQr.getPrice()));
                                                    remQuantity.setText("( " + String.valueOf(nInventoryQr.getQuantity()) + " left)");

                                                    if (v.getParent() != null) {
                                                        ((ViewGroup) v.getParent()).removeView(v);
                                                        break;
                                                    }

                                                    //Setting up custom dialog layout
                                                    AlertDialog updateAlertDialog = new AlertDialog.Builder(getContext())
                                                            .setView(v)
                                                            .create();
                                                    updateAlertDialog.show();

                                                    //adder
                                                    btnAddPurchase.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (purchaseQuantityEditText.getText().toString().isEmpty()) {
                                                                purchaseQuantityEditText.setError("Enter quantity first");
                                                            } else {
                                                                if (Integer.parseInt(purchaseQuantityEditText.getText().toString()) <= nInventoryQr.getQuantity()) {
                                                                    nInventory nInventory1 = nInventoryQr;
                                                                    nInventory1.setQuantity(Integer.parseInt(purchaseQuantityEditText.getText().toString()));
                                                                    double newPrice = nInventoryQr.getPrice() * nInventory1.getQuantity();
                                                                    nInventory1.setPrice(newPrice);
                                                                    FirebaseFirestore.getInstance().collection("Purchased").document(nInventory1.getName()).set(nInventory1);
                                                                    updateAlertDialog.dismiss();
                                                                } else {
                                                                    purchaseQuantityEditText.setError("Insufficient Quantity ");
                                                                }
                                                            }
                                                        }
                                                    });

                                                    break;
                                                }

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

                                for (int i = 0; i < animal.size(); i++) {
                                    final int position = i;
                                    animalRef.collection(animal.get(i).toUpperCase()).whereEqualTo("qrcode", qrCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                                            for (DocumentChange documentChange : documentChangeList) {
                                                final nInventory nInventoryQr = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
                                                if (nInventoryQr.getQRcode().equals(qrCode)) {

                                                    Glide.with(getContext())
                                                            .asBitmap()
                                                            .load(nInventoryQr.getDownloadURL())
                                                            .into(imageViewPurchase);

                                                    purchaseNameTextView.setText(nInventoryQr.getName());
                                                    purchasePriceTextView.setText(String.valueOf(nInventoryQr.getPrice()));
                                                    remQuantity.setText("( " + String.valueOf(nInventoryQr.getQuantity()) + " left)");

                                                    if (v.getParent() != null){
                                                        ((ViewGroup)v.getParent()).removeView(v);
                                                        break;
                                                    }

                                                    //Setting up custom dialog layout
                                                    AlertDialog updateAlertDialog = new AlertDialog.Builder(getContext())
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
                                                                if (Integer.parseInt(purchaseQuantityEditText.getText().toString()) <= nInventoryQr.getQuantity()) {
                                                                    nInventory nInventory1 = nInventoryQr;
                                                                    nInventory1.setQuantity(Integer.parseInt(purchaseQuantityEditText.getText().toString()));
                                                                    double newPrice = nInventoryQr.getPrice() * nInventory1.getQuantity();
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

                                                    break;
                                                }

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

                                for (int i = 0; i < aquarium.size(); i++) {
                                    final int position = i;
                                    aquariumRef.collection(aquarium.get(i).toUpperCase()).whereEqualTo("qrcode", qrCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                                            for (DocumentChange documentChange : documentChangeList) {
                                                final nInventory nInventoryQr = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
                                                if (nInventoryQr.getQRcode().equals(qrCode)) {

                                                    Glide.with(getContext())
                                                            .asBitmap()
                                                            .load(nInventoryQr.getDownloadURL())
                                                            .into(imageViewPurchase);

                                                    purchaseNameTextView.setText(nInventoryQr.getName());
                                                    purchasePriceTextView.setText(String.valueOf(nInventoryQr.getPrice()));
                                                    remQuantity.setText("( " + String.valueOf(nInventoryQr.getQuantity()) + " left)");

                                                    if (v.getParent() != null){
                                                        ((ViewGroup)v.getParent()).removeView(v);
                                                        break;
                                                    }

                                                    //Setting up custom dialog layout
                                                    AlertDialog updateAlertDialog = new AlertDialog.Builder(getContext())
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
                                                                if (Integer.parseInt(purchaseQuantityEditText.getText().toString()) <= nInventoryQr.getQuantity()) {
                                                                    nInventory nInventory1 = nInventoryQr;
                                                                    nInventory1.setQuantity(Integer.parseInt(purchaseQuantityEditText.getText().toString()));
                                                                    double newPrice = nInventoryQr.getPrice() * nInventory1.getQuantity();
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

                                                    break;
                                                }

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

                                for (int i = 0; i < cage.size(); i++) {
                                    final int position = i;
                                    cageRef.collection(cage.get(i).toUpperCase()).whereEqualTo("qrcode", qrCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                                            for (DocumentChange documentChange : documentChangeList) {
                                                final nInventory nInventoryQr = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
                                                if (nInventoryQr.getQRcode().equals(qrCode)) {

                                                    Glide.with(getContext())
                                                            .asBitmap()
                                                            .load(nInventoryQr.getDownloadURL())
                                                            .into(imageViewPurchase);

                                                    purchaseNameTextView.setText(nInventoryQr.getName());
                                                    purchasePriceTextView.setText(String.valueOf(nInventoryQr.getPrice()));
                                                    remQuantity.setText("( " + String.valueOf(nInventoryQr.getQuantity()) + " left)");

                                                    if (v.getParent() != null){
                                                        ((ViewGroup)v.getParent()).removeView(v);
                                                        break;
                                                    }

                                                    //Setting up custom dialog layout
                                                    AlertDialog updateAlertDialog = new AlertDialog.Builder(getContext())
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
                                                                if (Integer.parseInt(purchaseQuantityEditText.getText().toString()) <= nInventoryQr.getQuantity()) {
                                                                    nInventory nInventory1 = nInventoryQr;
                                                                    nInventory1.setQuantity(Integer.parseInt(purchaseQuantityEditText.getText().toString()));
                                                                    double newPrice = nInventoryQr.getPrice() * nInventory1.getQuantity();
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

                                                    break;
                                                }

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

                                for (int i = 0; i < food.size(); i++) {
                                    final int position = i;
                                    foodRef.collection(food.get(i).toUpperCase()).whereEqualTo("qrcode", qrCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                                            for (DocumentChange documentChange : documentChangeList) {
                                                final nInventory nInventoryQr = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
                                                if (nInventoryQr.getQRcode().equals(qrCode)) {

                                                    Glide.with(getContext())
                                                            .asBitmap()
                                                            .load(nInventoryQr.getDownloadURL())
                                                            .into(imageViewPurchase);

                                                    purchaseNameTextView.setText(nInventoryQr.getName());
                                                    purchasePriceTextView.setText(String.valueOf(nInventoryQr.getPrice()));
                                                    remQuantity.setText("( " + String.valueOf(nInventoryQr.getQuantity()) + " left)");

                                                    if (v.getParent() != null){
                                                        ((ViewGroup)v.getParent()).removeView(v);
                                                        break;
                                                    }

                                                    //Setting up custom dialog layout
                                                    AlertDialog updateAlertDialog = new AlertDialog.Builder(getContext())
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
                                                                if (Integer.parseInt(purchaseQuantityEditText.getText().toString()) <= nInventoryQr.getQuantity()) {
                                                                    nInventory nInventory1 = nInventoryQr;
                                                                    nInventory1.setQuantity(Integer.parseInt(purchaseQuantityEditText.getText().toString()));
                                                                    double newPrice = nInventoryQr.getPrice() * nInventory1.getQuantity();
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

                                                    break;
                                                }

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

                                for (int i = 0; i < pump.size(); i++) {
                                    final int position = i;
                                    pumpRef.collection(pump.get(i).toUpperCase()).whereEqualTo("qrcode", qrCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                                            for (DocumentChange documentChange : documentChangeList) {
                                                final nInventory nInventoryQr = documentChange.getDocument().toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);
                                                if (nInventoryQr.getQRcode().equals(qrCode)) {

                                                    Glide.with(getContext())
                                                            .asBitmap()
                                                            .load(nInventoryQr.getDownloadURL())
                                                            .into(imageViewPurchase);

                                                    purchaseNameTextView.setText(nInventoryQr.getName());
                                                    purchasePriceTextView.setText(String.valueOf(nInventoryQr.getPrice()));
                                                    remQuantity.setText("( " + String.valueOf(nInventoryQr.getQuantity()) + " left)");

                                                    if (v.getParent() != null){
                                                        ((ViewGroup)v.getParent()).removeView(v);
                                                        break;
                                                    }

                                                    //Setting up custom dialog layout
                                                    AlertDialog updateAlertDialog = new AlertDialog.Builder(getContext())
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
                                                                if (Integer.parseInt(purchaseQuantityEditText.getText().toString()) <= nInventoryQr.getQuantity()) {
                                                                    nInventory nInventory1 = nInventoryQr;
                                                                    nInventory1.setQuantity(Integer.parseInt(purchaseQuantityEditText.getText().toString()));
                                                                    double newPrice = nInventoryQr.getPrice() * nInventory1.getQuantity();
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

                                                    break;
                                                }

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        purchaseRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                docuPrices.clear();
                for (DocumentSnapshot documentSnapshot:documentSnapshots){
                    docuPrices.add(documentSnapshot.getDouble("price"));
                }
                totalpriceTextView.setText(String.valueOf(docuPrices.stream().mapToDouble(Double::doubleValue).sum()));
            }
        });
    }

    //delete from purchaseRecyclerView
    @Override
    public void editInventoryItem(DocumentSnapshot documentSnapshot) {
        final nInventory nInventory = documentSnapshot.toObject(com.example.finaltest_version_one.Admin.Model.nInventory.class);

        purchaseRef.whereEqualTo("name",nInventory.getName()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch =FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot documentSnapshot:snapshots){
                    batch.delete(documentSnapshot.getReference());
                }
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(),"Item Deleted from Purchase",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
