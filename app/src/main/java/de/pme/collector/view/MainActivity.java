package de.pme.collector.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.pme.collector.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // set up bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation_view);

        NavController navController = Navigation.findNavController(this, R.id.navigation_home);

        NavigationUI.setupWithNavController(navigationView, navController);
    }
}