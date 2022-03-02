package com.example.finaltest_version_one.Employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.finaltest_version_one.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmployeeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        //Load default fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.employee_container, new Employee_PurchaseFragment());
        fragmentTransaction.commit();

        //Setting up Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.employee_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch(item.getItemId()){
            case R.id.employee_purchase:
                selectedFragment = new Employee_PurchaseFragment();
                break;
            case R.id.employee_inventory:
                selectedFragment = new Employee_nInventoryFragment();
                break;
        }
        assert selectedFragment != null;
        fragmentManager.beginTransaction().replace(R.id.employee_container,selectedFragment).addToBackStack(null).commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        fragmentManager.popBackStack();
    }
}
