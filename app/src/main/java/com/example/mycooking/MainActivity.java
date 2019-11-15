package com.example.mycooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mycooking.Home.HomeFragment;
import com.example.mycooking.Like.LikeFragment;
import com.example.mycooking.Profile.ProfileFragment;
import com.example.mycooking.Search.SearchFragment;
import com.example.mycooking.Share.ShareFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NO = 0;
    private Context mContext = MainActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;

                        case R.id.nav_share:
                            selectedFragment = new ShareFragment();
                            break;
                        case R.id.nav_like:
                            selectedFragment = new LikeFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();


                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }


            };

    private void showdialog() {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(this);
        dDialog.setTitle("Oh!! Android ");
        dDialog.setIcon(android.R.drawable.btn_star_big_on);
        dDialog.setMessage("Sawatdee Android!");
        dDialog.setPositiveButton("Close", null);
        dDialog.show();
    }

    }

