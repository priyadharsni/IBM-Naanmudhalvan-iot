package com.example.floodmonitoring;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class Weather extends AppCompatActivity {
    private EditText locationEditText;
    private Button getWeatherButton;
    private TextView locationTextView;
    private TextView temperatureTextView;
    private TextView rainTextView;
    private ImageView weatherIcon;
    private String apiKey = "b141741991279045db84b225a342d85e"; // Replace with your OpenWeatherMap API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        locationEditText = findViewById(R.id.locationEditText);
        getWeatherButton = findViewById(R.id.getWeatherButton);
        locationTextView = findViewById(R.id.locationTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        rainTextView = findViewById(R.id.rainTextView);


        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationEditText.getText().toString();
                // Fetch weather data for the entered location
                new WeatherTask().execute(location);
            }
        });
    }

    class WeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if (params.length != 1) {
                return "Invalid parameters"; // Handle invalid input
            }

            String location = params[0];
            String result = "";

            try {
                String url = "http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey;
                URL apiURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();

                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temperature = jsonObject.getJSONObject("main").getString("temp");
                // Extract rain information
                JSONObject rainData = jsonObject.optJSONObject("rain");
                String rainInfo = (rainData != null) ? rainData.toString() : "No rain data available";

                // Update the UI
                locationTextView.setText(city);
                temperatureTextView.setText(temperature + "°C");
                rainTextView.setText("Rain: " + rainInfo);
                // Load and display the weather icon (you may need to use a library like Picasso or Glide)
                // Set the weather icon based on the "icon" value (e.g., "01d" for clear sky)
                // Replace "R.drawable.icon01d" with the correct resource ID for your weather icons
                // weatherIcon.setImageResource(R.drawable.icon01d);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Weather.this, "Error fetching weather data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
