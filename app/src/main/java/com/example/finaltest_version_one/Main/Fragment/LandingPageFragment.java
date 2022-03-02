package com.example.finaltest_version_one.Main.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.finaltest_version_one.Main.ChildFragment.AccessoriesChildFragment;
import com.example.finaltest_version_one.Main.ChildFragment.AnimalChildFragment;
import com.example.finaltest_version_one.Main.ChildFragment.AquariumChildFragment;
import com.example.finaltest_version_one.Main.ChildFragment.CageChildFragment;
import com.example.finaltest_version_one.Main.ChildFragment.FoodChildFragment;
import com.example.finaltest_version_one.Main.ChildFragment.PumpChildFragment;
import com.example.finaltest_version_one.R;
import com.google.android.material.tabs.TabLayout;

public class LandingPageFragment extends Fragment implements View.OnLongClickListener {

    private TabLayout categoryTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_landing_page,container,false);
       Button mButton = v.findViewById(R.id.secret_login);
       mButton.setVisibility(View.VISIBLE);
        mButton.setBackgroundColor(Color.TRANSPARENT);
       mButton.setOnLongClickListener(this);

       //Load initially displayed child fragment which should display Accessories
        getChildFragmentManager().beginTransaction().add(R.id.child_fragment_container, new AccessoriesChildFragment()).commit();

       //TabLayout
        categoryTabLayout = v.findViewById(R.id.cTabLayout);
        categoryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Fragment selectedFragment = null;

                switch (tab.getPosition()){
                    //Accessories
                    case 0:
                        selectedFragment = new AccessoriesChildFragment();
                        break;
                    //Animal
                    case 1:
                        selectedFragment = new AnimalChildFragment();
                        break;
                    //Aquarium
                    case 2:
                        selectedFragment = new AquariumChildFragment();
                        break;
                    //Cage
                    case 3:
                        selectedFragment = new CageChildFragment();
                        break;
                    //Food
                    case 4:
                        selectedFragment = new FoodChildFragment();
                        break;
                    //Pump
                    case 5:
                        selectedFragment = new PumpChildFragment();
                        break;
                }
                assert selectedFragment !=null;
                getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container, selectedFragment).commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



       return v;
    }

    @Override
    public boolean onLongClick(View v) {

        getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.main_container, new LoginFragment()).addToBackStack(null).commit();
        return true;
    }
}
