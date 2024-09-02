package com.coding.myblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.coding.myblogapp.Fragments.Home;
import com.coding.myblogapp.Fragments.Profile;
import com.coding.myblogapp.Fragments.Publish;
import com.coding.myblogapp.databinding.ActivityDrawerBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityDrawerBinding binding;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("DrawerActivity", "DrawerActivity started.");

        showdp();
        setupdrawer();

        // Load Home fragment by default
        if (savedInstanceState == null) {
            Log.d("DrawerActivity", "Loading Home fragment.");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new Home());
            fragmentTransaction.commit();
        }
    }

    private void setupdrawer() {
        binding.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawer.openDrawer(Gravity.LEFT);
            }
        });
        binding.navigationView.setNavigationItemSelectedListener(this);
    }

    private void showdp() {
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Glide.with(this).load(account.getPhotoUrl()).into(binding.profileIcon);
            Log.d("DrawerActivity", "Profile picture loaded.");
        } else {
            Log.d("DrawerActivity", "Google account is null.");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()) {
            case R.id.nav_home:
                Log.d("DrawerActivity", "Home navigation item selected.");
                fragmentTransaction.replace(R.id.frame_layout, new Home());
                break;
            case R.id.nav_publish:
                Log.d("DrawerActivity", "Publish navigation item selected.");
                fragmentTransaction.replace(R.id.frame_layout, new Publish());
                break;
            case R.id.nav_profile:
                Log.d("DrawerActivity", "Profile navigation item selected.");
                fragmentTransaction.replace(R.id.frame_layout, new Profile());
                break;
            default:
                Log.w("DrawerActivity", "Unknown navigation item selected.");
                return false;
        }

        fragmentTransaction.commit();
        binding.drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
