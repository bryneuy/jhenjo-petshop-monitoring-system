package com.example.finaltest_version_one.Admin;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finaltest_version_one.Admin.Fragment.Admin_AccountFragment;
import com.example.finaltest_version_one.Admin.Fragment.Admin_FinanceFragment;
import com.example.finaltest_version_one.Admin.Fragment.Admin_InventoryFragment;
import com.example.finaltest_version_one.Admin.Fragment.Admin_PurchaseFragment;
import com.example.finaltest_version_one.Admin.Fragment.Admin_nInventoryFragment;
import com.example.finaltest_version_one.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Load default fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.admin_container, new Admin_AccountFragment());
        fragmentTransaction.commit();

        //Setting up Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch(item.getItemId()){
            case R.id.admin_account:
                selectedFragment = new Admin_AccountFragment();
                break;
            case R.id.admin_purchase:
                selectedFragment = new Admin_PurchaseFragment();
                break;
            case R.id.admin_finance:
                selectedFragment = new Admin_FinanceFragment();
                break;
            case R.id.admin_nInventory:
                selectedFragment = new Admin_nInventoryFragment();
                break;
        }
        assert selectedFragment != null;
        fragmentManager.beginTransaction().replace(R.id.admin_container,selectedFragment).addToBackStack(null).commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        fragmentManager.popBackStack();
    }


}
