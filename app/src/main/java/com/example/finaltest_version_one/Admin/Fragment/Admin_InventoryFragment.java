package com.example.finaltest_version_one.Admin.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.Inventory;
import com.example.finaltest_version_one.Admin.RecyclerView.InventoryRecyclerAdapter;
import com.example.finaltest_version_one.Main.MainActivity;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.app.Activity.RESULT_OK;


public class Admin_InventoryFragment extends Fragment implements InventoryRecyclerAdapter.InventoryListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri ImageUri;
    private CircleImageView circleImageView;
    private static final String TAG = "Admin_InventoryFragment";
    private CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Inventory");
    private ArrayList<String> documentNames = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin__inventory, container, false);
        recyclerView = v.findViewById(R.id.iRecyclerView);

        //Divider (for border outline of each view holder)
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));

        //Item Touch Helper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //RecyclerAdapter Initialize
        Query query = collectionReference
                .whereEqualTo("status", "Enabled");

        FirestoreRecyclerOptions<Inventory> options = new FirestoreRecyclerOptions.Builder<Inventory>()
                .setQuery(query, Inventory.class)
                .build();

        InventoryRecyclerAdapter inventoryRecyclerAdapter = new InventoryRecyclerAdapter(options, this);
        recyclerView.setAdapter(inventoryRecyclerAdapter);
        inventoryRecyclerAdapter.startListening();
        //RecyclerAdapter Initialization ends here

        FloatingActionButton add = v.findViewById(R.id.inventory_add);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        //Getting all the names of Documents realtime
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null){

                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();

                    //Adding each document's name one at a time inside the container "documentNames"
                    for (DocumentChange documentChange: documentChangeList) {
                        documentNames.add(Objects.requireNonNull(documentChange.getDocument().getString("name")).toUpperCase());
                    }
                    //Currently the inventory is empty, so we try'na proceed with this code underneath Dawg
                }else {
                    Toast.makeText(getActivity(), "Inventory List is currently empty.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: " + "Inventory List is currently empty.");
                }
            }
        });

        return v;
    }


    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            if (direction == ItemTouchHelper.LEFT) {
                InventoryRecyclerAdapter.InventoryVieHolder inventoryVieHolder = (InventoryRecyclerAdapter.InventoryVieHolder) viewHolder;
                inventoryVieHolder.disableItem();
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()),R.color.disable))
                    .addSwipeLeftActionIcon(R.drawable.disable)
                    .addSwipeLeftLabel("Disable")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(getActivity(),R.color.white))

                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    // Finding the custom layout and IDs inside the chosen layout
    private void showAlertDialog() {

        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_add_inventory, null);

        final EditText mName = v.findViewById(R.id.iName);
        final EditText mQuantity = v.findViewById(R.id.iQuantity);
        final EditText mPrice = v.findViewById(R.id.iPrice);
        final EditText mStatus = v.findViewById(R.id.iStatus);
        final EditText mCategory = v.findViewById(R.id.iCategory);
        final EditText mCriticalValue = v.findViewById(R.id.iCriticalValue);
        Button create = v.findViewById(R.id.iCreate);
        Button cancel = v.findViewById(R.id.iCancel);

        //CircleImage
        circleImageView = v.findViewById(R.id.iCircleImageView);

        //Once the circle image itself has been clicked, it will first ask for access permission of storage Device
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(Objects.requireNonNull(getActivity()), "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
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

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String name = mName.getText().toString();
                final int quantity = Integer.parseInt(mQuantity.getText().toString());
                final double price = Double.parseDouble(mPrice.getText().toString());
                final String status = mStatus.getText().toString();
                final String category = mCategory.getText().toString();
                final int criticalValue = Integer.parseInt(mCriticalValue.getText().toString());

                //Uploading Selected Image to FireBase Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference uploadRef = storage.getReference().child("Inventory").child(name.toUpperCase());
                Bitmap sampleBitmap = ((BitmapDrawable) circleImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                sampleBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                uploadRef.putBytes(baos.toByteArray())
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            }
                        });

                final String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                FirebaseFirestore.getInstance().collection("users").document(user).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    final String role = Objects.requireNonNull(task.getResult()).getString("role");
                                    final Inventory inventory = new Inventory(name, quantity, price, category, status, criticalValue, role,null);
                                    final String dName = inventory.getName();


                                    //After filling up the "documentNames" container, we good to go bruh - in the onCreateView Lifecycle of this Fragment

                                    //If it ain't new, then it won't be stored again Dawg
                                    if (documentNames.contains(dName.toUpperCase())) {
                                        Toast.makeText(getActivity(), dName + " already exist in the Inventory List.", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onEvent: " + dName + " already exist in the Inventory List.");
                                        uploadRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });

                                    //The created item is new, thus, it will be stored in FireStore
                                    } else {

                                        collectionReference.document(dName.toUpperCase()).set(inventory).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), dName + " added to Inventory List.", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onComplete: " + dName + " added to Inventory List.");


                                                    //Getting download URL of selected image
                                                    uploadRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {

                                                            final String downloadURL = Objects.requireNonNull(task.getResult()).toString();
                                                            collectionReference.document(name.toUpperCase()).update("downloadURL", downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                }
                                                            });

                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(getActivity(), "Failed to add " + dName + " to Inventory List.", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onComplete: " + "Failed to add " + dName + " to Inventory List.");
                                                }
                                            }
                                        });


                                    }
                                }

                            }


                        });

                alertDialog.dismiss();
            }
        });
        // When the user click cancel, then viewing of layout will be cancelled
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    // This method is use to update the selected item from the inventory list
    // This is an interface created in InventoryRecyclerAdapter then implemented in this activity to pass Data
    // It is much efficient for the Admin Activity to do such task, instead of the fragment doing it with regard to considering -->
    // --> Hierarchical level between activity and a fragment
    @Override
    public void editInventory(final DocumentSnapshot snapshot) {
        final Inventory inventory = snapshot.toObject(Inventory.class);
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_edit_inventory, null);

        final EditText mName = v.findViewById(R.id.eName);
        final EditText mQuantity = v.findViewById(R.id.eQuantity);
        final EditText mPrice = v.findViewById(R.id.ePrice);
        final EditText mStatus = v.findViewById(R.id.eStatus);
        final EditText mCategory = v.findViewById(R.id.eCategory);
        final EditText mCriticalValue = v.findViewById(R.id.eCriticalValue);
        Button update = v.findViewById(R.id.eUpdate);
        Button cancel = v.findViewById(R.id.eCancel);

        assert inventory != null;
        mName.setText(inventory.getName());
        mQuantity.setText(String.valueOf(inventory.getQuantity()));
        mPrice.setText(String.valueOf(inventory.getPrice()));
        mStatus.setText(inventory.getStatus());
        mCategory.setText(inventory.getCategory());
        mCriticalValue.setText(String.valueOf(inventory.getCriticalValue()));

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .create();
        alertDialog.show();


        // Upon clicking update button, the codes hereafter will commence
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = mName.getText().toString();
                final int quantity = Integer.parseInt(mQuantity.getText().toString());
                final double price = Double.parseDouble(mPrice.getText().toString());
                final String status = mStatus.getText().toString();
                final String category = mCategory.getText().toString();
                final int criticalValue = Integer.parseInt(mCriticalValue.getText().toString());

                inventory.setName(name);
                inventory.setQuantity(quantity);
                inventory.setPrice(price);
                inventory.setStatus(status);
                inventory.setCategory(category);
                inventory.setCriticalValue(criticalValue);

                snapshot.getReference().set(inventory).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Successfully updated " + name , Toast.LENGTH_SHORT).show();
                    }
                });





                alertDialog.dismiss();
            }
        });

        // When the user click cancel, then viewing of layout will be cancelled
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

    }

    // This method is use when the user swipe a certain inventory item to the left to disable it
    // This is an interface created in InventoryRecyclerAdapter then implemented in this activity to pass Data
    // It is much efficient for the Admin Activity to do such task, instead of the fragment doing it with regard to considering -->
    // --> Hierarchical level between activity and a fragment
    @Override
    public void disableItemInventory(final DocumentSnapshot snapshot) {

        final Inventory inventory = snapshot.toObject(Inventory.class);
        assert inventory != null;
        inventory.setStatus("Disabled");
        collectionReference.document(inventory.getName().toUpperCase()).set(inventory).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(recyclerView, inventory.getName() + " disabled.", Snackbar.LENGTH_LONG )
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                     collectionReference.document(inventory.getName().toUpperCase()).set(Objects.requireNonNull(snapshot.toObject(Inventory.class)));
                                }
                            }).show();
                }
            }
        });
    }

}
