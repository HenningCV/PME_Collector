package de.pme.collector.view.fragments.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import de.pme.collector.R;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // register the listener for preference changes
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        assert sharedPreferences != null;
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // dark-mode switch
        if (key.equals("dark_mode")) {

            boolean isDarkModeEnabled = sharedPreferences.getBoolean(key, false);

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


    private void updateThemeSwitch(boolean darkModeEnabled) {

        SwitchPreferenceCompat darkModeSwitch = findPreference("dark_mode");

        assert darkModeSwitch != null;
        darkModeSwitch.setChecked(darkModeEnabled);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        // unregister the listener to prevent memory leaks
        assert sharedPreferences != null;
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}