package com.example.self_care_companion;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.self_care_companion.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_mood, R.id.navigation_journal, R.id.navigation_habits)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.splashFragment || destination.getId() == R.id.loginFragment || destination.getId() == R.id.signUpFragment) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide(); // Hide top app bar
                }
                navView.setVisibility(View.GONE); // Hide bottom nav
            } else {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().show(); // Show top app bar
                }
                navView.setVisibility(View.VISIBLE); // Show bottom nav
            }
        });

        databaseHelper = new DatabaseHelper(this);

        // setup morning, noon, and evening notifications
        NotificationHelper.createNotificationChannel(this);
        NotificationScheduler.scheduleMorningNotification(this, 9, 0);  // 9:00 AM
        NotificationScheduler.scheduleMiddayNotification(this, 12, 00);   // 12:00 PM
        NotificationScheduler.scheduleEveningNotification(this, 17, 0); // 5:00 PM

    }


}