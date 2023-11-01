package com.example.floodmonitoring;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.floodmonitoring.GaugeView;
import com.google.firebase.database.ValueEventListener;

public class Firebase extends AppCompatActivity {

    private DatabaseReference sensorDataRef;
    private GaugeView distanceGauge;
    private GaugeView humidityGauge;
    private GaugeView temperatureGauge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        sensorDataRef = firebaseDatabase.getReference("sensor_data");

        // Get references to GaugeView widgets in your layout
        distanceGauge = findViewById(R.id.gaugeView);
        humidityGauge = findViewById(R.id.gaugeView3);
        temperatureGauge = findViewById(R.id.gaugeView4);

        // Set up a listener to retrieve data from Firebase
        sensorDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve values from the dataSnapshot
                    double distance = dataSnapshot.child("Distance").getValue(Double.class);
                    int humidity = dataSnapshot.child("Humidity").getValue(Integer.class);
                    int temperature = dataSnapshot.child("Temperature").getValue(Integer.class);

                    // Update the GaugeView widgets with the retrieved values
                    distanceGauge.setValue((int) distance);
                    humidityGauge.setValue(humidity);
                    temperatureGauge.setValue(temperature);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
