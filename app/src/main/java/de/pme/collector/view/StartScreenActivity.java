package de.pme.collector.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import de.pme.collector.R;


public class StartScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        Intent i = new Intent(StartScreenActivity.this, MainActivity.class);

        new Handler().postDelayed(() -> {
            startActivity( i );
            finish();    // clear activity from back stack
        }, 1000);
    }

}