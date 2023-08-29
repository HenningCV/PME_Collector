package de.pme.collector.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.pme.collector.R;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get SharedPreferences instance
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setTheme();

        setContentView(R.layout.activity_main);

        // set up bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation_view);

        // set up navigation-controller
        NavController navController = Navigation.findNavController(this, R.id.navigation_home);

        // set up bottom navigation with navigation-controller
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    private void setTheme() {
        // set default theme to night-mode
        boolean isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", true);

        if (isDarkModeEnabled) {
            // dark theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            // light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // update dark-mode switch state
        updateDarkModeSwitchPreference(isDarkModeEnabled);
    }


    private void updateDarkModeSwitchPreference(boolean isDarkModeEnabled) {
        // update dark mode switch preference value
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("dark_mode", isDarkModeEnabled);

        editor.apply();
    }
}