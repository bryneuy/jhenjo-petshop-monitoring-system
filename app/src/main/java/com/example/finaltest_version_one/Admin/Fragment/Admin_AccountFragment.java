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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.RecyclerView.AccountRecyclerAdapter;
import com.example.finaltest_version_one.Admin.Model.Accounts;
import com.example.finaltest_version_one.Main.MainActivity;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
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
import com.google.protobuf.StringValue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.app.Activity.RESULT_OK;

public class Admin_AccountFragment extends Fragment implements AccountRecyclerAdapter.AccountListener {

    private RecyclerView recyclerView;
    private static final String TAG = "Admin_AccountFragment";
    private CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Account");
    private CollectionReference dCollectionReference = FirebaseFirestore.getInstance().collection("defaultImage");
    private ArrayList<String> accountNames = new ArrayList<>();
    private ArrayList<String> accountEmails = new ArrayList<>();
    private CircleImageView circleImageView;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri ImageUri;

    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin__account,container,false);

        recyclerView = v.findViewById(R.id.aRecyclerView);

//        //Divider (for border outline of each view holder)
//        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));

        //Item Touch Helper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //RecyclerAdapter Initialize
        Query query = collectionReference;

        FirestoreRecyclerOptions<Accounts> options = new FirestoreRecyclerOptions.Builder<Accounts>()
                .setQuery(query, Accounts.class)
                .build();

        AccountRecyclerAdapter accountRecyclerAdapter = new AccountRecyclerAdapter(options,this);
        recyclerView.setAdapter(accountRecyclerAdapter);
        accountRecyclerAdapter.startListening();
        //RecyclerAdapter Initialization ends here

        //For Logging out, a dedicated "setting" button has been placed to every dashboards
        Button settings = v.findViewById(R.id.settings);
        FrameLayout fl = v.findViewById(R.id.fl_logout);

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

        FloatingActionButton add = v.findViewById(R.id.account_add);

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

                    //Adding each account's name one at a time inside the container "accountNames"
                    for (DocumentChange documentChange: documentChangeList) {
                        accountNames.add(Objects.requireNonNull(documentChange.getDocument().getString("fullName")));
                    }
                    //Currently the account list is empty, so we try'na proceed with this code underneath Dawg
                }else {
                    Toast.makeText(getActivity(), "Account List is currently empty.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: Account List is currently empty.");
                }
            }
        });

        //Getting all the emails of Documents realtime
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null){

                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();

                    //Adding each account's name one at a time inside the container "accountNames"
                    for (DocumentChange documentChange: documentChangeList) {
                        accountEmails.add(Objects.requireNonNull(documentChange.getDocument().getString("email")));
                    }
                    //Currently the account list is empty, so we try'na proceed with this code underneath Dawg
                }else {
                    Toast.makeText(getActivity(), "Account List is currently empty.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: Account List is currently empty.");
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
                AccountRecyclerAdapter.AccountViewHolder accountViewHolder = (AccountRecyclerAdapter.AccountViewHolder) viewHolder;
                accountViewHolder.disableItem();
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()),R.color.disable))
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(getActivity(),R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    // Finding the custom layout and IDs inside the chosen layout
    private void showAlertDialog() {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_add_account, null);

        final EditText aFullName = v.findViewById(R.id.aFullName);
        final EditText aEmail = v.findViewById(R.id.aEmail);
        final EditText aAddress = v.findViewById(R.id.aAddress);
        final EditText aPhoneNumber = v.findViewById(R.id.aPhoneNumber);
        final EditText aAge = v.findViewById(R.id.aAge);
        final EditText aStatus = v.findViewById(R.id.aStatus);
        final EditText aRole = v.findViewById(R.id.aRole);
        final EditText aPassword =  v.findViewById(R.id.aPassword);

        final Button register = v.findViewById(R.id.aRegister);
        final Button cancel = v.findViewById(R.id.aCancel);

        //CircleImage
        circleImageView = v.findViewById(R.id.acCircleImageView);

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
                    //If permission granted, code underneath will commence
                    } else {
                        //see method constructor below for reference as to what this does
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

        // Upon clicking register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fName = aFullName.getText().toString();
                final String email = aEmail.getText().toString();
                final String address = aAddress.getText().toString();
                final String pNumber = aPhoneNumber.getText().toString();
                final String age = aAge.getText().toString();
                final String status = aStatus.getText().toString();
                final String role = aRole.getText().toString();
                final String password = aPassword.getText().toString();

                if (TextUtils.isEmpty(fName)){
                    aFullName.setError("Name is Required");
                    return;
                }

                //Uploading Selected Image to FireBase Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference uploadRef = storage.getReference().child("Account").child(fName.toUpperCase());
                Bitmap sampleBitmap = ((BitmapDrawable) circleImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                sampleBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                uploadRef.putBytes(baos.toByteArray())
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            }
                        });

                if (TextUtils.isEmpty(email)){
                    aEmail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(address)){
                    aAddress.setError("Address is Required");
                    return;
                }
                if (TextUtils.isEmpty(pNumber)){
                    aPhoneNumber.setError("Phone number is Required");
                    return;
                }if (TextUtils.isEmpty(role)){
                    aRole.setError("Role is Required");
                    return;
                }
                if (TextUtils.isEmpty(age)){
                    aAge.setError("Age is Required");
                    return;
                }
                if (TextUtils.isEmpty(status)){
                    aStatus.setError("Status is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    aPassword.setError("Password is Required");
                    return;
                }


                if (accountNames.contains(fName) && accountEmails.contains(email)) {
                    Toast.makeText(getActivity(), "This account already exist.", Toast.LENGTH_SHORT).show();
                } else {
                    //Registering user with FireBaseAuth
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        final Accounts accounts = new Accounts(email,fName,address,Integer.parseInt(pNumber),Integer.parseInt(age),role,null,status,password);
                                        collectionReference.document(email.toUpperCase()).set(accounts).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Account successfully created", Toast.LENGTH_SHORT).show();

                                                    //Getting download URL of selected image
                                                    uploadRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {

                                                            final String downloadURL = Objects.requireNonNull(task.getResult()).toString();
                                                            collectionReference.document(email.toUpperCase()).update("downloadURL", downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                }
                                                            });

                                                        }
                                                    });

                                                    //Fetching data first from FireStore to get credential of the current Admin user
                                                    FirebaseFirestore.getInstance().collection("Admin").document("Currently Signed In")
                                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()){
                                                                //Signing out the registered account
                                                                FirebaseAuth.getInstance().signOut();
                                                                Log.d(TAG, "Signing out registered account: Successful.");
                                                                //Signing in account of current Admin user once again
                                                                FirebaseAuth.getInstance().signInWithEmailAndPassword(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getString("email")), Objects.requireNonNull(task.getResult().getString("password")))
                                                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                if (task.isSuccessful()){
                                                                                    String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                                                                                    Log.d(TAG, "Registration --> onSigningInAdminUserOnceAgain: " + user + " successfully signed in.");

                                                                                }else {
                                                                                    Log.d(TAG, "Registration --> on-Signing-In-Admin-User-Once-Again: Failed. ");
                                                                                }

                                                                            }
                                                                        });
                                                            } else {
                                                                Log.d(TAG, "Registration --> Unable to fetch currently signed in Admin User.");
                                                            }
                                                        }

                                                    });
                                                } else {
                                                    Toast.makeText(getActivity(), "Failed to create account for " + fName, Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });

                                    }
                                }
                            });
                }
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

    }

    //Responsible for updating accounts included in the list
    @Override
    public void editAccount(final DocumentSnapshot snapshot) {
        final Accounts accounts = snapshot.toObject(Accounts.class);
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_edit_account, null);

        final EditText aFullName = v.findViewById(R.id.eaFullName);
        final EditText aEmail = v.findViewById(R.id.eaEmail);
        final EditText aAddress = v.findViewById(R.id.eaAddress);
        final EditText aPhoneNumber = v.findViewById(R.id.eaPhoneNumber);
        final EditText aAge = v.findViewById(R.id.eaAge);
        final EditText aStatus = v.findViewById(R.id.eaStatus);
        final EditText aRole = v.findViewById(R.id.eaRole);
        final EditText aPassword =  v.findViewById(R.id.eaPassword);

        final Button update = v.findViewById(R.id.eaUpdate);
        final Button cancel = v.findViewById(R.id.eaCancel);

        //Setting current text value for each edit text base from what account was clicked by the admin
        assert accounts != null;
        aFullName.setText(accounts.getFullName());
        aEmail.setText(accounts.getEmail());
        aAddress.setText(accounts.getAddress());
        aPhoneNumber.setText(String.valueOf(accounts.getpNumber()));
        aAge.setText(String.valueOf(accounts.getAge()));
        aStatus.setText(accounts.getStatus());
        aRole.setText(accounts.getRole());
        aPassword.setText(accounts.getPassword());



        //CircleImage
        circleImageView = v.findViewById(R.id.eacCircleImageView);

        //Setting the current image of the account
        Glide.with(Objects.requireNonNull(getActivity()))
                .asBitmap()
                .load(accounts.getDownloadURL())
                .into(circleImageView);

        //Once the circle image itself has been clicked, it will first ask for access permission of storage Device
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(Objects.requireNonNull(getActivity()), "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    //If permission granted, code underneath will commence
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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fName = aFullName.getText().toString();
                final String email = aEmail.getText().toString();
                final String address = aAddress.getText().toString();
                final String pNumber = aPhoneNumber.getText().toString();
                final String age = aAge.getText().toString();
                final String status = aStatus.getText().toString();
                final String role = aRole.getText().toString();
                final String password = aPassword.getText().toString();

                if (TextUtils.isEmpty(fName)){
                    aFullName.setError("Name is Required");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    aEmail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(address)){
                    aAddress.setError("Address is Required");
                    return;
                }
                if (TextUtils.isEmpty(pNumber)){
                    aPhoneNumber.setError("Phone number is Required");
                    return;
                }if (TextUtils.isEmpty(role)){
                    aRole.setError("Role is Required");
                    return;
                }
                if (TextUtils.isEmpty(age)){
                    aAge.setError("Age is Required");
                    return;
                }
                if (TextUtils.isEmpty(status)){
                    aStatus.setError("Status is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    aPassword.setError("Password is Required");
                    return;
                }

                //Signing out Admin account in order to sign in to the selected account
                //No other way but this method since in order to delete an account from FireBaseAuth -->
                //Only currently signed in account can be updated or be deleted
                //Thus, after signing out, the selected account will be signed in to enable deletion and update (for email and password)
                FirebaseAuth.getInstance().signOut();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Log.d(TAG, "on-Signing-In-Selected-Account: Sign in successful");
                                    //updating email (whether it is changed or not)
                                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                                    //updating password (whether it is changed or not)
                                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                }
                            }
                        });


                //Fetching current details of the selected account
                accounts.setFullName(fName);
                accounts.setEmail(email);
                accounts.setAddress(address);
                accounts.setpNumber(Integer.parseInt(pNumber));
                accounts.setAge(Integer.parseInt(age));
                accounts.setStatus(status);
                accounts.setRole(role);
                accounts.setPassword(password);

                //Updating current details of the selected account based from the data fetched to FireStore
                snapshot.getReference().set(accounts).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
                    }
                });

                //Fetching data first from FireStore to get credential of the current Admin user
                FirebaseFirestore.getInstance().collection("Admin").document("Currently Signed In")
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            //Signing out the selected account
                            FirebaseAuth.getInstance().signOut();
                            Log.d(TAG, "Signing out selected account: Successful.");
                            //Signing in account of current Admin user once again
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getString("email")), Objects.requireNonNull(task.getResult().getString("password")))
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()){
                                                String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                                                Log.d(TAG, "onSigningInAdminUserOnceAgain: " + user + " successfully signed in.");

                                                //Uploading Selected Image to FireBase Storage
                                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                                final StorageReference uploadRef = storage.getReference().child("Account").child(accounts.getFullName().toUpperCase());
                                                Bitmap sampleBitmap = ((BitmapDrawable) circleImageView.getDrawable()).getBitmap();
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                sampleBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                                                uploadRef.putBytes(baos.toByteArray())
                                                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                if (task.isSuccessful()) {

                                                                    //Getting download URL of selected image
                                                                    uploadRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Uri> task) {

                                                                            final String downloadURL = Objects.requireNonNull(task.getResult()).toString();
                                                                            collectionReference.document(accounts.getEmail().toUpperCase()).update("downloadURL", downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        Log.d(TAG, "on-Updating-Selected-Accounts-Image: Successful.");
                                                                                    } else {
                                                                                        Log.d(TAG, "on-Updating-Selected-Accounts-Image: Failed.");
                                                                                    }

                                                                                }
                                                                            });

                                                                        }
                                                                    });


                                                                }else {
                                                                    Log.d(TAG, "Uploading updated image failed. ");
                                                                }
                                                            }
                                                        });
                                            }else {
                                                Log.d(TAG, "on-Signing-In-Admin-User-Once-Again: Failed. ");
                                            }

                                        }
                                    });
                        } else {
                            Log.d(TAG, "Unable to fetch currently signed in Admin User.");
                        }
                        }

                });

                alertDialog.dismiss();
            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });


    }

    @Override
    public void deleteAccount(final DocumentSnapshot snapshot) {

        final Accounts accounts = snapshot.toObject(Accounts.class);

        //Temporarily signing out account of current Admin user -->
        //--> to enable deletion of the selected account
        FirebaseAuth.getInstance().signOut();

        //Signing in selected account
        assert accounts != null;
        FirebaseAuth.getInstance().signInWithEmailAndPassword(accounts.getEmail(),accounts.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Commencing deletion of the selected account
                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete();

                            //Signing out the selected account which is already deleted
                            FirebaseAuth.getInstance().signOut();

                            //Fetching data first from FireStore to get credential of the current Admin user
                            FirebaseFirestore.getInstance().collection("Admin").document("Currently Signed In")
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){

                                        //Signing in account of current Admin user once again
                                        FirebaseAuth.getInstance().signInWithEmailAndPassword(Objects.requireNonNull(Objects.requireNonNull(task.getResult())
                                                .getString("email")), Objects.requireNonNull(task.getResult().getString("password")))
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()){
                                                            String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                                            Log.d(TAG, "Signed in email: " + user);

                                                            //Once the account is deleted from FireBase Authentication -->
                                                            //--> it is also necessary to delete it from the FireStore database itself -->
                                                            //--> so as to prevent such data from being displayed in the account dashboard of the Admin

                                                            //Commencing deletion of the selected account's document from FireStore
                                                            collectionReference.document(accounts.getEmail().toUpperCase()).delete()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){

                                                                                //To undo deletion of account (Restoring data to both FireBase Auth & FireStore dBase)
                                                                                Snackbar.make(recyclerView,accounts.getFullName() + " Account deleted.", Snackbar.LENGTH_LONG)
                                                                                        .setAction("Undo", new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {

                                                                                                //Restoring data to FireSore dBase
                                                                                                collectionReference.document(accounts.getEmail().toUpperCase()).set(Objects.requireNonNull(snapshot.toObject(Accounts.class)));

                                                                                                //Restoring data to FireBase Authentication (Actually, just creating it once again)
                                                                                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(accounts.getEmail(), accounts.getPassword())
                                                                                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                                                if (task.isSuccessful()){
                                                                                                                    Log.d(TAG, "onRestoringAccount: Successful." );
                                                                                                                }else {
                                                                                                                    Log.d(TAG, "onRestoringAccount: Failed." );
                                                                                                                }
                                                                                                            }
                                                                                                        });

                                                                                            }
                                                                                        }).show();
                                                                                Toast.makeText(getActivity(), "Account successfully removed.", Toast.LENGTH_SHORT).show();
                                                                            }else {
                                                                                Log.d(TAG, "onAccountDeletion: Failed. ");
                                                                            }

                                                                        }
                                                                    });
                                                        }

                                                    }
                                                });
                                    }
                                }

                            });
                        }
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
