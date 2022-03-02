package com.example.finaltest_version_one.Main.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.finaltest_version_one.Admin.AdminActivity;
import com.example.finaltest_version_one.Employee.EmployeeActivity;
import com.example.finaltest_version_one.Main.Model.Admin;
import com.example.finaltest_version_one.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login,container, false);
        final EditText sign_email = v.findViewById(R.id.sign_email);
        final EditText sign_password = v.findViewById(R.id.sign_password);
        final ProgressBar progressBar = v.findViewById(R.id.progressBar);
        Button login = v.findViewById(R.id.sign_login);

        final FirebaseAuth fAuth = FirebaseAuth.getInstance();
        final CollectionReference cRef = FirebaseFirestore.getInstance().collection("Account");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = sign_email.getText().toString().trim();
                final String password = sign_password.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    sign_email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    sign_password.setError("Password is required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //authenticating user
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            final String sEmail = Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()).toUpperCase();
                            cRef.document(sEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        final String role = Objects.requireNonNull(task.getResult()).getString("role");
                                        Log.d(TAG, "onComplete: " + role);

                                        assert role != null;
                                        if (role.equals("admin") || role.equals("Admin")){

                                            final Admin admin = new Admin();
                                            admin.setEmail(email);
                                            admin.setPassword(password);
                                            FirebaseFirestore.getInstance().collection("Admin").document("Currently Signed In")
                                                    .set(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Log.d(TAG, "onStoringCurrentlySignedIn: Update successful.");
                                                    }
                                                }
                                            });
                                            Intent intent = new Intent(getContext(), AdminActivity.class);
                                            startActivity(intent);
                                            Objects.requireNonNull(getActivity()).finish();
                                        }else {
                                            Intent intent = new Intent(getContext(), EmployeeActivity.class);
                                            startActivity(intent);
                                            Objects.requireNonNull(getActivity()).finish();
                                        }
                                    }

                                }
                            });
                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Invalid credentials. Please try Again.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });

        return v;
    }

}
