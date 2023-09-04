package de.pme.collector.view.fragments.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import de.pme.collector.R;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;


    // =================================
    // LiveCycle
    // =================================

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // register the listener for preference changes
        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        assert sharedPreferences != null;
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @NonNull String key) {

        // dark-mode switch
        if (key.equals("dark_mode")) {

            boolean isDarkModeEnabled = sharedPreferences.getBoolean(key, true);

            updateThemeSwitch(isDarkModeEnabled);

            if (isDarkModeEnabled) {
                // dark theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else {
                // light theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // recreate activity to apply theme changes
            requireActivity().recreate();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // unregister the listener to prevent memory leaks
        assert sharedPreferences != null;
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    // =================================
    // Switch
    // =================================

    private void updateThemeSwitch(boolean darkModeEnabled) {

        SwitchPreferenceCompat darkModeSwitch = findPreference("dark_mode");

        assert darkModeSwitch != null;
        darkModeSwitch.setChecked(darkModeEnabled);
    }
}