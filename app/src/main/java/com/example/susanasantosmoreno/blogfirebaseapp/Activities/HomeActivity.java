package com.example.susanasantosmoreno.blogfirebaseapp.Activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.susanasantosmoreno.blogfirebaseapp.Fragments.HomeFragment;
import com.example.susanasantosmoreno.blogfirebaseapp.Fragments.ProfileFragment;
import com.example.susanasantosmoreno.blogfirebaseapp.Fragments.SettingsFragment;
import com.example.susanasantosmoreno.blogfirebaseapp.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Set;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private TextView navUserName;
    private TextView navUserMail;
    private ImageView navUserImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.setTitle("Home");

        //init views and firebase.
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch ( menuItem.getItemId()){
            case R.id.navItemHome:
                getSupportActionBar().setTitle(getString(R.string.drawerOptionHome));
                fragment = new HomeFragment();
                displaySelectedFragment(fragment);
                break;
            case R.id.navItemProfile:
                getSupportActionBar().setTitle(getString(R.string.drawerOptionProfile));
                fragment = new ProfileFragment();
                displaySelectedFragment(fragment);
                break;
            case R.id.navItemSettings:
                getSupportActionBar().setTitle(getString(R.string.drawerOptionSettings));
                fragment = new SettingsFragment();
                displaySelectedFragment(fragment);
                break;
            case R.id.navItemSignOut:
                FirebaseAuth.getInstance().signOut();
                Intent loginActivity = new Intent(getApplicationContext(),
                        com.example.susanasantosmoreno.blogfirebaseapp.Activities.loginActivity.class);
                startActivity(loginActivity);
                finish();
                break;
            default:
                return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displaySelectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    public void updateNavHeader(){
        View headerView = navigationView.getHeaderView(0);
        navUserName = headerView.findViewById(R.id.navUserName);
        navUserMail = headerView.findViewById(R.id.navUserMail);
        navUserImg = headerView.findViewById(R.id.navUserImage);

        navUserMail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserImg);
    }


}
