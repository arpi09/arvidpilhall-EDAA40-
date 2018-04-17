package com.example.coordinates;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Application extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates);
    }

    public void onClickCompass(View view) {
        Intent intent = new Intent(Application.this, Compass.class);
        startActivity(intent);
    }
    public void onClickDisplayed(View v) {
        Intent i = new Intent(Application.this, Accelerometer.class);
        startActivity(i);
    }
}
