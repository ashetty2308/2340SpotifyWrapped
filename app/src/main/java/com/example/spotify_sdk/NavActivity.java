package com.example.spotify_sdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class NavActivity extends AppCompatActivity {

    Fragment startupFrag;
    boolean startup;
    Bundle dataBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        Button logoutBtn = (Button) findViewById(R.id.logout_button);

//        startupFrag = new FragmentOne();
        startup = false;
        dataBundle = getIntent().getExtras().getBundle("DATA_BUNDLE");

        if (startupFrag == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentOne tempFrag1 = new FragmentOne();
            tempFrag1.setArguments(dataBundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, tempFrag1);
            fragmentTransaction.commit();
            startup = true;
        }

//        Bundle bundle = getIntent().getExtras().getBundle("DATA_BUNDLE");
//        Log.d("bundle", bundle.toString());
//        FragmentOne frag1 = new FragmentOne();
//        frag1.setArguments(bundle);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.Home:
                        replaceFragment(new FragmentOne());
                        return true;

                    case R.id.Profile:
                        replaceFragment(new FragmentTwo());
                        return true;

                    case R.id.Settings:
                        FragmentThree frag3 = new FragmentThree();
                        Log.d("DATA BUNDLE PLEASE", dataBundle.toString());
                        frag3.setArguments(dataBundle);
                        replaceFragment(frag3);
                        return true;
                }
                return false;
            }
        });

        logoutBtn.setOnClickListener((v) -> {
            startActivity(new Intent(NavActivity.this, SignUpActivity.class));
        });
    }


    private void replaceFragment(Fragment fragment){
        fragment.setArguments(dataBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}