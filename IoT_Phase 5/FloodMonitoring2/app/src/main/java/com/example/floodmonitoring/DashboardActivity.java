package com.example.floodmonitoring;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button findParkingButton = findViewById(R.id.findParkingButton);
        Button myReservationsButton = findViewById(R.id.myReservationsButton);
        Button paymentButton = findViewById(R.id.paymentButton);
        Button settingsButton = findViewById(R.id.settingsButton);


        String user = getIntent().getStringExtra("userName");


        TextView userview=findViewById(R.id.textView4);

        userview.setText("WELCOME"+" "+user);


        // Set click listeners for buttons to navigate to different activities
        findParkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, Firebase.class));

            }
        });

        myReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, Weather.class));
            }
        });

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Add click listeners for more buttons as needed
    }
}
