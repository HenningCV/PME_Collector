package de.pme.collector.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.pme.collector.R;
import de.pme.collector.viewModel.SharedViewModel;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private NavController navController;

    private SharedViewModel sharedViewModel;


    // =================================
    // LiveCycle
    // =================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get SharedPreferences instance
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // set light or dark theme depending on what the user selected in the settings
        // dark-mode is enabled by default
        setTheme();

        // set activity content from a layout resource
        setContentView(R.layout.activity_main);

        // set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        setNavigationController();

        // set up bottom navigation with navigation-controller
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // initialize SharedViewModel-instance
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // set up the navigation-listener to save the last visited fragment
        // used to return to the view you were last on when switching to the Settings-tab and back to the Games-tab
        setNavControllerDestinationChangedListener();

        // handle click-events on the bottom navigation
        setBottomNavigationItemSelectedListener(bottomNavigationView);
    }


    // =================================
    // Setups
    // =================================

    private void setNavigationController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_home);

        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
    }


    private void setNavControllerDestinationChangedListener() {

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            // exclude settings from saving
            if (destinationId == R.id.navigation_settings) {
                return;
            }

            sharedViewModel.setCurrentDestinationId(destinationId);

            if (arguments != null) {
                sharedViewModel.setArguments(arguments);
            }
            else {
                sharedViewModel.setArguments(new Bundle());
            }
        });
    }


    private void setBottomNavigationItemSelectedListener(@NonNull BottomNavigationView bottomNavigationView) {

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int currentNavigationTabId = item.getItemId();

            if (currentNavigationTabId == R.id.navigation_home) {
                displayLastVisitedFragment(navController);
                return true;
            }

            if (item.getItemId() == R.id.navigation_settings) {
                displaySettingsFragment(navController);
                return true;
            }

            return false;
        });
    }


    // =================================
    // Theme
    // =================================

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

        // update dark-mode switch state in the settings-screen
        updateDarkModeSwitchPreference(isDarkModeEnabled);
    }


    private void updateDarkModeSwitchPreference(boolean isDarkModeEnabled) {
        // update dark mode switch preference value
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("dark_mode", isDarkModeEnabled);

        editor.apply();
    }


    // =================================
    // Fragments
    // =================================

    // restore last visited fragment from saved state
    private void displayLastVisitedFragment(@NonNull NavController navController) {
        navController.navigate(sharedViewModel.getLastDestinationId(), sharedViewModel.getLastDestinationArguments());
    }


    private void displaySettingsFragment(@NonNull NavController navController) {
        navController.navigate(R.id.navigation_settings);
    }
}