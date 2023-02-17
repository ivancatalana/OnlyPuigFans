package com.example.onlypuigfans;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlypuigfans.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
// Public para poderlo Cambiar
    private DrawerLayout drawer;

   // private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

         drawer = binding.drawerLayout;
         //Bloqueamos el drawer para que no se pueda desplegar con el dedo
         lockDrawer();
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment, R.id.profileFragment, R.id.misFotosFragment, R.id.signOutFragment
        )
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View header = navigationView.getHeaderView(0);
        final ImageView photo = header.findViewById(R.id.photoImageView);
        final TextView name = header.findViewById(R.id.displayNameTextView);
        final TextView email = header.findViewById(R.id.emailTextView);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    if (user.getPhotoUrl() != null) {
                        Glide.with(MainActivity.this)
                                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString())
                                .circleCrop()
                                .into(photo);
                        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    } else {
                        Glide.with(MainActivity.this)
                                .load(R.drawable.profile)
                                .circleCrop()
                                .into(photo);
                        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    }
                }
            }
        });
        FirebaseFirestore.getInstance().setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            int requestCode = 0;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void lockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void unlockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public class DetailViewActivity extends AppCompatActivity implements MyInterface {
        @Override
        public void lockDrawer() {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        @Override
        public void unlockDrawer() {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
    public void replaceFragment(Fragment fragmentName){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.signInFragment, fragmentName);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}