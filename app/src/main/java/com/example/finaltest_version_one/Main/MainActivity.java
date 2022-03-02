package com.example.finaltest_version_one.Main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finaltest_version_one.Main.Fragment.LandingPageFragment;
import com.example.finaltest_version_one.R;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load default fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, new LandingPageFragment());
        fragmentTransaction.addToBackStack(null).commit();
    }
    @Override
    public void onBackPressed() {

        int count = fragmentManager.getBackStackEntryCount();

        if (count == 1) {
            new AlertDialog.Builder(this)
                    .setTitle("Closing Application")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    }).create().show();
        } else {
            fragmentManager.popBackStack();
        }

    }

}
